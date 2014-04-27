package com.oresomecraft.coin;

import com.oresomecraft.coin.database.MySQL;
import org.bukkit.Bukkit;

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
                        mysql.createTable("CREATE TABLE wallets ( id int NOT NULL AUTO_INCREMENT, uuid VARCHAR(64) NOT NULL, name VARCHAR(16), balance FLOAT NOT NULL, PRIMARY KEY (id) ) ENGINE=MyISAM;");
                    }
                    if (!mysql.checkTable("transactions")) {
                        plugin.getLogger().info("Creating table 'transactions' in database " + mysql_db);
                        mysql.createTable("CREATE TABLE transactions ( id int NOT NULL AUTO_INCREMENT, fromId int NOT NULL, toId int NOT NULL, amount int NOT NULL, time VARCHAR(32) NOT NULL, PRIMARY KEY (id) ) ENGINE=MyISAM;");
                    }
                } else {
                    plugin.getLogger().severe("Error connecting to database, there'll most likely be a lot of console errors!");
                }

                mysql.close();
            }
        });
    }

}