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
import org.bukkit.boss.BossBar;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.MalformedParametersException;
import java.lang.reflect.Method;

public class BossBarInitializer implements Initializer<RegisteredBossBar>{

    @Override
    public void check(Class<?> clazz, Plugin plugin) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(BootsBossBar.class)) {
                if (method.getParameterCount() > 0) {
                    throw new MalformedParametersException("BootsBossBar methods cannot have parameters");
                }
                load(method.getAnnotation(BootsBossBar.class), method, clazz, plugin);
            }
        }
    }

    @Override
    public void register(RegisteredBossBar registeredBossBar, Plugin plugin) {
        Boots.getBossBarManager().registerBossBar(registeredBossBar);
        Boots.getBootsLogger().info(getPluginName(plugin) + "Registered boss bar [" + registeredBossBar.getId() + "] from class " + registeredBossBar.getClazz().getName());
    }

    private void load(BootsBossBar bootsBossBar, Method method, Class<?> clazz, Plugin plugin){
        BossBar bossbar = clazz.equals(plugin.getClass()) ?
                (BossBar) invokeMainClassMethod(plugin, method) :
                (BossBar) invokeMethod(clazz, method);
        BootsManager.getRegisteredBossBars().put(new RegisteredBossBar(bootsBossBar.value(), bossbar, clazz), plugin);

    }

}
