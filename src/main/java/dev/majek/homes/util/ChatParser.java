package dev.majek.homes.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatParser {

    private final TextComponent.Builder builder;
    private String previousColors;
    private final List<String> validFunctions;

    public ChatParser() {
        builder = Component.empty().toBuilder();
        previousColors = null;
        validFunctions = new ArrayList<>(Arrays.asList("hover", "hover-text", "command", "run-command", "clipboard",
                "suggest-command", "link", "url", "open-url", "copy-to-clipboard", "hover-command", "hover-suggest"));
    }

    /**
     * Parse the provided string starting at the provided start location. This will find all functions and format
     * them into {@link Component}s. If there is an error parsing the text this will throw {@link ParseException}.
     *
     * @param string The string containing the functions to parse.
     * @param start The location to start parsing in the string.
     * @return Instance of itself. Use {@link ChatParser#getAsComponent()}, {@link ChatParser#getAsBaseComponent()}, or
     * {@link ChatParser#getAsRawString()} to finish the process.
     */
    public ChatParser parse(String string, int start) {
        for (int i = start; i < string.length(); i++) {

            // Check for function char
            if (string.charAt(i) == '$') {
                // Avoid index out of bounds exceptions
                if (i != 0) {
                    // Check if the function is escaped
                    if (string.charAt(i - 1) == '\\')
                        continue;
                }
                if (i == string.length() - 1)
                    break;

                // Check for just a dollar sign by itself
                if (string.charAt(i + 1) != '{')
                    continue;

                // Get the function text within the brackets
                Pair<String, Integer> functionPair = getEnclosed(i + 1, string);
                String functionText = functionPair.getFirst();

                // Error in syntax
                if (functionText == null)
                    throw new ParseException("Invalid syntax in function at index " + i);

                // Add previous section before function to builder
                builder.append(getBuilderFromString((previousColors == null ? "" : previousColors) + string
                        .substring(start, i).replace("\\", "")));

                // Get colors from previous section before function to carry over beyond the function
                previousColors = ChatUtils.getColorCodes(string.substring(start, i));

                // Get the function's arguments
                List<String> args = new ArrayList<>();
                StringBuilder argBuilder = new StringBuilder();
                for (int j = 0; j < functionText.length(); j++) {
                    if (j != 0 && functionText.charAt(j) == ',' && functionText.charAt(j - 1) != '\\') {
                        args.add(argBuilder.toString());
                        argBuilder.setLength(0); // Clear string builder
                    } else
                        argBuilder.append(functionText.charAt(j));
                }
                args.add(argBuilder.toString());

                // Check for sufficient arguments
                if (args.size() < 3)
                    throw new ParseException("Insufficient arguments in function near index " + i);

                // Make sure the function is valid
                if (!validFunctions.contains(args.get(0)))
                    throw new ParseException("Unknown function: " + args.get(0));

                // Try to parse the function
                TextComponent.Builder newComponent = getBuilderFromString(args.get(1).replace("\\", ""));
                try {
                    String function = args.get(0);
                    String functionTodo = args.get(2).replace("\\", "");
                    switch (function) {
                        case "hover":
                        case "hover-text":
                            newComponent.hoverEvent(HoverEvent.showText(getBuilderFromString(functionTodo)));
                            break;
                        case "command":
                        case "run-command":
                            newComponent.clickEvent(ClickEvent.runCommand(functionTodo));
                            break;
                        case "suggest-command":
                            newComponent.clickEvent(ClickEvent.suggestCommand(functionTodo));
                            break;
                        case "link":
                        case "url":
                        case "open-url":
                            newComponent.clickEvent(ClickEvent.openUrl(functionTodo));
                            break;
                        case "clipboard":
                        case "copy-to-clipboard":
                            newComponent.clickEvent(ClickEvent.copyToClipboard(functionTodo));
                            break;
                        case "hover-command":
                            // Check for sufficient arguments
                            if (args.size() < 4)
                                throw new ParseException("Insufficient arguments in hover-command function near index " + i);
                            newComponent.hoverEvent(HoverEvent.showText(getBuilderFromString(functionTodo)));
                            newComponent.clickEvent(ClickEvent.runCommand(args.get(3)));
                            break;
                        case "hover-suggest":
                            // Check for sufficient arguments
                            if (args.size() < 4)
                                throw new ParseException("Insufficient arguments in hover-suggest function near index " + i);
                            newComponent.hoverEvent(HoverEvent.showText(getBuilderFromString(functionTodo)));
                            newComponent.clickEvent(ClickEvent.suggestCommand(args.get(3)));
                    }
                } catch (Exception exception) {
                    throw new ParseException("Invalid arguments in function near index " + i);
                }

                // Add newly parsed component to builder
                builder.append(newComponent);

                // Continue parsing past the parsed function
                parse(string, functionPair.getSecond());
                return this;
            }
        }
        builder.append(getBuilderFromString((previousColors == null ? "" : previousColors)
                    + string.substring(start).replace("\\", "")));
        return this;
    }

    /**
     * Parse the provided string starting at the beginning. This will find all functions and format
     * them into {@link Component}s. If there is an error parsing the text this will throw {@link ParseException}.
     *
     * @param string The string containing the functions to parse.
     * @return Instance of itself. Use {@link ChatParser#getAsComponent()}, {@link ChatParser#getAsBaseComponent()}, or
     * {@link ChatParser#getAsRawString()} to finish the process.
     */
    public ChatParser parse(String string) {
        return parse(string, 0);
    }

    /**
     * Parse the provided strings starting at the beginning. This will find all functions and format
     * them into {@link Component}s. If there is an error parsing the text this will throw {@link ParseException}.
     *
     * @param strings These strings will be joined using the provided delimiter.
     * @param delimiter The delimiter to join the string with.
     * @return Instance of itself. Use {@link ChatParser#getAsComponent()}, {@link ChatParser#getAsBaseComponent()}, or
     * {@link ChatParser#getAsRawString()} to finish the process.
     */
    public ChatParser parse(String delimiter, String... strings) {
        return parse(String.join(delimiter, strings));
    }

    /**
     * Clear all click events from the functions. If you want to clear all click events and hover events you
     * should be using {@link ChatParser#getAsRawString()}.
     *
     * @return Instance of itself. Use {@link ChatParser#getAsComponent()}, {@link ChatParser#getAsBaseComponent()}, or
     * {@link ChatParser#getAsRawString()} to finish the process.
     */
    public ChatParser clearClickEvents() {
        builder.applyDeep(componentBuilder -> componentBuilder.clickEvent(null));
        return this;
    }

    /**
     * Clear all hover events from the functions. If you want to clear all click events and hover events you
     * should be using {@link ChatParser#getAsRawString()}.
     *
     * @return Instance of itself. Use {@link ChatParser#getAsComponent()}, {@link ChatParser#getAsBaseComponent()}, or
     * {@link ChatParser#getAsRawString()} to finish the process.
     */
    public ChatParser clearHoverEvents() {
        builder.applyDeep(componentBuilder -> componentBuilder.hoverEvent(null));
        return this;
    }

    /**
     * Clear all colors from the parsed string.
     *
     * @return Instance of itself. Use {@link ChatParser#getAsComponent()}, {@link ChatParser#getAsBaseComponent()}, or
     * {@link ChatParser#getAsRawString()} to finish the process.
     */
    public ChatParser clearColors() {
        builder.applyDeep(componentBuilder -> componentBuilder.color(null));
        return this;
    }

    /**
     * Get the parsed string as an array of {@link BaseComponent}s.
     *
     * @return {@link BaseComponent} array of the parsed string.
     */
    public BaseComponent[] getAsBaseComponent() {
        return BungeeComponentSerializer.legacy().serialize(builder.asComponent());
    }

    /**
     * Get the parsed string as a {@link Component}.
     *
     * @return {@link Component} of the parsed string.
     */
    public Component getAsComponent() {
        return builder.asComponent();
    }

    /**
     * Get the parsed string as a raw string. This will remove all hover and click events but retain colors.
     * If you want to clear colors as well call {@link ChatParser#clearColors()} before this.
     *
     * @return Raw string of parsed string.
     */
    public String getAsRawString() {
        return LegacyComponentSerializer.legacyAmpersand().serialize(builder.asComponent());
    }

    /**
     * Thrown if there is an error parsing a function.
     */
    public static class ParseException extends RuntimeException {
        public ParseException(String exception) {
            super(exception);
        }
    }


    /*
     * ----- Private methods for parsing only -----
     */

    /**
     * Get a {@link TextComponent.Builder} formatted with color from a String containing
     * standard Minecraft color codes and hex codes (&#aabbcc).
     *
     * @param text The string with standard codes.
     * @return {@link TextComponent.Builder} formatted with color.
     */
    private TextComponent.Builder getBuilderFromString(String text) {
        text = ChatUtils.applyColorCodes(text).replace("§", "&");
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text).toBuilder();
    }

    /**
     * Get a {@link Component} formatted with color from a String containing
     * standard Minecraft color codes and hex codes (&#aabbcc).
     *
     * @param text The string with standard codes.
     * @return {@link TextComponent.Builder} formatted with color.
     */
    private Component getComponentFromString(String text) {
        text = ChatUtils.applyColorCodes(text).replace("§", "&");
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
    }

    /**
     * Using this to pull JSON Objects and brackets plus text inside from strings. If the end of the string is
     * encountered before the bracket is closed off, then {null, -1} is returned.
     *
     * @param start  the start index.
     * @param string the string to get the enclosed value.
     * @return a pair, where the first value is the enclosed string and the second value is the index of the character
     * after the closing bracket of the enclosed string.
     */
    private Pair<String, Integer> getEnclosed(int start, String string) {
        boolean curved = string.charAt(start) == '('; // ()s or {}s
        int depth = 1, i = start + 1;

        // Exits when there are no pairs of open brackets
        while (depth > 0) {
            // Avoid index out of bound errors
            if (i == string.length())
                return new Pair<>(null, -1);

            char c = string.charAt(i++);

            // Ignore escaped characters
            if (c == '\\')
                i++;

            // We've closed off a pair
            if (c == (curved ? ')' : '}'))
                --depth;
                // We've started a pair
            else if (c == (curved ? '(' : '{'))
                ++depth;
        }
        // Return the stuff inside the brackets, and the index of the char after the last bracket
        return new Pair<>(string.substring(start + 1, i - 1), i);
    }
}