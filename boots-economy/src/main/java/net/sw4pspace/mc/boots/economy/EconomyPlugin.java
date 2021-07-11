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

package net.sw4pspace.mc.boots.economy;

import net.sw4pspace.mc.boots.Boots;
import net.sw4pspace.mc.boots.annotations.*;
import net.sw4pspace.mc.boots.economy.manager.EconomyManager;
import net.sw4pspace.mc.boots.economy.manager.FileSystemEconomyManager;
import net.sw4pspace.mc.boots.utilities.FileUtility;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

@BootsPlugin
@BootsListener
public class EconomyPlugin extends JavaPlugin implements Listener {

    public static EconomyManager economyManager;

    @BootsInject
    private FileUtility fileUtility;

    @BootsValue("database.type")
    private String databaseType;

    @BootsValue("database.url")
    private String databaseUrl;

    @BootsValue("settings.startingBalance")
    private double startingBalance;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        if (!economyManager.exists(event.getPlayer())) {
            economyManager.deposit(event.getPlayer(), startingBalance);
        }

    }

    @OnRegister
    public void onRegister() {
        switch (databaseType.toLowerCase()) {
            case "file":
                economyManager = this.setupFileSystemDatabase();
                break;
            default:
                throw new UnsupportedOperationException("The database type supplied is not supported: " + databaseType);

        }
    }

    private EconomyManager setupFileSystemDatabase() {
        YamlConfiguration fileSystemDatabase = Boots.getConfigFileRegistry().loadYAMLConfigFile(databaseUrl, this);

        if (fileSystemDatabase == null) {

            getLogger().info("File System database file not found: " + databaseUrl);
            return null;
        }
        FileSystemEconomyManager fileSystemEconomyManager = new FileSystemEconomyManager(fileSystemDatabase, new File(this.getDataFolder(), databaseUrl));
        Boots.getHopper().registerToObject(EconomyManager.class, fileSystemEconomyManager);
        return fileSystemEconomyManager;
    }

}
