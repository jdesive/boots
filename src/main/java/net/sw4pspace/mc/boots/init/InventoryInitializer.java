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
import net.sw4pspace.mc.boots.annotations.BootsInventory;
import net.sw4pspace.mc.boots.annotations.BootsListener;
import net.sw4pspace.mc.boots.exception.BootsRegistrationException;
import net.sw4pspace.mc.boots.models.RegisteredInventory;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

public class InventoryInitializer implements Initializer<RegisteredInventory>{

    @Override
    public void check(Class<?> clazz, Plugin plugin) {
        for(Method method : clazz.getDeclaredMethods()) {
            if(method.isAnnotationPresent(BootsInventory.class)) {
                if (method.getReturnType().isAssignableFrom(org.bukkit.inventory.Inventory.class)) {
                    load(method.getAnnotation(BootsInventory.class), method, clazz, plugin);
                } else {
                    throw new BootsRegistrationException(method, BootsInventory.class, Inventory.class);
                }
            }
        }
    }

    @Override
    public void register(RegisteredInventory registeredInventory, Plugin plugin) {
        Boots.getInventoryManager().registerInventory(registeredInventory);
        Boots.getBootsLogger().info(getPluginName(plugin) + "Registered inventory [" + registeredInventory.getKey() + "] in class [" + registeredInventory.getClazz().getName());
    }

    private void load(BootsInventory bootsInventory, Method method, Class<?> clazz, Plugin plugin) {
        org.bukkit.inventory.Inventory inv = (org.bukkit.inventory.Inventory) (clazz.equals(plugin.getClass()) ?
                invokeMainClassMethod(plugin, method) :
                invokeMethod(clazz, method));
        BootsManager.getRegisteredInventories().put(new RegisteredInventory(clazz, bootsInventory.value(), inv), plugin);
    }

}
