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
import net.sw4pspace.mc.boots.annotations.ImplmenetedBy;
import net.sw4pspace.mc.boots.exception.BootsRegistrationException;
import org.bukkit.plugin.Plugin;

public class ImplmentedByClassInitializer implements ClassInitializer<Class<?>> {

    @Override
    public void check(Class<?> clazz, Plugin plugin) {
        if (clazz.isAnnotationPresent(ImplmenetedBy.class)) {
            if (clazz.isInterface()) {
                load(clazz, plugin);
            } else {
                throw new BootsRegistrationException(clazz, ImplmenetedBy.class);
            }
        }
    }

    @Override
    public void register(Class<?> clazz, Plugin plugin) {
        try {
            Boots.getPiston().load(clazz);
            Boots.getBootsLogger().info(getPluginName(plugin) + "Loaded dependency [" + clazz.getName() + "]");
        } catch (IllegalAccessException | InstantiationException e) {
            Boots.getBootsLogger().info(getPluginName(plugin) + "Error loading dependency [" + clazz.getName() + "]: " + e.getMessage());
        }
    }

    private void load(Class<?> clazz, Plugin plugin){
        ImplmenetedBy annotation = clazz.getAnnotation(ImplmenetedBy.class);
        Boots.getPiston().register(clazz, annotation.value());
        BootsManager.getRegisteredDependencies().put(clazz, plugin);
        Boots.getBootsLogger().info(getPluginName(plugin) + "Registering interface [" + clazz.getName() + "] to " + annotation.value().getName());
    }

}
