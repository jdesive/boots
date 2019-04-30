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

package net.sw4pspace.mc.boots.builder;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class InventoryBuilder {

    private Inventory inventory;

    public InventoryBuilder(String title) {
        this.inventory = Bukkit.createInventory(null, 9, title);
    }

    public InventoryBuilder(String title, int size) {
        this.inventory = Bukkit.createInventory(null, size, title);
    }

    public InventoryBuilder(String title, int size, InventoryHolder owner) {
        this.inventory = Bukkit.createInventory(owner, size, title);
    }

    public InventoryBuilder addItems(ItemStack... itemStacks) {
        this.inventory.addItem(itemStacks);
        return this;
    }

    public InventoryBuilder setItem(int idx, ItemStack itemStack) {
        this.inventory.setItem(idx, itemStack);
        return this;
    }

    public Inventory craft() {
        return this.inventory;
    }

}
