package net.clownercraft.modreqcc.ticket;

import net.clownercraft.modreqcc.ModReqCC;
import net.clownercraft.modreqcc.TicketFlagType;
import net.clownercraft.modreqcc.manager.TicketManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Gideon on 6/15/2017.
 */
public class Ticket {

    private OfflinePlayer author;
    private int id;
    private List<TicketComment> comments;
    private TicketLocation location;
    private boolean closed;
    private long timestamp;
    private String server;
    private List<TicketFlag> flags;

    public Ticket(int id, OfflinePlayer author, List<TicketComment> comments, TicketLocation location, boolean closed, long timestamp, String server, List<TicketFlag> flags){
        this.id = id;
        this.author = author;
        this.comments = comments;
        this.location = location;
        this.closed = closed;
        this.timestamp = timestamp;
        this.server = server;
        this.flags = flags;
    }

    public List<TicketFlag> getFlags(){
        return flags;
    }

    public List<TicketFlagType> getFlagTypes(){
        List<TicketFlagType> flagTypes = new ArrayList<TicketFlagType>();
        for(TicketFlag flag : flags){
            flagTypes.add(flag.getFlagType());
        }
        return flagTypes;
    }


    public void addFlag(TicketFlagType flagType, Player setter){

        try {
            TicketFlag flag = new TicketFlag(flagType, setter.getUniqueId());
            TicketManager.addFlagToTicket(this, flag);
            flags.add(flag);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public TicketComment addComment(Player author, String message, boolean notifyAuthor) throws SQLException {

        if(this.getAuthor().getPlayer() != null && notifyAuthor){
            this.getAuthor().getPlayer().sendMessage(ChatColor.GREEN + author.getName() + " added a comment to your ticket: " + ChatColor.GRAY + message);
        }


        return TicketManager.addComment(this.getID(), author.getUniqueId().toString(), message);
    }

    public TicketComment addComment(Player author, String message) throws SQLException {
        return this.addComment(author, message, true);
    }

    public boolean isCurrentServer(){
        if(ModReqCC.getBungeeCordServerName() == null) return true;
            return this.getServer().equals(ModReqCC.getBungeeCordServerName());
    }

    public int getID(){
        return this.id;
    }

    public String getServer() {
        return this.server;
    }

    public long getTimestamp(){
        return this.timestamp;
    }

    public Date getCreationDate(){
        return new Date(this.timestamp);
    }

    public boolean isClosed(){
        return this.closed;
    }

    public OfflinePlayer getAuthor(){
        return this.author;
    }

    public List<TicketComment> getComments(){
        return this.comments;
    }

    public TicketLocation getTicketLocation(){
        return this.location;
    }

    public void setClosed(boolean closed){
        this.closed = closed;

        TicketManager.setClosed(this.getID(), closed);


    }

    public void closeTicket(){
        this.setClosed(true);
    }

    public void openTicket(){
        this.setClosed(false);
    }

}
