/*
 * MIT License
 *
 * Copyright (c) 2019 Sw4pSpace
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 */

package net.sw4pspace.mc.boots;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.sw4pspace.mc.boots.annotations.*;
import net.sw4pspace.mc.boots.exception.BootsInitializationException;
import net.sw4pspace.mc.boots.models.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandMap;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.MalformedParametersException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

/**
 * BootsManager
 *
 * This class manages the registration of plugins to the boots framework.
 *
 * @author Sw4p
 */
public class BootsManager {

    private static Logger logger;
    private static ClassLoader classLoader;

    private static List<Plugin> registeredPlugins = Lists.newArrayList();
    private static HashMap<RegisteredRecipe, Plugin> registeredRecipes = Maps.newHashMap();
    private static HashMap<RegisteredAdvancement, Plugin> registeredAdvancements = Maps.newHashMap();
    private static HashMap<RegisteredInventory, Plugin> registeredInventories = Maps.newHashMap();
    private static HashMap<Listener, Plugin> listeners = Maps.newHashMap();
    private static HashMap<RegisteredScheduledTask, Plugin> registeredScheduledTasks = Maps.newHashMap();
    private static HashMap<OnRegisterMethod, Plugin> onRegisterMethods = Maps.newHashMap();

    BootsManager(JavaPlugin plugin, ClassLoader classLoader) {
        logger = plugin.getLogger();
        BootsManager.classLoader = classLoader;
    }

    /**
     * Register a plugin with Boots.
     *
     * This method is to be ran once during plugin onEnable. This is done automatically if your plugin
     * specifies Boots in the depend field in it's PluginDescriptionFile.
     *
     * @param plugin The plugin to register
     * @throws BootsInitializationException If the plugin does not contain a @BootsPlugin annotation
     */
    public static void register(Plugin plugin) {
        if(registeredPlugins.contains(plugin)) return;
        registerBootsPlugin(plugin);
        handleLoadingClasses(plugin);
        if(listeners.containsValue(plugin)) {
            listeners.entrySet()
                    .stream()
                    .filter(entry -> Objects.equals(entry.getValue(), plugin))
                    .forEach(entry -> registerListener(entry.getKey(), entry.getValue()));
        }
        if(registeredScheduledTasks.containsValue(plugin)) {
            registeredScheduledTasks.entrySet()
                    .stream()
                    .filter(entry -> Objects.equals(entry.getValue(), plugin))
                    .forEach(entry -> registerScheduledTask(entry.getKey(), entry.getValue()));
        }
        if(onRegisterMethods.containsValue(plugin)) {
            onRegisterMethods.entrySet()
                    .stream()
                    .filter(entry -> Objects.equals(entry.getValue(), plugin))
                    .forEach(entry -> runOnRegisterMethod(entry.getKey(), entry.getValue()));
        }
        if(registeredRecipes.containsValue(plugin)) {
            registeredRecipes.entrySet()
                    .stream()
                    .filter(entry -> Objects.equals(entry.getValue(), plugin))
                    .forEach(entry -> registerCraftingRecipe(entry.getKey(), entry.getValue()));
        }
        if(registeredInventories.containsValue(plugin)) {
            registeredInventories.entrySet()
                    .stream()
                    .filter(entry -> Objects.equals(entry.getValue(), plugin))
                    .forEach(entry -> registerInventory(entry.getKey(), entry.getValue()));
        }
        // TODO Expand out to an Advancement API, lets aim at making it easier to write out advancements.
        // This could be done with a builder pattern and some ENUM's.
        /*if(registeredAdvancements.containsValue(plugin)) {
            registeredAdvancements.entrySet()
                    .stream()
                    .filter(entry -> Objects.equals(entry.getValue(), plugin))
                    .forEach(entry -> );
        }*/
        registeredPlugins.add(plugin);
    }

    // Util Methods

    private static String getPluginName(Plugin plugin) {
        return "[" + plugin.getName() + "] ";
    }

    // Loading methods

    private static void loadInventory(BootsInventory bootsInventory, Method method, Class<?> clazz, Plugin plugin) {
        org.bukkit.inventory.Inventory inv = (org.bukkit.inventory.Inventory) (clazz.equals(plugin.getClass()) ?
                        invokeMainClassMethod(plugin, method) :
                        invokeMethod(clazz, method));
        registeredInventories.put(new RegisteredInventory(clazz, bootsInventory.value(), inv), plugin);
    }

