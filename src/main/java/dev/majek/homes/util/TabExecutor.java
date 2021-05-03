package dev.majek.homes.util;

import dev.majek.homes.Homes;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public interface TabExecutor extends TabCompleter, CommandExecutor {

    @SuppressWarnings("deprecation")
    default void sendFormattedMessage(CommandSender sender, String message) {
        try {
            sender.sendMessage(Chat.parseExpression(sender, Chat.applyColorCodes(message)));
        } catch (NoSuchMethodError error1) {
            try {
                sender.spigot().sendMessage(BungeeComponentSerializer.get().serialize(Chat
                        .parseExpression(sender, Chat.applyColorCodes(message))));
            } catch (NoSuchMethodError error2) {
                sender.sendMessage(Chat.applyColorCodes(LegacyComponentSerializer.legacyAmpersand()
                        .serialize(Chat.parseExpression(sender, Chat.applyColorCodes(message)))));
            }
        }
    }

    default void sendMessage(CommandSender sender, String path) {
        String message = (Homes.getCore().getConfig().getBoolean("use-prefix") ? Homes.getCore().getLang()
                .getString("prefix") + " " : "") + Homes.getCore().getLang().getString(path);
        sendFormattedMessage(sender, message);
    }

    default void sendMessageWithReplacement(CommandSender sender, String path, String target, String replacement) {
        String message = (Homes.getCore().getConfig().getBoolean("use-prefix") ? Homes.getCore().getLang()
                .getString("prefix") + " " : "") + Homes.getCore().getLang().getString(path);
        message = message.replace(target, replacement);
        sendFormattedMessage(sender, message);
    }

    default void sendMessageWithReplacements(CommandSender sender, String path, String target1, String replacement1,
                                             String target2, String replacement2) {
        String message = (Homes.getCore().getConfig().getBoolean("use-prefix") ? Homes.getCore().getLang()
                .getString("prefix") + " " : "") + Homes.getCore().getLang().getString(path);
        message = message.replace(target1, replacement1);
        message = message.replace(target2, replacement2);
        sendFormattedMessage(sender, message);
    }
}
