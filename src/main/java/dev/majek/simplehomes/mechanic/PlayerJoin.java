package dev.majek.simplehomes.mechanic;

import dev.majek.simplehomes.SimpleHomes;
import dev.majek.simplehomes.data.struct.HomesPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Handles making sure new players are added to storage
 */
public class PlayerJoin implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!SimpleHomes.core().getUserMap().containsKey(event.getPlayer().getUniqueId()))
            SimpleHomes.core().addToUserMap(new HomesPlayer(event.getPlayer()));
        HomesPlayer homesPlayer = SimpleHomes.core().getHomesPlayer(event.getPlayer().getUniqueId());
        homesPlayer.setLastSeenName(event.getPlayer().getName());
    }
}
