package me.mortaldev.simplehomes;

import me.mortaldev.simplehomes.commands.HomeCommand;
import me.mortaldev.simplehomes.configs.HomeConfig;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class Main extends JavaPlugin {

    static Main plugin;
    static String label = "SimpleHomes";

    @Override
    public void onEnable() {

        // DEPENDENCIES

//        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null){
//            getLogger().warning("Could not find PlaceholderAPI! This plugin is required.");
//            Bukkit.getPluginManager().disablePlugin(this);
//            return;
//        }

        plugin = this;

        // DATA FOLDER

        if (!getDataFolder().exists()){
            getDataFolder().mkdir();
        }
        new File(getDataFolder()+"/profiles/").mkdirs();

        // CONFIGS
        HomeConfig.loadConfig(true);


        // Events
//        getServer().getPluginManager().registerEvents(new PlayerJoin(), this);
//        getServer().getPluginManager().registerEvents(new PlayerQuit(), this);


        // COMMANDS

        new HomeCommand();

        getLogger().info(label + " Enabled");

    }

    @Override
    public void onDisable() {
        getLogger().info(label + " Disabled");
    }

    public static Main getPlugin() {
        return plugin;
    }

    public static String getLabel() {
        return label;
    }
}