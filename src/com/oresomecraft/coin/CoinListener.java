package com.oresomecraft.coin;

import com.oresomecraft.coin.database.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class CoinListener implements Listener {

    public CoinListener() {
        Bukkit.getPluginManager().registerEvents(this, OresomeCoin.getInstance());
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(OresomeCoin.getInstance(), new Runnable() {
            public void run() {
                UUID userId = event.getPlayer().getUniqueId();
                if (OresomeCoin.onlineWallets.get(userId.toString()) != null) {
                    OresomeCoin.onlineWallets.remove(userId.toString());
                }
                try {
                    MySQL mysql = new MySQL(OresomeCoin.getInstance().getLogger(), "[OresomeCoin]", SQLManager.mysql_host,
                            SQLManager.mysql_port, SQLManager.mysql_db, SQLManager.mysql_user, SQLManager.mysql_password);
                    mysql.open();
                    ResultSet resultSet = mysql.query("SELECT * FROM wallets WHERE uuid = '" + userId.toString() + "';");
                    if (resultSet.isBeforeFirst()) {
                        resultSet.next();
                        OresomeCoin.onlineWallets.put(userId.toString(), new Wallet(resultSet.getInt("id"), resultSet.getInt("balance"), event.getPlayer().getName()));
                        mysql.query("UPDATE wallets SET name = '" + event.getPlayer().getName() + "' WHERE uuid = '" + userId.toString() + "';");
                        mysql.close();
                    } else {
                        mysql.query("INSERT INTO wallets ( uuid, name, balance ) VALUES ( '" + userId.toString() + "', '" + event.getPlayer().getName() + "', 0 );");
                        ResultSet walletId = mysql.query("SELECT * FROM wallets WHERE uuid = '" + userId.toString() + "'");
                        if (walletId.isBeforeFirst()) {
                            walletId.next();
                        }
                        Wallet wallet = new Wallet(walletId.getInt("id"), 0, event.getPlayer().getName());
                        OresomeCoin.onlineWallets.put(userId.toString(), wallet);
                        OresomeCoin.getInstance().getLogger().info("Successfully created a wallet for " + userId.toString());
                        mysql.close();
                    }
                } catch (SQLException ex) {
                    OresomeCoin.getInstance().getLogger().warning("An SQL error occured while attempting to get a UUID's wallet!");
                    OresomeCoin.getInstance().getLogger().warning("UUID = " + userId.toString());
                }
            }
        });
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(OresomeCoin.getInstance(), new Runnable() {
            public void run() {
                UUID userId = event.getPlayer().getUniqueId();
                Wallet wallet = OresomeCoin.onlineWallets.get(userId.toString());
                if (wallet != null) {
                    MySQL mysql = new MySQL(OresomeCoin.getInstance().getLogger(), "[OresomeCoin]", SQLManager.mysql_host,
                            SQLManager.mysql_port, SQLManager.mysql_db, SQLManager.mysql_user, SQLManager.mysql_password);
                    mysql.open();
                    mysql.query("UPDATE wallets SET balance = " + wallet.getBalance() + " WHERE uuid= '" + userId.toString() + "';");
                    OresomeCoin.onlineWallets.remove(userId.toString());
                    OresomeCoin.getInstance().getLogger().info("Successfully pushed a Wallet to the database! [" + wallet.getWalletId() + "]");
                    mysql.close();
                }
            }
        });
    }

}