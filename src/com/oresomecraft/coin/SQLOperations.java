package com.oresomecraft.coin;

import com.oresomecraft.coin.database.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.sql.ResultSet;
import java.sql.SQLException;
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
        Wallet masterWallet = new Wallet(UUID.fromString("d6938711-6efc-4057-97e5-86aba8f6ed2b"), -1);
        pushWallet(wallet);
        logTransaction(new Transaction(masterWallet, wallet, amount));
    }

    public static void logTransaction(final Transaction transaction) {
        Bukkit.getScheduler().runTaskAsynchronously(OresomeCoin.getInstance(), new Runnable() {
            public void run() {
                if (transaction.getAmount() > 1 && !transaction.getFrom().getUserId().toString().equals("d6938711-6efc-4057-97e5-86aba8f6ed2b")) {
                    OresomeCoin.getInstance().getLogger().info(Bukkit.getPlayer(transaction.getFrom().getUserId()).getDisplayName() + " just paid " + Bukkit.getPlayer(transaction.getTo().getUserId()).getName() + " " + transaction.getAmount() + " OresomeCoins!");
                } else if (!transaction.getFrom().getUserId().toString().equals("d6938711-6efc-4057-97e5-86aba8f6ed2b")) {
                    OresomeCoin.getInstance().getLogger().info(Bukkit.getPlayer(transaction.getFrom().getUserId()).getDisplayName() + " just paid " + Bukkit.getPlayer(transaction.getTo().getUserId()).getName() + " " + transaction.getAmount() + " OresomeCoin!");
                }

                MySQL mysql = new MySQL(OresomeCoin.getInstance().getLogger(), "[OresomeCoin]", SQLManager.mysql_host,
                        SQLManager.mysql_port, SQLManager.mysql_db, SQLManager.mysql_user, SQLManager.mysql_password);
                mysql.open();
                mysql.query("INSERT INTO transactions ( fromId, toId, amount, time ) VALUES ( '" + transaction.getFrom().getUserId() + "', '" + transaction.getFrom().getUserId() + "', " + transaction.getAmount() + ", '" + transaction.getTime() + "' );");
                mysql.close();
            }
        });
    }

    /*public static void getBalance(final UUID userId) {
        Bukkit.getScheduler().runTaskAsynchronously(OresomeCoin.getInstance(), new Runnable() {
            public void run() {
                try {
                    MySQL mysql = new MySQL(OresomeCoin.getInstance().getLogger(), "[OresomeCoin]", SQLManager.mysql_host,
                            SQLManager.mysql_port, SQLManager.mysql_db, SQLManager.mysql_user, SQLManager.mysql_password);
                    mysql.open();
                    ResultSet resultSet = mysql.query("SELECT * FROM wallets WHERE uuid = '" + userId.toString() + "';");
                    if (resultSet.isBeforeFirst()) {
                        resultSet.next();
                    }
                    final int balance = resultSet.getInt("balance");
                    mysql.close();
                    OresomeCoin.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(OresomeCoin.getInstance(), new Runnable() {
                        public void run() {
                            OresomeCoin.balances.put(userId.toString(), Integer.toString(balance));
                        }
                    },20L);
                } catch (SQLException ex) {
                    OresomeCoin.getInstance().getLogger().warning("An SQL error occured while attempting to get a UUID's wallet!");
                    OresomeCoin.getInstance().getLogger().warning("UUID = " + userId.toString());
                }
            }
        });
    }*/

}
