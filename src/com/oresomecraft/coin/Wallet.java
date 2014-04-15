package com.oresomecraft.coin;

import java.util.UUID;

public class Wallet {

    public Wallet(UUID userId, int balance) {
        this.userId = userId;
        this.balance = balance;
    }

    private UUID userId;
    private String owner;
    private double balance;

    public UUID getUserId() {
        return this.userId;
    }

    public String getOwner() {
        return this.owner;
    }

    public double getBalance() {
        return this.balance;
    }

    protected void setBalance(double amount) {
        this.balance = amount;
    }

    protected void depositCoins(double amount) {
        this.balance = this.balance + amount;
    }

    protected void withdrawCoins(double amount) {
        this.balance = this.balance - amount;
    }

    synchronized void writeToDatabase() {
        SQLManager.pushWallet(this);
    }

}
