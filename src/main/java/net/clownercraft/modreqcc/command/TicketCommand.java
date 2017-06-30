package net.clownercraft.modreqcc.command;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.clownercraft.modreqcc.ModReqCC;
import net.clownercraft.modreqcc.ScarabUtil;
import net.clownercraft.modreqcc.TicketFlagType;
import net.clownercraft.modreqcc.manager.TicketManager;
import net.clownercraft.modreqcc.ticket.Ticket;
import net.clownercraft.modreqcc.ticket.TicketFlag;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Gideon on 6/15/2017.
 */
public class TicketCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if (p.hasPermission("modreqcc.moderator")) {
                if (args.length > 1 && !args[0].equalsIgnoreCase("page")) {
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
                        if(closed && t.isClosed()){
                            p.sendMessage(ChatColor.RED + "Ticket already closed!");
                            return true;
                        }else if(!closed && !t.isClosed()){
                            p.sendMessage(ChatColor.RED + "Ticket already open!");
                        }
                        if(closed)
                            t.closeTicket();
                        else
                            t.openTicket();
                        try {

                            if(args.length > 2){
                                List<String> msgList = new ArrayList<String>(Arrays.asList(args));
                                msgList.remove(0);
                                msgList.remove(0);
                                t.addComment(p, StringUtils.join(msgList, " ") + " " + ChatColor.GRAY + "[" + ChatColor.RED + "Ticked " + (closed ? "Closed" : "Opened") + ChatColor.GRAY + "]");
                            } else {
                                t.addComment(p, ChatColor.GRAY + "[" + ChatColor.RED + "Ticked " + (closed ? "Closed" : "Opened") + ChatColor.GRAY + "]");
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        p.sendMessage(ChatColor.GREEN + (closed ? "Closed" : "Opened") + " ticket #" + t.getID());
                    }else if(args[1].equalsIgnoreCase("teleport") || args[1].equalsIgnoreCase("tp")){

                        if(t.isCurrentServer()){
                            p.teleport(t.getTicketLocation().getBukkitLocation());
                            p.sendMessage(ChatColor.GREEN + "Teleporting to ticket #" + t.getID() + ".");
                            try {
                                t.addComment(p, "Teleported to ticket location.");
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }else{
                            ByteArrayDataOutput out = ByteStreams.newDataOutput();
                            out.writeUTF("Connect");
                            out.writeUTF(t.getServer());

                            p.sendPluginMessage(ModReqCC.getPlugin(), "BungeeCord", out.toByteArray());
                            p.sendMessage(ChatColor.GREEN + "Teleporting to server for ticket #" + t.getID() + ".");

                        }

                    }else if(args[1].equalsIgnoreCase("flag")){
                        if(args.length > 2) {
                            try {
                                TicketFlagType flagType = TicketFlagType.valueOf(args[2].toUpperCase());
                                if (args.length > 3) {
                                    List<String> msg = new ArrayList<String>(Arrays.asList(args));
                                    msg.remove(0);
                                    msg.remove(0);
                                    msg.remove(0);
                                    t.addComment(p, StringUtils.join(msg, " "));
                                }else{
                                    t.addComment(p, "Added flag \"" + flagType.getName() + "\".");
                                }
                                t.addFlag(flagType, p);
                                p.sendMessage(ChatColor.GREEN + "Flag \"" + flagType.getName() + "\" added to ticket #" + t.getID() + ".");
                                if(t.getAuthor().isOnline()){
                                    t.getAuthor().getPlayer().sendMessage(ChatColor.GOLD + p.getName() + " added a flag to your ticket: " + ChatColor.GRAY + "[" + ChatColor.DARK_AQUA.toString() + flagType.getName() + ChatColor.GRAY + "]");
                                }
                            } catch (IllegalArgumentException e) {
                                p.sendMessage(ChatColor.RED + "Flag must be one of the following:");
                                String flags = "";
                                for (TicketFlagType f : TicketFlagType.values()) {
                                    flags += f.toString();
                                    if (f != TicketFlagType.values()[TicketFlagType.values().length - 1]) {
                                        flags += ", ";
                                    }
                                }
                                p.sendMessage(flags);
                            } catch (SQLException e){
                                p.sendMessage(ChatColor.RED + "There was an internal error getting information from the database. Please report this to an admin.");
                                e.printStackTrace();
                            }
                        }else{
                            p.sendMessage(ChatColor.RED + "/ticket <id> flag <flag> [msg]");
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
                    if(tickets.size() == 0){
                        sender.sendMessage(ChatColor.RED + "No open tickets.");
                        return true;
                    }
                    int page = 1;
                    if(args.length == 1){
                        sender.sendMessage(ChatColor.RED + "Usage: /ticket page <page>");
                        return true;
                    }else if(args.length != 0) {
                        try {
                            page = Integer.valueOf(args[1]);
                        } catch (NumberFormatException e) {
                            sender.sendMessage(ChatColor.RED + "Expected page number, got text.");
                            return true;
                        }
                    }

                    int ticketsPerPage = 10;

                    if(tickets.size() <= (ticketsPerPage)){
                        p.sendMessage(ChatColor.GOLD + "---- Open Tickets (1/1) ----");
                    }else{
                        int pages = (int) Math.ceil((double)tickets.size() / (double)ticketsPerPage);
                        if(page == 0 || page > pages){
                            p.sendMessage(ChatColor.RED + "Page not found!");
                            return true;
                        }
                        p.sendMessage(ChatColor.GOLD + "---- Open Tickets (" + page + "/" + pages + ") ----");
                        page = page - 1;
                        tickets = tickets.subList(ticketsPerPage * page, (ticketsPerPage * page + ticketsPerPage > tickets.size() - 1 ? tickets.size() : ticketsPerPage * page + ticketsPerPage));
                    }
                    for(Ticket t : tickets){
                        String details = ScarabUtil.getSingleLineDetails(t);
                        if(!ScarabUtil.canModeratorHandleTicket(t, p))
                            details = ChatColor.DARK_GRAY + ChatColor.stripColor(details);
                        p.sendMessage(details);
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
