package com.statshologram.models;

import java.util.UUID;

public class PlayerStats {
    
    private final UUID playerUUID;
    private String playerName;
    private int deaths;
    private int kills;
    private int currentKillstreak;
    private int highestKillstreak;
    
    public PlayerStats(UUID playerUUID, String playerName) {
        this.playerUUID = playerUUID;
        this.playerName = playerName;
        this.deaths = 0;
        this.kills = 0;
        this.currentKillstreak = 0;
        this.highestKillstreak = 0;
    }
    
    public UUID getPlayerUUID() {
        return playerUUID;
    }
    
    public String getPlayerName() {
        return playerName;
    }
    
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
    
    public int getDeaths() {
        return deaths;
    }
    
    public void addDeath() {
        this.deaths++;
        this.currentKillstreak = 0;
    }
    
    public int getKills() {
        return kills;
    }
    
    public void addKill() {
        this.kills++;
        this.currentKillstreak++;
        if (this.currentKillstreak > this.highestKillstreak) {
            this.highestKillstreak = this.currentKillstreak;
        }
    }
    
    public double getKDRatio() {
        if (deaths == 0) {
            return kills;
        }
        return (double) kills / deaths;
    }
    
    public int getCurrentKillstreak() {
        return currentKillstreak;
    }
    
    public int getHighestKillstreak() {
        return highestKillstreak;
    }
    
    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }
    
    public void setKills(int kills) {
        this.kills = kills;
    }
    
    public void setCurrentKillstreak(int currentKillstreak) {
        this.currentKillstreak = currentKillstreak;
    }
    
    public void setHighestKillstreak(int highestKillstreak) {
        this.highestKillstreak = highestKillstreak;
    }
}
