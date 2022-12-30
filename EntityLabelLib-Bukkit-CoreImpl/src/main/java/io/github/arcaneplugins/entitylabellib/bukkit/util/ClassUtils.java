package io.github.arcaneplugins.entitylabellib.bukkit.util;

import java.util.Objects;
import javax.annotation.Nonnull;

public class ClassUtils {

    private ClassUtils() throws IllegalAccessException {
        throw new IllegalAccessException("Unable to instantiate utility class");
    }

    public static boolean classExists(
        final @Nonnull String classpath
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
