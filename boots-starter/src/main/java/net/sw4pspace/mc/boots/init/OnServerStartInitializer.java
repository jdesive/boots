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
import net.sw4pspace.mc.boots.models.OnRegisterMethod;
import net.sw4pspace.mc.boots.models.OnServerStartMethod;
import net.sw4pspace.mc.boots.processor.OnRegisterProcessor;
import net.sw4pspace.mc.boots.processor.OnServerStartProcessor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.MalformedParametersException;
import java.lang.reflect.Method;

public class OnServerStartInitializer implements MethodInitializer<OnServerStartMethod> {

    private OnServerStartProcessor processor;

    public OnServerStartInitializer(OnServerStartProcessor processor) {
        this.processor = processor;
    }

    @Override
    public void check(Method method, Plugin plugin) {
        if (method.getParameterCount() > 0) {
            throw new MalformedParametersException("On Server Start methods cannot have parameters");
        }
        load(method, method.getDeclaringClass(), plugin);
    }

    @Override
    public void register(OnServerStartMethod onServerStartMethod, Plugin plugin) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            onServerStartMethod.getTask().run();
            Boots.getBootsLogger().info(getPluginName(plugin) + "Running on server start method [" + onServerStartMethod.getMethod().getName() + "] in class [" + onServerStartMethod.getClazz().getName() + "]");
        });
    }

    private void load(Method method, Class<?> clazz, Plugin plugin) {
        Runnable task = clazz.equals(plugin.getClass()) ?
                () -> invokeMainClassMethod(plugin, method) :
                () -> invokeMethod(clazz, method);
        processor.getRegistry().put(new OnServerStartMethod(clazz, method, task), plugin);
    }

}
