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

package net.sw4pspace.mc.boots.quartz;

import net.sw4pspace.mc.boots.Boots;
import net.sw4pspace.mc.boots.annotations.BootsPlugin;
import net.sw4pspace.mc.boots.annotations.OnServerStart;
import org.bukkit.plugin.java.JavaPlugin;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

@BootsPlugin
public class QuartzConfig extends JavaPlugin {

    private static SchedulerFactory schedulerFactory;
    private static Scheduler scheduler;

    static {
        Boots.getPiston().registerToObject(SchedulerFactory.class, new StdSchedulerFactory());
        try {
            schedulerFactory = (SchedulerFactory) Boots.getPiston().get(SchedulerFactory.class);
            Boots.getPiston().registerToObject(Scheduler.class, schedulerFactory.getScheduler());
            scheduler = (Scheduler) Boots.getPiston().get(Scheduler.class);
        } catch (IllegalAccessException | InstantiationException | SchedulerException e) {
            e.printStackTrace();
        }
    }

    @OnServerStart
    public void onRegister() {
        try {
            scheduler.start();
            getLogger().info("Quartz scheduling started");
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

}
