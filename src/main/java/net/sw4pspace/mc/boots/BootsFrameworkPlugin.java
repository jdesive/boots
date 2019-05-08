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

import net.sw4pspace.mc.boots.annotations.*;
import net.sw4pspace.mc.boots.builder.InventoryBuilder;
import net.sw4pspace.mc.boots.models.RegionSelection;
import net.sw4pspace.mc.boots.models.WorldRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockState;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.stream.StreamSupport;

import static net.sw4pspace.mc.boots.Boots.selectRegion;

@BootsPlugin
@BootsListener
public class BootsFrameworkPlugin extends JavaPlugin implements Listener {

    /*
    - Advancements
    - Scoreboard
    - MOTD, PlayerCount, etc (These are plugin level annotations that must be specified in the main class)
    - Database manager
    - HTTP utility
    - Inventory pagination
    - TPS Util
    -
     */

    @Override
    public void onLoad() {
        new BootsManager(this, getClassLoader());
        super.onLoad();
    }

    @Override
    public void onEnable() {
        BootsManager.register(this);
        RegionSelection regionSelection = selectRegion(new Location(Bukkit.getWorld("world"), 0, 50, 0), new Location(Bukkit.getWorld("world"), 10, 60, 10));
        regionSelection.getRegion().forEach(block -> {
            BlockState state = block.getState();
            state.setType(Material.GOLD_BLOCK);
            state.update(true);
        });
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
        if (event.getPlugin().getDescription().getDepend().contains("Boots")) {
            BootsManager.register(event.getPlugin());
        }
    }

    @CraftingRecipe
    public ShapedRecipe getTestRecipe() {
        return new ShapedRecipe(new NamespacedKey("boots", "test-recipe"), new ItemStack(Material.CHEST))
                .shape("XXX", "OXO", "OXO")
                .setIngredient('X', Material.DIRT);
    }

    @BootsInventory("boots:testinv")
    public Inventory getTestInv() {
        return new InventoryBuilder("Boots Test Inventory")
                .addItems(new ItemStack(Material.CHEST))
                .craft();
    }

    @BootsBossBar("boots:testbar")
    public BossBar getTestBossBar() {
        return Bukkit.createBossBar("Test Bar", BarColor.BLUE,  BarStyle.SOLID);
    }

}