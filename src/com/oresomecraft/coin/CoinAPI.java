package com.oresomecraft.coin;

public class CoinAPI {

    /**
     * Gives a wallet a certain amount of coins
     *
     * @param wallet The wallet to give coins to
     * @param amount The amount of coins to give to a wallet
     */
    public static void giveCoins(Wallet wallet, int amount) {
        TransactionOperations.giveCoins(wallet, amount);
    }

    /**
     * Removes a certain amount of coins from a wallet
     *
     * @param wallet The wallet to remove coins from
     * @param amount The amount of coins to remove
     */
    public static void removeCoins(Wallet wallet, int amount) {
        TransactionOperations.removeCoins(wallet, amount);
    }

    /**
     * Executes a Transaction object.
     * The amount of coinage defined in the Transaction object will be withdrawn from the wallet that is making the transaction.
     * The amount of coinage will then be deposited in the destination wallet. The transaction is then logged to a database.
     *
     * @param transaction The Transaction object to execute
     */
    public static void executeTransaction(Transaction transaction) {
        TransactionOperations.executeTransaction(transaction);
    }

    /**
     * Pushes a wallet's contents and information to the database
     *
     * @param wallet The Wallet to synchronise
     */
    public static void pushWallet(Wallet wallet) {
        TransactionOperations.pushWallet(wallet);
    }

    /**
     * Logs a Transaction object to the database
     *
     * @param transaction The Transaction to log
     */
    public static void logTransaction(Transaction transaction) {
        TransactionOperations.logTransaction(transaction);
    }
}
