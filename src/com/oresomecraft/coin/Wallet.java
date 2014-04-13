package com.oresomecraft.coin;

public class Wallet {

    private Wallet(int ID) {
        this.ID = ID;
        this.fetch();
    }

    private int ID;
    private String owner;
    private double balance;

    public int getID() {
        return this.ID;
    }

    public String getOwner() {
        return this.owner;
    }

    public double getBalance() {
        return this.balance;
    }

    protected void depositCoins(double amount) {
        this.balance = this.balance + amount;
    }

    protected void withdrawCoins(double amount) {
        this.balance = this.balance - amount;
    }

    private synchronized void fetch() {
        // TODO: fetch account info from database, set amounts, etc
    }

    private synchronized void writeToDatabase() {
        // TODO: write/save into to data base
    }

}
