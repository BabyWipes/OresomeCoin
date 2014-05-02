package com.oresomecraft.coin;

public class Wallet {

    private int walletId;
    private double balance;
    private String owner;

    public Wallet(int walletId, int balance, String owner) {
        this.walletId = walletId;
        this.balance = balance;
        this.owner = owner;
    }

    /**
     * Gets the row identification number of the wallet
     *
     * @return The row identification number of the wallet
     */
    public int getWalletId() {
        return this.walletId;
    }

    /**
     * Gets the amount of OresomeCoin contained in the wallet
     *
     * @return The amount of OresomeCoin the wallet contains
     */
    public double getBalance() {
        return this.balance;
    }

    /**
     * Sets the wallet balance
     *
     * @param amount The amount of OrecomeCoin contained in the wallet
     */
    protected void setBalance(double amount) {
        this.balance = amount;
    }

    /**
     * Gets the owner of the wallet
     *
     * @return The wallet owner's name
     */
    public String getOwner() {
        return this.owner;
    }

    /**
     * Adds a certain amount of OresomeCoin to the wallet
     *
     * @param amount The amount of OresomeCoin to be added to the wallet
     */
    protected void depositCoins(double amount) {
        this.balance = this.balance + amount;
    }

    /**
     * Removes a certain amount of OresomeCoin from the wallet
     *
     * @param amount The amount of OresomeCoin to be removed from the wallet
     */
    protected void withdrawCoins(double amount) {
        this.balance = this.balance - amount;
    }

    /**
     * Synchronises the wallet with the MySQL database
     */
    synchronized void writeToDatabase() {
        TransactionOperations.pushWallet(this);
    }

}