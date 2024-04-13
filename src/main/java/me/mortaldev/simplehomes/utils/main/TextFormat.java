package me.mortaldev.simplehomes.utils.main;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class TextFormat {

    public static Component format(String str){
        String result = asString(str, false);
        result = asParam(result);
        return MiniMessage.miniMessage().deserialize(result);
    }

    public static Component format(String str, Boolean disableReset){
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

    public static String asParam(String str) {
        String[] split = str.split("##");
        HashMap<String, String> types = getTypes();
        Set<String> keys = types.keySet();
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
        }

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

    public static String asString(String str, Boolean disableReset){
        // &f replaced with <white>
        // &l replaced with <bold>
        // &r replaced with <reset> ect
        // &#ffffff to <#ffffff>
        str = str.replace("&nl", "<newline>");
        if (str.contains("&#")){ // Replacing hex tags
            ArrayList<String> rep = new ArrayList<>();
            char[] split = str.toCharArray();
            for (int i = 0; i < split.length; i++) {
                if (split[i] == '&' && split[i+1] == '#'){
                    rep.add(str.substring(i+2, i+8));
                }
            }
            for (String i : rep){
                str = str.replace("&#" + i, "<#" + i + ">");
            }
        }
        for (Map.Entry<String, String> entry : getColors().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (disableReset){
                str = str.replace("&" + key, "<" + value + ">");
            } else {
                str = str.replace("&" + key, "<reset><" + value + ">");
            }
        }
        for (Map.Entry<String, String> entry : getDecorations().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            str = str.replace("&" + key, "<" + value + ">");
        }
        return str;
    }


    // Copy me!##cpy:My mom##ttp:&7Your mom?##Idkk
    @NotNull
    private static HashMap<String, String> getTypes() {
        HashMap<String, String> types = new HashMap<>();
        // Click Actions
        types.put("pge:", "<click:change_page:'#arg#'>#input#</click>");
        types.put("cpy:", "<click:copy_to_clipboard:'#arg#'>#input#</click>");
        types.put("fle:", "<click:open_file:'#arg#'>#input#</click>");
        types.put("url:", "<click:open_url:'#arg#'>#input#</click>");
        types.put("cmd:", "<click:run_command:'#arg#'>#input#</click>");
        types.put("sgt:", "<click:suggest_command:'#arg#'>#input#</click>");

        // Hover
        types.put("ent:", "<hover:show_entity:'#arg#'>#input#</hover>");
        types.put("itm:", "<hover:show_item:'#arg#'>#input#</hover>");
        types.put("ttp:", "<hover:show_text:'#arg#'>#input#</hover>");

        // KEYBIND
        types.put("key:", "#input#<key:#arg#>");

        // TRANSLATE (be weary of accidental params)
        // ex. ##lng:block.minecraft.diamond_block
        // ex. ##lng:commands.drop.success.single:'<red>1':'<blue>Stone'
        types.put("lng:", "#input#<lang:#arg#>");

        // INSERT
        types.put("ins:", "<insert:'#arg#'>#input#</insert>");

        // RAINBOW
        // COLORS##rnb:##no colors
        types.put("rnb:", "<rainbow>#input#</rainbow>");

        // GRADIENT
        // colored##grd:#5e4fa2:#f79459##not colored
        // colored##grd:#5e4fa2:#f79459:red##not colored
        // colored##grd:green:blue##not colored
        types.put("grd:", "<gradient:#arg#>#input#</gradient>");

        // TRANSITION
        // colored##trn:[color1]:[color...]:[phase]##not colored
        // colored##trn:#00ff00:#ff0000:0##not colored
        types.put("trn:", "<transition:#arg#>#input#</transition>");

        // FONT
        types.put("fnt:", "<font:#arg#>#input#</font>");


        // SELECTOR
        // Hello ##slt:@e[limit=5]##, I'm ##slt:@s##!
        types.put("slt:", "#input#<selector:#arg#>");

        // SCORE
        // ##score:_name_:_objective_##
        // You have won ##scr:rymiel:gamesWon/## games!
        types.put("scr:", "#input#<score:#arg#>");

        // NBT
        // ##nbt:block|entity|storage:id:path[:_separator_][:interpret]##
        // Your health is ##nbt:entity:'@s':Health/##
        types.put("nbt:", "#input#<nbt:#arg#>");
        return types;
    }

    private static HashMap<String, String> getDecorations(){
        HashMap<String, String> decorations = new HashMap<>();
        decorations.put("l", "b");
        decorations.put("o", "em"); // &l = <b>
        decorations.put("n", "u");
        decorations.put("m", "st");
        decorations.put("k", "obf");
        decorations.put("r", "reset");
        return decorations;
    }

    @NotNull
    private static HashMap<String, String> getColors() {
        HashMap<String, String> colors = new HashMap<>();
        colors.put("0", "black");
        colors.put("1", "dark_blue");
        colors.put("2", "dark_green");
        colors.put("3", "dark_aqua");
        colors.put("4", "dark_red");
        colors.put("5", "dark_purple");
        colors.put("6", "gold");
        colors.put("7", "gray");
        colors.put("8", "dark_gray");
        colors.put("9", "blue");
        colors.put("a", "green");
        colors.put("b", "aqua");
        colors.put("c", "red");
        colors.put("d", "light_purple");
        colors.put("e", "yellow");
        colors.put("f", "white");
        return colors;
    }
}