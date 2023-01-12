package io.github.arcaneplugins.entitylabellib.bukit.protocollib;

import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import io.github.arcaneplugins.entitylabellib.bukkit.LabelHandler;
import io.github.arcaneplugins.entitylabellib.bukkit.PacketInterceptor;
import io.github.arcaneplugins.entitylabellib.bukkit.PacketInterceptor.LabelResponse;
import io.github.arcaneplugins.entitylabellib.bukkit.util.ClassUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class ProtocolLibLabelHandler extends LabelHandler {

    private ProtocolManager protocolManager = null;

    public ProtocolLibLabelHandler(
        final @NotNull JavaPlugin plugin
    ) {
        super(plugin);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateEntityLabel(
        final @NotNull Entity entity,
        final @NotNull Component labelComponent,
        final boolean labelAlwaysVisible,
        final @NotNull Player packetRecipient
    ) {
        Objects.requireNonNull(entity, "entity");
        Objects.requireNonNull(labelComponent, "labelComponent");
        Objects.requireNonNull(packetRecipient, "packetRecipient");
        //TODO
        throw new RuntimeException("Not implemented");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateEntityLabel(
        final @NotNull Entity entity,
        final @NotNull Component labelComponent,
        final @NotNull Player packetRecipient
    ) {
        Objects.requireNonNull(entity, "entity");
        Objects.requireNonNull(labelComponent, "labelComponent");
        Objects.requireNonNull(packetRecipient, "packetRecipient");
        //TODO
        throw new RuntimeException("Not implemented");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateEntityLabel(
        final @NotNull Entity entity,
        final boolean labelAlwaysVisible,
        final @NotNull Player packetRecipient
    ) {
        Objects.requireNonNull(entity, "entity");
        Objects.requireNonNull(packetRecipient, "packetRecipient");
        //TODO
        throw new RuntimeException("Not implemented");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateEntityLabel(
        final @NotNull Entity entity,
        final @NotNull Player packetRecipient
    ) {
        Objects.requireNonNull(entity, "entity");
        Objects.requireNonNull(packetRecipient, "packetRecipient");

        final PacketContainer packet = protocolManager().createPacket(Server.ENTITY_METADATA);
        packet.getIntegers().write(0, entity.getEntityId());
        protocolManager().sendServerPacket(packetRecipient, packet);
    }

    @Override
    public void registerListeners() {
        protocolManager = ProtocolLibrary.getProtocolManager();

        protocolManager().addPacketListener(new PacketAdapter(
            getPlugin(),
            ListenerPriority.NORMAL,
            Server.ENTITY_METADATA
        ) {
            @Override
            public void onPacketSending(final PacketEvent event) {
                if(event.isCancelled()) return;
                if(event.getPacketType() != Server.ENTITY_METADATA) return;

                final PacketContainer packet = event.getPacket();
                final Entity entity = packet.getEntityModifier(event).read(0);

                if(entity == null) return;
                if(entity.getType() == EntityType.PLAYER) return;

                final Zombie zombie = (Zombie) entity;
                final WrappedDataWatcher dataWatcher =
                    WrappedDataWatcher.getEntityWatcher(zombie).deepClone();

                final WrappedDataWatcher.Serializer chatSerializer =
                    Registry.getChatComponentSerializer(true);

                final WrappedDataWatcherObject optChatFieldWatcher =
                    new WrappedDataWatcherObject(2, chatSerializer);

                Component customName = null;
                Boolean customNameVisible = null;

                for(final PacketInterceptor interceptor : getRegisteredPacketInterceptors()) {
                    final LabelResponse response = interceptor.interceptEntityLabelPacket(
                        entity,
                        event.getPlayer()
                    );

                    Objects.requireNonNull(response, "response");

                    if(response.labelComponent() != null)
                        customName = response.labelComponent();

                    if(response.labelAlwaysVisible() != null)
                        customNameVisible = response.labelAlwaysVisible();
                }

                if(customName != null) {
                    final Optional<Object> optChatField = Optional.of(
                        WrappedChatComponent.fromChatMessage(
                            LegacyComponentSerializer.legacySection().serialize(customName)
                        )[0].getHandle()
                    );

                    dataWatcher.setObject(optChatFieldWatcher, optChatField);
                }

                if(customNameVisible != null) {
                    dataWatcher.setObject(3, customNameVisible);
                }

                if(ClassUtils
                    .classExists("com.comphenix.protocol.wrappers.WrappedDataValue")
                ) {
                    final List<WrappedDataValue> wrappedDataValueList = new ArrayList<>();

                    for(final WrappedWatchableObject entry : dataWatcher.getWatchableObjects()) {
                        if(entry == null) continue;

                        final WrappedDataWatcherObject watcherObject = entry.getWatcherObject();
                        wrappedDataValueList.add(
                            new WrappedDataValue(
                                watcherObject.getIndex(),
                                watcherObject.getSerializer(),
                                entry.getRawValue()
                            )
                        );
                    }

                    packet.getDataValueCollectionModifier().write(0, wrappedDataValueList);
                } else {
                    packet.getWatchableCollectionModifier()
                        .write(0, dataWatcher.getWatchableObjects());
                }

                event.setPacket(packet);

            }
        });
    }

    @Override
    public void unregisterListeners() {}

    private ProtocolManager protocolManager() {
        return Objects.requireNonNull(protocolManager, "protocolManager");
    }

    public static boolean isCompatible() {
        return Bukkit.getPluginManager().isPluginEnabled("ProtocolLib");
    }

}
