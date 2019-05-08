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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Entity;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Getter
@Setter
@AllArgsConstructor
public class WorldRegion implements Cloneable, ConfigurationSerializable, Iterable<Block> {

    private World world;
    private int xMax;
    private int xMin;
    private int yMax;
    private int yMin;
    private int zMax;
    private int zMin;

    public WorldRegion(WorldRegion worldRegion) {
        this.world = worldRegion.getWorld();
        this.xMax = worldRegion.getXMax();
        this.xMin = worldRegion.getXMin();
        this.yMax = worldRegion.getYMax();
        this.yMin = worldRegion.getYMin();
        this.zMax = worldRegion.getZMax();
        this.zMin = worldRegion.getZMin();
    }

    public boolean isInRegion(Entity entity) {
        return isInRegion(entity.getLocation());
    }

    public boolean isInRegion(Block block) {
        return isInRegion(block.getLocation());
    }

    public boolean isInRegion(Location location) {
        if(location == null) return false;
        if(location.getWorld() == null) return false;
        if(world.getUID().equals(location.getWorld().getUID())) return false;
        return (xMin <= location.getX() && xMax >= location.getX() && yMin <= location.getY() && yMax >= location.getY() && zMin <= location.getZ() && zMax >= location.getX());
    }

    @Override
    protected WorldRegion clone() throws CloneNotSupportedException {
        super.clone();
        return new WorldRegion(this);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("world", world.getUID());
        map.put("xMax", xMax);
        map.put("xMin", xMin);
        map.put("yMax", yMax);
        map.put("yMin", yMin);
        map.put("zMax", zMax);
        map.put("zMin", zMin);
        return map;
    }

    @Override
    public Iterator<Block> iterator() {
        return new WorldRegionIterator(new WorldRegion(this));
    }

    public class WorldRegionIterator implements Iterator<Block> {

        private WorldRegion region;
        private World world;
        private int baseX;
        private int baseY;
        private int baseZ;
        private int sizeX;
        private int sizeY;
        private int sizeZ;
        private int x;
        private int y;
        private int z;

        public WorldRegionIterator(WorldRegion region) {
            this.region = region;
            this.world = region.getWorld();
            this.x = this.y = this.z = 0;

            this.baseX = region.getXMin();
            this.baseY = region.getYMin();
            this.baseZ = region.getZMin();
            this.sizeX = Math.abs(region.getXMax() - region.getXMin()) + 1;
            this.sizeY = Math.abs(region.getYMax() - region.getYMin()) + 1;
            this.sizeZ = Math.abs(region.getZMax() - region.getZMin()) + 1;

        }

        @Override
        public boolean hasNext() {
            return x < sizeX && y < sizeY && z < sizeZ;
        }

        @Override
        public Block next() {
            Block block = world.getBlockAt(baseX + x, baseY + y, baseZ + z);
            if (++x >= sizeX) {
                x = 0;
                if (++y >= sizeY) {
                    y = 0;
                    ++z;
                }
            }
            return block;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Modifying the world region blocks is not allowed");
        }

        public Collection<Location> getAllBlockLocations() {
            List<Location> blocks = Lists.newArrayList();
            for(int x = region.getXMin(); x <= region.getXMax(); x++){
                for(int y = region.getYMin(); y <= region.getYMax(); y++){
                    for(int z = region.getZMin(); z <= region.getZMax(); z++){
                        blocks.add(new Location(world, x, y, z));
                    }
                }
            }
            return blocks;
        }

    }

}
