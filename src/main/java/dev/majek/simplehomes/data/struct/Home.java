package dev.majek.simplehomes.data.struct;

import com.google.gson.*;
import org.bukkit.Location;

import java.util.Map;

/**
 * Represents a player's home
 */
public class Home {

    /**
     * Name of the home.
     */
    private String name;
    /**
     * Location of the home.
     */
    private Location location;

    /**
     * Create a new player home from {@link dev.majek.simplehomes.command.CommandSetHome}.
     * @param name The name of the home.
     * @param location The {@link Location} of the home.
     */
    public Home(String name, Location location) {
        this.name = name;
        this.location = location;
    }

    /**
     * Creates a new player home from stored data.
     * @param name The name of the home.
     * @param serializedLocation Serialized {@link Location} using {@link Location#serialize()}.
     */
    @SuppressWarnings("unchecked")
    public Home(String name, JsonObject serializedLocation) {
        this.name = name;
        this.location = Location.deserialize(new Gson().fromJson(serializedLocation, Map.class));
    }

    /**
     * Get the name of the home.
     * @return Name.
     */
    public String name() {
        return name;
    }

    /**
     * Set the name of the home.
     * @param name New name.
     */
    public void name(String name) {
        this.name = name;
    }

    /**
     * Get the location of the home.
     * @return Location.
     */
    public Location location() {
        return location;
    }

    /**
     * Set the location of the home.
     * @param location New location.
     */
    public void location(Location location) {
        this.location = location;
    }

    /**
     * Clone the home.
     * @return An identical copy of the home.
     */
    public Home clone() {
        try {
            return (Home) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }

    /**
     * Serialize {@link Location} of the home with {@link Location#serialize()} and then convert the map to a
     * JSON string with {@link Gson#toJson(Object)}.
     * @return JSON string of home location.
     */
    public String locToJsonString() {
        return new Gson().toJson(location.serialize());
    }

    /**
     * Convert the JSON string from {@link Home#locToJsonString()} to a {@link JsonObject} for storage.
     * @return JsonObject of home location.
     */
    public JsonObject locAsJsonObject() {
        return (JsonObject) JsonParser.parseString(this.locToJsonString());
    }
}
