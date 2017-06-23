package net.clownercraft.modreqcc;

/**
 * Created by Gideon on 6/19/2017.
 */
public enum TicketFlagType {

    ONLINE("Requires Player Online"), ADMIN("Admin Required"), OTHER("Second moderator needed");

    private String name;

    TicketFlagType(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

}
