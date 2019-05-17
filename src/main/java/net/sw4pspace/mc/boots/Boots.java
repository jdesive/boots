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

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AtomicDouble;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import net.sw4pspace.mc.boots.di.Piston;
import net.sw4pspace.mc.boots.registries.BossBarRegistry;
import net.sw4pspace.mc.boots.registries.InventoryRegistry;
import net.sw4pspace.mc.boots.models.RegionSelection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

/**
 * Boots API
 *
 * This is the main class for the Boots API.
 *
 * @author Sw4p
 * @since 1.0
 */
public class Boots {

    @Getter private static Logger bootsLogger = Bukkit.getPluginManager().getPlugin("Boots").getLogger();
    @Getter private static Plugin bootsPlugin = Bukkit.getPluginManager().getPlugin("Boots");

    @Getter private static Piston piston;

    // Managers
    @Getter private static InventoryRegistry inventoryRegistry;
    @Getter private static BossBarRegistry bossBarRegistry;

    // Sever Statistics
    private static AtomicDouble currentTps = new AtomicDouble(20.0D); // Lombok on this seems to throw errors, should probably make a issue over at lombok github
    @Getter private static final AtomicLong currSec = new AtomicLong(0L);
    @Getter private static final AtomicInteger ticks = new AtomicInteger(0);
    @Getter private static LinkedList<Double> tpsHistory = Lists.newLinkedList();

    @Getter private Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    static {
        inventoryRegistry = new InventoryRegistry();
        bossBarRegistry = new BossBarRegistry();

        piston = new Piston();
    }

    public static AtomicDouble getCurrentTps() {
        return currentTps;
    }

    public static RegionSelection selectRegion(Location pos1, Location pos2) {
        return new RegionSelection(pos1, pos2);
    }

}
