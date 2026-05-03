package dev.majek.simplehomes.mechanic;

import dev.majek.simplehomes.SimpleHomes;
import dev.majek.simplehomes.data.struct.HomesPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 * Handles teleporting players to home on respawn
 */
public class PlayerRespawn implements Listener {

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (SimpleHomes.core().getConfig().getBoolean("respawn-home")) {
            Player player = event.getPlayer();
            HomesPlayer homesPlayer = SimpleHomes.core().getHomesPlayer(player.getUniqueId());
            event.setRespawnLocation(homesPlayer.getMainHome().location());
            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 140, 7));
            player.setFallDistance(0);
            player.setVelocity(new Vector(0, 0.3, 0));
        }
    }
}
