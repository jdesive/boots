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

package net.sw4pspace.mc.boots;

import net.sw4pspace.mc.boots.annotations.BootsScheduledTask;
import org.bukkit.Bukkit;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static net.sw4pspace.mc.boots.Boots.getCurrentTps;

public class BootsTasks {

    @BootsScheduledTask(name = "boots:tps-monitor", interval = 1L)
    public Runnable tpsMonitor() {
        final AtomicLong currSec = new AtomicLong(0L);
        final AtomicInteger ticks = new AtomicInteger(0);
        return () -> {
            long calcSec = (System.currentTimeMillis() / 1000L);
            if (currSec.get() == calcSec) {
                ticks.incrementAndGet();
                return;
            }
            currSec.set(calcSec);
            getCurrentTps().set(getCurrentTps().get() == 0 ? ticks.get() : ((getCurrentTps().get() + ticks.get()) / 2));
            ticks.set(0);
        };
    }

    @BootsScheduledTask(name = "boots:tps-broadcast", interval = 20L, async = true)
    public Runnable tpsBroadcast() {
        return () -> {
            Bukkit.broadcastMessage("TPS: " + getCurrentTps().get());
        };
    }

}
