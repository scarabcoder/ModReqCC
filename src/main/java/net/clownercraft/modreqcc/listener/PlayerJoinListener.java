package net.clownercraft.modreqcc.listener;/**
 * Created by Gideon on 6/27/2017.
 */

import net.clownercraft.modreqcc.ModReqCC;
import net.clownercraft.modreqcc.ScarabUtil;
import net.clownercraft.modreqcc.TicketFlagType;
import net.clownercraft.modreqcc.manager.TicketManager;
import net.clownercraft.modreqcc.ticket.Ticket;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent e){
        if(e.getPlayer().hasPermission("modreqcc.moderator")) {
            final int openTickets = ScarabUtil.getOpenTicketsForModerator(e.getPlayer());
            if (openTickets > 0) {
                BukkitRunnable r = new BukkitRunnable() {
                    public void run() {
                        e.getPlayer().sendMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + "There are " + ChatColor.GREEN + openTickets + ChatColor.GOLD + ChatColor.BOLD.toString() + " open tickets.");
                    }
                };
                r.runTaskLater(ModReqCC.getPlugin(), 40);
            }

            try {
                for (Ticket t : TicketManager.getOpenTickets()) {
                    if (t.getAuthor().getUniqueId().equals(e.getPlayer().getUniqueId()) && t.getFlagTypes().contains(TicketFlagType.ONLINE)) {
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if (p.hasPermission("modreqcc.moderator")) {
                                p.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + e.getPlayer().getName() + " has come online, ticket #" + t.getID() + " can now be processed.");
                            }
                        }
                    }
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }

}
