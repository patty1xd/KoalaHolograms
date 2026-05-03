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
    }
    
    public void clearLeftoverArmorStands() {
        for (org.bukkit.World world : plugin.getServer().getWorlds()) {
            world.getEntities().forEach(entity -> {
                if (entity instanceof org.bukkit.entity.ArmorStand) {
                    org.bukkit.entity.ArmorStand stand = (org.bukkit.entity.ArmorStand) entity;
                    // Remove if it has our tag OR if it's an invisible marker with custom name (old ones)
                    if (stand.hasMetadata("KoalaHologram") || 
                        (!stand.isVisible() && stand.isMarker() && stand.isCustomNameVisible())) {
                        entity.remove();
                    }
                }
            });
        }
    }
    
    public void loadHologramsDelayed() {
        if (!hologramsConfig.contains("holograms")) return;
        
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
                Hologram hologram = new Hologram(id, location);
                holograms.put(id, hologram);
                updateHologram(id, type, null);
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
        if (holograms.containsKey(id)) {
            holograms.get(id).remove();
            holograms.remove(id);
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
        
        String title = config.getString("titles." + type, "§d§l⚔ TOP 10 ⚔");
        hologram.addLine(title, currentOffset);
        currentOffset -= lineSpacing;
        
        String separator = config.getString("formatting.header-separator", "§d§m━━━━━━━━━━━━━━━━━━━━━━━━━");
        hologram.addLine(separator, currentOffset);
        currentOffset -= lineSpacing;
        
        hologram.addLine("§7", currentOffset);
        currentOffset -= lineSpacing * 0.7;
        
        List<PlayerStats> topPlayers = getTopPlayers(type, config.getInt("top-count", 10));
        String playerPrefix = config.getString("formatting.player-prefix", "§d#§6{position} §7» §e");
        String playerColor = config.getString("theme.player-color", "§e");
        String statColor = config.getString("theme.stat-color", "§6");
        
        for (int i = 0; i < topPlayers.size(); i++) {
            PlayerStats stats = topPlayers.get(i);
            String prefix = playerPrefix.replace("{position}", String.valueOf(i + 1));
            String line = prefix + playerColor + stats.getPlayerName() + " §8- " + statColor + getStatValue(stats, type);
            hologram.addLine(line, currentOffset);
            currentOffset -= lineSpacing;
        }
        
        hologram.addLine("§7", currentOffset);
        currentOffset -= lineSpacing * 0.7;
        
        hologram.addLine(separator, currentOffset);
        currentOffset -= lineSpacing;
        
        if (targetPlayer != null) {
            PlayerStats playerStats = plugin.getStatsManager().getPlayerStats(targetPlayer);
            String yourStatsPrefix = config.getString("formatting.your-stats-prefix", "§d§l» §fYour Stats§7: §6");
            String yourLine = yourStatsPrefix + getStatValue(playerStats, type);
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
        
        hologramsConfig.set("holograms", null);
        try {
            hologramsConfig.save(hologramsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public Set<String> getHologramIds() {
        return new HashSet<>(holograms.keySet());
    }
    
    public boolean hologramExists(String id) {
        return holograms.containsKey(id);
    }
}
