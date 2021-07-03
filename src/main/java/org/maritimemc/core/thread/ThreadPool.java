package org.maritimemc.core.thread;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {

    // Used for basic, small async tasks such as database-heavy commands.
    // Executors.newSingleThreadExecutor() should be used for long-term tasks.
    public static final ExecutorService ASYNC_POOL = Executors.newCachedThreadPool();

    /**
     * Runs a task sync on the next server tick.
     *
     * @param host     The plugin requesting this task.
     * @param runnable The runnable to be performed.
     */
    public static void runOnNextServerTick(JavaPlugin host, Runnable runnable) {
        Bukkit.getScheduler().runTask(host, runnable);
    }

}
