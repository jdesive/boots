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

import com.google.common.base.Strings;
import net.sw4pspace.mc.boots.Boots;
import net.sw4pspace.mc.boots.BootsManager;
import net.sw4pspace.mc.boots.annotations.BootsScheduledTask;
import net.sw4pspace.mc.boots.models.RegisteredScheduledTask;
import net.sw4pspace.mc.boots.processor.BootsScheduledTaskProcessor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.MalformedParametersException;
import java.lang.reflect.Method;

public class ScheduledTaskInitializer implements MethodInitializer<RegisteredScheduledTask> {

    private BootsScheduledTaskProcessor processor;

    public ScheduledTaskInitializer(BootsScheduledTaskProcessor processor) {
        this.processor = processor;
    }

    @Override
    public void check(Method method, Plugin plugin) {
        if (method.isAnnotationPresent(BootsScheduledTask.class)) {
            BootsScheduledTask bootsScheduledTask = method.getAnnotation(BootsScheduledTask.class);
            if (method.getParameterCount() > 0) {
                throw new MalformedParametersException("Scheduled Task [" + bootsScheduledTask.name() + "] has parameters. Scheduled Tasks are not allowed to have parameters");
            }
            if (Strings.isNullOrEmpty(bootsScheduledTask.name())) {
                throw new IllegalArgumentException("Scheduled Task in class [" + method.getDeclaringClass().getName() + "] tried to register without a name");
            }
            if (bootsScheduledTask.delay() <= 0L && bootsScheduledTask.interval() <= 0L) {
                throw new IllegalArgumentException("Scheduled task [" + bootsScheduledTask.name() + "] has to have either a delay or interval. They both cannot be 0");
            }
            load(bootsScheduledTask, method, method.getDeclaringClass(), plugin);
        }
    }

    @Override
    public void register(RegisteredScheduledTask task, Plugin plugin) {
        if (task.getBootsScheduledTask().async()) {
            if (task.getBootsScheduledTask().interval() <= 0L) {
                Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, task.getTask(), task.getBootsScheduledTask().delay());
            } else {
                Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, task.getTask(), task.getBootsScheduledTask().delay(), task.getBootsScheduledTask().interval());
            }
        } else {
            if (task.getBootsScheduledTask().interval() <= 0L) {
                Bukkit.getScheduler().runTaskLater(plugin, task.getTask(), task.getBootsScheduledTask().delay());
            } else {
                Bukkit.getScheduler().runTaskTimer(plugin, task.getTask(), task.getBootsScheduledTask().delay(), task.getBootsScheduledTask().interval());
            }
        }
        Boots.getBootsLogger().info(getPluginName(plugin) + "Registered schedule task [" + task.getBootsScheduledTask().name() + "]");
    }

    private void load(BootsScheduledTask bootsScheduledTask, Method method, Class<?> clazz, Plugin plugin) {
        Runnable task = clazz.equals(plugin.getClass()) ?
                () -> invokeMainClassMethod(plugin, method) :
                () -> invokeMethod(clazz, method);
        processor.getRegistry().put(new RegisteredScheduledTask(bootsScheduledTask, task), plugin);
    }

}
