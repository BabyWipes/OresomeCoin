package com.oresomecraft.coin.playerinterfaces;

import com.oresomecraft.coin.Wallet;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TransactionEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private Transaction transaction;

    private boolean cancelled;

    public TransactionEvent(Transaction transaction) {
        this.transaction = transaction;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Gets the Transaction object that was created when executing the transaction
     *
     * @return A Transaction object
     */
    public Transaction getTransaction() {
        return this.transaction;
    }

    /**
     * Gets the wallet from which the transaction was initiated
     *
     * @return The wallet owned by the transaction initiator
     */
    public Wallet getFromWallet() {
        return this.transaction.getFrom();
    }

    /**
     * Gets the transaction's target wallet
     *
     * @return The wallet owned by the player to receive the transaction
     */
    public Wallet getToWallet() {
        return this.transaction.getTo();
    }

    /**
     * Gets the transaction amount
     *
     * @return The amount to be transferred in the transaction
     */
    public int getAmount() {
        return this.transaction.getAmount();
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}