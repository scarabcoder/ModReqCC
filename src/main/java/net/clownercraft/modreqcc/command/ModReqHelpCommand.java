package net.clownercraft.modreqcc.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Gideon on 6/27/2017.
 */
public class ModReqHelpCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(sender.hasPermission("modreqcc.moderator")){
            sender.sendMessage(ChatColor.GOLD + "ModReqCC Help");
            List<String> help = Arrays.asList(
                    "/ticket <id/page> [close/open/comment/flag/teleport/page number] [msg]: Modify or view a ticket.",
                    "/close <id> [msg]: Close a ticket with an optional message.",
                    "/flag <id> <flag>: Flag a ticket.",
                    "/comment <id> <msg>: Comment on a ticket.",
                    "/tp-id <id>: Teleport to a ticket.",
                    "/tickets <username>: View all tickets by player.",
                    "/status: View your last five tickets.");
            for(String msg : help){
                String cmnd = msg.split(":")[0];
                String info = ":" + msg.split(":")[1];
                sender.sendMessage(ChatColor.DARK_AQUA + cmnd + ChatColor.GRAY + info);
            }
        }
        return true;
    }
}
