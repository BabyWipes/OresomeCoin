package com.oresomecraft.coin.battles;

import com.oresomecraft.OresomeBattles.api.events.BattleEndEvent;
import com.oresomecraft.OresomeBattles.api.events.EndBattleEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class WinGame implements Listener {

    @EventHandler
    public void onWinGame(BattleEndEvent event) {
        //TODO: Handle games won
    }
}
