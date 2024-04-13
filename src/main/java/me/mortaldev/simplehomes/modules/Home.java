package me.mortaldev.simplehomes.modules;

import org.bukkit.Location;

import java.util.Map;

public class Home {
    String name;
    Map<String, Object> location;

    public Home(String name, Location location) {
        this.name = name;
        this.location = location.serialize();
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return Location.deserialize(location);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(Location location) {
        this.location = location.serialize();
    }
}
