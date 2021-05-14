package dev.majek.homes.command;

import dev.majek.homes.Homes;
import dev.majek.homes.data.struct.Bar;
import dev.majek.homes.data.struct.Home;
import dev.majek.homes.data.struct.HomesPlayer;
import dev.majek.homes.util.Chat;
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
 * Handles traveling to homes
 */
public class CommandHome implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Console cannot go to homes
        if (!(sender instanceof Player)) {
            sendMessage(sender, "command.invalidSender");
            return true;
        }

        Player player = (Player) sender;
        HomesPlayer homesPlayer = Homes.getCore().getHomesPlayer(player.getUniqueId());

        // Player is teleporting to their own home
        if (args.length < 2) {
            String homeName = args.length > 0 ? args[0] : "home";

            // Check if the player has permission
            if (!player.hasPermission("majekhomes.home")) {
                sendMessage(player, "command.noPermission");
                return true;
            }

            // Check if the player just ran the command
            if (homesPlayer.cannotMove()) {
                sendMessage(player, "command.home.wait");
                return true;
            }

            // Check if the player has no homes
            if (homesPlayer.getTotalHomes() == 0) {
                sendMessage(player, "command.home.noHomes");
                return true;
            }

            // Make sure the home exists
            Home home = homesPlayer.getHome(homeName);
            if (home == null) {
                sendMessageWithReplacement(player, "command.home.invalidHome", "%name%", homeName);
                return true;
            }

            // Teleport the player to their home with delay if necessary
            int tpDelay= Homes.getCore().getConfig().getInt("teleport-delay");
            if (player.hasPermission("majekhomes.delay.bypass") || tpDelay <= 0) {
                Homes.safeTeleportPlayer(player, home.getLocation());
                sendMessageWithReplacement(player, "command.home.teleportedHome", "%name%", homeName);
            } else {
                sendMessageWithReplacement(player, "command.home.warmup", "%time%", String.valueOf(tpDelay));

                // Boss bar shizzle
                Bar bar = new Bar(Homes.getCore());
                if (Homes.getCore().getConfig().getBoolean("use-boss-bar")) {
                    homesPlayer.setBossBar(bar);
                    bar.createBar(tpDelay, Chat.applyColorCodes(Homes.getCore().getLang().getString("teleport-bar")));
                    bar.addPlayer(player);
                }

                homesPlayer.setNoMove(true);
                int taskID = Homes.getCore().getServer().getScheduler().runTaskLater(Homes.getCore(), () -> {
                    // Don't do this if they did move
                    if (homesPlayer.cannotMove()) {
                        Homes.safeTeleportPlayer(player, home.getLocation());
                        if (Homes.getCore().getConfig().getBoolean("use-boss-bar")) {
                            bar.removePlayer(player);
                            homesPlayer.setBossBar(null);
                        }
                        sendMessageWithReplacement(player, "command.home.teleportedHome", "%name%", homeName);
                        homesPlayer.setNoMove(false);
                    }
                }, tpDelay * 20L).getTaskId();
                homesPlayer.setBossBarTaskID(taskID);
            }
        }

        // Player is trying to teleport to someone else's home
        else {
            String homeName = args[1];
            HomesPlayer target = Homes.getCore().getHomesPlayer(args[0]);

            // Check if the player has permission
            if (!player.hasPermission("majekhomes.home.other")) {
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
                sendMessageWithReplacements(player, "command.home.invalidHomeOther", "%name%", homeName,
                        "%player%", target.getLastSeenName());
                return true;
            }

            // Teleport the player to their home with delay if necessary
            int tpDelay= Homes.getCore().getConfig().getInt("teleport-delay");
            if (player.hasPermission("majekhomes.delay.bypass") || tpDelay <= 0) {
                Homes.safeTeleportPlayer(player, home.getLocation());
                sendMessageWithReplacements(player, "command.home.teleportedOther", "%name%", homeName,
                        "%player%", target.getLastSeenName());
            } else {
                sendMessageWithReplacement(player, "command.home.warmup", "%time%", String.valueOf(tpDelay));
                homesPlayer.setNoMove(true);

                // Boss bar shizzle
                Bar bar = new Bar(Homes.getCore());
                if (Homes.getCore().getConfig().getBoolean("use-boss-bar")) {
                    homesPlayer.setBossBar(bar);
                    bar.createBar(tpDelay, Chat.applyColorCodes(Homes.getCore().getLang().getString("teleport-bar")));
                    bar.addPlayer(player);
                }

                int taskID = Homes.getCore().getServer().getScheduler().runTaskLater(Homes.getCore(), () -> {
                    // Don't do this if they did move
                    if (homesPlayer.cannotMove()) {
                        Homes.safeTeleportPlayer(player, home.getLocation());
                        if (Homes.getCore().getConfig().getBoolean("use-boss-bar")) {
                            bar.removePlayer(player);
                            homesPlayer.setBossBar(null);
                        }
                        sendMessageWithReplacements(player, "command.home.teleportedOther", "%name%", homeName,
                                "%player%", target.getLastSeenName());
                        homesPlayer.setNoMove(false);
                    }
                }, tpDelay * 20L).getTaskId();
                homesPlayer.setBossBarTaskID(taskID);
            }
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
            } else if (args.length == 2 && player.hasPermission("majekhomes.homes.others")) {
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
