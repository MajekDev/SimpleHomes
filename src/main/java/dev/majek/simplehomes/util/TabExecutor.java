package dev.majek.simplehomes.util;

import dev.majek.simplehomes.SimpleHomes;
import dev.majek.simplehomes.data.PAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

/**
 * Same as {@link org.bukkit.command.TabExecutor} but with some methods for message sending.
 */
public interface TabExecutor extends TabCompleter, CommandExecutor {

    @SuppressWarnings("deprecation")
    default void sendFormattedMessage(CommandSender sender, Component message) {
        try {
            sender.sendMessage(message);
        } catch (NoSuchMethodError error1) {
            try {
                sender.spigot().sendMessage(BungeeComponentSerializer.get().serialize(message));
            } catch (NoSuchMethodError error2) {
                sender.sendMessage(LegacyComponentSerializer.legacySection().serialize(message));
            }
        }
    }

    default void sendFormattedMessage(CommandSender sender, String message) {
        if (SimpleHomes.core().hasPapi && (sender instanceof Player))
            message = PAPI.applyPlaceholders((Player) sender, message);
        sendFormattedMessage(sender, MiniMessage.miniMessage().deserialize(message));
    }

    default void sendMessage(CommandSender sender, String path) {
        String message = (SimpleHomes.core().getConfig().getBoolean("use-prefix") ? SimpleHomes.core().getLang()
                .getString("prefix") + " " : "") + SimpleHomes.core().getLang().getString(path);
        sendFormattedMessage(sender, message);
    }

    default void sendMessageWithReplacement(CommandSender sender, String path, String target, String replacement) {
        String message = (SimpleHomes.core().getConfig().getBoolean("use-prefix") ? SimpleHomes.core().getLang()
                .getString("prefix") + " " : "") + SimpleHomes.core().getLang().getString(path);
        message = message.replace(target, replacement);
        sendFormattedMessage(sender, message);
    }

    default void sendMessageWithReplacements(CommandSender sender, String path, String target1, String replacement1,
                                             String target2, String replacement2) {
        String message = (SimpleHomes.core().getConfig().getBoolean("use-prefix") ? SimpleHomes.core().getLang()
                .getString("prefix") + " " : "") + SimpleHomes.core().getLang().getString(path);
        message = message.replace(target1, replacement1);
        message = message.replace(target2, replacement2);
        sendFormattedMessage(sender, message);
    }
}