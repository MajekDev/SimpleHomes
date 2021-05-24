package dev.majek.simplehomes.data;

import com.google.gson.*;

import java.io.*;

/**
 * Handles for JSON configuration files
 */
public class JSONConfig {

    private final File configFile;
    private final File pluginDataFolder;
    private final String name;

    /**
     * Please notice that the constructor does not yet create the JSON configuration file.
     * To create the file on the disk, use {@link JSONConfig#createConfig()}.
     *
     * @param pluginDataFolder The plugin's data directory, accessible with JavaPlugin#getDataFolder();
     * @param name The name of the config file excluding file extensions.
     */
    public JSONConfig(File pluginDataFolder, String name) {
        this.name = name;
        this.configFile = new File(pluginDataFolder, this.name);
        this.pluginDataFolder = pluginDataFolder;
    }

    /**
     * This creates the configuration file. If the data folder is invalid, it will be created along with the config file.
     */
    public void createConfig() throws FileNotFoundException {
        if (! configFile.exists()) {
            if (! this.pluginDataFolder.exists())
                IGNORE_RESULT(this.pluginDataFolder.mkdir());

            try {
               IGNORE_RESULT(this.configFile.createNewFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
            PrintWriter write = new PrintWriter(configFile);
            write.write("{ }");
            write.close();
        }
    }

    /**
     * @return The configuration file's directory. To get its name, use {@link JSONConfig#getName()} instead.
     */
    public File getDirectory() {
        return this.pluginDataFolder;
    }

    /**
     * This returns the name of the configuration file with the .json extension.
     * To get the file's directory, use {@link JSONConfig#getDirectory()}.
     *
     * @return The name of the configuration file, including file extensions.
     */
    public String getName() {
        return this.name;
    }

    /**
     * This returns the actual File object of the config file.
     *
     * @return The config file.
     */
    public File getFile() {
        return this.configFile;
    }

    /**
     * This deletes the config file.
     */
    public void deleteFile() {
        IGNORE_RESULT(this.configFile.delete());
    }

    /**
     * This deletes the config file's directory and all it's contents.
     */
    public void deleteParentDir() {
        IGNORE_RESULT(this.getDirectory().delete());
    }

    /**
     * This deletes and recreates the file, wiping all its contents.
     */
    public void reset() {
        this.deleteFile();
        try {
            IGNORE_RESULT(configFile.createNewFile());
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonElement je = JsonParser.parseString("{}");
            PrintWriter write = new PrintWriter(configFile);
            write.write(gson.toJson(je));
            write.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Wipe the config file's directory, including the file itself.
     */
    public void wipeDirectory() {
        IGNORE_RESULT(this.getDirectory().delete());
        IGNORE_RESULT(this.pluginDataFolder.mkdir());
    }

    /**
     * This will create a sub-directory in the plugin's data folder, which can be accessed with
     * {@link JSONConfig#getDirectory()}. If the entered name is not a valid name for a directory or the
     * sub-directory already exists or the data folder does not exist, an IOException will be thrown.
     *
     * @param name The sub directory's name.
     * @throws IOException If the entered string has a file extension or already exists.
     */
    public void createSubDirectory(String name) throws IOException {
        if (!pluginDataFolder.exists())
            throw new IOException("Data folder not found.");

        File subDir = new File(pluginDataFolder, name);

        if (subDir.exists())
            throw new IOException("Sub directory already existing.");

        IGNORE_RESULT(subDir.mkdir());
    }

    public JsonObject toJsonObject() throws IOException, JsonParseException {
        return (JsonObject) JsonParser.parseReader(new FileReader(configFile));
    }

    public void putInJsonObject(String k, JsonElement v) throws IOException, JsonParseException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject obj = (JsonObject) JsonParser.parseReader(new FileReader(configFile));
        obj.add(k, v);
        PrintWriter write = new PrintWriter(configFile);
        write.write(gson.toJson(obj));
        write.close();
    }

    public void putInJsonObject(String k, String v) throws IOException, JsonParseException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject obj = (JsonObject) JsonParser.parseReader(new FileReader(configFile));
        obj.addProperty(k, v);
        PrintWriter write = new PrintWriter(configFile);
        write.write(gson.toJson(obj));
        write.close();
    }

    public void putInJsonObject(String k, int v) throws IOException, JsonParseException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject obj = (JsonObject) JsonParser.parseReader(new FileReader(configFile));
        obj.addProperty(k, v);
        PrintWriter write = new PrintWriter(configFile);
        write.write(gson.toJson(obj));
        write.close();
    }

    public void removeFromJsonObject(String key) throws IOException, JsonParseException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject obj = (JsonObject) JsonParser.parseReader(new FileReader(configFile));
        obj.remove(key);
        PrintWriter write = new PrintWriter(configFile);
        write.write(gson.toJson(obj));
        write.close();
    }

    /**
     * Used to ignore the annoying "Result of ___ is ignored." warnings.
     * @param b boolean value.
     */
    @SuppressWarnings("unused")
    private void IGNORE_RESULT(boolean b) {

    }
}