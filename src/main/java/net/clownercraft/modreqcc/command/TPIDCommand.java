package net.clownercraft.modreqcc.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Created by Gideon on 6/27/2017.
 */
public class TPIDCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(args.length == 1){
            Bukkit.getServer().dispatchCommand(sender, "ticket " + args[0] + " teleport");
        }else{
            sender.sendMessage(ChatColor.RED + "Usage: /tp-id <id>");
        }
        return true;
    }
}
