package com.xcue.xcuelib;

import com.xcue.xcuelib.configuration.Config;
import com.xcue.xcuelib.inventory.InventoryHandler;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class XQPlugin extends JavaPlugin {
    protected final Config config;

    public XQPlugin() {
        config = new Config();
    }

    @Override
    public final void onEnable() {
        if (isUsingInventories()) {
            Bukkit.getPluginManager().registerEvents(new InventoryHandler(), this);
        }

        enable();
    }

    @Override
    public final void onDisable() {
        disable();
    }

    public final void onReload() {
        config.reload();

        reload();
    }

    public abstract void enable();

    public abstract void disable();

    public abstract void reload();

    protected abstract boolean isUsingInventories();

    protected void register(Listener handler) {
        Bukkit.getPluginManager().registerEvents(handler, this);
    }

    public Config getConfigs() {
        return this.config;
    }
}
