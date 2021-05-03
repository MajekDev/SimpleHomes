package dev.majek.homes.mechanic;

import dev.majek.homes.Homes;
import dev.majek.homes.data.struct.HomesPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class PlayerRespawn implements Listener {

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (Homes.getCore().getConfig().getBoolean("respawn-home")) {
            Player player = event.getPlayer();
            HomesPlayer homesPlayer = Homes.getCore().getHomesPlayer(player.getUniqueId());
            event.setRespawnLocation(homesPlayer.getMainHome().getLocation());
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 140, 7));
            player.setFallDistance(0);
            player.setVelocity(new Vector(0, 0.3, 0));
        }
    }
}
