package com.statshologram.listeners;

import com.statshologram.StatsHologramsPlugin;
import com.statshologram.models.PlayerStats;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerStatsListener implements Listener {
    
    private final StatsHologramsPlugin plugin;
    
    public PlayerStatsListener(StatsHologramsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // Initialize player stats if they don't exist
        plugin.getStatsManager().getPlayerStats(player);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Auto-save on quit
        plugin.getStatsManager().saveData();
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();
        
        // Record death
        PlayerStats victimStats = plugin.getStatsManager().getPlayerStats(victim);
        victimStats.addDeath();
        
        // Record kill if killed by another player
        if (killer != null && killer != victim) {
            PlayerStats killerStats = plugin.getStatsManager().getPlayerStats(killer);
            killerStats.addKill();
        }
        
        // Save stats
        plugin.getStatsManager().saveData();
    }
}
