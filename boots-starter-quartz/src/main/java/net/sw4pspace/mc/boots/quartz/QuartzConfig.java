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

    static {
        Boots.getHopper().registerToObject(SchedulerFactory.class, new StdSchedulerFactory());
        try {
            SchedulerFactory schedulerFactory = (SchedulerFactory) Boots.getHopper().get(SchedulerFactory.class);
            Boots.getHopper().registerToObject(Scheduler.class, schedulerFactory.getScheduler());
        } catch (IllegalAccessException | InstantiationException | SchedulerException e) {
            e.printStackTrace();
        }
    }

    @OnServerStart
    public void onServerStart() {
        try {
            ((Scheduler) Boots.getHopper().get(Scheduler.class)).start();
            getLogger().info("Quartz scheduling started");
        } catch (SchedulerException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }

}
