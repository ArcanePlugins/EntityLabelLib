package io.github.arcaneplugins.entitylabellib.bukkit.nms.util;

import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld;
import org.bukkit.plugin.Plugin;

//TODO javadoc
public class EntityUtils {

    private EntityUtils() throws IllegalAccessException {
        throw new IllegalAccessException("Unable to instantiate utility class");
    }

    //TODO javadoc
    public static @Nullable Entity getNmsEntityById(
        final int entityId,
        final Plugin plugin
    ) {
        try {
            return Bukkit.getScheduler().callSyncMethod(
                plugin,
                () -> {
                    for(final World world : Bukkit.getServer().getWorlds()) {
                        final ServerLevel nmsWorld = ((CraftWorld) world).getHandle();
                        final net.minecraft.world.entity.Entity entity =
                            nmsWorld.getEntity(entityId);
                        if(entity == null) continue;
                        return entity;
                    }

                    return null;
                }
            ).get();
        } catch(final Exception ex) {
            return null;
        }
    }

    //TODO javadoc
    public static @Nullable org.bukkit.entity.Entity getBukkitEntityById(
        final int entityId,
        final Plugin plugin
    ) {
        final Entity nmsEntity = getNmsEntityById(entityId, plugin);
        if(nmsEntity == null) return null;
        return nmsEntity.getBukkitEntity();
    }

}
