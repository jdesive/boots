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
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public interface Initializer<T> {

    void register(T obj, Plugin plugin);

    default String getPluginName(Plugin plugin) {
        return "[".concat(plugin.getName()).concat("] ");
    }

    default Object invokeMainClassMethod(Plugin plugin, Method method) {
        try {
            return method.invoke(plugin);
        } catch (IllegalAccessException | InvocationTargetException e) {
            Boots.getBootsLogger().severe("Error invoking method [" + method.getName() + "]: " + e.getMessage());
        }
        return null;
    }

    default Object invokeMethod(Class<?> clazz, Method method) {
        if(!BootsManager.getAnnotatedClasses().containsKey(clazz.getName())) {
            instanceFromName(clazz.getName());
        }
        try {
            return method.invoke(BootsManager.getAnnotatedClasses().get(clazz.getName()));
        } catch (IllegalAccessException | InvocationTargetException e) {
            Boots.getBootsLogger().severe("Error invoking method [" + method.getName() + "]: " + e.getMessage());
        }
        return null;
    }

    default Object instanceFromName(String name) {
        if(BootsManager.getAnnotatedClasses().containsKey(name)) {
            return BootsManager.getAnnotatedClasses().get(name);
        }
        try {
            Object instance = Class.forName(name).getConstructor().newInstance();
            BootsManager.getAnnotatedClasses().put(name, instance);
            return instance;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            Boots.getBootsLogger().severe("Error invoking class [" + name + "]: " + e.getMessage());
        }
        return null;
    }

}
