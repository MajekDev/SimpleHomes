package dev.majek.homes.command;

import dev.majek.homes.Homes;
import dev.majek.homes.util.TabCompleterBase;
import dev.majek.homes.util.TabExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

/**
 * Handles help and reloading
 */
public class CommandMajekHomes implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("help")) {
                for (String helpMsg : Homes.getCore().getLang().getStringList("command.help.help"))
                    sendFormattedMessage(sender, helpMsg);
                return true;
            }
            else if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("majekhomes.reload")) {
                    Homes.getCore().reload();
                    sendMessage(sender, "command.reloaded");
                } else {
                    sendMessage(sender, "command.noPermission");
                }
                return true;
            }
        } else {
            for (String helpMsg : Homes.getCore().getLang().getStringList("command.help.version"))
                sendFormattedMessage(sender, helpMsg.replace("%version%", Homes.getCore().getDescription().getVersion()));
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return TabCompleterBase.filterStartingWith(args[0], Arrays.asList("help", "reload"));
    }
}
