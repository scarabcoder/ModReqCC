package net.clownercraft.modreqcc;

/**
 * Created by Gideon on 6/19/2017.
 */
public enum TicketFlag {

    ONLINE("Requires Player Online"), ADMIN("Admin Required");

    private String name;

    TicketFlag(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

}
