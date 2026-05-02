package com.statshologram.holograms;

import com.statshologram.StatsHologramsPlugin;
import com.statshologram.models.PlayerStats;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

public class HologramManager {
    
    private final StatsHologramsPlugin plugin;
    private final Map<String, Hologram> holograms;
    private final DecimalFormat kdFormat;
    private File hologramsFile;
    private FileConfiguration hologramsConfig;
    
    public HologramManager(StatsHologramsPlugin plugin) {
        this.plugin = plugin;
        this.holograms = new HashMap<>();
        this.kdFormat = new DecimalFormat("#.##");
        loadHolograms();
    }
    
    private void loadHolograms() {
    hologramsFile = new File(plugin.getDataFolder(), "holograms.yml");
    if (!hologramsFile.exists()) {
        try {
            hologramsFile.createNewFile();
        } catch (IOException e) {
            plugin.getLogger().severe("Could not create holograms.yml!");
            e.printStackTrace();
        }
    }
    
    hologramsConfig = YamlConfiguration.loadConfiguration(hologramsFile);
    
    // Load saved holograms
    if (hologramsConfig.contains("holograms")) {
        for (String id : hologramsConfig.getConfigurationSection("holograms").getKeys(false)) {
            String path = "holograms." + id;
            String type = hologramsConfig.getString(path + ".type");
            String worldName = hologramsConfig.getString(path + ".world");
            double x = hologramsConfig.getDouble(path + ".x");
            double y = hologramsConfig.getDouble(path + ".y");
            double z = hologramsConfig.getDouble(path + ".z");
            
            org.bukkit.World world = plugin.getServer().getWorld(worldName);
            if (world != null) {
                Location location = new Location(world, x, y, z);
                // Kill any existing armor stands at this location first
                world.getNearbyEntities(location, 3, 5, 3).forEach(entity -> {
                    if (entity instanceof org.bukkit.entity.ArmorStand) {
                        entity.remove();
                    }
                });
                createHologram(id, type, location, null);
            }
        }
    }
}
    private void saveHolograms() {
        for (Map.Entry<String, Hologram> entry : holograms.entrySet()) {
            String id = entry.getKey();
            Hologram hologram = entry.getValue();
            Location loc = hologram.getLocation();
            
            String path = "holograms." + id;
            hologramsConfig.set(path + ".type", getHologramType(id));
            hologramsConfig.set(path + ".world", loc.getWorld().getName());
            hologramsConfig.set(path + ".x", loc.getX());
            hologramsConfig.set(path + ".y", loc.getY());
            hologramsConfig.set(path + ".z", loc.getZ());
        }
        
        try {
            hologramsConfig.save(hologramsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save holograms.yml!");
            e.printStackTrace();
        }
    }
    
    private String getHologramType(String id) {
        if (id.startsWith("deaths_")) return "deaths";
        if (id.startsWith("kills_")) return "kills";
        if (id.startsWith("kd_")) return "kd";
        if (id.startsWith("killstreak_")) return "killstreak";
        return "unknown";
    }
    
    public void createHologram(String id, String type, Location location, Player targetPlayer) {
        // Remove existing hologram if it exists
        if (holograms.containsKey(id)) {
            holograms.get(id).remove();
        }
        
        Hologram hologram = new Hologram(id, location);
        holograms.put(id, hologram);
        
        updateHologram(id, type, targetPlayer);
        saveHolograms();
    }
    
    private void updateHologram(String id, String type, Player targetPlayer) {
        Hologram hologram = holograms.get(id);
        if (hologram == null) return;
        
        hologram.clearLines();
        
        FileConfiguration config = plugin.getConfig();
        double lineSpacing = config.getDouble("line-spacing", 0.28);
        double currentOffset = 0;
        
        // Add title
        String title = config.getString("titles." + type, "§5§l◆ TOP 10 ◆");
        hologram.addLine(title, currentOffset);
        currentOffset -= lineSpacing;
        
        // Add separator
        String separator = config.getString("formatting.header-separator", "§5§m━━━━━━━━━━━━━━━━━━━━━━━━━");
        hologram.addLine(separator, currentOffset);
        currentOffset -= lineSpacing;
        
        // Add empty line for spacing
        hologram.addLine("§7", currentOffset);
        currentOffset -= lineSpacing * 0.7;
        
        // Add top players
        List<PlayerStats> topPlayers = getTopPlayers(type, config.getInt("top-count", 10));
        String playerPrefix = config.getString("formatting.player-prefix", "§5§l#§d{position} §7» §f");
        String playerColor = config.getString("theme.player-color", "§f");
        String statColor = config.getString("theme.stat-color", "§d");
        
        for (int i = 0; i < topPlayers.size(); i++) {
            PlayerStats stats = topPlayers.get(i);
            String prefix = playerPrefix.replace("{position}", String.valueOf(i + 1));
            String line = prefix + playerColor + stats.getPlayerName() + " §8- " + statColor + getStatValue(stats, type);
            hologram.addLine(line, currentOffset);
            currentOffset -= lineSpacing;
        }
        
        // Add empty line for spacing
        hologram.addLine("§7", currentOffset);
        currentOffset -= lineSpacing * 0.7;
        
        // Add footer separator
        hologram.addLine(separator, currentOffset);
        currentOffset -= lineSpacing;
        
        // Add "Your stats" line if targetPlayer is specified
        if (targetPlayer != null) {
            PlayerStats playerStats = plugin.getStatsManager().getPlayerStats(targetPlayer);
            String yourStatsPrefix = config.getString("formatting.your-stats-prefix", "§d§l» §fYour Stats§7: ");
            String yourLine = yourStatsPrefix + statColor + getStatValue(playerStats, type);
            hologram.addLine(yourLine, currentOffset);
        }
    }
    
    private List<PlayerStats> getTopPlayers(String type, int count) {
        switch (type.toLowerCase()) {
            case "deaths":
                return plugin.getStatsManager().getTopDeaths(count);
            case "kills":
                return plugin.getStatsManager().getTopKills(count);
            case "kd":
                return plugin.getStatsManager().getTopKD(count);
            case "killstreak":
                return plugin.getStatsManager().getTopKillstreaks(count);
            default:
                return new ArrayList<>();
        }
    }
    
    private String getStatValue(PlayerStats stats, String type) {
        switch (type.toLowerCase()) {
            case "deaths":
                return String.valueOf(stats.getDeaths());
            case "kills":
                return String.valueOf(stats.getKills());
            case "kd":
                return kdFormat.format(stats.getKDRatio());
            case "killstreak":
                return String.valueOf(stats.getHighestKillstreak());
            default:
                return "0";
        }
    }
    
    private String getStatName(String type) {
        switch (type.toLowerCase()) {
            case "deaths":
                return "Deaths";
            case "kills":
                return "Kills";
            case "kd":
                return "K/D";
            case "killstreak":
                return "Killstreak";
            default:
                return "Stats";
        }
    }
    
    public void updateAllHolograms() {
        for (String id : holograms.keySet()) {
            String type = getHologramType(id);
            updateHologram(id, type, null);
        }
    }
    
    public boolean removeHologram(String id) {
        Hologram hologram = holograms.remove(id);
        if (hologram != null) {
            hologram.remove();
            hologramsConfig.set("holograms." + id, null);
            saveHolograms();
            return true;
        }
        return false;
    }
    
    public void removeAllHolograms() {
        for (Hologram hologram : holograms.values()) {
            hologram.remove();
        }
        holograms.clear();
    }
    
    public Set<String> getHologramIds() {
        return new HashSet<>(holograms.keySet());
    }
    
    public boolean hologramExists(String id) {
        return holograms.containsKey(id);
    }
}
