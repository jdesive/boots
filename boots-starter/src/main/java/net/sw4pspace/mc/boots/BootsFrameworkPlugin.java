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

import net.sw4pspace.mc.boots.annotations.BootsListener;
import net.sw4pspace.mc.boots.annotations.BootsPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.java.JavaPlugin;

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
        new BootsManager(this);
        super.onLoad();
    }

    @Override
    public void onEnable() {
        BootsManager.scan(this);
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
        if (event.getPlugin().getDescription().getDepend().contains("Boots")) {
            BootsManager.scan(event.getPlugin());
        }
    }
}
