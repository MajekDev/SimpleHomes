package dev.majek.homes.mechanic;

import dev.majek.homes.Homes;
import dev.majek.homes.data.struct.Bar;
import dev.majek.homes.data.struct.HomesPlayer;
import dev.majek.homes.util.ChatUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMove implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // This checks to see if the player actually moved or just moved their head
        if (event.getTo().getBlockX() == event.getFrom().getBlockX() && event.getTo().getBlockY() ==
                event.getFrom().getBlockY() && event.getTo().getBlockZ() == event.getFrom().getBlockZ()) return;

        Player player = event.getPlayer();
        HomesPlayer homesPlayer = Homes.getCore().getHomesPlayer(player.getUniqueId());

        if (homesPlayer.cannotMove()) {
            homesPlayer.setNoMove(false);
            if (Homes.getCore().getConfig().getBoolean("use-boss-bar")) {
                Bar bar = homesPlayer.getBossBar();
                bar.removePlayer(player);
                bar.removeBar();
            }
            Homes.getCore().getServer().getScheduler().cancelTask(homesPlayer.getBossBarTaskID());
            String message = Homes.getCore().getLang().getString("command.home.teleportationCancelled", "null");
            if (Homes.getCore().getConfig().getBoolean("use-prefix"))
                message = Homes.getCore().getLang().getString("prefix") + " " + message;
            player.sendMessage(ChatUtils.applyColorCodes(message));
        }
    }
}
