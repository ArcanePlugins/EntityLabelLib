package io.github.arcaneplugins.entitylabellib.bukkit.nms;

import io.github.arcaneplugins.entitylabellib.bukkit.LabelHandler;
import io.github.arcaneplugins.entitylabellib.bukkit.PacketInterceptor;
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
import java.util.concurrent.CompletableFuture;
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

    public static final String HANDLER_ID = "Nms";

    private static final EntityDataAccessor<Optional<net.minecraft.network.chat.Component>> LABEL;
    private static final EntityDataAccessor<Boolean> LABEL_VISIBLE;

    static {
        Class<?> entityClass = net.minecraft.world.entity.Entity.class;

        try {
            //noinspection JavaReflectionMemberAccess
            Field label = entityClass.getDeclaredField("aM");
            label.setAccessible(true);
            //noinspection unchecked
            LABEL = (EntityDataAccessor<Optional<net.minecraft.network.chat.Component>>) label.get(null);

            //noinspection JavaReflectionMemberAccess
            Field labelVisible = entityClass.getDeclaredField("aN");
            labelVisible.setAccessible(true);
            //noinspection unchecked
            LABEL_VISIBLE = (EntityDataAccessor<Boolean>) labelVisible.get(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private final String packetChannelHandlerName;

    public NmsLabelHandler(
        final @NotNull JavaPlugin plugin
    ) {
        super(plugin);
        this.packetChannelHandlerName = plugin.getName() + "_EntityLabelLib_EntityMetadata";
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("DuplicatedCode")
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
    @SuppressWarnings("DuplicatedCode")
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
            @NotNull final ServerPlayer playerHandle,
            @NotNull final net.minecraft.world.entity.Entity entityHandle,
            @NotNull final SynchedEntityData entityData
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

    @Override
    public @NotNull String getHandlerId() {
        return HANDLER_ID;
    }

    /**
     * Listen for {@link PlayerJoinEvent} to add the packet listener to the pipelines of players
     * who are joining the server.
     *
     * @param event event object
     */
    @EventHandler
    public void onJoin(
        final @NotNull PlayerJoinEvent event
    ) {
        Objects.requireNonNull(event, "event");

        addPacketListenerToPipeline(event.getPlayer());
    }

    /**
     * Adds a packet listener to the player's network pipeline.
     *
     * @param player player to modify the pipeline of
     */
    private void addPacketListenerToPipeline(
        final @NotNull Player player
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
                final Runnable superWrite = () -> {
                    try {
                        super.write(context, message, promise);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                };

                if(!(message instanceof final ClientboundSetEntityDataPacket packet)) {
                    superWrite.run();
                    return;
                }

                final List<DataValue<?>> items = packet.packedItems();

                if(items == null) {
                    superWrite.run();
                    return;
                }

                final CompletableFuture<Entity> entityCf =
                    EntityUtils.getBukkitEntityById(packet.id(), getPlugin());

                entityCf.whenComplete((entity, error) -> {
                    if(error != null) {
                        error.printStackTrace();
                        superWrite.run();
                        return;
                    }

                    if(entity == null) {
                        superWrite.run();
                        return;
                    }

                    if(!(entity instanceof final LivingEntity lentity)) {
                        superWrite.run();
                        return;
                    }

                    if(lentity.getType() == EntityType.PLAYER) {
                        superWrite.run();
                        return;
                    }

                    for(final PacketInterceptor interceptor : getRegisteredPacketInterceptors()) {
                        interceptor
                            .interceptEntityLabelPacket(entity, player)
                            .whenComplete((response, error2) -> {
                                if(error2 != null) {
                                    error2.printStackTrace();
                                    return;
                                }

                                Objects.requireNonNull(response, "response");

                                Component advCustomName = null;
                                Boolean customNameVisible = null;

                                if(response.labelComponent() != null)
                                    advCustomName = response.labelComponent();

                                if(response.labelAlwaysVisible() != null)
                                    customNameVisible = response.labelAlwaysVisible();

                                if(advCustomName == null && customNameVisible == null) {
                                    superWrite.run();
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

                                superWrite.run();
                            });
                    }
                });
            }

        };

        playerHandle.connection.connection.channel.pipeline()
            .addBefore("packet_handler", packetChannelHandlerName, handler);
    }

    /**
     * Removes any EntityLabelLib packet listeners from the player's pipeline.
     *
     * @param player player to modify the pipeline of
     */
    private void removePacketListenerFromPipeline(
        final @NotNull Player player
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

    /**
     * Returns whether this label handler is compatible with the server software it is currently
     * running on.
     *
     * @return compatibility status
     */
    public static boolean isCompatible() {
        /*
        TODO:
              Check for minimum Minecraft version.
              At the moment, this is 1.19.3, though we will
              be working on 1.18.2 compatibility as well.
              For now, we will just return 'true' so that
              this label handler is usable in the mean time.
         */
        return true;
    }

}
