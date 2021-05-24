package dev.majek.simplehomes.command;

import dev.majek.simplehomes.SimpleHomes;
import dev.majek.simplehomes.api.HomeDeleteEvent;
import dev.majek.simplehomes.data.struct.Home;
import dev.majek.simplehomes.data.struct.HomesPlayer;
import dev.majek.simplehomes.util.TabCompleterBase;
import dev.majek.simplehomes.util.TabExecutor;
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
        HomesPlayer homesPlayer = SimpleHomes.core().getHomesPlayer(player.getUniqueId());

        // Player is deleting their own home
        if (args.length < 2) {
            String homeName = args.length > 0 ? args[0] : "home";

            // Check if the player has permission
            if (!player.hasPermission("simplehomes.delhome")) {
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
            HomeDeleteEvent event = new HomeDeleteEvent(player, homesPlayer, home, true);
            SimpleHomes.api().callEvent(event);
            if (event.isCancelled())
                return true;
            homesPlayer.removeHome(home);
            sendMessageWithReplacement(player, "command.delhome.deleted", "%name%", homeName);
        }

        // Player is trying to delete someone else's home
        else {
            String homeName = args[1];
            HomesPlayer target = SimpleHomes.core().getHomesPlayer(args[0]);

            // Check if the player has permission
            if (!player.hasPermission("simplehomes.delhome.other")) {
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
            HomeDeleteEvent event = new HomeDeleteEvent(player, target, home, false);
            SimpleHomes.api().callEvent(event);
            if (event.isCancelled())
                return true;
            target.removeHome(home);
            sendMessageWithReplacements(player, "command.delhome.deletedOther", "%name%", homeName,
                    "%player%", target.getLastSeenName());
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 1) {
                HomesPlayer homesPlayer = SimpleHomes.core().getHomesPlayer(player.getUniqueId());
                return TabCompleterBase.filterStartingWith(args[0], homesPlayer.getHomes().stream().map(Home::name)
                        .collect(Collectors.toList()));
            } else if (args.length == 2 && player.hasPermission("simplehomes.delhome.other")) {
                HomesPlayer homesPlayer = SimpleHomes.core().getHomesPlayer(args[0]);
                if (homesPlayer != null)
                    return TabCompleterBase.filterStartingWith(args[1], homesPlayer.getHomes().stream().map(Home::name)
                            .collect(Collectors.toList()));
                else
                    return Collections.emptyList();
            } else
                return Collections.emptyList();
        }
        return null;
    }
}
