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
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

@BootsPlugin
@BootsListener
@EnableWhitelist
@DefaultGamemode(GameMode.CREATIVE)
public class BootsFrameworkPlugin extends JavaPlugin implements Listener {

    /*
    - BossBars
    - Advancements
    - Scoreboard
    - MOTD, PlayerCount, etc (These are plugin level annotations that must be specified in the main class)
     */

    @Override
    public void onLoad() {
        new BootsManager(this, getClassLoader());
        super.onLoad();
    }

    @Override
    public void onEnable() {
        BootsManager.register(this);
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
        ShapedRecipe shapedRecipe = new ShapedRecipe(new NamespacedKey("boots", "test-recipe"), new ItemStack(Material.CHEST));
        shapedRecipe.shape("XXX", "OXO", "OXO");
        shapedRecipe.setIngredient('X', Material.DIRT);
        return shapedRecipe;
    }

    @BootsInventory("boots:testinv")
    public Inventory getTestInv() {
        return new InventoryBuilder("Boots Test Inventory")
                .addItems(new ItemStack(Material.CHEST))
                .craft();
    }

}