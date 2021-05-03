package dev.majek.homes.command;

import dev.majek.homes.Homes;
import dev.majek.homes.data.struct.Home;
import dev.majek.homes.data.struct.HomesPlayer;
import dev.majek.homes.util.TabExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * Handles recovering a deleted home
 */
public class CommandUndoDelHome implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Console cannot delete homes
        if (!(sender instanceof Player)) {
            sendMessage(sender, "command.invalidSender");
            return true;
        }

        Player player = (Player) sender;
        HomesPlayer homesPlayer = Homes.getCore().getHomesPlayer(player.getUniqueId());
        Home deletedHome = homesPlayer.getLastDeletedHome();

        // Check if the player has permission
        if (!player.hasPermission("majekhomes.undodelhome")) {
            sendMessage(player, "command.noPermission");
            return true;
        }

        // Make sure there is a recently deleted home stored
        if (deletedHome == null) {
            sendMessage(player, "command.undodelhome.noHome");
            return true;
        }

        // Recover home
        homesPlayer.addHome(deletedHome);
        sendMessageWithReplacement(player, "command.undodelhome.recoveredHome", "%name%", deletedHome.getName());
        homesPlayer.setLastDeletedHome(null);

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return Collections.emptyList();
    }
}
