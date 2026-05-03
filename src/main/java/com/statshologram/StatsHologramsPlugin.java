package com.statshologram;

import com.statshologram.commands.HologramCommand;
import com.statshologram.data.PlayerStatsManager;
import com.statshologram.holograms.HologramManager;
import com.statshologram.listeners.PlayerStatsListener;
import org.bukkit.plugin.java.JavaPlugin;

public class StatsHologramsPlugin extends JavaPlugin {
    
    private static StatsHologramsPlugin instance;
    private PlayerStatsManager statsManager;
    private HologramManager hologramManager;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Save default configuration
        saveDefaultConfig();
        
        // Initialize managers
        statsManager = new PlayerStatsManager(this);
        hologramManager = new HologramManager(this);
        
        // Register commands
        getCommand("hologram").setExecutor(new HologramCommand(this));
        
        // Register listeners
        getServer().getPluginManager().registerEvents(new PlayerStatsListener(this), this);
        
        // Start hologram update task
        int updateInterval = getConfig().getInt("update-interval", 100);
        getServer().getScheduler().runTaskTimer(this, () -> {
            hologramManager.updateAllHolograms();
        }, 20L, updateInterval);
        
        getLogger().info("StatsHolograms has been enabled!");
    }
    
   @Override
public void onDisable() {
    // DON'T remove holograms - let them persist
    // if (hologramManager != null) {
    //     hologramManager.removeAllHolograms();
    // }
    
    // Save stats data
    if (statsManager != null) {
        statsManager.saveData();
    }
    
    getLogger().info("KoalaHolograms has been disabled!");
}
    
    public static StatsHologramsPlugin getInstance() {
        return instance;
    }
    
    public PlayerStatsManager getStatsManager() {
        return statsManager;
    }
    
    public HologramManager getHologramManager() {
        return hologramManager;
    }
}
