package me.mortaldev.simplehomes.utils.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.mortaldev.simplehomes.Main;

import java.io.*;

public class Storage {

    public static File defaultFile(String fileName){
        if (fileName.contains(".json")){
            return new File(Main.getPlugin().getDataFolder().getAbsolutePath() + fileName);
        } else {
            return new File(Main.getPlugin().getDataFolder().getAbsolutePath() + fileName + ".json");
        }
    }

    //Storage.saveJsonObject(Storage.defaultFile("test"), testClass);

    public static <T> T getJsonObject(File file, Class<T> clazz){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        if (!file.exists()){
            return null;
        }

        try (Reader reader = new FileReader(file)){
            return gson.fromJson(reader, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveJsonObject(File file, Object object){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            file.getParentFile().mkdirs(); // Use mkdirs() instead of mkdir(), it will create all necessary parent directories.
            file.createNewFile();
            try (Writer writer = new FileWriter(file, false)) {
                gson.toJson(object, writer);
                writer.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}