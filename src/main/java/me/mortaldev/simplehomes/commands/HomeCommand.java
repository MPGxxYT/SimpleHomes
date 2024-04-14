package me.mortaldev.simplehomes.commands;

import me.mortaldev.simplehomes.configs.HomeConfig;
import me.mortaldev.simplehomes.modules.Home;
import me.mortaldev.simplehomes.modules.Profile;
import me.mortaldev.simplehomes.utils.main.CommandHandler;
import me.mortaldev.simplehomes.utils.main.Cooldown;
import me.mortaldev.simplehomes.utils.main.TextFormat;
import me.mortaldev.simplehomes.utils.main.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HomeCommand {

    Cooldown cooldown;
    Long teleportDelay;
    UUID variableID;
    List<World> whitelistedWorlds;
    List<World> blacklistedWorlds;
    String messagePrefix;
    String primaryColor;
    String secondaryColor;
    String tertiaryColor;

    public void loadVariables(){
        if (variableID != HomeConfig.getVariableID()){
            variableID = HomeConfig.getVariableID();
            whitelistedWorlds = HomeConfig.getWhitelistedWorlds();
            blacklistedWorlds = HomeConfig.getBlacklistedWorlds();
            teleportDelay = HomeConfig.getTeleportDelay();
            messagePrefix = HomeConfig.getMessagePrefix();
            primaryColor = HomeConfig.getPrimaryColor();
            secondaryColor = HomeConfig.getSecondaryColor();
            tertiaryColor = HomeConfig.getTertiaryColor();
        }
    }

//     /home <name> - Teleport to your home.
//     ~ simplehomes.basic / simplehomes.tp
//
//     /home help - Display all the commands
//     ~ simplehomes.basic / simplehomes.help
//
//     /home set/add <name> - Set a home to your location.
//     ~ simplehomes.basic / simplehomes.set
//
//     /home del/remove <name> - Remove a home
//     ~ simplehomes.basic / simplehomes.del
//
//     /home list - List your current homes
//     ~ simplehomes.basic / simplehomes.list
//
//     /home reload (admin command) - Reload the config
//     ~ * / simplehomes.admin / simplehomes.reload

    public HomeCommand() {
        cooldown = new Cooldown();
        new CommandHandler("home", -1, true) {
            @Override
            public boolean onCommand(@NotNull CommandSender sender, @NotNull String[] args) {
                loadVariables();
                Player player = (Player) sender;
                switch (args.length){
                    case 0 -> help(player);
                    case 1 -> {
                        if (args[0].equalsIgnoreCase("help")) {
                            help(player);
                        } else if (args[0].equalsIgnoreCase("list")) {
                            listHomes(player);
                        } else if (args[0].equalsIgnoreCase("reload")) {
                            if (Utils.hasAnyPermission(player, "*", "simplehomes.admin", "simplehomes.reload")) {
                                player.sendMessage(HomeConfig.loadConfig(true));
                            }
                        } else if ((args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("add" ))){
                            player.sendMessage(TextFormat.format(messagePrefix+"Provide a name to set the home to."));
                        } else if ((args[0].equalsIgnoreCase("del") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("delete"))){
                            player.sendMessage(TextFormat.format(messagePrefix+"Provide the name of the home to delete."));
                        } else {
                            teleportHome(player, args[0]);
                        }
                    }
                    case 2 -> {
                        if (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("add")){
                            addHome(player, args[1]);
                        } else if (args[0].equalsIgnoreCase("del") || args[0].equalsIgnoreCase("remove")  || args[0].equalsIgnoreCase("delete")){
                            delHome(player, args[1]);
                        }
                    }
                }
                return true;
            }

            @Override
            public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
                Player player = (Player) sender;
                List<String> returnList = new ArrayList<>();
                if (args.length == 1) {
                    returnList.add("help");
                    returnList.add("list");
                    returnList.add("set");
                    returnList.add("del");
                    if (Utils.hasAnyPermission(player, "*", "simplehomes.admin", "simplehomes.reload")) {
                        returnList.add("reload");
                    }
                    Profile profile = Profile.getProfile((player.getUniqueId()));
                    for (Home home : profile.getHomeList()) {
                        returnList.add(home.getName());
                    }
                    return returnList;
                }
                if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("del")) {
                        Profile profile = Profile.getProfile((player.getUniqueId()));
                        for (Home home : profile.getHomeList()) {
                            returnList.add(home.getName());
                        }
                    }
                }
                return returnList;
            }

            @Override
            public @NotNull String getUsage() {
                return "null";
            }

            @Override
            public String getPermission() {
                return "null";
            }

            @Override
            public @NotNull String getDescription() {
                return "null";
            }

            @Override
            public @NotNull List<String> getAliases() {
                return new ArrayList<>();
            }
        };
    }

    private void delHome(Player player, String homeName) {
        if (!Utils.hasAnyPermission(player, "*", "simplehomes.basic", "simplehomes.del")) {
            player.sendMessage(TextFormat.format(messagePrefix+"You do not have permission."));
            return;
        }
        Profile profile = Profile.getProfile(player.getUniqueId());
        for (Home home : profile.getHomeList()) {
            if (home.getName().equalsIgnoreCase(homeName)) {
                profile.delHome(home);
                player.sendMessage(TextFormat.format(messagePrefix + "Home "+primaryColor+home.getName()+"&f has been deleted"));
                return;
            }
        }
        player.sendMessage(TextFormat.format(messagePrefix + "Home "+primaryColor+homeName+"&f doesn't exist."));
    }

    private void addHome(Player player, String homeName) {
        if (!Utils.hasAnyPermission(player, "*", "simplehomes.basic", "simplehomes.add")) {
            player.sendMessage(TextFormat.format(messagePrefix+"You do not have permission."));
            return;
        }
        if ((!whitelistedWorlds.contains(player.getWorld()) && !whitelistedWorlds.isEmpty()) || blacklistedWorlds.contains(player.getWorld())) {
            player.sendMessage(TextFormat.format(messagePrefix+"You cannot set a home in this world."));
            return;
        }
        Profile profile = Profile.getProfile(player.getUniqueId());
        for (Home home : profile.getHomeList()) {
            if (home.getName().equalsIgnoreCase(homeName)) {
                player.sendMessage(TextFormat.format(messagePrefix+"Home "+primaryColor+home.getName()+"&f already exists."));
                return;
            }
        }
        profile.addHome(new Home(homeName, player.getLocation()));
        player.sendMessage(TextFormat.format(messagePrefix+"Home "+primaryColor+homeName+"&f has been created"));
    }

    private void teleportHome(Player player, String homeName) {
        if (!Utils.hasAnyPermission(player, "*", "simplehomes.basic", "simplehomes.tp")) {
            player.sendMessage(TextFormat.format(messagePrefix+"You do not have permission."));
            return;
        }
        Profile profile = Profile.getProfile(player.getUniqueId());
        for (Home home : profile.getHomeList()) {
            if (home.getName().equalsIgnoreCase(homeName)) {
                if (!cooldown.isDone(player.getUniqueId())) {
                    var timeLeft = cooldown.getTimeLeft(player.getUniqueId());
                    player.sendMessage(TextFormat.format(messagePrefix+"You must wait "+primaryColor+timeLeft+"&f seconds to do this again."));
                    return;
                }
                cooldown.start(player.getUniqueId(), teleportDelay);
                player.teleport(home.getLocation());
                player.sendMessage(TextFormat.format(messagePrefix+"You have been teleported to "+primaryColor+home.getName()+"&f!"));
                return;
            }
        }
        player.sendMessage(TextFormat.format(messagePrefix+"Home "+primaryColor+homeName+"&f does not exist."));
    }

    private void listHomes(Player player) {
        if (!Utils.hasAnyPermission(player, "*", "simplehomes.basic", "simplehomes.list")) {
            player.sendMessage(TextFormat.format(messagePrefix+"You do not have permission."));
            return;
        }
        Profile profile = Profile.getProfile(player.getUniqueId());
        var homeList = profile.getHomeList();
        if (homeList.isEmpty()) {
            player.sendMessage(TextFormat.format(messagePrefix+"You have no homes."));
            return;
        }
        player.sendMessage(TextFormat.format(secondaryColor+"---------- "+primaryColor+"&lYour Homes "+secondaryColor+"----------"));
        player.sendMessage("");
        for (Home home : profile.getHomeList()) {
            // &6Base##ttp:&7Teleport to Base##cmd:/home Base## ##&f[&6delete&f]##ttp:&7Delete Base##cmd:/home del Base
            player.sendMessage(TextFormat.format(secondaryColor+" - "+primaryColor+"&l"+home.getName() +
                    "##ttp:" + tertiaryColor + "Teleport to " + home.getName() + "##cmd:/home " + home.getName() + "##              ##" +
                    secondaryColor + "[" + primaryColor + "DELETE" + secondaryColor + "]" +
                    "##ttp:"+ tertiaryColor + "Delete " + home.getName() + "##cmd:/home del " + home.getName(),
                    true));
        }
        player.sendMessage("");
        player.sendMessage(TextFormat.format(secondaryColor+"---------------------------------"));
    }

    void help(Player player){
        if (!Utils.hasAnyPermission(player, "*", "simplehomes.basic", "simplehomes.help")) {
            player.sendMessage(TextFormat.format(messagePrefix+"You do not have permission."));
            return;
        }
        List<Component> messageList = new ArrayList<>(){{
            add(TextFormat.format(secondaryColor+"----------"+primaryColor+"&l Homes Help "+secondaryColor+"----------"));
            add(TextFormat.format(""));
            add(TextFormat.format(primaryColor+"/home "+secondaryColor+"<name>"+tertiaryColor+" - Teleport to your home." +
                    "##ttp:"+secondaryColor+"Click to select /home " +
                    "##sgt:/home ", true));
            add(TextFormat.format(primaryColor+"/home "+secondaryColor+"help"+tertiaryColor+" - Displays this menu." +
                    "##ttp:"+secondaryColor+"Click to select /home help" +
                    "##sgt:/home help", true));
            add(TextFormat.format(primaryColor+"/home "+secondaryColor+"set <name>"+tertiaryColor+" - Set a home to your location." +
                    "##ttp:"+secondaryColor+"Click to select /home set <name>" +
                    "##sgt:/home set ", true));
            add(TextFormat.format(primaryColor+"/home "+secondaryColor+"del <name>"+tertiaryColor+" - Deletes the given home." +
                    "##ttp:"+secondaryColor+"Click to select /home del <name>" +
                    "##sgt:/home del ", true));
            add(TextFormat.format(primaryColor+"/home "+secondaryColor+"list"+tertiaryColor+" - Lists all your homes." +
                    "##ttp:"+secondaryColor+"Click to select /home list" +
                    "##sgt:/home list", true));
            if (Utils.hasAnyPermission(player, "*", "simplehomes.admin", "simplehomes.reload")) {
                add(TextFormat.format(primaryColor+"/home "+secondaryColor+"reload"+tertiaryColor+" - Reloads the config." +
                        "##ttp:"+secondaryColor+"Click to select /home reload" +
                        "##sgt:/home reload", true));
            }
            add(TextFormat.format(""));
            add(TextFormat.format(secondaryColor+"--------------------------------"));
        }};
        for (Component component : messageList) {
            player.sendMessage(component);
        }
    }
}
