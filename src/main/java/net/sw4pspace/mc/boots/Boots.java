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

import lombok.Getter;
import net.sw4pspace.mc.boots.manager.InventoryManager;
import org.bukkit.Bukkit;

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

    @Getter
    private static final Logger bootsLogger = Bukkit.getPluginManager().getPlugin("Boots").getLogger();

    // Managers
    @Getter private static InventoryManager inventoryManager;

    static {
        inventoryManager = new InventoryManager();
    }


}
