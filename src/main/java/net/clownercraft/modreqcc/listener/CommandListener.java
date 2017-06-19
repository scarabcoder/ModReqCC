package net.clownercraft.modreqcc.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 * Created by Gideon on 6/16/2017.
 */
public class CommandListener implements Listener {

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e){
        if(e.getPlayer().hasPermission("modreqcc.moderator")){

        }
    }

}
