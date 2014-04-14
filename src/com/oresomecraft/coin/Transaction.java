package com.oresomecraft.coin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Transaction {

    private Player from, to;
    private int amount;
    private long timeInMills;
    private String time;

    static OresomeCoin plugin = OresomeCoin.getInstance();

    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    Date date = new Date();

    public Transaction(Player from, Player to, int amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.timeInMills = System.currentTimeMillis();
        this.time = dateFormat.format(date);
    }

    public Player getFrom() {
        return this.from;
    }

    public Player getTo() {
        return this.to;
    }

    public double getAmount() {
        return this.amount;
    }

    public String getTime() {
        return this.time;
    }

    public long getTimeInMills() {
        return this.timeInMills;
    }

    public String execute() {
        if (SQLManager.getWallet(this.getFrom().getUniqueId()) != null && SQLManager.getWallet(this.getTo().getUniqueId()) != null) {
            Wallet fromWallet = SQLManager.getWallet(this.getFrom().getUniqueId());
            Wallet toWallet = SQLManager.getWallet(this.getTo().getUniqueId());

            if (fromWallet.getBalance() >= this.getAmount()) {
                fromWallet.withdrawCoins(this.getAmount());
                toWallet.depositCoins(this.getAmount());
                Bukkit.getPluginManager().callEvent(new PlayerTransactionEvent(this.from, this.to, this.amount));
                log();
                if (this.amount > 1) {
                    return "You paid " + this.getTo().getDisplayName() + " " + this.amount + " OresomeCoins!";
                } else {
                    return "You paid " + this.getTo().getDisplayName() + " " + this.amount + " OresomeCoin!";
                }
            } else {
                return "You don't have enough OresomeCoin to carry out this transaction!";
            }
        } else {
            return "The player you're attempting to pay doesn't seem to be online!";
        }
    }

    private void log() {
        if (this.getAmount() > 1) {
            plugin.getLogger().info(this.getFrom().getName() + " just paid " + this.getTo().getName() + " " + this.getAmount() + " OresomeCoins!");
        } else {
            plugin.getLogger().info(this.getFrom().getName() + " just paid " + this.getTo().getName() + " " + this.getAmount() + " OresomeCoin!");
        }
        try {
            FileWriter writer = new FileWriter("transactions.log");
            writer.append("[" + this.getTime() + "] Transaction from " + this.getFrom().getName() + " to " + this.getTo().getName() + " of " + this.getAmount() + " OresomeCoins. [" + this.getFrom().getUniqueId() + "] [" + this.getTo().getUniqueId() + "] \n");
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            plugin.getLogger().warning("An IOException occured while attempting to log a transaction!");
        }
    }

}
