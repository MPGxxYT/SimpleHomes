package me.mortaldev.simplehomes.utils.main;

import me.mortaldev.simplehomes.Main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Objects;

public class YamlConfig {

    public static String failedToLoad(String configName, String configValue){
        return failedToLoad(configName, configValue, "INVALID VALUE");
    }
    public static String failedToLoad(String configName, String configValue, String failReason){
        String message = MessageFormat.format("[{0}.YML] Failed to load config value: {1} ({2})", configName, configValue, failReason);
        Main.getPlugin().getLogger().warning(message);
        YamlConfig.loadResource(configName);
        return message;
    }

    public static FileConfiguration createNewConfig(String name){
        if (!name.contains(".yml")){
            name = name.concat(".yml");
        }
        File file = new File(Main.getPlugin().getDataFolder(), name);
        if (file.exists()){
            return getConfig(name);
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    public static FileConfiguration getConfig(String name) {
        if (!name.contains(".yml")){
            name = name.concat(".yml");
        }
        File file = new File(Main.getPlugin().getDataFolder(), name);
        if (!file.exists()){
            loadResource(name);
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    public static FileConfiguration getOtherConfig(File file) {
        if (!file.exists()){
            Main.getPlugin().getLogger().warning("Error finding other config: "+ file);
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    public static void saveOtherConfig(File file, FileConfiguration config){
        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveConfig(FileConfiguration config, String name){
        if (!name.contains(".yml")){
            name = name.concat(".yml");
        }
        File file = new File(Main.getPlugin().getDataFolder(), name);
        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void loadResource(String name){
        if (!name.contains(".yml")){
            name = name.concat(".yml");
        }
        InputStream stream = null;
        try {
            stream = Main.getPlugin().getResource(name);
            if (stream == null){
                Main.getPlugin().getLogger().warning("Failed to load resource: " + name);
                return;
            }
            File file = new File(Main.getPlugin().getDataFolder(), name);
            if (!file.exists()){
                file.createNewFile();
            }
            OutputStream outputStream = new FileOutputStream(file);
            outputStream.write(stream.readAllBytes());
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    Bukkit.getLogger().warning("An exception has occurred: " + e);
                }
            }
        }
    }

    public static void saveDefaultConfig(FileConfiguration config){
        Main.getPlugin().saveResource(config.getName(), false);
    }

    public static void reloadConfig(FileConfiguration config) {
        config = YamlConfiguration.loadConfiguration(new File(config.getCurrentPath()));
        Reader stream = new InputStreamReader(Objects.requireNonNull(Main.getPlugin().getResource(config.getName())), StandardCharsets.UTF_8);
        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(stream);
        config.setDefaults(defConfig);
    }
}