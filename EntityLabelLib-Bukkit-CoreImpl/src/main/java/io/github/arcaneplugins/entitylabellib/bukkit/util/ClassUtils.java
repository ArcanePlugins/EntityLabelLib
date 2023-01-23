package io.github.arcaneplugins.entitylabellib.bukkit.util;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class ClassUtils {

    private ClassUtils() throws IllegalAccessException {
        throw new IllegalAccessException("Unable to instantiate utility class");
    }

    public static boolean classExists(
        final @NotNull String classpath
    ) {
        Objects.requireNonNull(classpath, "classpath");

        try {
            Class.forName(classpath);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

}
