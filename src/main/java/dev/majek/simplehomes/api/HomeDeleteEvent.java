package dev.majek.simplehomes.api;

import dev.majek.simplehomes.data.struct.Home;
import dev.majek.simplehomes.data.struct.HomesPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a player deletes a home. The home could belong to them or someone else if they have the right
 * permissions. You should first check {@link #deletingOwnHome()} to see if the player is deleting their own home
 * or someone else's. They will only be able to delete another player's if they have the permission <strong>simplehomes.delhome.other</strong>
 */
public class HomeDeleteEvent extends Event implements Cancellable {

    private boolean cancelled;
    private final Player player;
    private final HomesPlayer homesPlayer;
    private final Home home;
    private final boolean deletingOwnHome;
    private static final HandlerList HANDLER_LIST = new HandlerList();

    /**
     * Called when a player deletes a home. The home could belong to them or someone else if they have the right
     * permissions. You should first check {@link #deletingOwnHome()} to see if the player is deleting their own home
     * or someone else's. They will only be able to delete another player's if they have the permission <strong>simplehomes.delhome.other</strong>
     */
    public HomeDeleteEvent(Player player, HomesPlayer homesPlayer, Home home, boolean deletingOwnHome) {
        this.player = player;
        this.homesPlayer = homesPlayer;
        this.home = home;
        this.deletingOwnHome = deletingOwnHome;
    }

    /**
     * The player who will be deleting the home. May be different from the {@link #homesPlayer()}.
     * @return Deleting player.
     */
    public Player player() {
        return player;
    }

    /**
     * The HomesPlayer who the {@link #home()} belongs to. May be different from the {@link #player()}.
     * @return Owner of home.
     */
    public HomesPlayer homesPlayer() {
        return homesPlayer;
    }

    /**
     * The home the {@link #player()} is attempting to delete. Belongs to the {@link #homesPlayer()}.
     * @return Home.
     */
    public Home home() {
        return home;
    }

    /**
     * Whether or not the {@link #player()} is attempting to delete a home that belongs to them.
     * @return Own home?
     */
    public boolean deletingOwnHome() {
        return deletingOwnHome;
    }

    /**
     * The home will not be deleted if the event is canceled.
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
