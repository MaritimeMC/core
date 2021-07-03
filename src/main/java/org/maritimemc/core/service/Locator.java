package org.maritimemc.core.service;

import org.maritimemc.core.Module;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Service locator implementation for Module management.
 *
 * @author Embrasure
 */
public class Locator {

    private static final Map<Class<? extends Module>, Module> moduleMap = new HashMap<>();
    private static final Object LOCK = new Object();

    private static final Set<Class<? extends Module>> initialising = new HashSet<>();

    public static <T extends Module> T locate(Class<T> clazz) {
        synchronized (LOCK) {

            if (moduleMap.containsKey(clazz)) {
                return clazz.cast(moduleMap.get(clazz));
            }

            if (initialising.contains(clazz)) {
                throw new IllegalStateException("Circular initialisation; cannot proceed.");
            }

            try {
                initialising.add(clazz);

                T t = clazz.newInstance();
                initialising.remove(clazz);

                moduleMap.put(clazz, t);

                return t;
            } catch (InstantiationException | IllegalAccessException e) {
                initialising.remove(clazz);
                throw new RuntimeException("Failed to reflectively create module " + clazz.getSimpleName() + ".", e);
            }
        }
    }

    public static void disable() {
        for (Module value : moduleMap.values()) {
            if (value instanceof DisableAction) {
                ((DisableAction) value).disable();
            }
        }
    }
}
