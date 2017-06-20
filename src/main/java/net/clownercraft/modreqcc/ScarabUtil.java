package net.clownercraft.modreqcc;

import net.clownercraft.modreqcc.ticket.Ticket;
import net.clownercraft.modreqcc.ticket.TicketComment;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gideon on 6/16/2017.
 */
public class ScarabUtil {

    public static Location getLocationFromString(String loc){
        String[] coords = loc.split(":");
        return new Location(Bukkit.getWorld(coords[0]), Double.valueOf(coords[1]), Double.valueOf(coords[2]), Double.valueOf(coords[3]));

    }

    public static String locationToString(Location l){
        String sl = "";
        sl += l.getWorld().getName() + ":";
        sl += l.getX() + ":";
        sl += l.getY() + ":";
        sl += l.getZ();
        return sl;
    }

    public static List<String> getTicketDetails(Ticket t){
        List<String> strs = new ArrayList<String>();

        String flags = "";
        for(TicketFlag flag : t.getFlags()){
            if(!t.getFlags().get(t.getFlags().size() - 1).equals(flag)){
                flags += flag.getName() + ", ";
            }else{
                flags += flag.getName();
            }
        }
        if(flags == "")
            flags = "None";

        strs.add(ChatColor.BOLD + ChatColor.GREEN.toString() + "Ticket #" + t.getID());
        strs.add(ChatColor.AQUA + "Status: " + ChatColor.GRAY + (t.isClosed() ? ChatColor.RED + "Closed" : ChatColor.GREEN + "Open"));
        strs.add(ChatColor.AQUA + "Submitter: " + ChatColor.GRAY + t.getAuthor().getName());
        strs.add(ChatColor.AQUA + "World: " + ChatColor.GRAY + t.getTicketLocation().getWorld().getName());
        strs.add(ChatColor.AQUA + "Created on: " + ChatColor.GRAY + t.getCreationDate().toString());
        strs.add(ChatColor.AQUA + "Flags: " + ChatColor.GRAY + flags);
        strs.add(ChatColor.AQUA + "Comments:");
        for(TicketComment cmnt : t.getComments()){
            strs.add("  " + ChatColor.AQUA + cmnt.getAuthor().getName() + ": " + ChatColor.GRAY + cmnt.getMessage());
        }

        return strs;

    }

    public static String getSingleLineDetails(Ticket t){
        return  ChatColor.GOLD + "#" + t.getID() + ChatColor.AQUA + " @ " + t.getCreationDate().toString() + ChatColor.DARK_GREEN + " [" + (t.isClosed() ? "CLOSED" : "OPEN") + "] " +
                ChatColor.GRAY + (t.getComments().get(0).getMessage().length() > 13 ? t.getComments().get(0).getMessage().subSequence(0,12) : t.getComments().get(0).getMessage()) + "...";
    }

    public static long stringToSeed(String s) {
        if (s == null) {
            return 0;
        }
        long hash = 0;
        for (char c : s.toCharArray()) {
            hash = 31L*hash + c;
        }
        return hash;
    }

}
