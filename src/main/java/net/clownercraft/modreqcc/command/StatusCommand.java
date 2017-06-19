package net.clownercraft.modreqcc.command;

import net.clownercraft.modreqcc.ScarabUtil;
import net.clownercraft.modreqcc.manager.TicketManager;
import net.clownercraft.modreqcc.ticket.Ticket;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Gideon on 6/15/2017.
 */
public class StatusCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(sender instanceof Player){
            Player p = (Player) sender;
            if(args.length == 0){
                try {
                    List<Ticket> tickets = TicketManager.getTicketsforPlayer(p);

                    if(tickets.size() == 0){
                        p.sendMessage(ChatColor.RED + "You haven't created any tickets!");
                    }else{
                        p.sendMessage(ChatColor.GOLD + "---- Your tickets: ----");
                        for(Ticket t : tickets){
                            p.sendMessage(ChatColor.GOLD + "#" + t.getID() + ChatColor.AQUA + " @ " + t.getCreationDate().toString() + ChatColor.DARK_GREEN + " [" + (t.isClosed() ? "CLOSED" : "OPEN") + "] " +
                                    ChatColor.GRAY + (t.getComments().get(0).getMessage().length() > 13 ? t.getComments().get(0).getMessage().subSequence(0,12) : t.getComments().get(0).getMessage()) + "...");
                        }
                        p.sendMessage(ChatColor.GOLD + "Use /status <id> for more info.");
                    }


                } catch (SQLException e) {
                    e.printStackTrace();
                    p.sendMessage(ChatColor.RED + "There was a fatal error getting your tickets. Please report this to an admin.");
                }

            } else if (args.length == 1) {
                Ticket t = null;
                try {
                    t = TicketManager.getTicket(Integer.valueOf(args[0]));
                } catch (NumberFormatException e ){
                    p.sendMessage(ChatColor.RED + "Please enter a valid ID.");
                    return true;
                }
                if(t == null){
                    p.sendMessage(ChatColor.RED + "Ticket not found!");
                    return true;
                }
                if(!t.getAuthor().getUniqueId().equals(p.getUniqueId())){
                    p.sendMessage(ChatColor.RED + "This is not your ticket!");
                    return true;
                }
                for(String str : ScarabUtil.getTicketDetails(t)){
                    p.sendMessage(str);
                }
            }
        }
        return true;
    }
}
