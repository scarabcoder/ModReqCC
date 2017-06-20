package net.clownercraft.modreqcc.command;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.clownercraft.modreqcc.ModReqCC;
import net.clownercraft.modreqcc.ScarabUtil;
import net.clownercraft.modreqcc.TicketFlag;
import net.clownercraft.modreqcc.manager.TicketManager;
import net.clownercraft.modreqcc.ticket.Ticket;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Gideon on 6/15/2017.
 */
public class TicketCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if (p.hasPermission("modreqcc.moderator")) {
                if (args.length > 1) {
                    Ticket t = null;
                    try {
                        t = TicketManager.getTicket(Integer.valueOf(args[0]));
                    } catch (NumberFormatException e) {
                        p.sendMessage(ChatColor.RED + "Please enter a ticket ID.");
                        return true;
                    }
                    if(t == null){
                        p.sendMessage(ChatColor.RED + "Ticket not found!");
                        return true;
                    }
                    if (args[1].equalsIgnoreCase("comment")) {
                        if (args.length > 2) {
                            List<String> msgList = new ArrayList<String>(Arrays.asList(args));
                            msgList.remove(0);
                            msgList.remove(0);
                            String msg = StringUtils.join(msgList, " ");
                            try {
                                t.addComment(p, msg);
                                p.sendMessage(ChatColor.GREEN + "Commented on ticket #" + t.getID());
                            } catch (SQLException e) {
                                e.printStackTrace();
                                p.sendMessage(ChatColor.RED + "There was an internal error adding the comment. Please send the console stack trace to a dev..");
                            }
                        }
                    }else if(args[1].equalsIgnoreCase("close") || args[1].equalsIgnoreCase("open")){
                        boolean closed = args[1].equalsIgnoreCase("close");
                        if(closed)
                            t.closeTicket();
                        else
                            t.openTicket();
                        try {
                            t.addComment(p, "Ticket " + (closed ? "closed" : "opened") + ".");
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        p.sendMessage(ChatColor.GREEN + (closed ? "Closed" : "Opened") + " ticket #" + t.getID());
                    }else if(args[1].equalsIgnoreCase("teleport") || args[1].equalsIgnoreCase("tp")){

                        if(t.isCurrentServer()){
                            p.teleport(t.getTicketLocation());
                            p.sendMessage(ChatColor.GREEN + "Teleporting to ticket #" + t.getID() + ".");
                        }else{
                            ByteArrayDataOutput out = ByteStreams.newDataOutput();
                            out.writeUTF("Connect");
                            out.writeUTF(t.getServer());

                            p.sendPluginMessage(ModReqCC.getPlugin(), "BungeeCord", out.toByteArray());
                            p.sendMessage(ChatColor.GREEN + "Teleporting to server for ticket #" + t.getID() + ".");

                        }

                    }else if(args[1].equalsIgnoreCase("flag")){
                        try {
                            TicketFlag flag = TicketFlag.valueOf(args[2].toUpperCase());
                            t.addFlag(flag);
                            p.sendMessage(ChatColor.GREEN + "Flag \"" + flag.getName() + "\" added to ticket #" + t.getID() + ".");
                        } catch (IllegalArgumentException e){
                            p.sendMessage(ChatColor.RED + "Flag must be one of the following:");
                            String flags = "";
                            for(TicketFlag f : TicketFlag.values()){
                                flags += f.toString();
                            }
                            p.sendMessage(flags);
                        }

                    } else {
                        p.sendMessage(ChatColor.RED + "Usage: /ticket [id] [comment/close/open/teleport/tp] [message]");
                    }
                } else if(args.length == 1){
                    Ticket t = null;
                    try {
                        t = TicketManager.getTicket(Integer.valueOf(args[0]));
                    } catch (NumberFormatException e) {
                        p.sendMessage(ChatColor.RED + "Please enter a ticket ID.");
                        return true;
                    }
                    if(t == null){
                        p.sendMessage(ChatColor.RED + "Ticket not found!");
                        return true;
                    }

                    for(String str : ScarabUtil.getTicketDetails(t)){
                        p.sendMessage(str);
                    }

                } else {
                    List<Ticket> tickets = null;
                    try {
                        tickets = TicketManager.getOpenTickets();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        p.sendMessage(ChatColor.RED + "There was a fatal error getting the list of open tickets - please report this to an admin.");
                        return true;
                    }
                    if(tickets.size() > 0){
                        p.sendMessage(ChatColor.GOLD + "---- All Open Tickets ----");
                        for(Ticket t : tickets){
                            p.sendMessage(ScarabUtil.getSingleLineDetails(t));
                        }
                    }else{
                        p.sendMessage(ChatColor.RED + "No open tickets!");
                    }
                }
            } else {
                p.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            }
        }else{
            sender.sendMessage(ChatColor.RED + "Player-only command!");
        }
        return true;
    }
}
