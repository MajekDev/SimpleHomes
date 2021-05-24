package dev.majek.simplehomes.command;

import dev.majek.simplehomes.SimpleHomes;
import dev.majek.simplehomes.api.HomeSetEvent;
import dev.majek.simplehomes.data.struct.Home;
import dev.majek.simplehomes.data.struct.HomesPlayer;
import dev.majek.simplehomes.util.TabExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * Handles setting a new home
 */
public class CommandSetHome implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Console cannot set homes
        if (!(sender instanceof Player)) {
            sendMessage(sender, "command.invalidSender");
            return true;
        }

        Player player = (Player) sender;
        HomesPlayer homesPlayer = SimpleHomes.core().getHomesPlayer(player.getUniqueId());
        String homeName = args.length > 0 ? args[0] : "home";

        // Make sure their max homes based on permissions is up to date
        homesPlayer.updateMaxHomes(player);

        // Check if the player has permission
        if (!player.hasPermission("simplehomes.sethome")) {
            sendMessage(player, "command.noPermission");
            return true;
        }

        // Check if the user is at their home limit
        if (!homesPlayer.canAddHome()) {
            sendMessageWithReplacement(player, "command.sethome.noMoreHomes", "%limit%",
                    String.valueOf(homesPlayer.getMaxHomes()));
            return true;
        }

        // Make sure the home name is alphanumeric
        if (!homeName.matches("[a-zA-Z0-9]+")) {
            sendMessage(player, "command.sethome.nonAlphanumeric");
            return true;
        }

        // Check if the user has a home already with the same name
        if (homesPlayer.hasHome(homeName)) {
            sendMessageWithReplacement(player, "command.sethome.nameAlreadyExists", "%name%", homeName);
            return true;
        }

        // Create the home
        Home home = new Home(homeName, player.getLocation());
        HomeSetEvent event = new HomeSetEvent(player, homesPlayer, home);
        SimpleHomes.api().callEvent(event);
        if (event.isCancelled())
            return true;
        homesPlayer.addHome(home);
        sendMessageWithReplacement(player, "command.sethome.createdHome", "%name%", homeName);

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return Collections.emptyList();
    }
}
