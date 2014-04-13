package com.oresomecraft.coin;

import org.bukkit.plugin.java.JavaPlugin;

public class OresomeCoin extends JavaPlugin {

    private static OresomeCoin instance;
    public int oresomeBankID = -1;

    public void onEnable() {
        instance = this;
        SQLManager.setupDatabase();
    }

    public void onDisable() {

    }

    public static OresomeCoin getInstance() {
        return instance;
    }
}
