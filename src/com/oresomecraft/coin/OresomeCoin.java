package com.oresomecraft.coin;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class OresomeCoin extends JavaPlugin {

    private static OresomeCoin instance;
    public int oresomeBankID = -1;
    public static Map<String, Wallet> onlineWallets = new HashMap<String, Wallet>();

    public void onEnable() {
        instance = this;
        SQLManager.setupDatabase();
    }

    public void onDisable() {
        saveConfig();
        for (Wallet wallet : onlineWallets.values()) {
            wallet.writeToDatabase();
        }
    }

    public static OresomeCoin getInstance() {
        return instance;
    }
}
