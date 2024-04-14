package me.mortaldev.simplehomes.utils.main;

import me.mortaldev.simplehomes.utils.records.Pair;
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

    /**
     * Splits the given string by "##" and formats it according to the specified tags and values.
     *
     * @param str the string to be formatted
     * @return the formatted string
     */
    public static String asParam(String str) {
        if (str == null) { throw new IllegalArgumentException("Input string cannot be null."); }

        // Holds the split strings and their associated tags and values.
        HashMap<Integer, Pair<String, String>> clusters = new HashMap<>();
        // Get all the keys from Types enum
        List<String> keys = Arrays.stream(Types.getKeys()).toList();

        // Split the string on '##'.
        String[] split = str.split("##");

        // Loop over the split string.
        for (int i = 0; i < split.length; i++) {
            // Fill the clusters with appropriate entries.
            addToClusters(i, split[i], keys, clusters);
        }

        // Used to build up the output string.
        String past_text = "";
        // The final output string list.
        List<String> out = new ArrayList<>();

        // Process all entries in the clusters map.
        for (Map.Entry<Integer, Pair<String, String>> entry : clusters.entrySet()) {
            // Grabs the text from the clusters according to the protocol.
            past_text = processClusterEntry(entry, past_text, clusters, out);
        }

        // Join all the strings in `out` together and return the result.
        return String.join("", out);
    }

    // Helper function to fill the clusters with appropriate entries.
    private static void addToClusters(int index, String str, List<String> keys, HashMap<Integer, Pair<String, String>> clusters) {
        String tag = "";
        String value = "";
        // If the string is at least 4 characters long
        if (str != null && str.length() >= 4){
            // Grab the tag and value from the string
            tag = str.substring(0, 4);
            value = str.substring(4);
        }
        // Put the entry in the clusters. If tag exists, use the tag and value, otherwise use "text" as the tag
        if (keys.contains(tag)){
            clusters.put(index, new Pair<>(tag, value));
        } else {
            clusters.put(index, new Pair<>("text", str != null ? str : ""));
        }
    }

    // Processes a single cluster entry.
    private static String processClusterEntry(Map.Entry<Integer, Pair<String, String>> entry, String past_text, HashMap<Integer, Pair<String, String>> clusters, List<String> out) {
        int index = entry.getKey();
        String tag = getValueFromEntry(entry, 'k');
        String v = getValueFromEntry(entry, 'v');

        // If the tag is "text", just build up the past_text.
        // If not, do the replacement according to the Types value.
        if (Objects.equals(tag, "text")){
            if (!past_text.isEmpty()){
                out.add(past_text);
            }
            past_text = v;
        } else {
            past_text = performTypeValueReplacement(tag, v, past_text);
        }

        // If this the last cluster, append `past_text` to `out`.
        if (clusters.size() == index+1){
            out.add(past_text);
        }

        return past_text;
    }

    // Helper function to get value from map entry's key or value.
    private static String getValueFromEntry(Map.Entry<Integer, Pair<String, String>> entry, char keyOrValue) {
        // If keyOrValue is 'k', get the key; otherwise, get the value.
        return keyOrValue == 'k' ? entry.getValue().first() : entry.getValue().second();
    }

    // Helper function to perform replacement based on the Types value.
    private static String performTypeValueReplacement(String tag, String value, String past_text) {
        // Retrieve the value associated with `tag` from the Types.
        String typeValue = "";
        if (Types.getTypeFromKey(tag) != null) {
            typeValue = Types.getTypeFromKey(tag).value;
        }
        // Perform replacement on `typeValue` and assign it to `past_text`.
        return typeValue
                .replace("#arg#", value)
                .replace("#input#", past_text);
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

        public static Types getTypeFromKey(String string){
            for (Types value : values()) {
                if (value.getKey().equals(string)){
                    return value;
                }
            }
            return null;
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