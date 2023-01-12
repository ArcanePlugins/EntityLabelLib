package io.github.arcaneplugins.entitylabellib.bukkit.nms.util;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Component.Serializer;
import net.minecraft.network.chat.MutableComponent;

/**
 * This class contains utilities for using components, such as
 * converting chat component objects to those in different APIs.
 *
 * @author <a href="https://www.spigotmc.org/members/dman16.941198/">DMan16</a>
 */
public class ComponentUtils {

    private ComponentUtils() throws IllegalAccessException {
        throw new IllegalAccessException("Unable to instantiate utility class");
    }

    /**
     * Converts an Adventure Component object to an NMS MutableComponent object.
     *
     * @param advComp Adventure Component object
     * @return NMS MutableComponent object
     * @since 1.0.0
     */
    public static @Nonnull MutableComponent adventureToNmsComponent(
        final @Nullable net.kyori.adventure.text.Component advComp
    ) {
        if(advComp == null)
            return Component.empty();

        return Objects.requireNonNullElse(
            Serializer.fromJson(GsonComponentSerializer.gson().serialize(advComp)),
            Component.empty()
        );
    }

}
