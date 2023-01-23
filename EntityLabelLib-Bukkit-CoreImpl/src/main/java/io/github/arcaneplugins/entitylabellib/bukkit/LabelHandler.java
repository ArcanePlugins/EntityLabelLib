package io.github.arcaneplugins.entitylabellib.bukkit;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public abstract class LabelHandler {

    private final JavaPlugin plugin;

    public LabelHandler(
        final @NotNull JavaPlugin plugin
    ) {
        this.plugin = plugin;
    }

    private final Collection<PacketInterceptor> registeredPacketInterceptors = new LinkedList<>();

    /**
     * Modifies the client-side label of the entity.
     * Packet is received by all players in the entity's world.
     *
     * @param entity              entity to modify the label of
     * @param labelComponent      component to set the label to
     * @param labelAlwaysVisible  whether the label is always visible
     */
    public final void updateEntityLabel(
        final @NotNull Entity entity,
        final @NotNull Component labelComponent,
        final boolean labelAlwaysVisible
    ) {
        Objects.requireNonNull(entity, "entity");
        Objects.requireNonNull(labelComponent, "labelComponent");

        entity.getWorld().getPlayers().forEach(player ->
            updateEntityLabel(entity, labelComponent, labelAlwaysVisible, player)
        );
    }

    /**
     * Modifies the client-side label of the entity.
     * Packet is received by the specified player.
     *
     * @param entity              entity to modify the label of
     * @param labelComponent      component to set the label to
     * @param labelAlwaysVisible  whether the label is always visible
     * @param packetRecipient player to receive the packet
     */
    public abstract void updateEntityLabel(
        final @NotNull Entity entity,
        final @NotNull Component labelComponent,
        final boolean labelAlwaysVisible,
        final @NotNull Player packetRecipient
    );

    /**
     * Modifies the client-side label of the entity, without modifying the label's visibility.
     * Packet is received by all players in the entity's world.
     *
     * @param entity              entity to modify the label of
     * @param labelComponent      component to set the label to
     */
    public final void updateEntityLabel(
        final @NotNull Entity entity,
        final @NotNull Component labelComponent
    ) {
        Objects.requireNonNull(entity, "entity");
        Objects.requireNonNull(labelComponent, "labelComponent");

        entity.getWorld().getPlayers().forEach(player ->
            updateEntityLabel(entity, labelComponent, player)
        );
    }

    /**
     * Modifies the client-side label of the entity, without modifying the label's visibility.
     * Packet is received by the specified player.
     *
     * @param entity              entity to modify the label of
     * @param labelComponent      component to set the label to
     * @param packetRecipient player to receive the packet
     */
    public abstract void updateEntityLabel(
        final @NotNull Entity entity,
        final @NotNull Component labelComponent,
        final @NotNull Player packetRecipient
    );

    /**
     * Modifies the client-side label of the entity, without modifying the label's component.
     * Packet is received by all players in the entity's world.
     *
     * @param entity             entity to modify the label of
     * @param labelAlwaysVisible whether the label is always visible
     */
    public final void updateEntityLabel(
        final @NotNull Entity entity,
        final boolean labelAlwaysVisible
    ) {
        Objects.requireNonNull(entity, "entity");

        entity.getWorld().getPlayers().forEach(player ->
            updateEntityLabel(entity, labelAlwaysVisible, player)
        );
    }

    /**
     * Modifies the client-side label of the entity, without modifying the label's component.
     * Packet is received by the specified player.
     *
     * @param entity             entity to modify the label of
     * @param labelAlwaysVisible whether the label is always visible
     * @param packetRecipient player to receive the packet
     */
    public abstract void updateEntityLabel(
        final @NotNull Entity entity,
        final boolean labelAlwaysVisible,
        final @NotNull Player packetRecipient
    );

    /**
     * Sends an empty entity metadata packet.
     * Packet is received by all players in the entity's world.
     * <p>
     * Useful when working with a packet interceptor and wanting it to update a label when desired.
     *
     * @param entity entity to send the empty packet for
     */
    public final void updateEntityLabel(
        final @NotNull Entity entity
    ) {
        Objects.requireNonNull(entity, "entity");
        entity.getWorld().getPlayers().forEach(player -> updateEntityLabel(entity, player));
    }

    /**
     * Sends an empty entity metadata packet.
     * Packet is received by the specified player.
     * <p>
     * Useful when working with a packet interceptor and wanting it to update a label when desired.
     *
     * @param entity          entity to send the empty packet for
     * @param packetRecipient player to receive the packet
     */
    public abstract void updateEntityLabel(
        final @NotNull Entity entity,
        final @NotNull Player packetRecipient
    );

    public void registerInterceptor(
        final @NotNull PacketInterceptor packetInterceptor
    ) {
        if(getRegisteredPacketInterceptors().contains(packetInterceptor)) return;
        getRegisteredPacketInterceptors().add(packetInterceptor);
    }

    public void unregisterInterceptor(
        final @NotNull PacketInterceptor packetInterceptor
    ) {
        getRegisteredPacketInterceptors().remove(packetInterceptor);
    }

    public abstract void registerListeners();

    public abstract void unregisterListeners();

    protected @NotNull Collection<PacketInterceptor> getRegisteredPacketInterceptors() {
        return registeredPacketInterceptors;
    }

    protected @NotNull JavaPlugin getPlugin() {
        return plugin;
    }

    public abstract @NotNull String getHandlerId();
}
