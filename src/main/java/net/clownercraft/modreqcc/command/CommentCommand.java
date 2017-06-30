package net.clownercraft.modreqcc.command;

import net.clownercraft.modreqcc.manager.TicketManager;
import net.clownercraft.modreqcc.ticket.Ticket;
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
import java.util.List;

/**
 * Created by Gideon on 6/27/2017.
 */
public class CommentCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(sender.hasPermission("modreqc.moderator")) {
            if (args.length > 1) {
                List<String> msgList = new ArrayList<String>(Arrays.asList(args));
                msgList.remove(0);
                Bukkit.getServer().dispatchCommand(sender, "ticket " + args[0] + " comment " + StringUtils.join(msgList, " "));
            } else {
                sender.sendMessage(ChatColor.RED + "Usage: /comment <id> <msg>");
            }
        }else if(sender.hasPermission("modreqc.player")){
            if(sender instanceof Player) {
                Player p = (Player) sender;
                if (args.length > 1) {
                    try {
                        Ticket t = TicketManager.getTicket(Integer.valueOf(args[0]));
                        if (t == null) {
                            sender.sendMessage(ChatColor.RED + "Ticket not found!");
                            return true;
                        }
                        if (!t.getAuthor().getUniqueId().equals(p.getUniqueId())){
                            p.sendMessage(ChatColor.RED + "This is not your ticket!");
                            return true;
                        }

                        List<String> msg = new ArrayList<String>(Arrays.asList(args));

                        msg.remove(0);

                        try {
                            t.addComment(p, StringUtils.join(msg, " "), false);
                            p.sendMessage(ChatColor.GREEN + "Commented on ticket #" + t.getID() + ".");
                        } catch (SQLException e) {
                            p.sendMessage(ChatColor.RED + "There was an internal error with the database.");
                            e.printStackTrace();
                        }

                    } catch (NumberFormatException e) {
                        sender.sendMessage(ChatColor.RED + "Please enter a ticket ID.");
                    }
                }
            }
        }else{
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
        }
        return true;
    }
}
