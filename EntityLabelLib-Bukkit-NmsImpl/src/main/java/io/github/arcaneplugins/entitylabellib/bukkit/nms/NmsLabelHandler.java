package io.github.arcaneplugins.entitylabellib.bukkit.nms;

import io.github.arcaneplugins.entitylabellib.bukkit.LabelHandler;
import io.github.arcaneplugins.entitylabellib.bukkit.PacketInterceptor;
import io.github.arcaneplugins.entitylabellib.bukkit.PacketInterceptor.LabelResponse;
import io.github.arcaneplugins.entitylabellib.bukkit.nms.util.ComponentUtils;
import io.github.arcaneplugins.entitylabellib.bukkit.nms.util.EntityUtils;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.DataValue;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class NmsLabelHandler extends LabelHandler implements Listener {

    private static final EntityDataAccessor<Boolean> LABEL_VISIBLE;
    private static final EntityDataAccessor<Optional<net.minecraft.network.chat.Component>> LABEL;

    static {
        Class<?> entityClass = net.minecraft.world.entity.Entity.class;
        try {
            Field labelVisible = entityClass.getDeclaredField("DATA_CUSTOM_NAME_VISIBLE");
            labelVisible.setAccessible(true);
            LABEL_VISIBLE = (EntityDataAccessor<Boolean>) labelVisible.get(null);

            Field label = entityClass.getDeclaredField("DATA_CUSTOM_NAME");
            label.setAccessible(true);
            LABEL = (EntityDataAccessor<Optional<net.minecraft.network.chat.Component>>) label.get(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private final String packetChannelHandlerName;

    public NmsLabelHandler(
        final @Nonnull JavaPlugin plugin
    ) {
        super(plugin);
        this.packetChannelHandlerName = plugin.getName() + "_EntityLabelLib_EntityMetadata";
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
        net.minecraft.world.entity.Entity entityHandle = ((CraftEntity) entity).getHandle();
        ServerPlayer playerHandle = ((CraftPlayer) packetRecipient).getHandle();

        SynchedEntityData entityData = new SynchedEntityData(entityHandle);
        entityData.set(LABEL_VISIBLE, labelAlwaysVisible);
        entityData.set(LABEL, Optional.of(ComponentUtils.adventureToNmsComponent(labelComponent)));
        this.sendEntityData(playerHandle, entityHandle, entityData);
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

        net.minecraft.world.entity.Entity entityHandle = ((CraftEntity) entity).getHandle();
        ServerPlayer playerHandle = ((CraftPlayer) packetRecipient).getHandle();

        SynchedEntityData entityData = new SynchedEntityData(entityHandle);
        entityData.set(LABEL, Optional.of(ComponentUtils.adventureToNmsComponent(labelComponent)));
        this.sendEntityData(playerHandle, entityHandle, entityData);
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

        net.minecraft.world.entity.Entity entityHandle = ((CraftEntity) entity).getHandle();
        ServerPlayer playerHandle = ((CraftPlayer) packetRecipient).getHandle();

        SynchedEntityData entityData = new SynchedEntityData(entityHandle);
        entityData.set(LABEL_VISIBLE, labelAlwaysVisible);
        this.sendEntityData(playerHandle, entityHandle, entityData);
    }

    private void sendEntityData(
            @Nonnull final ServerPlayer playerHandle,
            @Nonnull final net.minecraft.world.entity.Entity entityHandle,
            @Nonnull final SynchedEntityData entityData
        ) {
        List<DataValue<?>> list = entityData.packDirty();
        if (list == null || list.isEmpty()) {
            return;
        }
        playerHandle.connection.send(new ClientboundSetEntityDataPacket(
            entityHandle.getId(),
            list
        ));
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

        final net.minecraft.world.entity.Entity entityHandle = ((CraftEntity) entity).getHandle();
        final ServerPlayer playerHandle = ((CraftPlayer) packetRecipient).getHandle();

        final ClientboundSetEntityDataPacket packet = new ClientboundSetEntityDataPacket(
            entity.getEntityId(),
            Objects.requireNonNullElse(
                new SynchedEntityData(entityHandle).getNonDefaultValues(),
                new LinkedList<>()
            )
        );

        playerHandle.connection.send(packet);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerListeners() {
        Bukkit.getPluginManager().registerEvents(this, getPlugin());
        Bukkit.getOnlinePlayers().forEach(this::addPacketListenerToPipeline);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unregisterListeners() {
        Bukkit.getOnlinePlayers().forEach(this::removePacketListenerFromPipeline);
    }

    //TODO Javadoc
    @EventHandler
    public void onJoin(
        final @NotNull PlayerJoinEvent event
    ) {
        Objects.requireNonNull(event, "event");

        addPacketListenerToPipeline(event.getPlayer());
    }

    //TODO Javadoc
    private void addPacketListenerToPipeline(
        final @Nonnull Player player
    ) {
        Objects.requireNonNull(player, "player");

        final ServerPlayer playerHandle = ((CraftPlayer) player).getHandle();

        final ChannelDuplexHandler handler = new ChannelDuplexHandler() {

            @Override
            public void write(
                final ChannelHandlerContext context,
                final Object message,
                final ChannelPromise promise
            ) throws Exception {
                if(!(message instanceof final ClientboundSetEntityDataPacket packet)) {
                    super.write(context, message, promise);
                    return;
                }

                final List<DataValue<?>> items = packet.packedItems();

                if(items == null) {
                    super.write(context, message, promise);
                    return;
                }

                final Entity entity = EntityUtils.getBukkitEntityById(packet.id(), getPlugin());

                if(entity == null) {
                    super.write(context, message, promise);
                    return;
                }

                if(!(entity instanceof final LivingEntity lentity)) {
                    super.write(context, message, promise);
                    return;
                }

                if(lentity.getType() == EntityType.PLAYER) {
                    super.write(context, message, promise);
                    return;
                }

                Component advCustomName = null;
                Boolean customNameVisible = null;

                for(final PacketInterceptor interceptor : getRegisteredPacketInterceptors()) {
                    final LabelResponse response = interceptor.interceptEntityLabelPacket(
                        entity,
                        player
                    );

                    Objects.requireNonNull(response, "response");

                    if(response.labelComponent() != null)
                        advCustomName = response.labelComponent();

                    if(response.labelAlwaysVisible() != null)
                        customNameVisible = response.labelAlwaysVisible();
                }

                if(advCustomName == null && customNameVisible == null) {
                    super.write(context, message, promise);
                    return;
                }

                Integer idxNameComponent = null;
                Integer idxNameVisible = null;

                for (int i = 0; i < items.size(); i++) {
                    final DataValue<?> item = items.get(i);
                    if(item.id() == 2) idxNameComponent = i;
                    if(item.id() == 3) idxNameVisible = i;
                }

                if(advCustomName != null) {
                    final net.minecraft.network.chat.Component customNameNmsComponent =
                        ComponentUtils.adventureToNmsComponent(advCustomName);

                    SynchedEntityData.DataValue<?> dataValueComponent =
                        new SynchedEntityData.DataItem<>(
                            new EntityDataAccessor<>(
                                2,
                                EntityDataSerializers.OPTIONAL_COMPONENT
                            ),
                            Optional.of(customNameNmsComponent)
                        ).value();

                    if(idxNameComponent == null) {
                        items.add(dataValueComponent);
                    } else {
                        items.set(idxNameComponent, dataValueComponent);
                    }
                }

                if(customNameVisible != null) {
                    SynchedEntityData.DataValue<Boolean> dataValueVisible =
                        new SynchedEntityData.DataItem<>(
                            new EntityDataAccessor<>(
                                3,
                                EntityDataSerializers.BOOLEAN
                            ),
                            customNameVisible
                        ).value();

                    if (idxNameVisible == null) {
                        items.add(dataValueVisible);
                    } else {
                        items.set(idxNameVisible, dataValueVisible);
                    }
                }

                super.write(context, message, promise);
            }

        };

        playerHandle.connection.connection.channel.pipeline()
            .addBefore("packet_handler", packetChannelHandlerName, handler);
    }

    //TODO Javadoc
    private void removePacketListenerFromPipeline(
        final @Nonnull Player player
    ) {
        Objects.requireNonNull(player, "player");

        final ServerPlayer playerHandle = ((CraftPlayer) player).getHandle();

        try {
            playerHandle
                .connection.connection.channel.pipeline()
                .remove(packetChannelHandlerName);
        } catch (final Exception ignored) {
            // safely ignore exception: player didn't have packet interceptor in pipeline
        }
    }

    public static boolean isCompatible() {
        return true;
    }

}
