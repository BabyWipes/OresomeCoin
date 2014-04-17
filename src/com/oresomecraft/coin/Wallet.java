package com.oresomecraft.coin;

import org.bukkit.Bukkit;

import java.util.UUID;

public class Wallet {

    public Wallet(UUID userId, int balance) {
        this.userId = userId;
        this.balance = balance;
        this.owner = Bukkit.getPlayer(userId).getName();
    }

    private UUID userId;
    private double balance;
    private String owner;

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
