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
import net.sw4pspace.mc.boots.annotations.Advancement;
import net.sw4pspace.mc.boots.annotations.BootsListener;
import net.sw4pspace.mc.boots.exception.BootsRegistrationException;
import net.sw4pspace.mc.boots.models.RegisteredAdvancement;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

public class AdvancementInitializer implements Initializer<RegisteredAdvancement>{

    @Override
    public void check(Class<?> clazz, Plugin plugin) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Advancement.class)) {
                if (method.getReturnType().isAssignableFrom(org.bukkit.advancement.Advancement.class)) {
                    load(method, clazz, plugin);
                } else {
                    Boots.getBootsLogger().severe("Method [" + method.getName() + "] has the Advancement annotation but doesn't return a type of " + org.bukkit.advancement.Advancement.class.getName());
                    // TODO Throw exception
                }
            }
        }
    }

    @Override
    public void register(RegisteredAdvancement registeredAdvancement, Plugin plugin) {
        // TODO Expand out to an Advancement API, lets aim at making it easier to write out advancements.
        // This could be done with a builder pattern and some ENUM's.
    }

    private void load(Method method, Class<?> clazz, Plugin plugin) {
        org.bukkit.advancement.Advancement advancement = (org.bukkit.advancement.Advancement) (clazz.equals(plugin.getClass()) ?
                invokeMainClassMethod(plugin, method) :
                invokeMethod(clazz, method));
        BootsManager.getRegisteredAdvancements().put(new RegisteredAdvancement(method, clazz, advancement), plugin);
    }

}
