package com.oresomecraft.coin;

import com.oresomecraft.coin.database.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class SQLManager implements Listener {

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
                        mysql.createTable("CREATE TABLE wallets ( uuid VARCHAR(255) NOT NULL, balance FLOAT NOT NULL, PRIMARY KEY (uuid) ) ENGINE=MyISAM;");
                    }
                    if (!mysql.checkTable("transactions")) {
                        plugin.getLogger().info("Creating table 'transactions' in database " + mysql_db);
                        mysql.createTable("CREATE TABLE transactions ( fromId VARCHAR(255) NOT NULL, toId VARCHAR(255) NOT NULL, amount int NOT NULL, time VARCHAR(32) NOT NULL, PRIMARY KEY (fromId) ) ENGINE=MyISAM;");
                    }
                } else {
                    plugin.getLogger().severe("Error connecting to database, there'll most likely be a lot of console errors!");
                }

                mysql.close();
            }
        });
    }

    public static void pushWallet(final Wallet wallet) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            public void run() {
                try {
                    Connection connection = mysql.open();
                    Statement statement = connection.createStatement();
                    statement.executeUpdate("UPDATE wallets ( 'uuid', 'balance' ) VALUES ( '" + wallet.getUserId() + "', '" + wallet.getBalance() + "' );");
                    plugin.getLogger().info("Successfully pushed a Wallet to the database! [" + wallet.getUserId() + "]");
                    connection.close();
                } catch (SQLException ex) {
                    plugin.getLogger().warning("An SQLException occured when trying to push a wallet's contents to the database! [" + wallet.getUserId() + "]");
                }
            }
        });
    }

    public static String executeTransaction(Transaction transaction) {
        if (!transaction.getFrom().getUserId().equals(transaction.getTo().getUserId())) {
            if (transaction.getFrom().getUserId() != null && transaction.getTo().getUserId() != null) {
                Wallet fromWallet = transaction.getFrom();
                Wallet toWallet = transaction.getTo();

                if (transaction.getAmount() > 0) {
                    if (fromWallet.getBalance() >= transaction.getAmount()) {
                        fromWallet.withdrawCoins(transaction.getAmount());
                        toWallet.depositCoins(transaction.getAmount());
                        Bukkit.getPluginManager().callEvent(new TransactionEvent(transaction.getFrom(), transaction.getTo(), transaction.getAmount()));
                        fromWallet.writeToDatabase();
                        toWallet.writeToDatabase();
                        logTransaction(transaction);
                        if (transaction.getAmount() > 1) {
                            return ChatColor.GREEN + "You paid " + Bukkit.getPlayer(transaction.getTo().getUserId()).getDisplayName() + " " + transaction.getAmount() + " OresomeCoins!";
                        } else {
                            return ChatColor.GREEN + "You paid " + Bukkit.getPlayer(transaction.getTo().getUserId()).getDisplayName() + " " + transaction.getAmount() + " OresomeCoin!";
                        }
                    } else {
                        return ChatColor.RED + "You don't have enough OresomeCoin to carry out this transaction!";
                    }
                } else {
                    return ChatColor.RED + "You can't pay " + Bukkit.getPlayer(transaction.getTo().getUserId()).getDisplayName() + " 0 coins!";
                }
            } else {
                return ChatColor.RED + "The player you're attempting to pay doesn't seem to be online!";
            }
        } else {
            return ChatColor.RED + "You can't pay yourself!";
        }
    }

    public static void logTransaction(final Transaction transaction) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            public void run() {
                if (transaction.getAmount() > 1) {
                    plugin.getLogger().info(Bukkit.getPlayer(transaction.getFrom().getUserId()).getDisplayName() + " just paid " + Bukkit.getPlayer(transaction.getTo().getUserId()).getName() + " " + transaction.getAmount() + " OresomeCoins!");
                } else {
                    plugin.getLogger().info(Bukkit.getPlayer(transaction.getFrom().getUserId()).getDisplayName() + " just paid " + Bukkit.getPlayer(transaction.getTo().getUserId()).getName() + " " + transaction.getAmount() + " OresomeCoin!");
                }
                try {
                    Connection connection = mysql.open();
                    Statement statement = connection.createStatement();
                    statement.executeUpdate("INSERT INTO transactions ( 'fromId', 'toId', 'amount' ) VALUES ( '" + transaction.getFrom().getUserId() + "', '" + transaction.getFrom().getUserId() + "', '" + transaction.getAmount() + "', '" + transaction.getTime() + "' );");
                    connection.close();
                } catch (SQLException ex) {
                    plugin.getLogger().warning("An SQLException occured while attempting to log a transaction!");
                }
            }
        });
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            public void run() {
                UUID userId = event.getPlayer().getUniqueId();
                if (OresomeCoin.onlineWallets.get(userId.toString()) != null) return;
                try {
                    Connection connection = mysql.open();
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery("SELECT * FROM wallets WHERE uuid = '" + userId.toString() + "';");
                    resultSet.next();
                    if (resultSet.getString("uuid") != null && !resultSet.getString("uuid").equals(" ") && !resultSet.getString("uuid").equals("")) {
                        OresomeCoin.onlineWallets.put(userId.toString(), new Wallet(userId, resultSet.getInt("balance")));
                    } else {
                        statement.executeUpdate("INSERT INTO wallets ( 'uuid', 'balance' ) VALUES ( '" + userId.toString() + "', '0' );");
                        Wallet wallet = new Wallet(userId, 0);
                        OresomeCoin.onlineWallets.put(userId.toString(), wallet);
                        plugin.getLogger().info("Successfully created a wallet for " + userId.toString());
                        connection.close();
                    }
                } catch (SQLException ex) {
                    plugin.getLogger().warning("An SQL error occured while attempting to get a UUID's wallet!");
                    plugin.getLogger().warning("UUID = " + userId.toString());
                }
            }
        });
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            public void run() {
                UUID userId = event.getPlayer().getUniqueId();
                Wallet wallet = OresomeCoin.onlineWallets.get(userId.toString());
                if (wallet != null) {
                    try {
                        Connection connection = mysql.open();
                        Statement statement = connection.createStatement();
                        statement.executeUpdate("UPDATE wallets ( 'uuid', 'balance' ) VALUES ( '" + wallet.getUserId() + "', '" + wallet.getBalance() + "' );");
                        plugin.getLogger().info("Successfully pushed a Wallet to the database! [" + wallet.getUserId() + "]");
                        connection.close();
                        OresomeCoin.onlineWallets.remove(userId.toString());
                    } catch (SQLException ex) {
                        plugin.getLogger().warning("An SQLException occured when trying to push a wallet's contents to the database! [" + wallet.getUserId() + "]");
                    }
                }
            }
        });
    }
}
