package com.oresomecraft.coin;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Transaction {
    private int from, to;
    private double amount;
    private long timeInMills;
    private String time;

    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    Date date = new Date();

    public Transaction(int from, int to, double amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.timeInMills = System.currentTimeMillis();
        this.time = dateFormat.format(date);
    }

    public int getFrom() {
        return this.from;
    }

    public int getTo() {
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

    public void execute() {
        // TODO: execute transaction
        log();
    }

    private void log() {
        // TODO: Log transaction to database
    }

}
