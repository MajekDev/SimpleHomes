package dev.majek.simplehomes.api;

import dev.majek.simplehomes.SimpleHomes;
import dev.majek.simplehomes.data.struct.Home;
import dev.majek.simplehomes.data.struct.HomesPlayer;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the SimpleHomes API
 */
public class SimpleHomesAPI {

    /**
     * Call an api event such as {@link HomeTeleportEvent}. This is really only used internally.
     * @param event The event to call.
     */
    public void callEvent(Event event) {
        SimpleHomes.core().getServer().getPluginManager().callEvent(event);
    }

    /**
     * Perform a home lookup with the provided name. This will return a list of {@link HomesPlayer}s who have a home
     * with that name. Do not use partial names. The list may be empty but this will not return null.
     * @param name The home name for lookup.
     * @return {@link HomesPlayer}s with a home matching the name.
     */
    public List<HomesPlayer> homeLookup(String name) {
        List<HomesPlayer> owners = new ArrayList<>();
        for (HomesPlayer homesPlayer : SimpleHomes.core().getUserMap().values()) {
            Home home = homesPlayer.getHome(name);
            if (home != null)
                owners.add(homesPlayer);
        }
        return owners;
    }

    /**
     * Get a list of a {@link Player}s homes.
     * @param player The player.
     * @return List of homes.
     */
    public List<Home> getHomes(Player player) {
        return SimpleHomes.core().getHomesPlayer(player.getUniqueId()).getHomes();
    }

    /**
     * Get a list of a {@link OfflinePlayer}s homes.
     * @param player The offline player.
     * @return List of homes.
     */
    public List<Home> getHomes(OfflinePlayer player) {
        return SimpleHomes.core().getHomesPlayer(player.getUniqueId()).getHomes();
    }

    /**
     * Create a new home for a {@link Player}. This will ignore all permission checks and simply add the home.
     * @param player The player to create a home for.
     * @param name The name of the new home.
     * @param location The location of the new home.
     */
    public void createHome(Player player, String name, Location location) {
        SimpleHomes.core().getHomesPlayer(player.getUniqueId()).addHome(new Home(name, location));
    }
}
