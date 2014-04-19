package com.oresomecraft.coin;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Transaction {

    private Wallet from, to;
    private int amount;
    private long timeInMills;
    private String time;

    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    Date date = new Date();

    public Transaction(Wallet from, Wallet to, int amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.timeInMills = System.currentTimeMillis();
        this.time = dateFormat.format(date);
    }

    public Wallet getFrom() {
        return this.from;
    }

    public Wallet getTo() {
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
