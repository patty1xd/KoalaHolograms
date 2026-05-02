package com.statshologram.holograms;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class Hologram {
    
    private final String id;
    private final Location location;
    private final List<ArmorStand> lines;
    
    public Hologram(String id, Location location) {
        this.id = id;
        this.location = location.clone();
        this.lines = new ArrayList<>();
    }
    
    public String getId() {
        return id;
    }
    
    public Location getLocation() {
        return location.clone();
    }
    
    public void addLine(String text, double offset) {
        Location lineLocation = location.clone().add(0, offset, 0);
        ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(lineLocation, EntityType.ARMOR_STAND);
        
        // Core settings
        armorStand.setVisible(false);
        armorStand.setGravity(false);
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);
        armorStand.setSmall(true);
        armorStand.setBasePlate(false);
        armorStand.setArms(false);
        
        // Custom name settings
        armorStand.setCustomNameVisible(true);
        armorStand.setCustomName(text);
        
        // Additional settings
        armorStand.setCanPickupItems(false);
        armorStand.setPersistent(true);
        armorStand.setRemoveWhenFarAway(false);
        armorStand.setSilent(true);
        
        // Disable all interactions
        armorStand.setCollidable(false);
        
        lines.add(armorStand);
    }
    
    public void updateLine(int index, String text) {
        if (index >= 0 && index < lines.size()) {
            lines.get(index).setCustomName(text);
        }
    }
    
    public void clearLines() {
        for (ArmorStand armorStand : lines) {
            armorStand.remove();
        }
        lines.clear();
    }
    
    public void remove() {
        clearLines();
    }
    
    public int getLineCount() {
        return lines.size();
    }
}
