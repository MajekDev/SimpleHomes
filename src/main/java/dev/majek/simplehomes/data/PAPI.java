package dev.majek.simplehomes.data;

import dev.majek.simplehomes.SimpleHomes;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Handles PlaceholderAPI integration
 */
public class PAPI extends PlaceholderExpansion {

    private final SimpleHomes plugin;

    public PAPI(SimpleHomes plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean canRegister(){
        return true;
    }

    @Override
    public boolean persist(){
        return true;
    }

    @Override
    public @NotNull String getAuthor(){
        return plugin.getPluginMeta().getAuthors().get(0);
    }

    @Override
    public @NotNull String getIdentifier(){
        return plugin.getPluginMeta().getName().toLowerCase();
    }

    @Override
    public @NotNull String getVersion(){
        return plugin.getPluginMeta().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String identifier) {
        return null;
    }

    public static String applyPlaceholders(Player player, String message) {
        return PlaceholderAPI.setPlaceholders(player, message);
    }
}
