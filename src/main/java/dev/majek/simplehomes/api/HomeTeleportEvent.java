package dev.majek.simplehomes.api;

import dev.majek.simplehomes.data.struct.Home;
import dev.majek.simplehomes.data.struct.HomesPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a player teleports to a home. The home could belong to them or someone else if they have the right
 * permissions. You should first check {@link #toOwnHome()} to see if the player is teleporting to their own home
 * or someone else's. They will only be able to teleport to another player's if they have the permission <strong>simplehomes.home.other</strong>
 */
public class HomeTeleportEvent extends Event implements Cancellable {

    private boolean cancelled;
    private final Player player;
    private final HomesPlayer homesPlayer;
    private final Home home;
    private Location teleportingTo;
    private final Location teleportingFrom;
    private boolean hasTeleportDelay;
    private int teleportDelay;
    private final boolean toOwnHome;
    private static final HandlerList HANDLER_LIST = new HandlerList();

    /**
     * Called when a player teleports to a home. The home could belong to them or someone else if they have the right
     * permissions. You should first check {@link #toOwnHome()} to see if the player is teleporting to their own home
     * or someone else's. They will only be able to teleport to another player's if they have the permission <strong>simplehomes.home.other</strong>
     */
    public HomeTeleportEvent(Player player, HomesPlayer homesPlayer, Home home, Location teleportingFrom,
                             boolean hasTeleportDelay, int teleportDelay, boolean toOwnHome) {
        this.player = player;
        this.homesPlayer = homesPlayer;
        this.home = home;
        this.teleportingTo = home.location();
        this.teleportingFrom = teleportingFrom;
        this.hasTeleportDelay = hasTeleportDelay;
        this.teleportDelay = teleportDelay;
        this.toOwnHome = toOwnHome;
    }

    /**
     * The player who will be teleporting to the home. May be different from the {@link #homesPlayer()}.
     * @return Teleporting player.
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
     * The home the {@link #player()} is attempting to teleport to. Belongs to the {@link #homesPlayer()}.
     * @return Home.
     */
    public Home home() {
        return home;
    }

    /**
     * The location the {@link #player()} is teleporting to. Unless changed this is the {@link #home()}'s location.
     * @return Teleport location.
     */
    public Location teleportingTo() {
        return teleportingTo;
    }

    /**
     * Set where the {@link #player()} is teleporting to.
     * @param teleportingTo Teleport location.
     */
    public void teleportingTo(Location teleportingTo) {
        this.teleportingTo = teleportingTo;
    }

    /**
     * The location the {@link #player()} is teleporting from. This cannot be changed.
     * @return Previous location.
     */
    public Location teleportingFrom() {
        return teleportingFrom;
    }

    /**
     * Returns true if the {@link #player()} will teleport with no delay.
     * @return True if no teleport delay.
     */
    public boolean noTeleportDelay() {
        return !hasTeleportDelay;
    }

    /**
     * Set whether or not the {@link #player()} should teleport without a delay.
     * @param hasTeleportDelay Teleport delay?
     */
    public void hasTeleportDelay(boolean hasTeleportDelay) {
        this.hasTeleportDelay = hasTeleportDelay;
    }

    /**
     * Get the teleport delay if there is one. If there isn't one this will return 0, not null.
     * @return Teleport delay.
     */
    public int teleportDelay() {
        return teleportDelay;
    }

    /**
     * Set the teleport delay for the teleporting {@link #player()}.
     * @param teleportDelay Teleport delay.
     */
    public void teleportDelay(int teleportDelay) {
        this.teleportDelay = teleportDelay;
    }

    /**
     * Whether or not the {@link #player()} is teleporting to their own {@link #home()}.
     * @return Own home?
     */
    public boolean toOwnHome() {
        return toOwnHome;
    }

    /**
     * The {@link #player()} will not be teleported if the event is canceled.
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
