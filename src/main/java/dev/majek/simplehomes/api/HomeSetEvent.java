package dev.majek.simplehomes.api;

import dev.majek.simplehomes.data.struct.Home;
import dev.majek.simplehomes.data.struct.HomesPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a player sets a home. Player's may only set home for themselves so the {@link #player()} and
 * {@link #homesPlayer()} cannot be different users entirely.
 */
public class HomeSetEvent extends Event implements Cancellable {

    private boolean cancelled;
    private final Player player;
    private final HomesPlayer homesPlayer;
    private final Home home;
    private static final HandlerList HANDLER_LIST = new HandlerList();

    /**
     * Called when a player sets a home. Player's may only set home for themselves so the {@link #player()} and
     * {@link #homesPlayer()} cannot be different users entirely.
     */
    public HomeSetEvent(Player player, HomesPlayer homesPlayer, Home home) {
        this.player = player;
        this.homesPlayer = homesPlayer;
        this.home = home;
    }

    /**
     * The in-game player setting the home.
     * @return Setting player.
     */
    public Player player() {
        return player;
    }

    /**
     * The HomesPlayer who the {@link #home()} will belong to.
     * @return Owner of home.
     */
    public HomesPlayer homesPlayer() {
        return homesPlayer;
    }

    /**
     * The home the {@link #player()} is attempting to set.
     * @return Home.
     */
    public Home home() {
        return home;
    }

    /**
     * The home will not be set if the event is canceled.
     * @return Whether or not the event is canceled.
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Set whether or not the event is canceled.
     * @param cancel Cancel status.
     */
    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
