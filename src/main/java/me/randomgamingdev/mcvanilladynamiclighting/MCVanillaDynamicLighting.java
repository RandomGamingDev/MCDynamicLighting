package me.randomgamingdev.mcvanilladynamiclighting;

import org.bukkit.plugin.java.JavaPlugin;

public final class MCVanillaDynamicLighting extends JavaPlugin {
    LightCalc lightCalc;

    @Override
    public void onEnable() {
        System.out.println("MCVanillaDynamicLighting is starting up!");
        lightCalc = new LightCalc(this);
        lightCalc.runTaskTimer(this, 0, 1);
    }

    @Override
    public void onDisable() {
        System.out.println("MCVanillaDynamicLighting is shutting down!");
        lightCalc.ReplaceLightSources();
    }
}