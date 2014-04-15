package com.oresomecraft.coin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

    public int getAmount() {
        return this.amount;
    }

    public String getTime() {
        return this.time;
    }

    public long getTimeInMills() {
        return this.timeInMills;
    }

}
