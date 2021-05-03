package dev.majek.homes;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import dev.majek.homes.command.*;
import dev.majek.homes.data.*;
import dev.majek.homes.data.struct.HomesPlayer;
import dev.majek.homes.mechanic.PlayerJoin;
import dev.majek.homes.mechanic.PlayerMove;
import dev.majek.homes.mechanic.PlayerRespawn;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class Homes extends JavaPlugin {

    private static Homes instance;
    private final Map<UUID, HomesPlayer> userMap;
    public FileConfiguration lang;
    public static boolean hasPapi = false;

    public Homes() {
        instance = this;
        this.userMap = new HashMap<>();
    }

    @Override
    public void onEnable() {

        // Update config.yml and lang.yml
        reload();

        // Load player data
        File folder = new File(getDataFolder() + File.separator + "playerdata");
        if (!folder.exists())
            if (folder.mkdirs())
                getLogger().info("Player data folder created.");
            else
                getLogger().severe("Unable to create player data folder.");
        if (folder.listFiles() != null) {
            for (File file : Objects.requireNonNull(folder.listFiles())) {
                JSONConfig dataStorage = new JSONConfig(folder, file.getName());
                JsonObject fileContents;
                try {
                    fileContents = dataStorage.toJsonObject();
                } catch (IOException | JsonParseException e) {
                    Homes.getCore().getLogger().severe("Critical error loading player data from "
                            + dataStorage.getFile().getName());
                    e.printStackTrace();
                    continue;
                }
                addToUserMap(new HomesPlayer(dataStorage, fileContents));
            }
        }

        // Hook into PAPI if it's enabled
        if (this.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI") &&
                this.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            getLogger().info("Hooking into PlaceholderAPI...");
            new PAPI(this).register();
            hasPapi = true;
        }

        // Metrics
        new Metrics(this, 11237);

        // Set command executors and tab completers
        registerCommands();

        // Register events
        getServer().getPluginManager().registerEvents(new PlayerJoin(), this);
        getServer().getPluginManager().registerEvents(new PlayerMove(), this);
        getServer().getPluginManager().registerEvents(new PlayerRespawn(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @SuppressWarnings("ConstantConditions")
    public void registerCommands() {
        getCommand("home").setExecutor(new CommandHome());
        getCommand("home").setTabCompleter(new CommandHome());
        getCommand("homes").setExecutor(new CommandHomes());
        getCommand("homes").setTabCompleter(new CommandHomes());
        getCommand("sethome").setExecutor(new CommandSetHome());
        getCommand("sethome").setTabCompleter(new CommandSetHome());
        getCommand("delhome").setExecutor(new CommandDelHome());
        getCommand("delhome").setTabCompleter(new CommandDelHome());
        getCommand("movehome").setExecutor(new CommandMoveHome());
        getCommand("movehome").setTabCompleter(new CommandMoveHome());
        getCommand("majekhomes").setExecutor(new CommandMajekHomes());
        getCommand("majekhomes").setTabCompleter(new CommandMajekHomes());
        getCommand("undodelhome").setExecutor(new CommandUndoDelHome());
        getCommand("undodelhome").setTabCompleter(new CommandUndoDelHome());
    }

    public void reload() {
        // Initialize main config
        saveDefaultConfig();
        File configFile = new File(instance.getDataFolder(), "config.yml");
        try {
            ConfigUpdater.update(instance, "config.yml", configFile, Collections.emptyList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        reloadConfig();

        // Initialize lang config
        YAMLConfig langConfig = new YAMLConfig(Homes.getCore(), null, "lang.yml");
        langConfig.saveDefaultConfig();
        File langFile = new File(instance.getDataFolder(), "lang.yml");
        try {
            ConfigUpdater.update(instance, "lang.yml", langFile, Collections.emptyList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        langConfig.reloadConfig();
        lang = langConfig.getConfig();
    }

    public static Homes getCore() {
        return instance;
    }

    public FileConfiguration getLang() {
        return instance.lang;
    }

    public HomesPlayer getHomesPlayer(UUID uuid) {
        return this.userMap.get(uuid);
    }

    public HomesPlayer getHomesPlayer(String name) {
        for (HomesPlayer homesPlayer : userMap.values()) {
            if (homesPlayer.getLastSeenName().equalsIgnoreCase(name))
                return homesPlayer;
        }
        return null;
    }

    public void addToUserMap(HomesPlayer homesPlayer) {
        this.userMap.put(homesPlayer.getUuid(), homesPlayer);
    }

    public Map<UUID, HomesPlayer> getUserMap() {
        return this.userMap;
    }

    public static void safeTeleportPlayer(final Player player, final Location location) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 140, 7));
        player.teleport(location);
        player.setFallDistance(0);
        player.setVelocity(new Vector(0, 0.3, 0));
    }
}
