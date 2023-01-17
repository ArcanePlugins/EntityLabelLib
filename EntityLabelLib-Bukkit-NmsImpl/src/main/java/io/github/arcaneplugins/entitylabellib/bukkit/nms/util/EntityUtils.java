package io.github.arcaneplugins.entitylabellib.bukkit.nms.util;

import java.util.concurrent.CompletableFuture;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

//TODO javadoc
public class EntityUtils {

    private EntityUtils() throws IllegalAccessException {
        throw new IllegalAccessException("Unable to instantiate utility class");
    }

    public static CompletableFuture<Entity> getNmsEntityById(
        final int entityId,
        final Plugin plugin
    ) {
        final CompletableFuture<Entity> cf = new CompletableFuture<>();

        Bukkit.getScheduler().callSyncMethod(
            plugin,
            () -> {
                for(final World world : Bukkit.getServer().getWorlds()) {
                    final ServerLevel nmsWorld = ((CraftWorld) world).getHandle();
                    final Entity entity =
                        nmsWorld.getEntity(entityId);
                    if(entity == null) continue;
                    cf.complete(entity);
                    return cf;
                }

                return cf.complete(null);
            }
        );

        return cf;
    }

    //TODO javadoc
    public static @NotNull CompletableFuture<org.bukkit.entity.Entity> getBukkitEntityById(
        final int entityId,
        final Plugin plugin
    ) {
        System.out.println("Fetching bukkit entity by ID...");
        final CompletableFuture<org.bukkit.entity.Entity> cf = new CompletableFuture<>();

        getNmsEntityById(entityId, plugin).whenComplete((nmsEntity, error) -> {
            cf.complete(nmsEntity.getBukkitEntity());
            System.out.println("Fetched bukkit entity by ID.");
        });

        return cf;
    }

}
