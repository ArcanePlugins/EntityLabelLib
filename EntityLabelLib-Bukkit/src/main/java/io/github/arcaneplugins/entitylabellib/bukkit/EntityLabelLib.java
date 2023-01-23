package io.github.arcaneplugins.entitylabellib.bukkit;

import io.github.arcaneplugins.entitylabellib.bukit.protocollib.ProtocolLibLabelHandler;
import io.github.arcaneplugins.entitylabellib.bukkit.nms.NmsLabelHandler;
import java.util.List;
import java.util.Objects;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class EntityLabelLib extends JavaPlugin {

    public static @NotNull LabelHandler instantiateLabelHandler(
        final @NotNull JavaPlugin plugin,
        final @NotNull List<String> disabledHandlerIds
    ) {
        Objects.requireNonNull(plugin, "plugin");

        if(!disabledHandlerIds.contains(NmsLabelHandler.HANDLER_ID) &&
            NmsLabelHandler.isCompatible()
        ) {
            return new NmsLabelHandler(plugin);
        }

        if(!disabledHandlerIds.contains(ProtocolLibLabelHandler.HANDLER_ID) &&
            ProtocolLibLabelHandler.isCompatible()
        ) {
            return new NmsLabelHandler(plugin);
        }

        throw new IllegalStateException("[EntityLabelLib] No compatible label handlers detected.");
    }

}
