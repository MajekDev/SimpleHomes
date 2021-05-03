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
 * Handles moving a home to a new location
 */
public class CommandMoveHome implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Console cannot move homes
        if (!(sender instanceof Player)) {
            sendMessage(sender, "command.invalidSender");
            return true;
        }

        Player player = (Player) sender;
        HomesPlayer homesPlayer = Homes.getCore().getHomesPlayer(player.getUniqueId());
        String homeName = args.length > 0 ? args[0] : "home";

        // Check if the player has permission
        if (!player.hasPermission("majekhomes.movehome")) {
            sendMessage(player, "command.noPermission");
            return true;
        }

        // Make sure the home exists
        Home home = homesPlayer.getHome(homeName);
        if (home == null) {
            sendMessageWithReplacement(player, "command.movehome.invalidHome", "%name%", homeName);
            return true;
        }

        // Move home
        homesPlayer.removeHome(home);
        home.setLocation(player.getLocation());
        homesPlayer.addHome(home);
        sendMessageWithReplacement(player, "command.movehome.homeMoved", "%name%", homeName);

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
            } else
                return Collections.emptyList();
        }
        return null;
    }
}
