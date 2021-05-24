package dev.majek.simplehomes.mechanic;

import dev.majek.simplehomes.SimpleHomes;
import dev.majek.simplehomes.data.struct.Bar;
import dev.majek.simplehomes.data.struct.HomesPlayer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Handles players remaining still on teleport
 */
public class PlayerMove implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // This checks to see if the player actually moved or just moved their head
        if (event.getTo().getBlockX() == event.getFrom().getBlockX() && event.getTo().getBlockY() ==
                event.getFrom().getBlockY() && event.getTo().getBlockZ() == event.getFrom().getBlockZ()) return;

        Player player = event.getPlayer();
        HomesPlayer homesPlayer = SimpleHomes.core().getHomesPlayer(player.getUniqueId());

        if (homesPlayer.cannotMove()) {
            homesPlayer.setNoMove(false);
            if (SimpleHomes.core().getConfig().getBoolean("use-boss-bar")) {
                Bar bar = homesPlayer.getBossBar();
                bar.removePlayer(player);
                bar.removeBar();
            }
            SimpleHomes.core().getServer().getScheduler().cancelTask(homesPlayer.getBossBarTaskID());
            String message = SimpleHomes.core().getLang().getString("command.home.teleportationCancelled", "null");
            if (SimpleHomes.core().getConfig().getBoolean("use-prefix"))
                message = SimpleHomes.core().getLang().getString("prefix") + " " + message;
            player.sendMessage(MiniMessage.get().parse(message));
        }
    }
}
