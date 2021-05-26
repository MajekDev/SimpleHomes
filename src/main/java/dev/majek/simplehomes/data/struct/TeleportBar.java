package dev.majek.simplehomes.data.struct;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Handles boss bars for player teleporting with a delay
 */
public class TeleportBar {

    private BossBar bossBar;
    private Audience audience;
    private int taskID;
    private final JavaPlugin plugin;

    public TeleportBar(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void createBar(Component text) {
        bossBar = BossBar.bossBar(text, 1, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS);
    }

    public void showBar(Player player, int length) {
        audience = BukkitAudiences.create(plugin).player(player);
        audience.showBossBar(bossBar);
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            final double time = 1.0 / (length * 20);
            @Override
            public void run() {
                if ((bossBar.progress() - time) <= 0.0) {
                    Bukkit.getScheduler().cancelTask(taskID);
                    return;
                }
                bossBar.progress((float) (bossBar.progress() - time));
            }
        }, 0 ,0);
    }

    public void hideBar() {
        audience.hideBossBar(bossBar);
    }
}