    private static void loadCraftingRecipe(Method method, Class<?> clazz, Plugin plugin) {
        Runnable task = clazz.equals(plugin.getClass()) ?
                () -> plugin.getServer().addRecipe((Recipe) invokeMainClassMethod(plugin, method)) :
                () -> plugin.getServer().addRecipe((Recipe) invokeMethod(clazz, method));
        registeredRecipes.put(new RegisteredRecipe(clazz, method, task), plugin);
    }

    private static void loadCommand(BukkitCommand command, Plugin plugin) throws NoSuchFieldException, IllegalAccessException {
        final Field bukkitCommandMap = plugin.getServer().getClass().getDeclaredField("commandMap");
        bukkitCommandMap.setAccessible(true);
        CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
        commandMap.register(command.getName(), command);
        logger.info(getPluginName(plugin) + "Registered command \'/" + command.getName() + "\' to [" + command.getClass().getName() + "]");
    }

    private static void loadListener(Listener listener, Plugin plugin) {
        listeners.put(listener, plugin);
    }

    private static void loadScheduleTask(BootsScheduledTask bootsScheduledTask, Method method, Class<?> clazz, Plugin plugin) {
        Runnable task = clazz.equals(plugin.getClass()) ?
                        () -> invokeMainClassMethod(plugin, method) :
                        () -> invokeMethod(clazz, method);
        registeredScheduledTasks.put(new RegisteredScheduledTask(bootsScheduledTask, task), plugin);
    }

    private static void loadOnRegisterMethod(Method method, Class<?> clazz, Plugin plugin) {
        Runnable task = clazz.equals(plugin.getClass()) ?
                () -> invokeMainClassMethod(plugin, method) :
                () -> invokeMethod(clazz, method);
        onRegisterMethods.put(new OnRegisterMethod(clazz, method, task), plugin);
    }

    private static void loadAdvancement(Method method, Class<?> clazz, Plugin plugin) {
        org.bukkit.advancement.Advancement advancement = (org.bukkit.advancement.Advancement) (clazz.equals(plugin.getClass()) ?
                        invokeMainClassMethod(plugin, method) :
                        invokeMethod(clazz, method));
        registeredAdvancements.put(new RegisteredAdvancement(method, clazz, advancement), plugin);
    }

    private static Object invokeMainClassMethod(Plugin plugin, Method method) {
        try {
            return method.invoke(plugin);
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.severe("Error invoking method [" + method.getName() + "]: " + e.getMessage());
        }
        return null;
    }

    private static Object invokeMethod(Class<?> clazz, Method method) {
        try {
            return method.invoke(clazz.newInstance());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            logger.severe("Error invoking method [" + method.getName() + "]: " + e.getMessage());
        }
        return null;
    }

    // Run/Register methods

    private static void registerBootsPlugin(Plugin plugin) {
        Class<?> clazz = plugin.getClass();
        if(clazz.isAnnotationPresent(BootsPlugin.class)) {
            if(clazz.isAnnotationPresent(EnableWhitelist.class)) {
                Bukkit.setWhitelist(true);
                logger.info(getPluginName(plugin) + "Whitelist: true");
            }
            if(clazz.isAnnotationPresent(DefaultGamemode.class)) {
                GameMode defaultGamemode = clazz.getAnnotation(DefaultGamemode.class).value();
                Bukkit.setDefaultGameMode(defaultGamemode);
                logger.info(getPluginName(plugin) + "Default Gamemode: " + defaultGamemode.name());
            }
            if(clazz.isAnnotationPresent(IdleTimeout.class)) {
                int timeout = clazz.getAnnotation(IdleTimeout.class).value();
                Bukkit.setIdleTimeout(timeout);
                logger.info(getPluginName(plugin) + "Idle Timeout: " + timeout);
            }
            if(clazz.isAnnotationPresent(SpawnRadius.class)) {
                int spawnRadius = clazz.getAnnotation(SpawnRadius.class).value();
                Bukkit.setSpawnRadius(spawnRadius);
                logger.info(getPluginName(plugin) + "Spawn Radius: " + spawnRadius);
            }
        } else {
            throw new BootsInitializationException(plugin);
        }
    }

