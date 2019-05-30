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

package net.sw4pspace.mc.boots.quartz.processor;

import com.google.common.collect.Maps;
import net.sw4pspace.mc.boots.quartz.annotations.BootsScheduledJob;
import net.sw4pspace.mc.boots.quartz.init.ScheduledJobInitializer;
import net.sw4pspace.mc.boots.quartz.models.RegisteredScheduledJob;
import net.sw4pspace.mc.boots.AnnotationProcessor;
import net.sw4pspace.mc.boots.Boots;
import net.sw4pspace.mc.boots.annotations.BootsAnnotationProcessor;
import net.sw4pspace.mc.boots.init.Initializer;
import net.sw4pspace.mc.boots.processor.ProcessorPriority;
import org.bukkit.plugin.Plugin;

import java.lang.annotation.Annotation;
import java.util.Map;

@BootsAnnotationProcessor
public class BootsScheduledJobProcessor implements AnnotationProcessor<RegisteredScheduledJob> {

    private final Map<RegisteredScheduledJob, Plugin> REGISTRY = Maps.newHashMap();

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return BootsScheduledJob.class;
    }

    @Override
    public Initializer<RegisteredScheduledJob> getInitializer() {
        return new ScheduledJobInitializer(this);
    }

    @Override
    public Map<RegisteredScheduledJob, Plugin> getRegistry() {
        return REGISTRY;
    }

    @Override
    public Plugin getOwningPlugin() {
        return Boots.getBootsPlugin();
    }

    @Override
    public int getPriority() {
        return ProcessorPriority.HIGH;
    }
}
