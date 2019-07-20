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

package net.sw4pspace.mc.boots.init;

import net.sw4pspace.mc.boots.Boots;
import net.sw4pspace.mc.boots.BootsManager;
import net.sw4pspace.mc.boots.annotations.BootsValue;
import net.sw4pspace.mc.boots.models.RegisteredConfigValue;
import net.sw4pspace.mc.boots.models.RegisteredDependency;
import net.sw4pspace.mc.boots.processor.BootsInjectProcessor;
import net.sw4pspace.mc.boots.processor.BootsValueProcessor;
import net.sw4pspace.mc.boots.registries.ConfigFileRegistry;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;

public class BootsValueInitializer implements FieldInitializer<RegisteredConfigValue> {

    private BootsValueProcessor processor;

    public BootsValueInitializer(BootsValueProcessor processor) {
        this.processor = processor;
    }

    @Override
    public void check(Field field, Plugin plugin) {
        Object instance = null;
        if(Boots.getHopper().exists(field.getDeclaringClass())) {
            try {
                instance = Boots.getHopper().fetchInstance(field.getDeclaringClass());
            } catch (InstantiationException | IllegalAccessException e) {
                Boots.getBootsLogger().info("Error fetching hopper class " + field.getDeclaringClass() + ": " + e.getMessage());
            }
        } else {
            instance = field.getDeclaringClass().equals(plugin.getClass()) ? plugin : instanceFromName(field.getDeclaringClass().getName());
        }

        if(instance != null) {
            BootsValue value = field.getAnnotation(BootsValue.class);
            processor.getRegistry().put(new RegisteredConfigValue(value, field.getDeclaringClass(), field, instance), plugin);
        }
    }

    @Override
    public void register(RegisteredConfigValue registeredConfigValue, Plugin plugin) {
        boolean accessible = registeredConfigValue.getField().isAccessible();
        try {
            registeredConfigValue.getField().setAccessible(true);
            YamlConfiguration configuration = Boots.getConfigFileRegistry().getConfig(registeredConfigValue.getBootsValue().config(), plugin);
            if(configuration != null && configuration.contains(registeredConfigValue.getBootsValue().value())) {
                registeredConfigValue.getField().set(registeredConfigValue.getContainingClassInstance(), configuration.get(registeredConfigValue.getBootsValue().value()));
                Boots.getBootsLogger().info(getPluginName(plugin) + "Injected config value [" + registeredConfigValue.getBootsValue().value() + "] to field [" + registeredConfigValue.getField().getName() + "] to \'" + registeredConfigValue.getField().get(registeredConfigValue.getContainingClassInstance()) + "\'");
            } else {
                Boots.getBootsLogger().info(getPluginName(plugin) + "Could not inject config value in field [" + registeredConfigValue.getField().getName() + "], config not found");
            }
            registeredConfigValue.getField().setAccessible(accessible);
        } catch (IllegalAccessException e) {
            Boots.getBootsLogger().info(getPluginName(plugin) + "Error injecting config value [" + registeredConfigValue.getField().getType() + "] in field [" + registeredConfigValue.getField().getName() + "] in class " + registeredConfigValue.getClazz().getName() + ": " + e.getMessage());
        }
    }

}
