package net.clownercraft.modreqcc.command;

import net.clownercraft.modreqcc.ScarabUtil;
import net.clownercraft.modreqcc.manager.TicketManager;
import net.clownercraft.modreqcc.ticket.Ticket;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Gideon on 6/27/2017.
 */
public class TicketsCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(sender.hasPermission("modreqcc.moderator")) {
            if (args.length > 0) {

                int page = 1;
                if (args.length == 2) {
                    try {
                        page = Integer.valueOf(args[1]);
                    } catch (NumberFormatException e) {
                    }
                }

                OfflinePlayer pl = null;
                for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
                    if (p.getName().equalsIgnoreCase(args[0])) {
                        pl = p;
                        break;
                    }
                }
                if (pl == null) {
                    sender.sendMessage(ChatColor.RED + "Couldn't find player " + args[0] + "!");
                    return true;
                }

                try {
                    int ticketsPerPage = 10;

                    List<Ticket> tickets = TicketManager.getTicketsforPlayer(pl);

                    if (tickets.size() == 0) {
                        sender.sendMessage(ChatColor.RED + "No open tickets for player " + pl.getName() + ".");
                        return true;
                    }

                    int ts = tickets.size();

                    int totalPages = (int) Math.ceil((double) ts / (double) ticketsPerPage);


                    if (page < 1 || page > totalPages) {
                        sender.sendMessage(ChatColor.RED + "Page number not found!");
                        return true;
                    }
                    sender.sendMessage(ChatColor.GOLD + "---- Tickets for " + pl.getName() + " (Page " + page + "/" + totalPages + ") ----");

                    page = page - 1;

                    tickets = tickets.subList(page * ticketsPerPage, (page * ticketsPerPage + ticketsPerPage > tickets.size() ? tickets.size() : page * ticketsPerPage + ticketsPerPage));

                    for (Ticket t : tickets) {
                        sender.sendMessage(ScarabUtil.getSingleLineDetails(t));
                    }

                } catch (SQLException e) {
                    sender.sendMessage(ChatColor.RED + "There was an error getting information from the database.");
                    e.printStackTrace();
                }

            }
        }else{
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
        }
        return true;
    }
}
