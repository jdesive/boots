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
import net.sw4pspace.mc.boots.exception.BootsRegistrationException;
import net.sw4pspace.mc.boots.models.RegisteredInventory;
import net.sw4pspace.mc.boots.processor.BootsInventoryProcessor;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

public class InventoryInitializer implements MethodInitializer<RegisteredInventory> {

    private BootsInventoryProcessor processor;

    public InventoryInitializer(BootsInventoryProcessor processor) {
        this.processor = processor;
    }

    @Override
    public void check(Method method, Plugin plugin) {
        BootsInventory annotation = method.getAnnotation(BootsInventory.class);
        Inventory inv = (Inventory) (method.getDeclaringClass().equals(plugin.getClass()) ?
                invokeMainClassMethod(plugin, method) :
                invokeMethod(method.getDeclaringClass(), method));
        processor.getRegistry().put(new RegisteredInventory(method.getDeclaringClass(), annotation.value(), inv), plugin);
    }

    @Override
    public void register(RegisteredInventory registeredInventory, Plugin plugin) {
        Boots.getInventoryRegistry().registerInventory(registeredInventory);
        Boots.getBootsLogger().info(getPluginName(plugin) + "Registered inventory [" + registeredInventory.getKey() + "] in class [" + registeredInventory.getClazz().getName());
    }

}