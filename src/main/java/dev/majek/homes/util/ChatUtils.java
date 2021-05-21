package dev.majek.homes.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUtils {

    /** List of all valid color characters */
    public static final List<Character> COLOR_CHARS = Arrays.asList('0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'l', 'm', 'n', 'o', 'r', 'x');
    /** Pattern matching "nicer" legacy hex chat color codes - &#rrggbb */
    private static final Pattern HEX_COLOR_PATTERN_SIX = Pattern.compile("&#([0-9a-fA-F]{6})");
    /** Pattern matching "nicer" 3 character hex chat color codes - &#rgb */
    private static final Pattern HEX_COLOR_PATTERN_THREE = Pattern.compile("&#([0-9a-fA-F]{3})");

    /**
     * This will translate 6 character and 3 character hex codes (&#rrggbb and &#rgb)
     * and standard minecraft color codes (ex. &a, &7, etc.) to color.
     *
     * @param string The string to colorize.
     * @param blockDarkColors Whether or not to disallow colors that are too dark.
     * @return Formatted string.
     */
    public static String applyColorCodes(String string, boolean blockDarkColors) {
        // Check if the string has no length - will throw error with matcher
        if (string.length() == 0)
            return string;

        // Do this first so it doesn't affect &0 in hex codes since they haven't been formatted
        if (blockDarkColors)
            string = string.replace("&0", "");

        // Do 6 char first since the 3 char pattern will also match 6 char occurrences
        StringBuilder sb6 = new StringBuilder();
        Matcher matcher6 = HEX_COLOR_PATTERN_SIX.matcher(string);
        while (matcher6.find()) {
            StringBuilder replacement = new StringBuilder(14).append("&x");
            for (char character : matcher6.group(1).toCharArray())
                replacement.append('&').append(character);
            if (blockDarkColors && getLuminescence(replacement.toString()) < 16)
                matcher6.appendReplacement(sb6, "");
            else
                matcher6.appendReplacement(sb6, replacement.toString());
        }
        matcher6.appendTail(sb6);
        string = sb6.toString();

        // Now convert 3 char to the same format ex. &#363 -> &x&3&3&6&6&3&3
        StringBuilder sb3 = new StringBuilder();
        Matcher matcher3 = HEX_COLOR_PATTERN_THREE.matcher(string);
        while (matcher3.find()) {
            StringBuilder replacement = new StringBuilder(14).append("&x");
            for (char character : matcher3.group(1).toCharArray())
                replacement.append('&').append(character).append('&').append(character);
            if (blockDarkColors && getLuminescence(replacement.toString()) < 16)
                matcher3.appendReplacement(sb3, "");
            else
                matcher3.appendReplacement(sb3, replacement.toString());
        }
        matcher3.appendTail(sb3);

        // Translate '&' to '§' (all standard color codes done here)
        return ChatColor.translateAlternateColorCodes('&', sb3.toString());
    }

    /**
     * Apply color codes using {@link ChatUtils#applyColorCodes(String, boolean)}
     * and defaulting blockDarkColors to false.
     *
     * @param string The string to colorize.
     * @return Formatted string.
     */
    public static String applyColorCodes(String string) {
        return applyColorCodes(string, false);
    }

    /**
     * Completely remove all color codes from a string.
     * This will strip all standard codes and hex codes.
     *
     * @param string The string to remove colors from.
     * @return Clean string.
     */
    public static String removeColorCodes(String string) {
        // Colorize it first to properly strip hex codes
        string = applyColorCodes(string);
        StringBuilder sb = new StringBuilder(string.length());
        char[] chars = string.toCharArray();
        for (int i = 0; i < chars.length; ++i) {
            if (chars[i] == '&' || chars[i] == ChatColor.COLOR_CHAR &&
                    i < chars.length - 1 && COLOR_CHARS.contains(chars[i + 1])) {
                ++i;
                continue;
            }
            sb.append(chars[i]);
        }
        return sb.toString();
    }

    /**
     * Get luminescence of a formatted hex color - "&x&r&r&g&g&b&b".
     *
     * @param color The formatted hex string.
     * @return Luminescence value 0-255.
     */
    public static double getLuminescence(String color) {
        color = color.replace("&", "").replace("x", "");
        int red   = Integer.valueOf(color.substring(0,2), 16);
        int green = Integer.valueOf(color.substring(2,4), 16);
        int blue  = Integer.valueOf(color.substring(4,6), 16);
        return (0.2126 * red) + (0.7152 * green) + (0.0722 * blue);
    }

    /**
     * Get only the color codes from a given string. This includes hex codes.
     *
     * @param string The string to remove color codes from.
     * @return String of only color codes.
     */
    public static String getColorCodes(String string) {
        // Colorize it first to properly get hex codes
        string = applyColorCodes(string);
        string = string.replace("§", "&");
        StringBuilder sb = new StringBuilder(string.length());
        char[] chars = string.toCharArray();
        for (int i = 0; i < chars.length; ++i) {
            if (chars[i] == '&' || chars[i] == ChatColor.COLOR_CHAR &&
                    i < chars.length - 1 && COLOR_CHARS.contains(chars[i + 1])) {
                sb.append(chars[i]).append(chars[i+1]);
                ++i;
            }
        }
        return sb.toString();
    }

    /**
     * Get a {@link Component} from a provided string.
     * Note: If you want the component to contain color then use {@link ChatUtils#applyColorCodes} first.
     *
     * @param text The string.
     * @return {@link Component}
     */
    public static Component getComponentFromString(String text) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
    }
}
