package net.clownercraft.modreqcc.command;

import net.clownercraft.modreqcc.manager.TicketManager;
import net.clownercraft.modreqcc.ticket.Ticket;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Gideon on 6/15/2017.
 */
public class ModReqCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if(sender instanceof Player){
            Player p = (Player) sender;
            if(p.hasPermission("modreqcc.player")) {
                if (args.length > 0) {
                    List<Ticket> tickets = TicketManager.getOpenTicketsForPlayer(p);

                    if (tickets.size() > 4 && !p.hasPermission("modreqcc.moderator")) {
                        p.sendMessage(ChatColor.RED + "You can only have 5 open tickets at a time!");
                        return true;
                    }
                    String msg = StringUtils.join(args, " ");

                    try {
                        Ticket t = TicketManager.createTicket(p, msg);
                        p.sendMessage(ChatColor.GREEN + "Created ticket with ID #" + t.getID() + ", please wait for a moderator to view it.");
                    } catch (SQLException e) {
                        e.printStackTrace();
                        p.sendMessage(ChatColor.RED + "There was a fatal error trying to create a ticket. Please report this to admins!");
                    }
                } else {
                    p.sendMessage(ChatColor.RED + "Usage: /modreq <message>");
                }
            }else{
                p.sendMessage(ChatColor.RED + "You don't have permission to make tickets!");
            }

        }

        return true;
    }
}