    private static void registerInventory(RegisteredInventory registeredInventory, Plugin plugin) {
        Boots.getInventoryManager().registerInventory(registeredInventory);
        logger.info(getPluginName(plugin) + "Registered inventory [" + registeredInventory.getKey().toString() + "] in class [" + registeredInventory.getClazz().getName());
    }

    private static void registerCraftingRecipe(RegisteredRecipe registeredRecipe, Plugin plugin) {
        registeredRecipe.getTask().run();
        logger.info(getPluginName(plugin) + "Registered crafting recipe [" + registeredRecipe.getMethod().getName() + "] in class [" + registeredRecipe.getClazz().getName() + "]");
    }

    private static void runOnRegisterMethod(OnRegisterMethod onRegisterMethod, Plugin plugin) {
        onRegisterMethod.getTask().run();
        logger.info(getPluginName(plugin) + "Running on register method [" + onRegisterMethod.getMethod().getName() + "] in class [" + onRegisterMethod.getClazz().getName() + "]");
    }

    private static void registerListener(org.bukkit.event.Listener listener, Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        logger.info(getPluginName(plugin) + "Registered listener [" + listener.getClass().getName() + "]");
    }

    private static void registerScheduledTask(RegisteredScheduledTask task, Plugin plugin) {
        if(task.getBootsScheduledTask().async()) {
            if(task.getBootsScheduledTask().interval() <= 0L) {
                Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, task.getTask(), task.getBootsScheduledTask().delay());
            } else {
                Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, task.getTask(), task.getBootsScheduledTask().delay(), task.getBootsScheduledTask().interval());
            }
        } else {
            if(task.getBootsScheduledTask().interval() <= 0L) {
                Bukkit.getScheduler().runTaskLater(plugin, task.getTask(), task.getBootsScheduledTask().delay());
            } else {
                Bukkit.getScheduler().runTaskTimer(plugin, task.getTask(), task.getBootsScheduledTask().delay(), task.getBootsScheduledTask().interval());
            }
        }
        logger.info(getPluginName(plugin) + "Registered schedule task [" + task.getBootsScheduledTask().name() + "]");
    }

    // Class Loading Methods

    private static void handleLoadingClasses(Plugin plugin) {
        Method getFileMethod;
        try {
            getFileMethod = JavaPlugin.class.getDeclaredMethod("getFile");
            getFileMethod.setAccessible(true);
            File file = (File) getFileMethod.invoke(plugin);
            List<Class<?>> classes = findJarClasses(file);

            classes.forEach(clazz -> {
                try {
                    checkCommand(clazz, plugin);
                    checkListener(clazz, plugin);
                    checkScheduledTask(clazz, plugin);
                    checkOnRegister(clazz, plugin);
                    checkCraftingRecipe(clazz, plugin);
                    checkAdvancement(clazz, plugin);
                    checkInventory(clazz, plugin);
                } catch (IllegalAccessException | InstantiationException | NoSuchFieldException e) {
                    logger.severe("Error loading class " + clazz.getName() + " for plugin \'" + plugin.getName() + "\': " + e.getMessage());
                } catch (NoSuchMethodException | InvocationTargetException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            });
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | IOException | ClassNotFoundException e) {
            logger.severe("Error loading classes for plugin \'" + plugin.getName() + "\': " + e.getMessage());
        }
    }

    private static List<Class<?>> findJarClasses(File file) throws IOException, ClassNotFoundException {
        ArrayList<Class<?>> classes = new ArrayList<>();
        JarFile jar = new JarFile(file);
        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            if (name.endsWith(".class")) {
                name = name.substring(0, name.lastIndexOf('.'));
                // So i dont like that we load each class then parse them...
                // Spigot/Bukkit will load the classes again anyway. Nothing i can do about that.
                // Its a giant waste of time here, needs some work - Sw4p TODO
                Class<?> cls = classLoader.loadClass(name.replace("/", "."));
                classes.add(cls);
            }
        }
        return classes;
    }

    private static void checkOnRegister(Class<?> clazz, Plugin plugin) {
        for(Method method : clazz.getDeclaredMethods()) {
            if(method.isAnnotationPresent(OnRegister.class)) {
                if(method.getParameterCount() > 0) {
                    throw new MalformedParametersException("On Register methods cannot have parameters");
                }
                loadOnRegisterMethod(method, clazz, plugin);
            }
        }
    }

    private static void checkScheduledTask(Class<?> clazz, Plugin plugin) {
        for (Method declaredMethod : clazz.getDeclaredMethods()) {
            if(declaredMethod.isAnnotationPresent(BootsScheduledTask.class)) {
                BootsScheduledTask bootsScheduledTask = declaredMethod.getAnnotation(BootsScheduledTask.class);
                if(declaredMethod.getParameterCount() > 0) {
                    throw new MalformedParametersException("Scheduled Task [" + bootsScheduledTask.name() + "] has parameters. Scheduled Tasks are not allowed to have parameters");
                }
                if(Strings.isNullOrEmpty(bootsScheduledTask.name())) {
                    throw new IllegalArgumentException("Scheduled Task in class [" + clazz.getName() + "] tried to register without a name");
                }
                if(bootsScheduledTask.delay() <= 0L && bootsScheduledTask.interval() <= 0L) {
                    throw new IllegalArgumentException("Scheduled task [" + bootsScheduledTask.name() + "] has to have either a delay or interval. They both cannot be 0");
                }
                loadScheduleTask(bootsScheduledTask, declaredMethod, clazz, plugin);
            }
        }
    }

    private static void checkCommand(Class<?> clazz, Plugin plugin) throws IllegalAccessException, InstantiationException, NoSuchFieldException {
        if (clazz.isAnnotationPresent(BootsCommand.class)) {
            if (BukkitCommand.class.isAssignableFrom(clazz)) {
                if(clazz.equals(plugin.getClass()))
                    loadCommand((BukkitCommand) plugin, plugin);
                else
                    loadCommand((BukkitCommand) clazz.newInstance(), plugin);
            } else {
                logger.severe("Class " + clazz.getName() + " has the BootsCommand annotation but does not extend " + BukkitCommand.class.getName());
            }
        }
    }

    private static void checkListener(Class<?> clazz, Plugin plugin) throws IllegalAccessException, ClassNotFoundException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        if (clazz.isAnnotationPresent(BootsListener.class)) {
            if (Listener.class.isAssignableFrom(clazz)) {
                if(clazz.equals(plugin.getClass()))
                    loadListener((Listener) plugin, plugin);
                else
                    loadListener((Listener) Class.forName(clazz.getName()).getConstructor().newInstance(), plugin);
            } else {
                logger.severe("Class " + clazz.getName() + " has the BootsListener annotation but does not extend " + org.bukkit.event.Listener.class.getName());
            }
        }
    }

    private static void checkCraftingRecipe(Class<?> clazz, Plugin plugin) {
        for(Method method : clazz.getDeclaredMethods()) {
            if(method.isAnnotationPresent(CraftingRecipe.class)) {
                if(method.getReturnType().isAssignableFrom(ShapedRecipe.class) || method.getReturnType().isAssignableFrom(ShapelessRecipe.class)) {
                    loadCraftingRecipe(method, clazz, plugin);
                } else {
                    logger.severe("Method [" + method.getName() + "] has the CraftingRecipe annotation but doesn't return a type of " + Recipe.class.getName());
                }
            }
        }
    }

    private static void checkAdvancement(Class<?> clazz, Plugin plugin) {
        for(Method method : clazz.getDeclaredMethods()) {
            if(method.isAnnotationPresent(Advancement.class)) {
                if(method.getReturnType().isAssignableFrom(org.bukkit.advancement.Advancement.class)) {
                    loadAdvancement(method, clazz, plugin);
                } else {
                    logger.severe("Method [" + method.getName() + "] has the Advancement annotation but doesn't return a type of " + org.bukkit.advancement.Advancement.class.getName());
                }
            }
        }
    }

    private static void checkInventory(Class<?> clazz, Plugin plugin) {
        for(Method method : clazz.getDeclaredMethods()) {
            if(method.isAnnotationPresent(BootsInventory.class)) {
                if (method.getReturnType().isAssignableFrom(org.bukkit.inventory.Inventory.class)) {
                    loadInventory(method.getAnnotation(BootsInventory.class), method, clazz, plugin);
                } else {
                    logger.severe("Method [" + method.getName() + "] has the BootsInventory annotation but doesn't return a type of " + org.bukkit.inventory.Inventory.class.getName());
                }
            }
        }
    }

}
