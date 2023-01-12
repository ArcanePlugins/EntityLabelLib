package io.github.arcaneplugins.entitylabellib.bukkit;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public abstract class PacketInterceptor {

    private final LabelHandler labelHandler;

    //TODO javadoc
    public PacketInterceptor(
        final @Nonnull LabelHandler labelHandler
    ) {
        Objects.requireNonNull(labelHandler, "labelHandler");

        this.labelHandler = labelHandler;
    }

    /**
     * Asks the packet interceptor implementation if any updates should be made to the entity's
     * label.
     * <p>
     * When responding to this method, do not access the Bukkit API without explicitly doing so
     * on the main thread.
     *
     * @param entity entity which the entity metadata packet being intercepted belongs to
     * @return label response from the packet interceptor implementation
     */
    public abstract @Nonnull LabelResponse interceptEntityLabelPacket(
        final @Nonnull Entity entity,
        final @Nonnull Player player
    );

    @SuppressWarnings("unused")
    //TODO javadoc
    public final void register() {
        getLabelHandler().registerInterceptor(this);
    }

    @SuppressWarnings("unused")
    //TODO javadoc
    public final void unregister() {
        getLabelHandler().unregisterInterceptor(this);
    }

    //TODO javadoc
    public @Nonnull LabelHandler getLabelHandler() {
        return labelHandler;
    }

    /**
     * Represents possible changes that a packet interceptor can make to an entity.
     *
     * @param labelComponent     label component change that the interceptor
     *                           wants the entity to have
     * @param labelAlwaysVisible label visibility change that the interceptor
     *                           wants the entity to have
     */
    public record LabelResponse(
        @Nullable Component labelComponent,
        @Nullable Boolean labelAlwaysVisible
    ) {}

}
