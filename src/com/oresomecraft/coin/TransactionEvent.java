package com.oresomecraft.coin;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TransactionEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private Wallet fromWallet;
    private Wallet toWallet;
    private int amount;

    private boolean cancelled;

    public TransactionEvent(Wallet fromWallet, Wallet toWallet, int amount) {
        this.fromWallet = fromWallet;
        this.toWallet = toWallet;
        this.amount = amount;
    }

    public Wallet getFromWallet() {
        return this.fromWallet;
    }

    public Wallet getToWallet() {
        return this.toWallet;
    }

    public int getAmount() {
        return this.amount;
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

    public static HandlerList getHandlerList() {
        return handlers;
    }

}