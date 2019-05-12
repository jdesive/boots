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
import net.sw4pspace.mc.boots.annotations.BootsInject;
import net.sw4pspace.mc.boots.annotations.ImplmenetedBy;
import net.sw4pspace.mc.boots.exception.BootsRegistrationException;
import net.sw4pspace.mc.boots.models.RegisteredDependency;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;

public class BootsInjectInitializer implements Initializer<RegisteredDependency>{

    @Override
    public void check(Class<?> clazz, Plugin plugin) {
        for(Field field : clazz.getDeclaredFields()) {
            if(field.isAnnotationPresent(BootsInject.class)) {
                load(clazz, field, plugin);
            }
        }
    }

    @Override
    public void register(RegisteredDependency dependency, Plugin plugin) {
        dependency.getField().setAccessible(true);
        try {
            dependency.getField().set(dependency.getContainingClassInstance(), Boots.getPiston().get(dependency.getField().getType()));
            Boots.getBootsLogger().info(getPluginName(plugin) + "Injected dependency [" + dependency.getField().getType() + "] to field [" + dependency.getField().getName() + "]");
        } catch (IllegalAccessException | InstantiationException e) {
            Boots.getBootsLogger().info(getPluginName(plugin) + "Error injecting dependency [" + dependency.getField().getType() + "] in field [" + dependency.getField().getName() + "] in class " + dependency.getClazz().getName() + ": " + e.getMessage());
        }
    }

    private void load(Class<?> clazz, Field field, Plugin plugin){
        Object instance = clazz.equals(plugin.getClass()) ? plugin : instanceFromName(clazz.getName());
        BootsManager.getRegisteredInjections().put(new RegisteredDependency(clazz, instance, field), plugin);
    }

}
