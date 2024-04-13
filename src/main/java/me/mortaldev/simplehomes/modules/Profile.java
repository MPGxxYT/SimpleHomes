package me.mortaldev.simplehomes.modules;

import me.mortaldev.simplehomes.Main;
import me.mortaldev.simplehomes.configs.HomeConfig;
import me.mortaldev.simplehomes.utils.main.Storage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Profile {
    UUID uuid;
    List<Home> homeList;
    Integer maxHomes;

    public Profile(UUID uuid) {
        this(uuid, new ArrayList<>(), HomeConfig.getDefaultMaxHomes());
    }

    public Profile(UUID uuid, Integer maxHomes) {
        this(uuid, new ArrayList<>(), maxHomes);
    }

    public Profile(UUID uuid, List<Home> homeList) {
        this(uuid, homeList, HomeConfig.getDefaultMaxHomes());
    }

    public Profile(UUID uuid, List<Home> homeList, Integer maxHomes) {
        this.uuid = uuid;
        this.homeList = homeList;
        this.maxHomes = maxHomes;
    }

    public UUID getUuid() {
        return uuid;
    }

    public List<Home> getHomeList() {
        return homeList;
    }

    public static Profile getProfile(UUID uuid){
        var file = new File(Main.getPlugin().getDataFolder().getAbsolutePath() + "/profiles/" + uuid.toString() + ".json");
        return (Profile) Storage.getJsonObject(file, Profile.class);
    }

    public Home getHome(String homeName) {
        for (Home home : homeList) {
            if (home.getName().equals(homeName)) {
                return home;
            }
        }
        return null;
    }

    public Boolean addHome(Home home) {
        homeList.add(home);
        saveToStorage();
        return true;
    }

    public Boolean delHome(Home home) {
        homeList.remove(home);
        saveToStorage();
        return true;
    }

    public Integer getMaxHomes() {
        return maxHomes;
    }

    void saveToStorage(){
        var file = new File(Main.getPlugin().getDataFolder().getAbsolutePath() + "/profiles/" + uuid.toString() + ".json");
        Storage.saveJsonObject(file, this);
    }

}
