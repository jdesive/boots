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
import net.sw4pspace.mc.boots.annotations.BootsAnnotationProcessor;
import net.sw4pspace.mc.boots.exception.BootsInitializationException;
import net.sw4pspace.mc.boots.init.*;
import net.sw4pspace.mc.boots.models.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import org.reflections.scanners.*;
import org.reflections.util.ClasspathHelper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.*;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.regex.Pattern;

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

    // Initializers
    private static AnnotationProcessorInitializer annotationProcessorInitializer = new AnnotationProcessorInitializer();

    private static List<RegisteredAnnotationProcessor> annotationProcessors = Lists.newArrayList();

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
        scanPluginClasses(plugin);
        annotationProcessors.stream()
                .sorted(Comparator.comparing(RegisteredAnnotationProcessor::getPriority))
                .forEach(annotationProcessor -> runInitializer(annotationProcessor.getTargetMap(), plugin, annotationProcessor.getInitializer()));
        registeredPlugins.add(plugin);
    }

    public static void registerAnnotationProcessor(RegisteredAnnotationProcessor annotationProcessor) {
        annotationProcessors.add(annotationProcessor);
    }

    public static void registerAnnotationProcessor(Class<? extends Annotation> annotation, Initializer<?> initializer, HashMap<?, Plugin> targetMap, Plugin owningPlugin, int priority) {
        annotationProcessors.add(new RegisteredAnnotationProcessor(annotation, initializer, targetMap, owningPlugin, priority));
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

        // Save default config
        if(plugin.getResource("config.yml") != null)
            plugin.saveDefaultConfig();

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
            // I think that the class loader for all plugins is the same, not sure.
            // In which case we could just grab the one from the main class and skip this reflection
            getClassLoaderMethod = JavaPlugin.class.getDeclaredMethod("getClassLoader");
            getClassLoaderMethod.setAccessible(true);

            ClassLoader pluginClassLoader = (ClassLoader) getClassLoaderMethod.invoke(plugin);
            Reflections reflections = new Reflections(
                    ClasspathHelper.forClass(plugin.getClass(), pluginClassLoader),
                    new SubTypesScanner(false),
                    new TypeAnnotationsScanner(),
                    new FieldAnnotationsScanner(),
                    new MethodAnnotationsScanner(),
                    new ResourcesScanner());

            // Register annotation processors
            reflections.getTypesAnnotatedWith(BootsAnnotationProcessor.class).forEach(clazz -> annotationProcessorInitializer.check(clazz, plugin));
            annotationProcessorInitializer.getRegistry().forEach((processor, plug) -> annotationProcessorInitializer.register(processor, plug));
            annotationProcessorInitializer.getRegistry().clear();

            // Register Configuration files
            reflections.getResources(Pattern.compile("boots-.*\\.yml")).forEach(str -> {
                InputStream is = pluginClassLoader.getResourceAsStream(str);
                if(is == null) {
                    Boots.getBootsLogger().info(getPluginName(plugin) + "Unable to read config file [" + str + "] from plugin \'" + plugin.getName() + "\'");
                }
                Scanner s = new Scanner(is).useDelimiter("\\A");
                String yaml = s.hasNext() ? s.next() : "";
                String finalPath = str.replace("boots-", "");
                File configFile = new File(plugin.getDataFolder(), finalPath);
                try {
                    Files.write(configFile.toPath(), yaml.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            annotationProcessors.stream()
                    .sorted(Comparator.comparing(RegisteredAnnotationProcessor::getPriority))
                    .forEach(annotationProcessor -> {
                        ElementType[] elementTypes = ((Target) annotationProcessor.getAnnotation().getAnnotation(Target.class)).value();
                        for (ElementType type : elementTypes) {
                            switch (type) {
                                case TYPE:
                                    processClass(reflections, annotationProcessor, plugin);
                                    break;
                                case FIELD:
                                    processField(reflections, annotationProcessor, plugin);
                                    break;
                                case METHOD:
                                    processMethod(reflections, annotationProcessor, plugin);
                                    break;
                                case PARAMETER:
                                    break;
                                case CONSTRUCTOR:
                                    break;
                                case LOCAL_VARIABLE:
                                    break;
                                default:
                                    break;
                            }
                        }
                    });
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            logger.severe("Error loading classes for plugin \'" + plugin.getName() + "\': " + e.getMessage());
        }
    }

    private static void processClass(Reflections reflections, RegisteredAnnotationProcessor annotationProcessor, Plugin plugin) {
        if(!(annotationProcessor.getInitializer() instanceof ClassInitializer)) {
            return;
        }
        reflections.getTypesAnnotatedWith(annotationProcessor.getAnnotation())
                .forEach(clazz -> ((ClassInitializer)annotationProcessor.getInitializer()).check((Class<?>) clazz, plugin));
    }

    private static void processField(Reflections reflections, RegisteredAnnotationProcessor annotationProcessor, Plugin plugin) {
        if(!(annotationProcessor.getInitializer() instanceof FieldInitializer)) {
            return;
        }
        reflections.getFieldsAnnotatedWith(annotationProcessor.getAnnotation())
                .forEach(field -> ((FieldInitializer)annotationProcessor.getInitializer()).check((Field) field, plugin));
    }

    private static void processMethod(Reflections reflections, RegisteredAnnotationProcessor annotationProcessor, Plugin plugin) {
        if(!(annotationProcessor.getInitializer() instanceof MethodInitializer)) {
            return;
        }
        reflections.getMethodsAnnotatedWith(annotationProcessor.getAnnotation())
                .forEach(method -> ((MethodInitializer)annotationProcessor.getInitializer()).check((Method) method, plugin));
    }

}
