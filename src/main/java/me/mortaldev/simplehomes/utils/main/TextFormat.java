package me.mortaldev.simplehomes.utils.main;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextFormat {

    /**
     * Formats the given string using MiniMessage format tags and returns it as a Component object.
     *
     * @param str the string to be formatted
     * @return the formatted string as a Component object
     */
    public static Component format(String str){
        String result = asString(str, false);
        result = asParam(result);
        return MiniMessage.miniMessage().deserialize(result);
    }

    /**
     * Formats the given string using MiniMessage format tags and returns it as a Component object.
     *
     * @param str the string to be formatted
     * @param disableReset whether to disable the reset tag or not
     * @return the formatted string as a Component object
     */
    public static Component format(String str, boolean disableReset){
        String result = asString(str, disableReset);
        result = asParam(result);
        return MiniMessage.miniMessage().deserialize(result);
    }

    // Welcome Home##My love!##sgt:/home ##ttp:Click Here
    // [EXTRA TEXT ] [ INPUT] [PAR][ARG  ] [PAR][   ARG  ]
    //             ||        ||          ||
    //
    // INPUT: /home <home> - Teleport to your home.
    // PARAM: "sgt:" arg = "/home "
    // PARAM: "ttp:" arg = ":Click to select /home"

    /**
     * Splits the given string by "##" and formats it according to the specified tags and values.
     *
     * @param str the string to be formatted
     * @return the formatted string
     */
    public static String asParam(String str) {
        // Splits the string by "##"
        String[] split = str.split("##");
        // Gets the types of tags
        Types[] types = Types.values();

        // Then gets the keys of the tags, which are used to identify the type of tag.
        String[] keys = Types.getKeys();
        HashMap<Integer, AbstractMap.SimpleEntry<String, String>> clusters = new HashMap<>();

        for (int i = 0; i < split.length; i++) {
            String v = split[i];
            String tag = "";
            String value = "";
            if (split[i].length() >= 4){
                tag = split[i].substring(0, 4);
                value = split[i].substring(4);
            }

            if (keys.contains(tag)){
                clusters.put(i, new AbstractMap.SimpleEntry<>(tag, value));
            } else {
                clusters.put(i, new AbstractMap.SimpleEntry<>("text", v));
            }
        } // THIS IS BEING REFACTORED, DO NOT USE

        String past_text = "";
        ArrayList<String> out = new ArrayList<>();
        for (Map.Entry<Integer, AbstractMap.SimpleEntry<String, String>> entry : clusters.entrySet()){
            int i = entry.getKey();
            String text = entry.getValue().getValue();
            String tag = entry.getValue().getKey();

            if (Objects.equals(tag, "text")){
                if (!past_text.isEmpty()){
                    out.add(past_text);
                }
                past_text = text;
            } else {
                String value = types.get(tag);
                past_text = value.replace("#arg#", text).replace("#input#", past_text);
            }
            if (clusters.size() == i+1){
                out.add(past_text);
            }
        }
        return String.join("", out);
    }

    /**
     * Converts a given string to a formatted string using MiniMessage format tags based on provided options.
     * Replaces instances of "&nl" with "<newline>".
     * Replaces hexadecimal HTML character references with the corresponding format.
     * Replaces color tags with the corresponding format from the Colors enum.
     * Replaces decoration tags with the corresponding format from the Decorations enum.
     *
     * @param str the string to be formatted
     * @param disableReset whether to disable the reset tag or not
     * @return the formatted string
     */
    public static String asString(String str, boolean disableReset){
        // Create a StringBuilder from the input string for efficient string manipulation
        StringBuilder stringBuilder = new StringBuilder(str);

        // Replace all instances of "&nl" with "<newline>"
        stringBuilder.replace(0, stringBuilder.length(),
                stringBuilder.toString().replace("&nl", "<newline>"));

        // Define a regular expression pattern to match hexadecimal HTML character references
        Pattern hexPattern = Pattern.compile("&#(.{6})");
        // Create a Matcher object with the input string.
        Matcher hexMatcher = hexPattern.matcher(str);

        // Find each occurrence of the pattern in the input string
        while (hexMatcher.find()){
            // Extract the hexadecimal code from the Matcher
            String hexCode = hexMatcher.group(1);
            // Replace the matched hexadecimal HTML character reference with the desired format
            stringBuilder.replace(0, stringBuilder.length(),
                    stringBuilder.toString().replace("&#" + hexCode, "<#" + hexCode + ">"));
        }

        // For each possible color in the Colors enum
        for (Colors color : Colors.values()) {
            // Define the key and value to be used in the replacement
            String key = "&" + color.getKey();
            String value = disableReset ?
                    "<" + color.getValue() + ">" :    // if disableReset is true, don't insert a reset
                    "<reset><" + color.getValue() + ">";  // if disableReset is false, insert a reset before the color
            // Replace the key with the value in the StringBuilder
            stringBuilder.replace(0, stringBuilder.length(),
                    stringBuilder.toString().replace(key, value));
        }

        // Follow similar steps for each possible decoration in the Decorations enum
        for (Decorations decoration : Decorations.values()) {
            String key = "&" + decoration.getKey();
            String value = "<" + decoration.getValue() + ">";
            stringBuilder.replace(0, stringBuilder.length(),
                    stringBuilder.toString().replace(key, value));
        }

        // Convert the final StringBuilder to a String and return it
        return stringBuilder.toString();
    }

    /**
     * Replaces all occurrences of a specified string with another string in a StringBuilder.
     *
     * @param sb the StringBuilder in which the replacements should be made
     * @param from the string to be replaced
     * @param to the replacement string
     */
    public static void replaceAll(StringBuilder sb, String from, String to) {
        int index = sb.indexOf(from);
        while (index != -1) {
            sb.replace(index, index + from.length(), to);
            index += to.length(); // Move to the end of the replacement
            index = sb.indexOf(from, index);
        }
    }


    // Copy me!##cpy:My mom##ttp:&7Your mom?##Idkk

    public enum Types {
        // CLICK ACTIONS
        CHANGE_PAGE("pge:", "<click:change_page:'#arg#'>#input#</click>"),
        COPY_TO_CLIPBOARD("cpy:", "<click:copy_to_clipboard:'#arg#'>#input#</click>"),
        OPEN_FILE("fle:", "<click:open_file:'#arg#'>#input#</click>"),
        OPEN_PAGE("url:", "<click:open_url:'#arg#'>#input#</click>"),
        RUN_COMMAND("cmd:", "<click:run_command:'#arg#'>#input#</click>"),
        SUGGEST_COMMAND("sgt:", "<click:suggest_command:'#arg#'>#input#</click>"),

        // HOVER
        SHOW_ENTITY("ent:", "<hover:show_entity:'#arg#'>#input#</hover>"),
        SHOW_ITEM("itm:", "<hover:show_item:'#arg#'>#input#</hover>"),
        SHOW_TEXT("ttp:", "<hover:show_text:'#arg#'>#input#</hover>"),

        // KEYBIND
        KEY("key:", "#input#<key:#arg#>"),

        // TRANSLATE
        // ex. ##lng:block.minecraft.diamond_block
        // ex. ##lng:commands.drop.success.single:'<red>1':'<blue>Stone'
        LANG("lng:", "#input#<lang:#arg#>"),

        // INSERT
        INSERT("ins:", "<insert:'#arg#'>#input#</insert>"),

        // RAINBOW
        // COLORS##rnb:##no colors
        RAINBOW("rnb:", "<rainbow>#input#</rainbow>"),

        //GRADIENT
        // colored##grd:#5e4fa2:#f79459##not colored
        // colored##grd:#5e4fa2:#f79459:red##not colored
        // colored##grd:green:blue##not colored
        GRADIENT("grd:", "<gradient:#arg#>#input#</gradient>"),

        // TRANSITION
        // colored##trn:[color1]:[color...]:[phase]##not colored
        // colored##trn:#00ff00:#ff0000:0##not colored
        TRANSITION("trn:", "<transition:#arg#>#input#</transition>"),

        // FONT
        FONT("fnt:", "<font:#arg#>#input#</font>"),

        // SELECTOR
        // Hello ##slt:@e[limit=5]##, I'm ##slt:@s##!
        SELECTOR("slt:", "#input#<selector:#arg#>"),

        // SCORE
        // ##score:_name_:_objective_##
        // You have won ##scr:rymiel:gamesWon/## games!
        SCORE("scr:", "#input#<score:#arg#>"),

        // NBT
        // ##nbt:block|entity|storage:id:path[:_separator_][:interpret]##
        // Your health is ##nbt:entity:'@s':Health/##
        NBT("nbt:", "#input#<nbt:#arg#>");

        private final String key;
        private final String value;

        Types(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public static String[] getKeys() {
            List<String> keys = new ArrayList<>();
            for (Types types : Types.values()) {
                keys.add(types.getKey());
            }
            return keys.toArray(new String[0]);
        }

        public String getValue() {
            return value;
        }
    }

    public enum Decorations {

        BOLD("l", "b"),
        ITALIC("o", "em"),
        UNDERLINE("n", "u"),
        STRIKETHROUGH("m", "st"),
        OBFUSCATED("k", "obf"),
        RESET("r", "reset");

        private final String key;
        private final String value;

        Decorations(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public static String[] getKeys() {
            List<String> keys = new ArrayList<>();
            for (Types types : Types.values()) {
                keys.add(types.getKey());
            }
            return keys.toArray(new String[0]);
        }

        public String getValue() {
            return value;
        }
    }


    public enum Colors {

        BLACK("0", "black"),
        DARK_BLUE("1", "dark_blue"),
        DARK_GREEN("2", "dark_green"),
        DARK_AQUA("3", "dark_aqua"),
        DARK_RED("4", "dark_red"),
        DARK_PURPLE("5", "dark_purple"),
        GOLD("6", "gold"),
        GREY("7", "gray"),
        DARK_GREY("8", "dark_gray"),
        BLUE("9", "blue"),
        GREEN("a", "green"),
        AQUA("b", "aqua"),
        RED("c", "red"),
        LIGHT_PURPLE("d", "light_purple"),
        YELLOW("e", "yellow"),
        WHITE("f", "white");

        private final String key;
        private final String value;

        Colors(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public static String[] getKeys() {
            List<String> keys = new ArrayList<>();
            for (Types types : Types.values()) {
                keys.add(types.getKey());
            }
            return keys.toArray(new String[0]);
        }

        public String getValue() {
            return value;
        }
    }
}