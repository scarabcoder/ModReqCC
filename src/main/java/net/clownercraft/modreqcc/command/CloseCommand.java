package net.clownercraft.modreqcc.command;

import net.clownercraft.modreqcc.manager.TicketManager;
import net.clownercraft.modreqcc.ticket.Ticket;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Gideon on 6/20/2017.
 */
public class CloseCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(args.length > 0){
            String msg = "";
            if(args.length > 1){
                List<String> msgList = new ArrayList<String>(Arrays.asList(args));

                msgList.remove(0);
                msg = " " + StringUtils.join(msgList, " ");
            }
            Bukkit.getServer().dispatchCommand(sender, "ticket " + args[0] + " close" + msg);
        }else{
            sender.sendMessage(ChatColor.RED + "Usage: /close <ticket> [msg]");
        }
        return true;
    }
}
