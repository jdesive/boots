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

package net.sw4pspace.mc.boots.quartz.init;

import net.sw4pspace.mc.boots.quartz.BootsJob;
import net.sw4pspace.mc.boots.quartz.annotations.BootsScheduledJob;
import net.sw4pspace.mc.boots.quartz.models.RegisteredScheduledJob;
import net.sw4pspace.mc.boots.quartz.processor.BootsScheduledJobProcessor;
import net.sw4pspace.mc.boots.Boots;
import net.sw4pspace.mc.boots.init.ClassInitializer;
import org.bukkit.plugin.Plugin;
import org.quartz.*;

public class ScheduledJobInitializer implements ClassInitializer<RegisteredScheduledJob> {

    private BootsScheduledJobProcessor processor;

    public ScheduledJobInitializer(BootsScheduledJobProcessor processor) {
        this.processor = processor;
    }

    @Override
    public void check(Class<?> clazz, Plugin plugin) {
        if(BootsJob.class.isAssignableFrom(clazz)) {
            processor.getRegistry().put(new RegisteredScheduledJob(clazz.getAnnotation(BootsScheduledJob.class), (Class<? extends Job>) clazz), plugin);
        }
    }

    @Override
    public void register(RegisteredScheduledJob job, Plugin plugin) {
        BootsJob bootsJob = (BootsJob) instanceFromName(job.getClazz().getName());
        Trigger trigger = bootsJob.getSchedule();
        JobDetail detail = JobBuilder.newJob(job.getClazz())
                .storeDurably(bootsJob.isDurable())
                .withIdentity(bootsJob.getJobKey())
                .withDescription(bootsJob.getDescription())
                .build();
        try {
            Scheduler scheduler = (Scheduler) Boots.getPiston().get(Scheduler.class);
            scheduler.scheduleJob(detail, trigger);
            Boots.getBootsLogger().info(getPluginName(plugin) + "Registered scheduled job [" + job.getClass().getName() + "]");
        } catch (IllegalAccessException | InstantiationException | SchedulerException e) {
            e.printStackTrace();
        }
    }

}
