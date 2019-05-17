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

package net.sw4pspace.mc.boots.processor;

import com.google.common.collect.Maps;
import net.sw4pspace.mc.boots.AnnotationProcessor;
import net.sw4pspace.mc.boots.Boots;
import net.sw4pspace.mc.boots.annotations.BootsAnnotationProcessor;
import net.sw4pspace.mc.boots.annotations.BootsInject;
import net.sw4pspace.mc.boots.annotations.ImplementedBy;
import net.sw4pspace.mc.boots.init.BootsInjectInitializer;
import net.sw4pspace.mc.boots.init.ImplementedByClassInitializer;
import net.sw4pspace.mc.boots.init.Initializer;
import net.sw4pspace.mc.boots.models.RegisteredDependency;
import org.bukkit.plugin.Plugin;

import java.lang.annotation.Annotation;
import java.util.Map;

@BootsAnnotationProcessor
public class BootsInjectProcessor implements AnnotationProcessor<RegisteredDependency> {

    private final Map<RegisteredDependency, Plugin> REGISTRY = Maps.newHashMap();

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return BootsInject.class;
    }

    @Override
    public Initializer<RegisteredDependency> getInitializer() {
        return new BootsInjectInitializer(this);
    }

    @Override
    public Map<RegisteredDependency, Plugin> getRegistry() {
        return REGISTRY;
    }

    @Override
    public Plugin getOwningPlugin() {
        return Boots.getBootsPlugin();
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
