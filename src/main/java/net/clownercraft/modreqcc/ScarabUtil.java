package net.clownercraft.modreqcc;

import net.clownercraft.modreqcc.manager.TicketManager;
import net.clownercraft.modreqcc.ticket.Ticket;
import net.clownercraft.modreqcc.ticket.TicketComment;
import net.clownercraft.modreqcc.ticket.TicketFlag;
import net.clownercraft.modreqcc.ticket.TicketLocation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Gideon on 6/16/2017.
 */
public class ScarabUtil {

    public static TicketLocation getLocationFromString(String loc){
        String[] coords = loc.split(":");
        return new TicketLocation(coords[0], Double.valueOf(coords[1]), Double.valueOf(coords[2]), Double.valueOf(coords[3]));

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
                flags += flag.getFlagType().getName() + ChatColor.GRAY + " (" + flag.getSetter().getName() + "), " + ChatColor.RESET;
            }else{
                flags += flag.getFlagType().getName() + ChatColor.GRAY + " (" + flag.getSetter().getName() + ")" + ChatColor.RESET;
            }
        }
        if(flags == "")
            flags = "None";
        String date = new SimpleDateFormat("MMM dd @ HH:mm").format(t.getCreationDate());
        strs.add(ChatColor.BOLD + ChatColor.GREEN.toString() + "Ticket #" + t.getID());
        strs.add(ChatColor.AQUA + "Status: " + ChatColor.GRAY + (t.isClosed() ? ChatColor.RED + "Closed" : ChatColor.GREEN + "Open"));
        strs.add(ChatColor.AQUA + "Submitter: " + ChatColor.GRAY + t.getAuthor().getName());
        strs.add(ChatColor.AQUA + "World: " + ChatColor.GRAY + t.getTicketLocation().getWorld());
        strs.add(ChatColor.AQUA + "Created on: " + ChatColor.GRAY + date);
        strs.add(ChatColor.AQUA + "Flags: " + ChatColor.GRAY + flags);
        strs.add(ChatColor.AQUA + "Comments:");
        for(TicketComment cmnt : t.getComments()){
            strs.add("  " + ChatColor.AQUA + cmnt.getAuthor().getName() + ": " + ChatColor.GRAY + cmnt.getMessage());
        }

        return strs;

    }

    public static String getSingleLineDetails(Ticket t){

        String flags = "";
        for(TicketFlag flag : t.getFlags()){
            flags += " " + ChatColor.RESET + "[" + ChatColor.DARK_AQUA + flag.getFlagType().getShortName() + ChatColor.RESET + "]";
        }

        String date = new SimpleDateFormat("MMM dd HH:mm").format(t.getCreationDate());

        String msg = t.getComments().get(0).getMessage();
        if(msg.length() > 12){
            msg = msg.substring(0, 12) + "...";
        }


        String name = (t.getAuthor().isOnline() ? ChatColor.GREEN + t.getAuthor().getName() : ChatColor.RED + t.getAuthor().getName());

        String closed = ChatColor.GRAY + "[" + (t.isClosed() ? ChatColor.RED + "Closed" : ChatColor.GREEN + "Open") + ChatColor.GRAY + "]";

        return ChatColor.GOLD + "#" + t.getID() + " " + ChatColor.WHITE + name + ChatColor.GRAY + " @ " + ChatColor.AQUA + date + " " + closed + flags + ChatColor.GRAY + ": " + msg + " " + ChatColor.DARK_GRAY + "(" + (t.getComments().size() - 1) + ")";

        //return ChatColor.GOLD + "ID #" + t.getID() + ChatColor.WHITE + t.getAuthor().getName() + ChatColor.AQUA + " @ " + date + ChatColor.DARK_GRAY + "[" + (t.isClosed() ? "CLOSED" : "OPEN") + "] " + flags + ChatColor.DARK_GRAY + ": " + msg + ChatColor.GRAY + "(" + (t.getComments().size() - 1) + " comments)";




        //return  ChatColor.GOLD + "ID #" + t.getID() + ChatColor.AQUA + " @ " + date + ChatColor.DARK_GREEN + " [" + (t.isClosed() ? "CLOSED" : "OPEN") + "]" + flags + " " +
        //        ChatColor.WHITE + t.getAuthor().getName() + ": " + ChatColor.GRAY + (t.getComments().get(0).getMessage().length() > 13 ? t.getComments().get(0).getMessage().subSequence(0,12) : t.getComments().get(0).getMessage()) + "...";
    }

    public static boolean canModeratorHandleTicket(Ticket t, Player p){

        for(TicketFlag flag : t.getFlags()){
            if(flag.getFlagType().equals(TicketFlagType.OTHER) && flag.getSetter().getUniqueId().equals(p.getUniqueId())){
                return false;
            }
            if(flag.getFlagType().equals(TicketFlagType.ADMIN) && !p.hasPermission("modreqcc.admin")){
                return false;
            }
            if(flag.getFlagType().equals(TicketFlagType.ONLINE) && !t.getAuthor().isOnline()){
                return false;
            }
        }

        return true;
    }

    public static void playNotificationSound(Player p){
        p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 0.8f, 3f);
    }

    public static int getOpenTicketsForModerator(Player p){

        try {
            int open = 0;
            List<Ticket> openTickets = TicketManager.getOpenTickets();
            for(Ticket t: openTickets){
                if(ScarabUtil.canModeratorHandleTicket(t, p)){
                    open++;
                }
            }
            return open;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
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
