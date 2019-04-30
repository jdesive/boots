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
import net.sw4pspace.mc.boots.annotations.OnRegister;
import net.sw4pspace.mc.boots.exception.BootsRegistrationException;
import net.sw4pspace.mc.boots.models.OnRegisterMethod;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.MalformedParametersException;
import java.lang.reflect.Method;

public class OnRegisterInitializer implements Initializer<OnRegisterMethod>{

    @Override
    public void check(Class<?> clazz, Plugin plugin) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(OnRegister.class)) {
                if (method.getParameterCount() > 0) {
                    throw new MalformedParametersException("On Register methods cannot have parameters");
                }
                load(method, clazz, plugin);
            }
        }
    }

    @Override
    public void register(OnRegisterMethod onRegisterMethod, Plugin plugin) {
        onRegisterMethod.getTask().run();
        Boots.getBootsLogger().info(getPluginName(plugin) + "Running on register method [" + onRegisterMethod.getMethod().getName() + "] in class [" + onRegisterMethod.getClazz().getName() + "]");
    }

    private void load(Method method, Class<?> clazz, Plugin plugin) {
        Runnable task = clazz.equals(plugin.getClass()) ?
                () -> invokeMainClassMethod(plugin, method) :
                () -> invokeMethod(clazz, method);
        BootsManager.getOnRegisterMethods().put(new OnRegisterMethod(clazz, method, task), plugin);
    }

}
