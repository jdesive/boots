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

package net.sw4pspace.mc.boots.registries;

import com.google.common.collect.Maps;
import net.sw4pspace.mc.boots.Boots;
import net.sw4pspace.mc.boots.BootsManager;
import net.sw4pspace.mc.boots.models.RegisteredConfig;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class ConfigFileRegistry {

    private final Map<RegisteredConfig, Plugin> configFiles = Maps.newHashMap();

    // Keep in mind that SnakeYAML is not thread safe. Each thread needs its own instance of YAML

    public YamlConfiguration loadYAMLConfigFile(String name, Plugin plugin) {
        YamlConfiguration configuration = new YamlConfiguration();

        name = name.replace(".yml", "");

        File configFile = new File(plugin.getDataFolder(), name + ".yml");
        if(!configFile.exists()) {
            Boots.getBootsLogger().info("Error, cannot find config file \'" + name + "\'");
        }

        Boots.getBootsLogger().info("Loading config file [" + name + "] from plugin " + plugin.getName());

        try {
            configuration.load(configFile);
            synchronized (configFiles) {
                configFiles.put(new RegisteredConfig(name, configuration), plugin);
            }
            Boots.getBootsLogger().info("Successfully loaded config file [" + name + "] from plugin " + plugin.getName());
            return configuration;
        } catch (InvalidConfigurationException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Optional<YamlConfiguration> exists(String name, Plugin plugin) {
        return configFiles.entrySet().stream()
                .filter(entry -> entry.getKey().getName().equals(name) && entry.getValue().getName().equals(plugin.getName()))
                .map(entry -> entry.getKey().getConfiguration())
                .findFirst();

    }

    public YamlConfiguration getConfig(String name, Plugin plugin) {
        return exists(name, plugin).orElseGet(() -> loadYAMLConfigFile(name, plugin));
    }


}
