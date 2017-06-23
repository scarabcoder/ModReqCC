package net.clownercraft.modreqcc.ticket;

import net.clownercraft.modreqcc.TicketFlagType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

/**
 * Created by Gideon on 6/22/2017.
 */
public class TicketFlag {

    public TicketFlagType type;

    public UUID setter;

    public TicketFlag(TicketFlagType flag, UUID player){
        this.type = flag;
        this.setter = player;
    }

    public TicketFlagType getFlagType(){
        return this.type;
    }

    public OfflinePlayer getSetter(){
        return Bukkit.getOfflinePlayer(setter);
    }

}
