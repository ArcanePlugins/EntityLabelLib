package io.github.arcaneplugins.entitylabellib.bukkit;

import io.github.arcaneplugins.entitylabellib.bukit.protocollib.ProtocolLibLabelHandler;
import io.github.arcaneplugins.entitylabellib.bukkit.nms.NmsLabelHandler;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
public class EntityLabelLib extends JavaPlugin {

    public static @Nonnull LabelHandler instantiateLabelHandler(
        final @Nonnull JavaPlugin plugin
    ) {
        Objects.requireNonNull(plugin, "plugin");

        if(NmsLabelHandler.isCompatible()) return new NmsLabelHandler(plugin);
        if(ProtocolLibLabelHandler.isCompatible()) return new ProtocolLibLabelHandler(plugin);

        throw new IllegalStateException("[EntityLabelLib] No compatible label handlers detected.");
    }

}
