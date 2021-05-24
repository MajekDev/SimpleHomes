package dev.majek.simplehomes.data;

import dev.majek.simplehomes.SimpleHomes;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Handles PlaceholderAPI integration
 */
public class PAPI extends PlaceholderExpansion {

    private final SimpleHomes plugin;
    private String yes;
    private String no;

    public PAPI(SimpleHomes plugin){
        this.plugin = plugin;
        try {
            yes = PlaceholderAPIPlugin.booleanTrue();
            no = PlaceholderAPIPlugin.booleanFalse();
        } catch (Exception err) {
            plugin.getLogger().info("Unable to hook into PAPI API for boolean results. Defaulting...");
        }
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
        return plugin.getDescription().getAuthors().get(0);
    }

    @Override
    public @NotNull String getIdentifier(){
        return plugin.getDescription().getName().toLowerCase();
    }

    @Override
    public @NotNull String getVersion(){
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String identifier) {



        return null;
    }

    public static String applyPlaceholders(Player player, String message) {
        return PlaceholderAPI.setPlaceholders(player, message);
    }

}
