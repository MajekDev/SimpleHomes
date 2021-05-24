package dev.majek.simplehomes.command;

import dev.majek.simplehomes.SimpleHomes;
import dev.majek.simplehomes.api.HomeTeleportEvent;
import dev.majek.simplehomes.data.struct.Bar;
import dev.majek.simplehomes.data.struct.Home;
import dev.majek.simplehomes.data.struct.HomesPlayer;
import dev.majek.simplehomes.util.TabCompleterBase;
import dev.majek.simplehomes.util.TabExecutor;
import net.kyori.adventure.text.minimessage.MiniMessage;
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
        HomesPlayer homesPlayer = SimpleHomes.core().getHomesPlayer(player.getUniqueId());

        // Player is teleporting to their own home
        if (args.length < 2) {
            String homeName = args.length > 0 ? args[0] : "home";

            // Check if the player has permission
            if (!player.hasPermission("simplehomes.home")) {
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
            int tpDelay = SimpleHomes.core().getConfig().getInt("teleport-delay");
            HomeTeleportEvent tpEvent = new HomeTeleportEvent(player, homesPlayer, home, player.getLocation(),
                    true, tpDelay, true);
            SimpleHomes.api().callEvent(tpEvent);
            if (tpEvent.isCancelled())
                return true;

            if (player.hasPermission("simplehomes.delay.bypass") || tpEvent.teleportDelay() <= 0 || tpEvent.noTeleportDelay()) {
                SimpleHomes.core().safeTeleportPlayer(player, tpEvent.teleportingTo());
                sendMessageWithReplacement(player, "command.home.teleportedHome", "%name%", homeName);
            } else {
                sendMessageWithReplacement(player, "command.home.warmup", "%time%", String.valueOf(tpDelay));

                // Boss bar shizzle
                Bar bar = new Bar(SimpleHomes.core());
                if (SimpleHomes.core().getConfig().getBoolean("use-boss-bar")) {
                    homesPlayer.setBossBar(bar);
                    bar.createBar(tpDelay, MiniMessage.get().parse(SimpleHomes.core().getLang().getString("teleportBar", "null")));
                    bar.addPlayer(player);
                }

                homesPlayer.setNoMove(true);
                int taskID = SimpleHomes.core().getServer().getScheduler().runTaskLater(SimpleHomes.core(), () -> {
                    // Don't do this if they did move
                    if (homesPlayer.cannotMove()) {
                        SimpleHomes.core().safeTeleportPlayer(player, tpEvent.teleportingTo());
                        if (SimpleHomes.core().getConfig().getBoolean("use-boss-bar")) {
                            bar.removePlayer(player);
                            homesPlayer.setBossBar(null);
                        }
                        sendMessageWithReplacement(player, "command.home.teleportedHome", "%name%", homeName);
                        homesPlayer.setNoMove(false);
                    }
                }, tpEvent.teleportDelay() * 20L).getTaskId();
                homesPlayer.setBossBarTaskID(taskID);
            }
        }

        // Player is trying to teleport to someone else's home
        else {
            String homeName = args[1];
            HomesPlayer target = SimpleHomes.core().getHomesPlayer(args[0]);

            // Check if the player has permission
            if (!player.hasPermission("simplehomes.home.other")) {
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

            // Teleport the player to target's home with delay if necessary
            int tpDelay = SimpleHomes.core().getConfig().getInt("teleport-delay");
            HomeTeleportEvent tpEvent = new HomeTeleportEvent(player, target, home, player.getLocation(),
                    true, tpDelay, false);
            SimpleHomes.api().callEvent(tpEvent);
            if (tpEvent.isCancelled())
                return true;

            if (player.hasPermission("simplehomes.delay.bypass") || tpEvent.teleportDelay() <= 0 || tpEvent.noTeleportDelay()) {
                SimpleHomes.core().safeTeleportPlayer(player, tpEvent.teleportingTo());
                sendMessageWithReplacements(player, "command.home.teleportedOther", "%name%", homeName,
                        "%player%", target.getLastSeenName());
            } else {
                sendMessageWithReplacement(player, "command.home.warmup", "%time%", String.valueOf(tpDelay));
                homesPlayer.setNoMove(true);

                // Boss bar shizzle
                Bar bar = new Bar(SimpleHomes.core());
                if (SimpleHomes.core().getConfig().getBoolean("use-boss-bar")) {
                    homesPlayer.setBossBar(bar);
                    bar.createBar(tpDelay, MiniMessage.get().parse(SimpleHomes.core().getLang().getString("teleportBar", "null")));
                    bar.addPlayer(player);
                }

                int taskID = SimpleHomes.core().getServer().getScheduler().runTaskLater(SimpleHomes.core(), () -> {
                    // Don't do this if they did move
                    if (homesPlayer.cannotMove()) {
                        SimpleHomes.core().safeTeleportPlayer(player, tpEvent.teleportingTo());
                        if (SimpleHomes.core().getConfig().getBoolean("use-boss-bar")) {
                            bar.removePlayer(player);
                            homesPlayer.setBossBar(null);
                        }
                        sendMessageWithReplacements(player, "command.home.teleportedOther", "%name%", homeName,
                                "%player%", target.getLastSeenName());
                        homesPlayer.setNoMove(false);
                    }
                }, tpEvent.teleportDelay() * 20L).getTaskId();
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
                HomesPlayer homesPlayer = SimpleHomes.core().getHomesPlayer(player.getUniqueId());
                return TabCompleterBase.filterStartingWith(args[0], homesPlayer.getHomes().stream().map(Home::name)
                        .collect(Collectors.toList()));
            } else if (args.length == 2 && player.hasPermission("simplehomes.homes.others")) {
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
