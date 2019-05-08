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

package net.sw4pspace.mc.boots.models;

import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;

@Getter
@Setter
@AllArgsConstructor
public class RegionSelection {

    private Location pos1;
    private Location pos2;
    private WorldRegion region;

    public RegionSelection(Location pos1, Location pos2) {
        Preconditions.checkNotNull(pos1);
        Preconditions.checkNotNull(pos2);
        Preconditions.checkNotNull(pos1.getWorld());
        Preconditions.checkNotNull(pos2.getWorld());
        Preconditions.checkArgument(pos1.getWorld().getUID().equals(pos2.getWorld().getUID()), "The two positions must be in the same world");
        this.pos1 = pos1;
        this.pos2 = pos2;
        World world = pos1.getWorld();
        int xMax = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int xMin = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int yMax = Math.max(pos1.getBlockY(), pos2.getBlockY());
        int yMin = Math.min(pos1.getBlockY(), pos2.getBlockY());
        int zMax = Math.max(pos1.getBlockZ(), pos2.getBlockZ());
        int zMin = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        region = new WorldRegion(world, xMax, xMin, yMax, yMin, zMax, zMin);
    }
}
