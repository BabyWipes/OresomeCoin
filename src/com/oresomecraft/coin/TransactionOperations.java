package com.oresomecraft.coin;

import com.oresomecraft.coin.database.MySQL;
import com.oresomecraft.coin.playerinterfaces.Transaction;
import com.oresomecraft.coin.playerinterfaces.TransactionEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TransactionOperations {

    public static void pushWallet(final Wallet wallet) {
        Bukkit.getScheduler().runTaskAsynchronously(OresomeCoin.getInstance(), new Runnable() {
            public void run() {
                MySQL mysql = new MySQL(OresomeCoin.getInstance().getLogger(), "[OresomeCoin]", SQLManager.mysql_host,
                        SQLManager.mysql_port, SQLManager.mysql_db, SQLManager.mysql_user, SQLManager.mysql_password);
                mysql.open();
                mysql.query("UPDATE wallets SET balance = " + wallet.getBalance() + " WHERE id= '" + wallet.getWalletId() + "';");
                OresomeCoin.getInstance().getLogger().info("Successfully pushed a Wallet to the database! [" + wallet.getWalletId() + "]");
                mysql.close();
            }
        });
    }

    public static String executeTransaction(Transaction transaction) {
        if (!transaction.getFrom().getOwner().equals(transaction.getTo().getOwner())) {
            if (transaction.getFrom().getOwner() != null && transaction.getTo().getOwner() != null) {
                Wallet fromWallet = transaction.getFrom();
                Wallet toWallet = transaction.getTo();

                if (transaction.getAmount() > 0) {
                    if (fromWallet.getBalance() >= transaction.getAmount()) {
                        fromWallet.withdrawCoins(transaction.getAmount());
                        toWallet.depositCoins(transaction.getAmount());
                        Bukkit.getPluginManager().callEvent(new TransactionEvent(transaction));
                        fromWallet.writeToDatabase();
                        toWallet.writeToDatabase();
                        if (transaction.getAmount() > 1) {
                            return ChatColor.GREEN + "You paid " + transaction.getTo().getOwner() + " " + transaction.getAmount() + " OresomeCoins!";
                        } else {
                            return ChatColor.GREEN + "You paid " + transaction.getTo().getOwner() + " " + transaction.getAmount() + " OresomeCoin!";
                        }
                    } else {
                        return ChatColor.RED + "You don't have enough OresomeCoin to carry out this transaction!";
                    }
                } else {
                    return ChatColor.RED + "You can't pay " + transaction.getTo().getOwner() + " 0 coins!";
                }
            } else {
                return ChatColor.RED + "The player you're attempting to pay doesn't seem to be online!";
            }
        } else {
            return ChatColor.RED + "You can't pay yourself!";
        }
    }

    public static void giveCoins(final Wallet wallet, final int amount) {
        wallet.depositCoins(amount);
        pushWallet(wallet);
        logTransaction(new Transaction(OresomeCoin.masterWallet, wallet, amount));
    }

    public static void removeCoins(final Wallet wallet, final int amount) {
        wallet.withdrawCoins(amount);
        pushWallet(wallet);
        logTransaction(new Transaction(OresomeCoin.masterWallet, wallet, amount));
    }

    public static Wallet getWallet(final Player player) {
        try {
            MySQL mysql = new MySQL(OresomeCoin.getInstance().getLogger(), "[OresomeCoin]", SQLManager.mysql_host,
                    SQLManager.mysql_port, SQLManager.mysql_db, SQLManager.mysql_user, SQLManager.mysql_password);
            mysql.open();
            ResultSet resultSet = mysql.query("SELECT * FROM wallets WHERE uuid = '" + player.getUniqueId().toString() + "';");
            if (resultSet.isBeforeFirst()) {
                resultSet.next();
                mysql.close();
                return new Wallet(resultSet.getInt("id"), resultSet.getInt("balance"), resultSet.getString("name"));
            }
        } catch (SQLException ex) {
            OresomeCoin.getInstance().getLogger().warning("An SQL error occured while attempting to get a UUID's wallet!");
            OresomeCoin.getInstance().getLogger().warning("UUID = " + player.getUniqueId().toString());
        }
        return null;
    }

    public static void logTransaction(final Transaction transaction) {
        Bukkit.getScheduler().runTaskAsynchronously(OresomeCoin.getInstance(), new Runnable() {
            public void run() {
                if (transaction.getAmount() > 1) {
                    OresomeCoin.getInstance().getLogger().info(transaction.getFrom().getOwner() + " just paid " + Bukkit.getPlayer(transaction.getTo().getOwner() + " " + transaction.getAmount() + " OresomeCoins!"));
                } else {
                    OresomeCoin.getInstance().getLogger().info(transaction.getFrom().getOwner() + " just paid " + Bukkit.getPlayer(transaction.getTo().getOwner() + " " + transaction.getAmount() + " OresomeCoin!"));
                }

                MySQL mysql = new MySQL(OresomeCoin.getInstance().getLogger(), "[OresomeCoin]", SQLManager.mysql_host,
                        SQLManager.mysql_port, SQLManager.mysql_db, SQLManager.mysql_user, SQLManager.mysql_password);
                mysql.open();
                mysql.query("INSERT INTO playerinterfaces ( fromId, toId, amount, time ) VALUES ( '" + transaction.getFrom().getWalletId() + "', '" + transaction.getFrom().getWalletId() + "', " + transaction.getAmount() + ", '" + transaction.getTime() + "' );");
                mysql.close();
            }
        });
    }
}
