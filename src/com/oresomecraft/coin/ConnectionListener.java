package com.oresomecraft.coin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class ConnectionListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID userId = event.getPlayer().getUniqueId();
        if (!SQLManager.hasWallet(userId)) {
            SQLManager.generateWallet(userId);
        } else {
            OresomeCoin.onlineWallets.put(userId.toString(), SQLManager.getWallet(userId));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID userId = event.getPlayer().getUniqueId();
        SQLManager.getWallet(userId).writeToDatabase();
        OresomeCoin.onlineWallets.remove(userId.toString());
    }
}
