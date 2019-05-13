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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import net.sw4pspace.mc.boots.annotations.*;
import net.sw4pspace.mc.boots.exception.BootsInitializationException;
import net.sw4pspace.mc.boots.init.*;
import net.sw4pspace.mc.boots.models.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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

    // Registered Items Maps
    private static List<Plugin> registeredPlugins = Lists.newArrayList();
    @Getter private static HashMap<String, Object> annotatedClasses = Maps.newHashMap();
    @Getter private static HashMap<RegisteredRecipe, Plugin> registeredRecipes = Maps.newHashMap();
    @Getter private static HashMap<RegisteredAdvancement, Plugin> registeredAdvancements = Maps.newHashMap();
    @Getter private static HashMap<RegisteredInventory, Plugin> registeredInventories = Maps.newHashMap();
    @Getter private static HashMap<Listener, Plugin> listeners = Maps.newHashMap();
    @Getter private static HashMap<RegisteredScheduledTask, Plugin> registeredScheduledTasks = Maps.newHashMap();
    @Getter private static HashMap<OnRegisterMethod, Plugin> onRegisterMethods = Maps.newHashMap();
    @Getter private static HashMap<RegisteredCommand, Plugin> registeredCommands = Maps.newHashMap();
    @Getter private static HashMap<RegisteredBossBar, Plugin> registeredBossBars = Maps.newHashMap();
    //@Getter private static HashMap<RegisteredJSONConfigFile, Plugin> registeredJSONConfigFiles = Maps.newHashMap();
    @Getter private static HashMap<Class<?>, Plugin> registeredDependencies = Maps.newHashMap();
    @Getter private static HashMap<RegisteredDependency, Plugin> registeredInjections = Maps.newHashMap();

    // Initializers
    private static ListenerClassInitializer listenerInitializer = new ListenerClassInitializer();
    private static InventoryInitializer inventoryInitializer = new InventoryInitializer();
    private static RecipeInitializer recipeInitializer = new RecipeInitializer();
    private static ScheduledTaskInitializer scheduledTaskInitializer = new ScheduledTaskInitializer();
    private static OnRegisterInitializer onRegisterInitializer = new OnRegisterInitializer();
    private static CommandClassInitializer commandInitializer = new CommandClassInitializer();
    private static AdvancementInitializer advancementInitializer = new AdvancementInitializer();
    private static BossBarInitializer bossBarInitializer = new BossBarInitializer();
    //private static JSONConfigurationFileInitializer jsonConfigurationFileInitializer = new JSONConfigurationFileInitializer();
    private static ImplmentedByClassInitializer implmentedByInitializer = new ImplmentedByClassInitializer();
    private static BootsInjectInitializer bootsInjectInitializer = new BootsInjectInitializer();

    @Getter
    private static List<Initializer<?>> initializers = Lists.newArrayList(
            //jsonConfigurationFileInitializer,
            listenerInitializer,
            inventoryInitializer,
            recipeInitializer,
            scheduledTaskInitializer,
            onRegisterInitializer,
            commandInitializer,
            bossBarInitializer,
            implmentedByInitializer,
            bootsInjectInitializer
    );

    BootsManager(JavaPlugin plugin) {
        logger = plugin.getLogger();
    }

    /**
     * Register a plugin with Boots.
     * <p>
     * This method is to be ran once during plugin onEnable. This is done automatically if your plugin
     * specifies Boots in the depend field in it's PluginDescriptionFile.
     *
     * @param plugin The plugin to register
     * @throws BootsInitializationException If the plugin does not contain a @BootsPlugin annotation
     */
    public static void register(Plugin plugin) {
        if (registeredPlugins.contains(plugin)) return;
        registerBootsPlugin(plugin);
        //handleLoadingClasses(plugin);
        scanPluginClasses(plugin);
        runInitializer(registeredDependencies, plugin, implmentedByInitializer);
        runInitializer(registeredInjections, plugin, bootsInjectInitializer);
        //runInitializer(registeredJSONConfigFiles, plugin, jsonConfigurationFileInitializer);
        runInitializer(listeners, plugin, listenerInitializer);
        runInitializer(registeredScheduledTasks, plugin, scheduledTaskInitializer);
        runInitializer(registeredRecipes, plugin, recipeInitializer);
        runInitializer(registeredInventories, plugin, inventoryInitializer);
        runInitializer(registeredCommands, plugin, commandInitializer);
        runInitializer(registeredAdvancements, plugin, advancementInitializer);
        runInitializer(registeredBossBars, plugin, bossBarInitializer);
        runInitializer(onRegisterMethods, plugin, onRegisterInitializer);
        registeredPlugins.add(plugin);
    }

    private static <T> void runInitializer(HashMap<T, Plugin> targetMap, Plugin plugin, Initializer<T> initializer) {
        if (targetMap.containsValue(plugin)) {
            targetMap.entrySet()
                    .stream()
                    .filter(entry -> Objects.equals(entry.getValue(), plugin))
                    .forEach(entry -> initializer.register(entry.getKey(), entry.getValue()));
        }
    }

    // Util Methods

    private static String getPluginName(Plugin plugin) {
        return "[" + plugin.getName() + "] ";
    }

    // Run/Register methods

    private static void registerBootsPlugin(Plugin plugin) {
        Class<?> clazz = plugin.getClass();
        if (clazz.isAnnotationPresent(BootsPlugin.class)) {
            if (clazz.isAnnotationPresent(EnableWhitelist.class)) {
                Bukkit.setWhitelist(true);
                logger.info(getPluginName(plugin) + "Whitelist: true");
            }
            if (clazz.isAnnotationPresent(DefaultGamemode.class)) {
                GameMode defaultGamemode = clazz.getAnnotation(DefaultGamemode.class).value();
                Bukkit.setDefaultGameMode(defaultGamemode);
                logger.info(getPluginName(plugin) + "Default Gamemode: " + defaultGamemode.name());
            }
            if (clazz.isAnnotationPresent(IdleTimeout.class)) {
                int timeout = clazz.getAnnotation(IdleTimeout.class).value();
                Bukkit.setIdleTimeout(timeout);
                logger.info(getPluginName(plugin) + "Idle Timeout: " + timeout);
            }
            if (clazz.isAnnotationPresent(SpawnRadius.class)) {
                int spawnRadius = clazz.getAnnotation(SpawnRadius.class).value();
                Bukkit.setSpawnRadius(spawnRadius);
                logger.info(getPluginName(plugin) + "Spawn Radius: " + spawnRadius);
            }
        } else {
            throw new BootsInitializationException(plugin);
        }
    }

    // Class Loading Methods

    private static void scanPluginClasses(Plugin plugin) {
        Method getClassLoaderMethod;
        try {
            getClassLoaderMethod = JavaPlugin.class.getDeclaredMethod("getClassLoader");
            getClassLoaderMethod.setAccessible(true);

            ClassLoader pluginClassLoader = (ClassLoader) getClassLoaderMethod.invoke(plugin);
            Reflections reflections = new Reflections(
                    ClasspathHelper.forClass(plugin.getClass(), pluginClassLoader),
                    new SubTypesScanner(false),
                    new TypeAnnotationsScanner(),
                    new FieldAnnotationsScanner(),
                    new MethodAnnotationsScanner());

            reflections.getTypesAnnotatedWith(ImplmenetedBy.class).forEach(clazz -> implmentedByInitializer.check(clazz, plugin));
            reflections.getFieldsAnnotatedWith(BootsInject.class).forEach(field -> bootsInjectInitializer.check(field, plugin));
            reflections.getTypesAnnotatedWith(BootsListener.class).forEach(clazz -> listenerInitializer.check(clazz, plugin));
            reflections.getTypesAnnotatedWith(BootsCommand.class).forEach(clazz -> commandInitializer.check(clazz, plugin));
            reflections.getMethodsAnnotatedWith(BootsInventory.class).forEach(method -> inventoryInitializer.check(method, plugin));
            reflections.getMethodsAnnotatedWith(BootsScheduledTask.class).forEach(method -> scheduledTaskInitializer.check(method, plugin));
            reflections.getMethodsAnnotatedWith(CraftingRecipe.class).forEach(method -> recipeInitializer.check(method, plugin));
            reflections.getMethodsAnnotatedWith(BootsBossBar.class).forEach(method -> bossBarInitializer.check(method, plugin));
            reflections.getMethodsAnnotatedWith(Advancement.class).forEach(method -> advancementInitializer.check(method, plugin));
            reflections.getMethodsAnnotatedWith(OnRegister.class).forEach(method -> onRegisterInitializer.check(method, plugin));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            logger.severe("Error loading classes for plugin \'" + plugin.getName() + "\': " + e.getMessage());
        }
    }

}
