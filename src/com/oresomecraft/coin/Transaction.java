package com.oresomecraft.coin;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Transaction {

    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    Date date = new Date();
    private Wallet from, to;
    private int amount;
    private long timeInMills;
    private String time;

    public Transaction(Wallet from, Wallet to, int amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.timeInMills = System.currentTimeMillis();
        this.time = dateFormat.format(date);
    }

    /**
     * Gets the transaction initiator's wallet
     *
     * @return The wallet owned by the transaction initiator
     */
    public Wallet getFrom() {
        return this.from;
    }

    /**
     * Gets the transaction target's wallet
     *
     * @return The wallet owned by the player to receive the transaction
     */
    public Wallet getTo() {
        return this.to;
    }

    /**
     * Gets the transaction amount
     *
     * @return The amount to be transferred in the transaction
     */
    public int getAmount() {
        return this.amount;
    }

    /**
     * Gets the time at which the transaction was made
     *
     * @return The time at which the transaction was made
     */
    public String getTime() {
        return this.time;
    }

    /**
     * Gets the time at which the transaction was made in milliseconds
     *
     * @return The amount of milliseconds the system was at when the transaction was made
     */
    public long getTimeInMills() {
        return this.timeInMills;
    }

}
