package net.clownercraft.modreqcc;

/**
 * Created by Gideon on 6/19/2017.
 */
public enum TicketFlagType {

    ONLINE("Requires Player Online", "ON"), ADMIN("Admin Required", "AD"), OTHER("Second moderator needed", "SO");

    private String name;
    private String shortName;

    TicketFlagType(String name, String shortName){
        this.name = name;
        this.shortName = shortName;
    }

    public String getName(){
        return this.name;
    }

    public String getShortName() {
        return this.shortName;
    }

}
