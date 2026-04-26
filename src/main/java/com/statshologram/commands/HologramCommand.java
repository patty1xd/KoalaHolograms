package com.statshologram.commands;

import com.statshologram.StatsHologramsPlugin;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class HologramCommand implements CommandExecutor, TabCompleter {
    
    private final StatsHologramsPlugin plugin;
    
    public HologramCommand(StatsHologramsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("statsholograms.admin")) {
            player.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }
        
        if (args.length == 0) {
            sendHelpMessage(player);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "deaths":
                createDeathsHologram(player);
                break;
            case "kills":
                createKillsHologram(player);
                break;
            case "kd":
                createKDHologram(player);
                break;
            case "killstreak":
                createKillstreakHologram(player);
                break;
            case "remove":
                removeHologram(player, args);
                break;
            case "list":
                listHolograms(player);
                break;
            case "removeall":
                removeAllHolograms(player);
                break;
            case "reload":
                reloadConfig(player);
                break;
            default:
                sendHelpMessage(player);
                break;
        }
        
        return true;
    }
    
    private void createDeathsHologram(Player player) {
        Location location = player.getLocation();
        String id = "deaths_" + System.currentTimeMillis();
        plugin.getHologramManager().createHologram(id, "deaths", location, player);
        player.sendMessage("§5§l◆ §dDeaths hologram created at your location!");
        player.sendMessage("§7ID: " + id);
    }
    
    private void createKillsHologram(Player player) {
        Location location = player.getLocation();
        String id = "kills_" + System.currentTimeMillis();
        plugin.getHologramManager().createHologram(id, "kills", location, player);
        player.sendMessage("§5§l◆ §dKills hologram created at your location!");
        player.sendMessage("§7ID: " + id);
    }
    
    private void createKDHologram(Player player) {
        Location location = player.getLocation();
        String id = "kd_" + System.currentTimeMillis();
        plugin.getHologramManager().createHologram(id, "kd", location, player);
        player.sendMessage("§5§l◆ §dK/D Ratio hologram created at your location!");
        player.sendMessage("§7ID: " + id);
    }
    
    private void createKillstreakHologram(Player player) {
        Location location = player.getLocation();
        String id = "killstreak_" + System.currentTimeMillis();
        plugin.getHologramManager().createHologram(id, "killstreak", location, player);
        player.sendMessage("§5§l◆ §dKillstreak hologram created at your location!");
        player.sendMessage("§7ID: " + id);
    }
    
    private void removeHologram(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cUsage: /hologram remove <id>");
            player.sendMessage("§7Use /hologram list to see all hologram IDs");
            return;
        }
        
        String id = args[1];
        if (plugin.getHologramManager().removeHologram(id)) {
            player.sendMessage("§5§l◆ §dHologram removed successfully!");
        } else {
            player.sendMessage("§cHologram with ID '" + id + "' not found!");
        }
    }
    
    private void listHolograms(Player player) {
        Set<String> hologramIds = plugin.getHologramManager().getHologramIds();
        
        if (hologramIds.isEmpty()) {
            player.sendMessage("§5§l◆ §dNo holograms found!");
            return;
        }
        
        player.sendMessage("§5§l◆ §d Active Holograms:");
        player.sendMessage("§5§m━━━━━━━━━━━━━━━━━━━━");
        for (String id : hologramIds) {
            String type = getHologramTypeDisplay(id);
            player.sendMessage("§d• §f" + id + " §7(" + type + "§7)");
        }
        player.sendMessage("§5§m━━━━━━━━━━━━━━━━━━━━");
        player.sendMessage("§7Total: " + hologramIds.size() + " hologram(s)");
    }
    
    private void removeAllHolograms(Player player) {
        Set<String> hologramIds = plugin.getHologramManager().getHologramIds();
        int count = hologramIds.size();
        
        if (count == 0) {
            player.sendMessage("§cNo holograms to remove!");
            return;
        }
        
        for (String id : new ArrayList<>(hologramIds)) {
            plugin.getHologramManager().removeHologram(id);
        }
        
        player.sendMessage("§5§l◆ §dRemoved " + count + " hologram(s)!");
    }
    
    private void reloadConfig(Player player) {
        plugin.reloadConfig();
        player.sendMessage("§5§l◆ §dConfiguration reloaded!");
    }
    
    private String getHologramTypeDisplay(String id) {
        if (id.startsWith("deaths_")) return "§cDeaths";
        if (id.startsWith("kills_")) return "§aKills";
        if (id.startsWith("kd_")) return "§bK/D Ratio";
        if (id.startsWith("killstreak_")) return "§eKillstreak";
        return "§7Unknown";
    }
    
    private void sendHelpMessage(Player player) {
        player.sendMessage("§5§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        player.sendMessage("§5§l  StatsHolograms Commands");
        player.sendMessage("§5§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        player.sendMessage("§d/hologram deaths §7- Create a deaths hologram");
        player.sendMessage("§d/hologram kills §7- Create a kills hologram");
        player.sendMessage("§d/hologram kd §7- Create a K/D ratio hologram");
        player.sendMessage("§d/hologram killstreak §7- Create a killstreak hologram");
        player.sendMessage("§d/hologram remove <id> §7- Remove a hologram");
        player.sendMessage("§d/hologram list §7- List all holograms");
        player.sendMessage("§d/hologram removeall §7- Remove all holograms");
        player.sendMessage("§d/hologram reload §7- Reload configuration");
        player.sendMessage("§5§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            List<String> subCommands = Arrays.asList("deaths", "kills", "kd", "killstreak", "remove", "list", "removeall", "reload");
            return subCommands.stream()
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        
        if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
            return new ArrayList<>(plugin.getHologramManager().getHologramIds()).stream()
                    .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }
        
        return completions;
    }
}
