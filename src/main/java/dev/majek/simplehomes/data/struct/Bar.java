package dev.majek.simplehomes.data.struct;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Handles teleportation boss bar.
 */
public class Bar {

    private BossBar bar;
    private int taskID;
    private final JavaPlugin plugin;

    public Bar(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void addPlayer(Player player) {
        bar.addPlayer(player);
    }

    public void removePlayer(Player player) {
        bar.removePlayer(player);
    }

    public void createBar(int delay, Component text) {
        // TODO: Fix this bullshit
        bar = Bukkit.createBossBar(LegacyComponentSerializer.legacySection().serialize(text), BarColor.BLUE, BarStyle.SOLID);
        bar.setVisible(true);
        cast(delay);
    }

    public void removeBar() {
        bar.setVisible(false);
    }

    public BossBar getBar() {
        return bar;
    }

    public void cast(int delay) {
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            double progress = 1.0;
            final double time = 1.0 / (delay * 20);
            @Override
            public void run() {
                getBar().setProgress(progress);
                progress = progress - time;
                if (progress <= 0.0)
                    Bukkit.getScheduler().cancelTask(taskID);
            }
        }, 0, 0);
    }
}
