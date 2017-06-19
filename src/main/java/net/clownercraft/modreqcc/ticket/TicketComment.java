package net.clownercraft.modreqcc.ticket;

import org.bukkit.OfflinePlayer;

import java.util.Date;

/**
 * Created by Gideon on 6/15/2017.
 */
public class TicketComment {

    private int id;
    private int ticketID;
    private OfflinePlayer author;
    private String message;
    private long timestamp;

    public TicketComment(int id, int ticketID, OfflinePlayer author, String message, long timestamp){
        this.id = id;
        this.ticketID = ticketID;
        this.author = author;
        this.message = message;
        this.timestamp = timestamp;
    }

    public int getCommentID(){
        return this.id;
    }

    public int getTicketID(){
        return ticketID;
    }

    public OfflinePlayer getAuthor(){
        return this.author;
    }

    public String getMessage(){
        return this.message;
    }

    public long getTimestamp(){
        return this.timestamp;
    }

    public Date getDate(){
        return new Date(this.timestamp);
    }

}
