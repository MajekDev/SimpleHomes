package dev.majek.simplehomes.data.struct;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.majek.simplehomes.SimpleHomes;
import dev.majek.simplehomes.data.JSONConfig;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * Any player who has joined the server while the plugin is running will generate a new HomesPlayer
 */
public class HomesPlayer {

    private UUID uuid;
    private String lastSeenName;
    private final Map<String, Home> homes;
    private int maxHomes;
    private final JSONConfig dataStorage;
    private boolean noMove;
    private Bar bossBar;
    private int bossBarTaskID;

    /**
     * Constructed when a player joins for the first time and has never been registered by the plugin.
     * @param player The player who is joining.
     */
    public HomesPlayer(Player player) {
        this.dataStorage = new JSONConfig(new File(SimpleHomes.core().getDataFolder() + File.separator + "playerdata"),
                player.getUniqueId() + ".json");
        try {
            dataStorage.createConfig();
        } catch (FileNotFoundException e) {
            SimpleHomes.core().getLogger().severe("Error creating data storage for " + player.getName() + "!");
            e.printStackTrace();
        }
        setUuid(player.getUniqueId());
        setLastSeenName(player.getName());
        this.homes = new HashMap<>();
        setMaxHomes(player.hasPermission("majekhomes.sethome.unlimited") ? -1 : SimpleHomes.core().getConfig()
                .getInt("default-max-homes", 1));
        updateMaxHomes(player);
        this.noMove = false;
    }

    /**
     * Constructed when home data is pulled from Json storage.
     * @param dataStorage The config file with the data.
     * @param fileContents The contents of the config file.
     */
    public HomesPlayer(JSONConfig dataStorage, JsonObject fileContents) {
        this.dataStorage = dataStorage;
        this.uuid = UUID.fromString(fileContents.get("uuid").getAsString());
        this.lastSeenName = fileContents.get("last-seen-as").getAsString();
        this.homes = new HashMap<>();
        if (fileContents.get("homes") != null) {
            JsonObject homesJson = fileContents.get("homes").getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : homesJson.entrySet())
                addHome(new Home(entry.getKey(), entry.getValue().getAsJsonObject()));
        }
        this.maxHomes = fileContents.get("max-homes").getAsInt();
        this.noMove = false;
    }

    /**
     * Set the unique id.
     * @param uuid Unique id.
     */
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
        try {
            dataStorage.putInJsonObject("uuid", uuid.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the stored unique id.
     * @return Unique id.
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Get the name the player was last seen as.
     * @return Last seen name.
     */
    public String getLastSeenName() {
        return lastSeenName;
    }

    /**
     * Set the name the player was last seen as.
     * @param lastSeenName Last seen name.
     */
    public void setLastSeenName(String lastSeenName) {
        this.lastSeenName = lastSeenName;
        try {
            dataStorage.putInJsonObject("last-seen-as", lastSeenName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get a home from a provided name. This may return null.
     * @param name Home name.
     * @return Home if it exists.
     */
    @Nullable
    public Home getHome(String name) {
        return homes.get(name);
    }

    /**
     * Get a list of all of the player's homes.
     * @return All homes.
     */
    public List<Home> getHomes() {
        return new ArrayList<>(homes.values());
    }

    /**
     * Get the number of total homes.
     * @return Total homes.
     */
    public int getTotalHomes() {
        return homes.values().size();
    }

    /**
     * Add a new home to this player. Will be immediately written to JSON.
     * @param home New home.
     */
    public void addHome(Home home) {
        this.homes.put(home.name(), home);
        try {
            JsonObject homes = this.dataStorage.toJsonObject().getAsJsonObject("homes");
            if (homes == null) {
                this.dataStorage.putInJsonObject("homes", new JsonObject());
                homes = this.dataStorage.toJsonObject().getAsJsonObject("homes");
            }
            homes.add(
                    home.name(),
                    home.locAsJsonObject());
            dataStorage.putInJsonObject("homes", homes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove a home from this player. Will be immediately removed from JSON.
     * @param home Deleted home.
     */
    public void removeHome(Home home) {
        this.homes.remove(home.name());
        try {
            JsonObject homes = this.dataStorage.toJsonObject().getAsJsonObject("homes");
            homes.remove(home.name());
            dataStorage.putInJsonObject("homes", homes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the players main home. This will return the home named "home" if it exists or the first home in the list.
     * @return Main home.
     */
    public Home getMainHome() {
        for (Home home : this.getHomes()) {
            if (home.name().equalsIgnoreCase("home"))
                return home;
        }
        return getHomes().get(0);
    }

    /**
     * Whether or not the player can add another home.
     * @return True -> can add.
     */
    public boolean canAddHome() {
        return getMaxHomes() == -1 || getMaxHomes() >= getTotalHomes() + 1;
    }

    /**
     * Whether or not the player already has a home of a certain name.
     * @param name The name to check.
     * @return True -> has a home with that name.
     */
    public boolean hasHome(String name) {
        return homes.containsKey(name);
    }

    /**
     * The maximum number of homes the player can have. May return -1 for unlimited.
     * @return Max homes.
     */
    public int getMaxHomes() {
        return maxHomes;
    }

    /**
     * Set the number of max homes the player can have.
     * @param maxHomes Max homes.
     */
    public void setMaxHomes(int maxHomes) {
        this.maxHomes = maxHomes;
        try {
            dataStorage.putInJsonObject("max-homes", maxHomes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Run an update check to see if a new permission will change the number of max homes.
     * @param player The in-game player to run the update check on.
     */
    public void updateMaxHomes(Player player) {
        if (player.hasPermission("simplehomes.sethome.unlimited")) {
            setMaxHomes(-1);
            return;
        }
        for (PermissionAttachmentInfo permissionInfo : player.getEffectivePermissions()) {
            String permission = permissionInfo.getPermission();
            if (permission.contains("simplehomes.sethome.max")) {
                String[] args = permission.split("\\.");
                int max;
                try {
                    max = Integer.parseInt(args[args.length - 1]);
                } catch (NumberFormatException ex) {
                    continue;
                }
                if (max > this.maxHomes && this.maxHomes != -1)
                    setMaxHomes(max);
            }
        }
    }

    /**
     * Whether or not the player must remain still (teleporting with delay).
     * @return True -> cannot move.
     */
    public boolean cannotMove() {
        return this.noMove;
    }

    /**
     * Set whether or not the player must remain still (teleporting with delay).
     * @param noMove No move status.
     */
    public void setNoMove(boolean noMove) {
        this.noMove = noMove;
    }

    /**
     * Get the player's boss bar if there is one. May be null. Used internally to cancel boss bar on move.
     * @return Boss bar.
     */
    public Bar getBossBar() {
        return bossBar;
    }

    /**
     * Set the player's boss bar. Used internally to cancel boss bar on move.
     */
    public void setBossBar(Bar bossBar) {
        this.bossBar = bossBar;
    }

    /**
     * Get the player's boss bar task id if there is one. May be null. Used internally to cancel boss bar on move.
     * @return Boss bar task id.
     */
    public Integer getBossBarTaskID() {
        return bossBarTaskID;
    }

    /**
     * Set the player's boss bar task id. Used internally to cancel boss bar on move.
     */
    public void setBossBarTaskID(int bossBarTaskID) {
        this.bossBarTaskID = bossBarTaskID;
    }
}
