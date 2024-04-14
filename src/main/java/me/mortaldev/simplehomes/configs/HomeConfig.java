package me.mortaldev.simplehomes.configs;

import me.mortaldev.simplehomes.utils.main.YamlConfig;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HomeConfig {
    static FileConfiguration config;
    static UUID variableID;
    static List<World> whitelistedWorlds;
    static List<World> blacklistedWorlds;
    static Integer defaultMaxHomes = 3;
    static Long teleportDelay = 3L;
    static String messagePrefix;
    static String primaryColor = "&6";
    static String secondaryColor = "&f";
    static String tertiaryColor = "&7";

    /**
     * Retrieves a list of worlds based on the given string list of world names.
     * If a world name contains whitespace, it is modified by removing leading and trailing whitespace
     * and replacing any whitespace in between with a single underscore.
     * Worlds that do not exist are logged as warnings and excluded from the resulting list.
     * The provided string list is updated with the modified world names and saved to the config file if any changes are made.
     *
     * @param stringList the list of string world names
     * @return a list of World objects representing the existing worlds
     */
    public static List<World> getWorldsList(List<String> stringList){
        boolean update = false; // Boolean for updating config incase of errors.
        List<World> worldsList = new ArrayList<>();
        for (String worldName : stringList) { // Loop through the string list of world names
            if (worldName.contains(" ")){ // if theres any whitespace in the worldName
                // Trim the string (removes whitespace at start and end of string)
                // Then replace any whitespace with a single underscore.
                // ex. '   hello  the    world ' becomes 'hello_the_world'
                worldName = worldName.trim().replaceAll("\\s+", "_");
                update = true; // update at the end
            }
            World world = Bukkit.getWorld(worldName); // Attempt to get the world
            if (world != null) { // if it exists and isnt null
                worldsList.add(world); //add it to the list
            } else {
                // otherwise, warn the console
                Bukkit.getLogger().warning("Can't load world '"+worldName+"' because it doesn't exist. (config.yml)");
                stringList.remove(worldName); // and remove it from the list
                update = true; // then update at end
            }
        }
        if (update) {
            config.set("whitelistedWorlds", stringList);
            YamlConfig.saveConfig(config, "config");
        }
        return worldsList;
    }

    public static String loadConfig(Boolean updateConfig){
        if (updateConfig){ // if we're updating the config, then update it
            config = YamlConfig.getConfig("config");
            variableID = UUID.randomUUID();
        }
        boolean update = false;
        // get the whitelisted worlds
        whitelistedWorlds = getWorldsList(config.getStringList("whitelistedWorlds"));
        // get the blacklisted worlds
        blacklistedWorlds = getWorldsList(config.getStringList("blacklistedWorlds"));
        // get the defaultMaxHomes integer
        defaultMaxHomes = config.getInt("defaultMaxHomes");
        if (defaultMaxHomes < -1) { // if its lower than -1
            // let the player know
            Bukkit.getLogger().warning("defaultMaxHomes can only go as low as -1 (config.yml)");
            // set the default to -1
            defaultMaxHomes = -1;
            // and update it in the config
            config.set("defaultMaxHomes", -1);
            update = true;
        }
        teleportDelay = config.getLong("teleportDelay");
        messagePrefix = config.getString("messagePrefix");
        primaryColor = config.getString("primaryColor");
        if (primaryColor == null || primaryColor.isBlank()) {
            primaryColor = "&6";
            config.set("primaryColor", primaryColor);
            update = true;
        }
        secondaryColor = config.getString("secondaryColor");
        if (secondaryColor == null || secondaryColor.isBlank()) {
            secondaryColor = "&f";
            config.set("secondaryColor", secondaryColor);
            update = true;
        }
        tertiaryColor = config.getString("tertiaryColor");
        if (tertiaryColor == null || tertiaryColor.isBlank()) {
            tertiaryColor = "&7";
            config.set("tertiaryColor", tertiaryColor);
            update = true;
        }
        if (update){
            YamlConfig.saveConfig(config, "config");
        }
        return "config.yml Loaded";
    }

    public static List<World> getWhitelistedWorlds() {
        return whitelistedWorlds;
    }

    public static List<World> getBlacklistedWorlds() {
        return blacklistedWorlds;
    }

    public static Integer getDefaultMaxHomes() {
        return defaultMaxHomes;
    }

    public static Long getTeleportDelay() {
        return teleportDelay;
    }

    public static String getMessagePrefix() {
        return messagePrefix;
    }

    public static String getPrimaryColor() {
        return primaryColor;
    }

    public static String getSecondaryColor() {
        return secondaryColor;
    }

    public static String getTertiaryColor() {
        return tertiaryColor;
    }

    public static UUID getVariableID() {
        return variableID;
    }
}
