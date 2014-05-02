package com.oresomecraft.coin;

public class CoinAPI {

    /**
     * Gives a wallet a certain amount of coins
     *
     * @param wallet The wallet to give coins to
     * @param amount The amount of coins to give to a wallet
     */
    public static void giveCoins(Wallet wallet, int amount) {
        SQLOperations.giveCoins(wallet, amount);
    }

    /**
     * Executes a Transaction object.
     * The amount of coinage defined in the Transaction object will be withdrawn from the wallet that is making the transaction.
     * The amount of coinage will then be deposited in the destination wallet. The transaction is then logged to a database.
     *
     * @param transaction The Transaction object to execute
     */
    public static void executeTransaction(Transaction transaction) {
        SQLOperations.executeTransaction(transaction);
    }

    /**
     * Pushes a wallet's contents and information to the database
     *
     * @param wallet The Wallet to synchronise
     */
    public static void pushWallet(Wallet wallet) {
        SQLOperations.pushWallet(wallet);
    }

    /**
     * Logs a Transaction object to the database
     *
     * @param transaction The Transaction to log
     */
    public static void logTransaction(Transaction transaction) {
        SQLOperations.logTransaction(transaction);
    }
}
