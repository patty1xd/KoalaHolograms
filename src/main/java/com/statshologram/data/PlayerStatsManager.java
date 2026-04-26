package com.statshologram.data;

import com.statshologram.StatsHologramsPlugin;
import com.statshologram.models.PlayerStats;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class PlayerStatsManager {
    
    private final StatsHologramsPlugin plugin;
    private final Map<UUID, PlayerStats> playerStats;
    private File dataFile;
    private FileConfiguration dataConfig;
    
    public PlayerStatsManager(StatsHologramsPlugin plugin) {
        this.plugin = plugin;
        this.playerStats = new HashMap<>();
        loadData();
    }
    
    private void loadData() {
        dataFile = new File(plugin.getDataFolder(), "playerdata.yml");
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create playerdata.yml!");
                e.printStackTrace();
            }
        }
        
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        
        // Load all player stats
        if (dataConfig.contains("players")) {
            for (String uuidString : dataConfig.getConfigurationSection("players").getKeys(false)) {
                UUID uuid = UUID.fromString(uuidString);
                String path = "players." + uuidString;
                
                String name = dataConfig.getString(path + ".name", "Unknown");
                int deaths = dataConfig.getInt(path + ".deaths", 0);
                int kills = dataConfig.getInt(path + ".kills", 0);
                int currentKillstreak = dataConfig.getInt(path + ".current-killstreak", 0);
                int highestKillstreak = dataConfig.getInt(path + ".highest-killstreak", 0);
                
                PlayerStats stats = new PlayerStats(uuid, name);
                stats.setDeaths(deaths);
                stats.setKills(kills);
                stats.setCurrentKillstreak(currentKillstreak);
                stats.setHighestKillstreak(highestKillstreak);
                
                playerStats.put(uuid, stats);
            }
        }
        
        plugin.getLogger().info("Loaded stats for " + playerStats.size() + " players");
    }
    
    public void saveData() {
        for (PlayerStats stats : playerStats.values()) {
            String path = "players." + stats.getPlayerUUID().toString();
            dataConfig.set(path + ".name", stats.getPlayerName());
            dataConfig.set(path + ".deaths", stats.getDeaths());
            dataConfig.set(path + ".kills", stats.getKills());
            dataConfig.set(path + ".current-killstreak", stats.getCurrentKillstreak());
            dataConfig.set(path + ".highest-killstreak", stats.getHighestKillstreak());
        }
        
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save playerdata.yml!");
            e.printStackTrace();
        }
    }
    
    public PlayerStats getPlayerStats(Player player) {
        return getPlayerStats(player.getUniqueId(), player.getName());
    }
    
    public PlayerStats getPlayerStats(UUID uuid, String name) {
        PlayerStats stats = playerStats.get(uuid);
        if (stats == null) {
            stats = new PlayerStats(uuid, name);
            playerStats.put(uuid, stats);
        } else {
            stats.setPlayerName(name); // Update name in case it changed
        }
        return stats;
    }
    
    public List<PlayerStats> getTopDeaths(int count) {
        return playerStats.values().stream()
                .sorted(Comparator.comparingInt(PlayerStats::getDeaths).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
    
    public List<PlayerStats> getTopKills(int count) {
        return playerStats.values().stream()
                .sorted(Comparator.comparingInt(PlayerStats::getKills).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
    
    public List<PlayerStats> getTopKD(int count) {
        return playerStats.values().stream()
                .filter(stats -> stats.getDeaths() > 0 || stats.getKills() > 0)
                .sorted(Comparator.comparingDouble(PlayerStats::getKDRatio).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
    
    public List<PlayerStats> getTopKillstreaks(int count) {
        return playerStats.values().stream()
                .sorted(Comparator.comparingInt(PlayerStats::getHighestKillstreak).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}
