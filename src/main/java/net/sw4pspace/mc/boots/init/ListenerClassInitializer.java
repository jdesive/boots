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
import net.sw4pspace.mc.boots.annotations.BootsListener;
import net.sw4pspace.mc.boots.exception.BootsRegistrationException;
import net.sw4pspace.mc.boots.processor.BootsListenerProcessor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class ListenerClassInitializer implements ClassInitializer<Listener> {

    private BootsListenerProcessor processor;

    public ListenerClassInitializer(BootsListenerProcessor processor) {
        this.processor = processor;
    }

    @Override
    public void check(Class<?> clazz, Plugin plugin) {
        if (clazz.isAnnotationPresent(BootsListener.class)) {
            if (Listener.class.isAssignableFrom(clazz)) {
                load(clazz, plugin);
            } else {
                throw new BootsRegistrationException(clazz, BootsListener.class, Listener.class);
            }
        }
    }

    @Override
    public void register(Listener listener, Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        Boots.getBootsLogger().info(getPluginName(plugin) + "Registered listener [" + listener.getClass().getName() + "]");
    }

    private void load(Class<?> clazz, Plugin plugin){
        Listener listener = clazz.equals(plugin.getClass()) ?
                (Listener) plugin :
                (Listener) instanceFromName(clazz.getName());
        processor.getRegistry().put(listener, plugin);

    }

}
