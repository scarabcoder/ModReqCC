package net.clownercraft.modreqcc;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.clownercraft.modreqcc.command.CloseCommand;
import net.clownercraft.modreqcc.command.ModReqCommand;
import net.clownercraft.modreqcc.command.StatusCommand;
import net.clownercraft.modreqcc.command.TicketCommand;
import net.clownercraft.modreqcc.manager.TicketManager;
import net.clownercraft.modreqcc.ticket.Ticket;
import net.clownercraft.modreqcc.ticket.TicketFlag;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ModReqCC extends JavaPlugin implements PluginMessageListener{

    private static Connection connection;

    private static String serverName;

    private static Plugin plugin;

    private static ServerSocket socket;

    @Override
    public void onEnable(){

        plugin = this;

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);


        this.getConfig().options().copyDefaults(true);
        this.saveDefaultConfig();

        FileConfiguration plyml = YamlConfiguration.loadConfiguration(new InputStreamReader(this.getResource("plugin.yml")));

        Logger l = Logger.getLogger(plyml.getString("name"));

        l.log(Level.INFO, "Started " + plyml.getString("name") + " version " + plyml.getString("version") + ".");
        try {
            ModReqCC.connection = DriverManager.getConnection("jdbc:mysql://" + this.getConfig().getString("mysql.address") + ":" + this.getConfig().getString("mysql.port") + "/" + this.getConfig().getString("mysql.schema"),
                    this.getConfig().getString("mysql.user"), this.getConfig().getString("mysql.password"));
        } catch (SQLException e) {
            e.printStackTrace();
            l.log(Level.SEVERE, "Could not connect to the MySQL database!");

        }

        String createTicketsTable = "CREATE TABLE IF NOT EXISTS `" + this.getConfig().getString("mysql.schema") + "`.`tickets` (" +
                "  `id` INT NOT NULL AUTO_INCREMENT," +
                "  `author` VARCHAR(36) NULL," +
                "  `location` MEDIUMTEXT NULL," +
                "  `closed` TINYINT(1) NULL," +
                "  `timestamp` BIGINT NULL," +
                "  `server` MEDIUMTEXT NULL," +
                "  `flags` MEDIUMTEXT NULL," +
                "  PRIMARY KEY (`id`))";

        String createCommentsTable = "CREATE TABLE IF NOT EXISTS `" + this.getConfig().getString("mysql.schema") + "`.`ticketcomments` (\n" +
                "  `id` INT NOT NULL AUTO_INCREMENT,\n" +
                "  `ticketid` INT NULL,\n" +
                "  `author` VARCHAR(36) NULL,\n" +
                "  `message` MEDIUMTEXT NULL,\n" +
                "  `timestamp` BIGINT NULL,\n" +
                "  PRIMARY KEY (`id`))\n" +
                "ENGINE = InnoDB";

        Connection c = ModReqCC.getConnection();
        l.log(Level.INFO, "Creating default tables.");
        try {
            c.prepareStatement(createTicketsTable).executeUpdate();
            c.prepareStatement(createCommentsTable).executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            l.log(Level.SEVERE, "There was an error creating the default tables!");
        }


        try {
            ServerSocket socket = new ServerSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.registerCommands();

        BukkitRunnable r = new BukkitRunnable() {
            public void run() {

                int ts = 0;
                List<Ticket> openTickets = null;
                try {
                    openTickets = new CopyOnWriteArrayList<Ticket>(TicketManager.getOpenTickets());

                    for(Ticket t : openTickets){
                        if(t.getFlags().contains(TicketFlagType.ONLINE)){
                            openTickets.remove(t);
                        }
                    }
                    ts = TicketManager.getOpenTicketAmount();

                } catch (SQLException e) {
                    e.printStackTrace();
                    return;
                }
                if(ts > 0){
                    for(Player p : Bukkit.getOnlinePlayers()){
                        List<Ticket> specificTickets = new CopyOnWriteArrayList<Ticket>(openTickets);
                        for(Ticket t : specificTickets){
                            if(t.getFlagTypes().contains(TicketFlagType.ADMIN) && !p.hasPermission("modreqcc.admin")){
                                specificTickets.remove(t);
                            }else if(t.getFlagTypes().contains(TicketFlagType.ONLINE) && t.getAuthor().getPlayer() == null){
                                specificTickets.remove(t);
                            }
                            for(TicketFlag flag : t.getFlags()){
                                if(flag.getFlagType().equals(TicketFlagType.OTHER) && flag.getSetter().getUniqueId().equals(p.getUniqueId()) && specificTickets.contains(t)){
                                    specificTickets.remove(t);
                                }
                            }
                        }
                        if(specificTickets.size() > 0) {
                            if (p.hasPermission("modreqcc.moderator")) {
                                p.sendMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + "There are " + ChatColor.GREEN + specificTickets.size() + ChatColor.GOLD + ChatColor.BOLD.toString() + " open tickets.");
                                //p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                            }
                        }
                    }
                }

            }
        };
        r.runTaskTimer(this, 0, 20 * 120);

        BukkitRunnable getCurrServer = new BukkitRunnable() {

            public void run() {
                if(ModReqCC.serverName != null){
                    List<Player> pls = new ArrayList<Player>(Bukkit.getServer().getOnlinePlayers());
                    if(pls.size() > 0){
                        Player p = pls.get(0);
                        ByteArrayDataOutput out = ByteStreams.newDataOutput();
                        out.writeUTF("GetServer");
                        p.sendPluginMessage(ModReqCC.getPlugin(), "BungeeCord", out.toByteArray());
                    }

                }else{
                    this.cancel();
                }
            }
        };

        getCurrServer.runTaskTimer(this, 0, 40);

    }

    public static ServerSocket getSocket(){
        return socket;
    }

    public static Plugin getPlugin(){
        return plugin;
    }

    private void registerCommands(){
        this.getCommand("modreq").setExecutor(new ModReqCommand());
        this.getCommand("status").setExecutor(new StatusCommand());
        this.getCommand("close").setExecutor(new CloseCommand());
        this.getCommand("ticket").setExecutor(new TicketCommand());
    }

    public static String getBungeeCordServerName(){
        return serverName;
    }

    public static Connection getConnection(){
        return connection;
    }

    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();
        if (subchannel.equals("GetServer")) {
            serverName = in.readUTF();

        }
    }
}
