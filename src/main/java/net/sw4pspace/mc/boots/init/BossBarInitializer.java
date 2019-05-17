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
import net.sw4pspace.mc.boots.annotations.BootsBossBar;
import net.sw4pspace.mc.boots.models.RegisteredBossBar;
import net.sw4pspace.mc.boots.processor.BootsBossBarProcessor;
import org.bukkit.boss.BossBar;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.MalformedParametersException;
import java.lang.reflect.Method;

public class BossBarInitializer implements MethodInitializer<RegisteredBossBar> {

    private BootsBossBarProcessor processor;

    public BossBarInitializer(BootsBossBarProcessor processor) {
        this.processor = processor;
    }

    @Override
    public void check(Method method, Plugin plugin) {
        if (method.getParameterCount() > 0) {
            throw new MalformedParametersException("BootsBossBar methods cannot have parameters");
        }
        load(method.getAnnotation(BootsBossBar.class), method, method.getDeclaringClass(), plugin);
    }

    @Override
    public void register(RegisteredBossBar registeredBossBar, Plugin plugin) {
        Boots.getBossBarRegistry().registerBossBar(registeredBossBar);
        Boots.getBootsLogger().info(getPluginName(plugin) + "Registered boss bar [" + registeredBossBar.getId() + "] from class " + registeredBossBar.getClazz().getName());
    }

    private void load(BootsBossBar bootsBossBar, Method method, Class<?> clazz, Plugin plugin){
        BossBar bossbar = clazz.equals(plugin.getClass()) ?
                (BossBar) invokeMainClassMethod(plugin, method) :
                (BossBar) invokeMethod(clazz, method);
        processor.getRegistry().put(new RegisteredBossBar(bootsBossBar.value(), bossbar, clazz), plugin);

    }

}
