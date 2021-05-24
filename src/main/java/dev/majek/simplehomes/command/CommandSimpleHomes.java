package dev.majek.simplehomes.command;

import dev.majek.simplehomes.SimpleHomes;
import dev.majek.simplehomes.util.TabCompleterBase;
import dev.majek.simplehomes.util.TabExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

/**
 * Handles help and reloading
 */
public class CommandSimpleHomes implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("help")) {
                for (String helpMsg : SimpleHomes.core().getLang().getStringList("command.help.help"))
                    sendFormattedMessage(sender, helpMsg);
                return true;
            }
            else if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("simplehomes.reload")) {
                    SimpleHomes.core().reload();
                    sendMessage(sender, "command.reloaded");
                } else {
                    sendMessage(sender, "command.noPermission");
                }
                return true;
            }
        } else {
            for (String helpMsg : SimpleHomes.core().getLang().getStringList("command.help.version"))
                sendFormattedMessage(sender, helpMsg.replace("%version%", SimpleHomes.core().getDescription().getVersion()));
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return TabCompleterBase.filterStartingWith(args[0], Arrays.asList("help", "reload"));
    }
}
