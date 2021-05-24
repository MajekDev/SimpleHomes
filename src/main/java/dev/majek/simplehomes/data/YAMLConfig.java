package dev.majek.simplehomes.data;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

/**
 * Handles for YAML configuration files
 */
public class YAMLConfig {
    private final JavaPlugin plugin;
    private FileConfiguration dataConfig = null;
    private File configFile = null;
    private final String folderName;
    private final String fileName;

    public YAMLConfig(JavaPlugin instance, String folderName, String fileName) {
        this.plugin = instance;
        this.folderName = folderName;
        this.fileName = fileName;
    }

    public void createFile(String message, String header) {
        reloadConfig();
        saveConfig();
        loadConfig(header);
        if (message != null) {
            this.plugin.getLogger().info(message);
        }
    }

    public void loadConfig(String header) {
        this.dataConfig.options().header(header);
        this.dataConfig.options().copyDefaults(true);
        saveConfig();
    }

    public void reloadConfig() {
        if (this.configFile == null)
            this.configFile = new File(this.plugin.getDataFolder(), this.fileName);
        this.dataConfig = YamlConfiguration.loadConfiguration(this.configFile);
        InputStream defaultStream = this.plugin.getResource(this.fileName);
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            this.dataConfig.setDefaults(defaultConfig);
        }
    }

    /*

    public void reloadConfig() {
        if (this.configFile == null && !(folderName == null))
            this.configFile = new File(this.plugin.getDataFolder() + File.separator + folderName, this.fileName);
        else if (this.configFile == null)
            this.configFile = new File(this.plugin.getDataFolder(), this.fileName);
        this.dataConfig = YamlConfiguration.loadConfiguration(this.configFile);
        InputStream defaultStream = this.plugin.getResource(this.fileName);
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            this.dataConfig.setDefaults(defaultConfig);
        }
    }

     */

    public FileConfiguration getConfig() {
        if (this.dataConfig == null)
            reloadConfig();
        return this.dataConfig;
    }

    public void saveConfig() {
        if (this.dataConfig == null || this.configFile == null)
            return;
        try {
            this.getConfig().save(this.configFile);
        } catch (IOException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Could not save config to " + this.configFile, e);
        }
    }

    public void saveDefaultConfig() {
        if (this.configFile == null)
            this.configFile = new File(this.plugin.getDataFolder(), fileName);
        if (!this.configFile.exists())
            this.plugin.saveResource(fileName, false);
    }

    /*

    public void saveDefaultConfig() {
        if (this.configFile == null && !(folderName == null))
            this.configFile = new File(this.plugin.getDataFolder() + File.separator + folderName, this.fileName);
        else if (this.configFile == null)
            this.configFile = new File(this.plugin.getDataFolder(), this.fileName);
        if (!this.configFile.exists())
            this.plugin.saveResource(fileName, false);
    }

     */
}
