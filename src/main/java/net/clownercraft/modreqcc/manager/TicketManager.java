package net.clownercraft.modreqcc.manager;

import com.sun.org.apache.xpath.internal.operations.Mod;
import net.clownercraft.modreqcc.ModReqCC;
import net.clownercraft.modreqcc.ScarabUtil;
import net.clownercraft.modreqcc.TicketFlag;
import net.clownercraft.modreqcc.command.TicketCommand;
import net.clownercraft.modreqcc.ticket.Ticket;
import net.clownercraft.modreqcc.ticket.TicketComment;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by Gideon on 6/15/2017.
 */
public class TicketManager {

    public static List<Ticket> getOpenTickets() throws SQLException {

        List<Ticket> tickets = new ArrayList<Ticket>();

        Connection c = ModReqCC.getConnection();

        PreparedStatement st = c.prepareStatement("SELECT * FROM tickets WHERE closed=?");
        st.setBoolean(1, false);

        ResultSet set = st.executeQuery();
        while(set.next()){
            List<TicketComment> cmnts = TicketManager.getTicketComments(set.getInt("id"));
            List<TicketFlag> flags = new ArrayList<TicketFlag>();

            for(String str : set.getString("flags").split(",")){
                flags.add(TicketFlag.valueOf(str));
            }

            tickets.add(new Ticket(set.getInt("id"),
                    Bukkit.getOfflinePlayer(UUID.fromString(set.getString("author"))),
                    cmnts,
                    ScarabUtil.getLocationFromString(set.getString("location")),
                    set.getBoolean("closed"),
                    set.getLong("timestamp"),
                    set.getString("server"),
                    flags));
        }

        return tickets;

    }

    public static List<Ticket> getTicketsforPlayer(OfflinePlayer p) throws SQLException {
        List<Ticket> tickets = new ArrayList<Ticket>();

        Connection c = ModReqCC.getConnection();

        PreparedStatement st = c.prepareStatement("SELECT * FROM tickets WHERE author=?");
        st.setString(1,p.getUniqueId().toString());

        ResultSet set = st.executeQuery();
        while(set.next()){
            List<TicketComment> cmnts = TicketManager.getTicketComments(set.getInt("id"));

            List<TicketFlag> flags = new ArrayList<TicketFlag>();

            for(String str : set.getString("flags").split(",")){
                flags.add(TicketFlag.valueOf(str));
            }

            tickets.add(new Ticket(set.getInt("id"),
                    Bukkit.getOfflinePlayer(UUID.fromString(set.getString("author"))),
                    cmnts,
                    ScarabUtil.getLocationFromString(set.getString("location")),
                    set.getBoolean("closed"),
                    set.getLong("timestamp"),
                    set.getString("server"),
                    flags));
        }

        return tickets;
    }

    public static int getOpenTicketAmount() throws SQLException {

        Connection c = ModReqCC.getConnection();
        PreparedStatement st = c.prepareStatement("SELECT 1 FROM tickets WHERE closed=?");
        st.setBoolean(1, false);

        ResultSet set = st.executeQuery();
        set.last();
        return set.getRow();

    }


    public static List<TicketComment> getTicketComments(int ticketID) throws SQLException {
        Connection c = ModReqCC.getConnection();
        PreparedStatement st = c.prepareStatement("SELECT * FROM ticketcomments WHERE ticketid=?");
        st.setInt(1, ticketID);

        ResultSet s = st.executeQuery();
        List<TicketComment> cmnts = new ArrayList<TicketComment>();
        while(s.next()){
            cmnts.add(new TicketComment(s.getInt("id"),
                    ticketID,
                    Bukkit.getOfflinePlayer(UUID.fromString(s.getString("author"))),
                    s.getString("message"),
                    s.getLong("timestamp")));
        }
        return cmnts;
    }

