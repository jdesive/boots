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

import com.google.common.collect.Maps;
import lombok.Getter;
import net.sw4pspace.mc.boots.AnnotationProcessor;
import net.sw4pspace.mc.boots.Boots;
import net.sw4pspace.mc.boots.BootsManager;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class AnnotationProcessorInitializer implements ClassInitializer<AnnotationProcessor<?>> {

    @Getter
    private Map<AnnotationProcessor<?>, Plugin> registry = Maps.newHashMap();

    @Override
    public void check(Class<?> clazz, Plugin plugin) {
        if(AnnotationProcessor.class.isAssignableFrom(clazz) && !clazz.equals(plugin.getClass())) {
            AnnotationProcessor<?> annotationProcessor = (AnnotationProcessor<?>) instanceFromName(clazz.getName());
            registry.put(annotationProcessor, plugin);
        }
    }

    @Override
    public void register(AnnotationProcessor<?> obj, Plugin plugin) {
        BootsManager.registerAnnotationProcessor(obj.getAnnotation(), obj.getInitializer(), (HashMap<?, Plugin>) obj.getRegistry(), obj.getOwningPlugin(), obj.getPriority());
        Boots.getBootsLogger().info("Registered annotation processor [" + obj.getClass().getName() + "] for " + obj.getAnnotation().getName());
    }
}
