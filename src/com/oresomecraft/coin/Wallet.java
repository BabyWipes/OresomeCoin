package com.oresomecraft.coin;

public class Wallet {

    public Wallet(int walletId, int balance, String owner) {
        this.walletId = walletId;
        this.balance = balance;
        this.owner = owner;
    }

    private int walletId;
    private double balance;
    private String owner;

    public int getWalletId() {
        return this.walletId;
    }

    public double getBalance() {
        return this.balance;
    }

    public String getOwner() {
        return this.owner;
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
        SQLOperations.pushWallet(this);
    }

}
