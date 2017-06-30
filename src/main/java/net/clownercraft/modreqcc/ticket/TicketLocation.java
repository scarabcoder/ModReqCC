package net.clownercraft.modreqcc.ticket;

import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * Created by Gideon on 6/30/2017.
 */
public class TicketLocation {

    private String world;
    private double x;
    private double y;
    private double z;

    public TicketLocation(String world, double x, double y, double z){
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public TicketLocation(Location loc){
        this.world = loc.getWorld().getName();
        this.x = loc.getX();
        this.y = loc.getY();
        this.z = loc.getZ();
    }

    public String getWorld(){
        return this.world;
    }

    public double getX(){
        return this.x;
    }

    public double getY(){
        return this.y;
    }

    public double getZ(){
        return this.z;
    }


    public Location getBukkitLocation(){
        return new Location(Bukkit.getWorld(this.getWorld()), this.getX(), this.getY(), this.getZ());
    }

}
