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

package net.sw4pspace.mc.boots.economy.manager;

import net.sw4pspace.mc.boots.annotations.BootsValue;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class FileSystemEconomyManager implements EconomyManager {

    private YamlConfiguration config;
    private File configFile;

    @BootsValue("settings.startingBalance")
    private double startingBalance;

    public FileSystemEconomyManager(YamlConfiguration config, File configFile) {
        this.config = config;
        this.configFile = configFile;
    }

    @Override
    public boolean deposit(Player player, double amount) {
        return this.deposit(player.getUniqueId(), amount);
    }

    @Override
    public boolean deposit(UUID player, double amount) {
        if(isLocked(player)) return false;

        String path = "database." + player.toString() + ".balance";
        double bal = this.getBalance(player);
        config.set(path, bal + amount);
        return this.save();
    }

    @Override
    public boolean withdrawal(Player player, double amount) {
        return this.withdrawal(player.getUniqueId(), amount, false);
    }

    @Override
    public boolean withdrawal(UUID player, double amount) {
        return this.withdrawal(player, amount, false);
    }

    @Override
    public boolean withdrawal(Player player, double amount, boolean overdraw) {
        return this.withdrawal(player.getUniqueId(), amount, overdraw);
    }

    @Override
    public boolean withdrawal(UUID player, double amount, boolean overdraw) {
        if(isLocked(player)) return false;

        String path = "database." + player.toString() + ".balance";
        double bal = this.getBalance(player);
        if((bal - amount) < 0 && !overdraw) { // Transaction declined
            return false;
        } else {
            config.set(path, bal - amount);
            return this.save();
        }
    }

    @Override
    public boolean transfer(Player player, Player target, double amount) {
        return withdrawal(player, amount) && deposit(target, amount);
    }

    @Override
    public boolean transfer(UUID player, UUID target, double amount) {
        return withdrawal(player, amount) && deposit(target, amount);
    }

    @Override
    public double getBalance(Player player) {
        return getBalance(player.getUniqueId());
    }

    @Override
    public double getBalance(UUID player) {
        String path = "database." + player.toString() + ".balance";
        if(!this.config.contains(path)) {
            return this.startingBalance;
        }
        return this.config.getDouble(path);
    }

    @Override
    public boolean isLocked(Player player) {
        return this.isLocked(player.getUniqueId());
    }

    @Override
    public boolean isLocked(UUID player) {
        String path = "database." + player.toString() + ".locked";
        if(this.config.contains(path)) {
            this.config.set(path, false);
            this.save();
            return false;
        }
        return config.getBoolean(path);
    }

    @Override
    public boolean exists(Player player) {
        return exists(player.getUniqueId());
    }

    @Override
    public boolean exists(UUID player) {
        String path = "database." + player.toString();
        return this.config.contains(path);
    }

    private boolean save() {
        try {
            config.save(this.configFile);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
