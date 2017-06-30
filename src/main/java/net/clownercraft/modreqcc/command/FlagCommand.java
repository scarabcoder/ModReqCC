package net.clownercraft.modreqcc.command;

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
 * Created by Gideon on 6/27/2017.
 */
public class FlagCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(args.length > 1){
            List<String> msgs = new ArrayList<String>(Arrays.asList(args));
            msgs.remove(0);
            msgs.remove(0);
            Bukkit.getServer().dispatchCommand(sender, "ticket " + args[0] + " flag "  + args[1] + " " + StringUtils.join(msgs, " "));
        }else{
            sender.sendMessage(ChatColor.RED + "Usage: /flag <id> <flag>");
        }
        return true;
    }
}
