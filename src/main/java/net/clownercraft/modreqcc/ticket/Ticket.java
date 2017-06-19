package net.clownercraft.modreqcc.ticket;

import net.clownercraft.modreqcc.manager.TicketManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Created by Gideon on 6/15/2017.
 */
public class Ticket {

    private OfflinePlayer author;
    private int id;
    private List<TicketComment> comments;
    private Location location;
    private boolean closed;
    private long timestamp;
    private String server;

    public Ticket(int id, OfflinePlayer author, List<TicketComment> comments, Location location, boolean closed, long timestamp, String server){
        this.id = id;
        this.author = author;
        this.comments = comments;
        this.location = location;
        this.closed = closed;
        this.timestamp = timestamp;
        this.server = server;
    }

    public TicketComment addComment(Player author, String message) throws SQLException {

        if(this.getAuthor().getPlayer() != null){
            this.getAuthor().getPlayer().sendMessage(ChatColor.GREEN + author.getName() + " added a comment to your ticket: " + message);
        }

        return TicketManager.addComment(this.getID(), author.getUniqueId().toString(), message);
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

    public Location getTicketLocation(){
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
