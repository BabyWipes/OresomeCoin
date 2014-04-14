package com.oresomecraft.coin;

import com.oresomecraft.coin.database.MySQL;
import org.bukkit.Bukkit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SQLManager {

    public static MySQL mysql;
    public static String mysql_host;
    public static String mysql_port;
    public static String mysql_db;
    public static String mysql_user;
    public static String mysql_password;

    static OresomeCoin plugin = OresomeCoin.getInstance();

    public static void setupDatabase() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            public void run() {
                mysql_host = plugin.getConfig().getString("mysql.host");
                mysql_db = plugin.getConfig().getString("mysql.database");
                mysql_user = plugin.getConfig().getString("mysql.username");
                mysql_password = plugin.getConfig().getString("mysql.password");
                mysql_port = plugin.getConfig().getString("mysql.port");

                mysql = new MySQL(plugin.getLogger(), "[OresomeCoin]", mysql_host, mysql_port, mysql_db, mysql_user, mysql_password);

                plugin.getLogger().info("Connecting to MySQL database...");
                mysql.open();

                if (mysql.checkConnection()) {
                    plugin.getLogger().info("Successfully connected to database!");

                    if (!mysql.checkTable("wallets")) {
                        plugin.getLogger().info("Creating table 'wallets' in database " + mysql_db);
                        mysql.createTable("CREATE TABLE wallets ( uuid string NOT NULL AUTO_INCREMENT, balance FLOAT NOT NULL, PRIMARY KEY (uuid) ) ENGINE=MyISAM;");
                    }

                } else {
                    plugin.getLogger().severe("Error connecting to database, there'll most likely be a lot of console errors!");
                }

                mysql.close();
            }
        });
    }

    public static boolean hasWallet(UUID userId) {
        try {
            mysql.open();
            ResultSet resultSet = mysql.query("SELECT * FROM wallets WHERE uuid = '" + userId.toString() + "'");
            mysql.close();
            return resultSet.getString("uuid") != null && !resultSet.getString("uuid").equals(" ") && !resultSet.getString("uuid").equals("");
        } catch (SQLException ex) {
            plugin.getLogger().warning("An SQL error occured while attempting to check if a UUID was attached to a wallet!");
            plugin.getLogger().warning("UUID = " + userId.toString());
        }
        return false;
    }

    public static Wallet getWallet(UUID userId) {
        if (OresomeCoin.onlineWallets.get(userId.toString()) != null) {
            return OresomeCoin.onlineWallets.get(userId.toString());
        } else if (hasWallet(userId)) {
            try {
                mysql.open();
                ResultSet resultSet = mysql.query("SELECT * FROM wallets WHERE uuid = '" + userId.toString() + "'");
                UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                mysql.close();
                return new Wallet(uuid, resultSet.getInt("balance"));
            } catch (SQLException ex) {
                plugin.getLogger().warning("An SQL error occured while attempting to get a UUID's wallet!");
                plugin.getLogger().warning("UUID = " + userId.toString());
            }
        }
        return null;
    }

    public static void generateWallet(UUID userId) {
        if (!hasWallet(userId)) {
            mysql.open();
            mysql.query("INSERT INTO wallets WHERE uuid = '" + userId.toString() + "' AND balance = 0");
            Wallet wallet = new Wallet(userId, 0);
            OresomeCoin.onlineWallets.put(userId.toString(), wallet);
            plugin.getLogger().info("Successfully created a wallet for " + userId.toString());
            mysql.close();
        } else {
            plugin.getLogger().warning("Attempted to create a wallet but the UUID already has a wallet attached.");
            plugin.getLogger().warning("UUID = " + userId.toString());
        }
    }
}
