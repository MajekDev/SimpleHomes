package dev.majek.homes.mechanic;

import dev.majek.homes.Homes;
import dev.majek.homes.data.struct.HomesPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!Homes.getCore().getUserMap().containsKey(event.getPlayer().getUniqueId()))
            Homes.getCore().addToUserMap(new HomesPlayer(event.getPlayer()));
        HomesPlayer homesPlayer = Homes.getCore().getHomesPlayer(event.getPlayer().getUniqueId());
        homesPlayer.setLastSeenName(event.getPlayer().getName());
    }
}
