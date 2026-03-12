package com.ezinnovations.eznightvision.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public final class SchedulerUtil {

    private final Plugin plugin;
    private final boolean foliaEntitySchedulerAvailable;

    public SchedulerUtil(Plugin plugin) {
        this.plugin = plugin;
        this.foliaEntitySchedulerAvailable = detectFoliaEntityScheduler();
    }

    public void runPlayerTaskLater(final Player player, final Runnable task, long delayTicks) {
        if (foliaEntitySchedulerAvailable && scheduleViaFoliaEntityScheduler(player, task, delayTicks)) {
            return;
        }

        Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
    }

    private boolean detectFoliaEntityScheduler() {
        try {
            Class<?> entitySchedulerClass = Class.forName("io.papermc.paper.threadedregions.scheduler.EntityScheduler");
            Method getSchedulerMethod = Player.class.getMethod("getScheduler");
            Class<?> consumerClass = Class.forName("java.util.function.Consumer");
            entitySchedulerClass.getMethod("runDelayed", Plugin.class, consumerClass, Runnable.class, long.class);
            return getSchedulerMethod != null;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private boolean scheduleViaFoliaEntityScheduler(final Player player, final Runnable task, long delayTicks) {
        try {
            Method getSchedulerMethod = player.getClass().getMethod("getScheduler");
            Object entityScheduler = getSchedulerMethod.invoke(player);
            if (entityScheduler == null) {
                return false;
            }

            Class<?> consumerClass = Class.forName("java.util.function.Consumer");
            Object consumerProxy = Proxy.newProxyInstance(
                    consumerClass.getClassLoader(),
                    new Class<?>[]{consumerClass},
                    new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) {
                            if ("accept".equals(method.getName())) {
                                task.run();
                            }
                            return null;
                        }
                    }
            );

            Method runDelayedMethod = entityScheduler.getClass().getMethod(
                    "runDelayed",
                    Plugin.class,
                    consumerClass,
                    Runnable.class,
                    long.class
            );

            runDelayedMethod.invoke(entityScheduler, plugin, consumerProxy, task, Long.valueOf(delayTicks));
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }
}
