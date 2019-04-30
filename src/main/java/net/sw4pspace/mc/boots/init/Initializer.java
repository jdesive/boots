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
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public interface Initializer<T> {

    void check(Class<?> clazz, Plugin plugin);

    void register(T clazz, Plugin plugin);

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
        try {
            return method.invoke(clazz.newInstance());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            Boots.getBootsLogger().severe("Error invoking method [" + method.getName() + "]: " + e.getMessage());
        }
        return null;
    }

    default Object newInstanceFromName(String name) {
        try {
            return Class.forName(name).getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            Boots.getBootsLogger().severe("Error invoking class [" + name + "]: " + e.getMessage());
        }
        return null;
    }

}