    public static Ticket getTicket(int id){

        Connection c = ModReqCC.getConnection();

        try {
            List<TicketComment> cmnts = TicketManager.getTicketComments(id);

            PreparedStatement st = c.prepareStatement("SELECT * FROM tickets WHERE id=?");
            st.setInt(1, id);
            ResultSet s = st.executeQuery();
            if(!s.next()) return null;
            Location l = null;
            if(s.getString("server").equalsIgnoreCase(ModReqCC.getBungeeCordServerName())){
                l = ScarabUtil.getLocationFromString(s.getString("location"));
            }
            List<TicketFlag> flags = new ArrayList<TicketFlag>();

            for(String str : s.getString("flags").split(",")){
                flags.add(TicketFlag.valueOf(str));
            }

            Ticket t = new Ticket(id, Bukkit.getOfflinePlayer(UUID.fromString(s.getString("author"))),
                    cmnts,
                    l,
                    s.getBoolean("closed"), s.getLong("timestamp"),
                    s.getString("server"), flags);
            return t;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean removeTicket(int id) throws SQLException {

        Connection c = ModReqCC.getConnection();

        PreparedStatement st = c.prepareStatement("SELECT 1 FROM tickets WHERE id=?");
        st.setInt(1, id);

        ResultSet s = st.executeQuery();
        if(!s.next())
            return false;

        st = c.prepareStatement("DELETE FROM tickets WHERE id=?");
        st.setInt(1, id);
        st.executeUpdate();

        return true;

    }

    public static TicketComment addComment(int ticketID, String author, String message) throws SQLException {

        long currTime = System.currentTimeMillis();

        Connection c = ModReqCC.getConnection();

        PreparedStatement st = c.prepareStatement("INSERT INTO ticketcomments (ticketid, author, message, timestamp) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

        st.setInt(1, ticketID);
        st.setString(2, author);
        st.setString(3, message);
        st.setLong(4, currTime);

        st.executeUpdate();

        ResultSet set = st.getGeneratedKeys();
        set.next();
        int id = set.getInt(1);



        return new TicketComment(id, ticketID, Bukkit.getOfflinePlayer(UUID.fromString(author)), message, currTime);



    }

    public static void addFlagToTicket(Ticket t, TicketFlag flag) throws SQLException {
        Connection c = ModReqCC.getConnection();

        PreparedStatement st = c.prepareStatement("UPDATE tickets SET flags=? WHERE id=?");
        st.setInt(2, t.getID());

        List<TicketFlag> flags = t.getFlags();
        if(!flags.contains(flag)){
            flags.add(flag);
            st.setString(1, StringUtils.join(flags, ","));
            st.executeUpdate();
        }

    }

    public static Ticket createTicket(Player author, String message) throws SQLException{

        long currTime = System.currentTimeMillis();

        Location l = author.getLocation();

        Connection c = ModReqCC.getConnection();


        PreparedStatement st = c.prepareStatement("INSERT INTO tickets (author, location, closed, timestamp, server) VALUES(?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

        st.setString(1, author.getUniqueId().toString());
        st.setString(2, ScarabUtil.locationToString(l));
        st.setBoolean(3,false);
        st.setLong(4, currTime);
        st.setString(5, ModReqCC.getBungeeCordServerName());


        st.executeUpdate();
        ResultSet s = st.getGeneratedKeys();

        s.next();
        int ticketID = s.getInt(1);

        st = c.prepareStatement("INSERT INTO ticketcomments (ticketid, author, message, timestamp) VALUES(?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
        st.setInt(1, ticketID);
        st.setString(2, author.getUniqueId().toString());
        st.setString(3, message);
        st.setLong(4, currTime);

        st.executeUpdate();

        s = st.getGeneratedKeys();
        s.next();
        int commentID = s.getInt(1);




        TicketComment comment = new TicketComment(commentID, ticketID, author, message, currTime);

        Ticket t = new Ticket(ticketID, author, Arrays.asList(comment), l, false, currTime, ModReqCC.getBungeeCordServerName(), new ArrayList<TicketFlag>());

        for(Player p : Bukkit.getOnlinePlayers()){
            if(p.hasPermission("modreq.moderator")){

                TextComponent tc = new TextComponent(ChatColor.BOLD.toString() + ChatColor.GREEN + author.getName() + " started a ticket: \"" + message + "\" " + ChatColor.RESET + "(Click to teleport).");
                tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ticket " + ticketID + " teleport"));

                p.spigot().sendMessage(tc);
                p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
            }
        }

        return t;
    }

    public static void setClosed(int ticketID, boolean closed){
        Connection c = ModReqCC.getConnection();

        try {
            PreparedStatement st = c.prepareStatement("UPDATE tickets SET closed=? WHERE id=?");

            st.setBoolean(1, closed);
            st.setInt(2, ticketID);

            st.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


}
