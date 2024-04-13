package me.mortaldev.simplehomes.utils.main;

import org.bukkit.entity.Player;

public class Utils {
    public static boolean hasAnyPermission(Player player, String... permissions) {
        for (String permission : permissions) {
            if (player.hasPermission(permission)) {
                return true;
            }
        }
        return false;
    }

}
