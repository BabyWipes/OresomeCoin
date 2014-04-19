package com.oresomecraft.coin;

import com.oresomecraft.coin.database.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.UUID;

public class SQLOperations {

    public static void pushWallet(final Wallet wallet) {
        Bukkit.getScheduler().runTaskAsynchronously(OresomeCoin.getInstance(), new Runnable() {
            public void run() {
                MySQL mysql = new MySQL(OresomeCoin.getInstance().getLogger(), "[OresomeCoin]", SQLManager.mysql_host,
                        SQLManager.mysql_port, SQLManager.mysql_db, SQLManager.mysql_user, SQLManager.mysql_password);
                mysql.open();
                mysql.query("UPDATE wallets SET balance = " + wallet.getBalance() + " WHERE uuid= '" + wallet.getUserId().toString() + "';");
                OresomeCoin.getInstance().getLogger().info("Successfully pushed a Wallet to the database! [" + wallet.getUserId() + "]");
                mysql.close();
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

    public static void giveCoins(final Wallet wallet, final int amount) {
        wallet.depositCoins(amount);
        Wallet masterWallet = new Wallet(UUID.fromString("-1"), -1);
        pushWallet(wallet);
        logTransaction(new Transaction(masterWallet, wallet, amount));
    }

    public static void logTransaction(final Transaction transaction) {
        Bukkit.getScheduler().runTaskAsynchronously(OresomeCoin.getInstance(), new Runnable() {
            public void run() {
                if (transaction.getAmount() > 1) {
                    OresomeCoin.getInstance().getLogger().info(Bukkit.getPlayer(transaction.getFrom().getUserId()).getDisplayName() + " just paid " + Bukkit.getPlayer(transaction.getTo().getUserId()).getName() + " " + transaction.getAmount() + " OresomeCoins!");
                } else {
                    OresomeCoin.getInstance().getLogger().info(Bukkit.getPlayer(transaction.getFrom().getUserId()).getDisplayName() + " just paid " + Bukkit.getPlayer(transaction.getTo().getUserId()).getName() + " " + transaction.getAmount() + " OresomeCoin!");
                }

                MySQL mysql = new MySQL(OresomeCoin.getInstance().getLogger(), "[OresomeCoin]", SQLManager.mysql_host,
                        SQLManager.mysql_port, SQLManager.mysql_db, SQLManager.mysql_user, SQLManager.mysql_password);
                mysql.open();
                mysql.query("INSERT INTO transactions ( fromId, toId, amount ) VALUES ( '" + transaction.getFrom().getUserId() + "', '" + transaction.getFrom().getUserId() + "', " + transaction.getAmount() + ", '" + transaction.getTime() + "' );");
                mysql.close();
            }
        });
    }

}
