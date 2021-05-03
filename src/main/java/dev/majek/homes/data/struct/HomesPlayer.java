package dev.majek.homes.data.struct;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.majek.homes.Homes;
import dev.majek.homes.data.JSONConfig;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class HomesPlayer {

    private UUID uuid;
    private String lastSeenName;
    private final Map<String, Home> homes;
    private int maxHomes;
    private final JSONConfig dataStorage;
    private boolean noMove;
    private Home lastDeletedHome;
    private final List<SharedHome> sharedHomes;

    /**
     * Constructed when a player joins for the first time and has never been registered by the plugin.
     * @param player The player who is joining.
     */
    public HomesPlayer(Player player) {
        this.dataStorage = new JSONConfig(new File(Homes.getCore().getDataFolder() + File.separator + "playerdata"),
                player.getUniqueId() + ".json");
        try {
            dataStorage.createConfig();
        } catch (FileNotFoundException e) {
            Homes.getCore().getLogger().severe("Error creating data storage for " + player.getName() + "!");
            e.printStackTrace();
        }
        setUuid(player.getUniqueId());
        setLastSeenName(player.getName());
        this.homes = new HashMap<>();
        setMaxHomes(player.hasPermission("majekhomes.sethome.unlimited") ? -1 : Homes.getCore().getConfig()
                .getInt("default-max-homes", 1));
        updateMaxHomes(player);
        this.noMove = false;
        this.lastDeletedHome = null;
        this.sharedHomes = new ArrayList<>();
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
        this.lastDeletedHome = null;
        this.sharedHomes = new ArrayList<>();
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
        try {
            dataStorage.putInJsonObject("uuid", uuid.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getLastSeenName() {
        return lastSeenName;
    }

    public void setLastSeenName(String lastSeenName) {
        this.lastSeenName = lastSeenName;
        try {
            dataStorage.putInJsonObject("last-seen-as", lastSeenName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    public Home getHome(String name) {
        return homes.get(name);
    }

    public List<Home> getHomes() {
        return new ArrayList<>(homes.values());
    }

    public int getTotalHomes() {
        return homes.values().size();
    }

    public void addHome(Home home) {
        this.homes.put(home.getName(), home);
        try {
            JsonObject homes = this.dataStorage.toJsonObject().getAsJsonObject("homes");
            if (homes == null) {
                this.dataStorage.putInJsonObject("homes", new JsonObject());
                homes = this.dataStorage.toJsonObject().getAsJsonObject("homes");
            }
            homes.add(
                    home.getName(),
                    home.getLocAsJsonObject());
            dataStorage.putInJsonObject("homes", homes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeHome(Home home) {
        this.homes.remove(home.getName());
        try {
            JsonObject homes = this.dataStorage.toJsonObject().getAsJsonObject("homes");
            homes.remove(home.getName());
            dataStorage.putInJsonObject("homes", homes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Home getMainHome() {
        for (Home home : this.getHomes()) {
            if (home.getName().equalsIgnoreCase("home"))
                return home;
        }
        return getHomes().get(0);
    }

    public boolean canAddHome() {
        return getMaxHomes() == -1 || getMaxHomes() >= getTotalHomes() + 1;
    }

    public boolean hasHome(String name) {
        return homes.containsKey(name);
    }

    public int getMaxHomes() {
        return maxHomes;
    }

    public void setMaxHomes(int maxHomes) {
        this.maxHomes = maxHomes;
        try {
            dataStorage.putInJsonObject("max-homes", maxHomes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateMaxHomes(Player player) {
        if (player.hasPermission("majekhomes.sethome.unlimited")) {
            setMaxHomes(-1);
            return;
        }
        for (PermissionAttachmentInfo permissionInfo : player.getEffectivePermissions()) {
            String permission = permissionInfo.getPermission();
            if (permission.contains("majekhomes.sethome.max")) {
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

    public boolean cannotMove() {
        return this.noMove;
    }

    public void setNoMove(boolean noMove) {
        this.noMove = noMove;
    }

    public Home getLastDeletedHome() {
        return this.lastDeletedHome;
    }

    public void setLastDeletedHome(Home deletedHome) {
        this.lastDeletedHome = deletedHome;
    }

    public void addSharedHome(SharedHome home) {
        this.sharedHomes.add(home);
    }

    public SharedHome getSharedHome(String sender) {
        for (SharedHome home : this.sharedHomes) {
            if (home.getSender().equalsIgnoreCase(sender))
                return home;
        }
        return null;
    }

    public boolean canAddSharedHome(String sender) {
        for (SharedHome home : this.sharedHomes) {
            if (home.getSender().equalsIgnoreCase(sender))
                return false;
        }
        return true;
    }

    public void removeSharedHome(SharedHome home) {
        this.sharedHomes.remove(home);
    }

}