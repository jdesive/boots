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
import net.sw4pspace.mc.boots.annotations.BootsValue;
import net.sw4pspace.mc.boots.models.RegisteredConfigValue;
import net.sw4pspace.mc.boots.processor.BootsValueProcessor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;

public class BootsValueInitializer implements FieldInitializer<RegisteredConfigValue> {

    private BootsValueProcessor processor;

    public BootsValueInitializer(BootsValueProcessor processor) {
        this.processor = processor;
    }

    @Override
    public void check(Field field, Plugin plugin) {
        Object instance = field.getDeclaringClass().equals(plugin.getClass()) ? plugin : instanceFromName(field.getDeclaringClass().getName());
        if (instance != null) {
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
            if (configuration != null && configuration.contains(registeredConfigValue.getBootsValue().value())) {
                Object value = configuration.get(registeredConfigValue.getBootsValue().value());

                if (Boots.getHopper().exists(registeredConfigValue.getField().getDeclaringClass())) {
                    try {
                        Object instance;
                        instance = Boots.getHopper().load(Boots.getHopper().fetchClassMapValue(registeredConfigValue.getClazz()));

                        if (instance != null) {
                            Field field1 = instance.getClass().getField(registeredConfigValue.getField().getName());

                            field1.set(instance, value);
                            Boots.getBootsLogger().info(getPluginName(plugin) + "Injected config value [" + registeredConfigValue.getBootsValue().value() + "] to field [" + registeredConfigValue.getField().getName() + "] to class \'" + registeredConfigValue.getField().getDeclaringClass().toString() + "\'");
                            return;
                        }

                    } catch (InstantiationException | NoSuchFieldException | IllegalAccessException e) {
                        Boots.getBootsLogger().info(getPluginName(plugin) + "Error fetching hopper class " + registeredConfigValue.getField().getDeclaringClass() + ": " + e);
                        e.printStackTrace();
                        return;
                    }
                }
                registeredConfigValue.getField().set(registeredConfigValue.getContainingClassInstance(), value);
                Boots.getBootsLogger().info(getPluginName(plugin) + "Injected config value [" + registeredConfigValue.getBootsValue().value() + "] to field [" + registeredConfigValue.getField().getName() + "] to class \'" + registeredConfigValue.getField().getDeclaringClass().toString() + "\'");
            } else {
                Boots.getBootsLogger().info(getPluginName(plugin) + "Could not inject config value in field [" + registeredConfigValue.getField().getName() + "], config not found");
            }
            registeredConfigValue.getField().setAccessible(accessible);
        } catch (IllegalAccessException e) {
            Boots.getBootsLogger().info(getPluginName(plugin) + "Error injecting config value [" + registeredConfigValue.getField().getType() + "] in field [" + registeredConfigValue.getField().getName() + "] in class " + registeredConfigValue.getClazz().getName() + ": " + e.getMessage());
        }
    }

}
