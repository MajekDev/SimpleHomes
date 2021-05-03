package dev.majek.homes.command;

import dev.majek.homes.Homes;
import dev.majek.homes.data.struct.Home;
import dev.majek.homes.data.struct.HomesPlayer;
import dev.majek.homes.util.TabCompleterBase;
import dev.majek.homes.util.TabExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles deleting homes
 */
public class CommandDelHome implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Console cannot delete homes
        if (!(sender instanceof Player)) {
            sendMessage(sender, "command.invalidSender");
            return true;
        }

        Player player = (Player) sender;
        HomesPlayer homesPlayer = Homes.getCore().getHomesPlayer(player.getUniqueId());

        // Player is deleting their own home
        if (args.length < 2) {
            String homeName = args.length > 0 ? args[0] : "home";

            // Check if the player has permission
            if (!player.hasPermission("majekhomes.delhome")) {
                sendMessage(player, "command.noPermission");
                return true;
            }

            // Make sure the home exists
            Home home = homesPlayer.getHome(homeName);
            if (home == null) {
                sendMessageWithReplacement(player, "command.delhome.invalidHome", "%name%", homeName);
                return true;
            }

            // Delete home
            homesPlayer.removeHome(home);
            sendMessageWithReplacement(player, "command.delhome.deleted", "%name%", homeName);
            // Save last deleted home until restart
            homesPlayer.setLastDeletedHome(home);

        }

        // Player is trying to delete someone else's home
        else {
            String homeName = args[1];
            HomesPlayer target = Homes.getCore().getHomesPlayer(args[0]);

            // Check if the player has permission
            if (!player.hasPermission("majekhomes.delhome.other")) {
                sendMessage(player, "command.noPermission");
                return true;
            }

            // Make sure the player is found
            if (target == null) {
                sendMessageWithReplacement(player, "command.unknownPlayer", "%player%", args[0]);
                return true;
            }

            // Make sure the home exists
            Home home = target.getHome(homeName);
            if (home == null) {
                sendMessageWithReplacements(player, "command.delhome.invalidHomeOther", "%name%", homeName,
                        "%player%", target.getLastSeenName());
                return true;
            }

            // Delete home
            target.removeHome(home);
            sendMessageWithReplacements(player, "command.delhome.deletedOther", "%name%", homeName,
                    "%player%", target.getLastSeenName());
            // Save last deleted home until restart
            target.setLastDeletedHome(home);

        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 1) {
                HomesPlayer homesPlayer = Homes.getCore().getHomesPlayer(player.getUniqueId());
                return TabCompleterBase.filterStartingWith(args[0], homesPlayer.getHomes().stream().map(Home::getName)
                        .collect(Collectors.toList()));
            } else if (args.length == 2 && player.hasPermission("majekhomes.delhome.other")) {
                HomesPlayer homesPlayer = Homes.getCore().getHomesPlayer(args[0]);
                if (homesPlayer != null)
                    return TabCompleterBase.filterStartingWith(args[0], homesPlayer.getHomes().stream().map(Home::getName)
                            .collect(Collectors.toList()));
                else
                    return Collections.emptyList();
            } else
                return Collections.emptyList();
        }
        return null;
    }
}
