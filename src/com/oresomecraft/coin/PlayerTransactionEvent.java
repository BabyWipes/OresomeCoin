package com.oresomecraft.coin;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerTransactionEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private Player target;
    private int amount;

    private boolean cancelled;

    public PlayerTransactionEvent(Player player, Player target, int amount) {
        super(player);
        this.target = target;
        this.amount = amount;
    }

    public Player getTarget() {
        return this.target;
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