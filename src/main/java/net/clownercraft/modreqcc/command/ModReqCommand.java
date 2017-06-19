package net.clownercraft.modreqcc.command;

import net.clownercraft.modreqcc.manager.TicketManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.sql.SQLException;

/**
 * Created by Gideon on 6/15/2017.
 */
public class ModReqCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if(sender instanceof Player){
            Player p = (Player) sender;
            if(args.length > 0) {
                String msg = StringUtils.join(args, " ");

                try {
                    TicketManager.createTicket(p, msg);
                    p.sendMessage(ChatColor.GREEN + "Created ticket, please wait for a moderator to view it.");
                } catch (SQLException e) {
                    e.printStackTrace();
                    p.sendMessage(ChatColor.RED + "There was a fatal error trying to create a ticket. Please report this to admins!");
                }
            }else{
                p.sendMessage(ChatColor.RED + "Usage: /modreq <message>");
            }

        }

        return true;
    }
}
