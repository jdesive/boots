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

package net.sw4pspace.mc.boots.init;

import net.sw4pspace.mc.boots.Boots;
import net.sw4pspace.mc.boots.BootsManager;
import net.sw4pspace.mc.boots.annotations.BootsCommand;
import net.sw4pspace.mc.boots.annotations.BootsListener;
import net.sw4pspace.mc.boots.exception.BootsRegistrationException;
import net.sw4pspace.mc.boots.models.RegisteredCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;

public class CommandInitializer implements Initializer<RegisteredCommand> {

    @Override
    public void check(Class<?> clazz, Plugin plugin) {
        if (clazz.isAnnotationPresent(BootsCommand.class)) {
            if (BukkitCommand.class.isAssignableFrom(clazz)) {
                load(clazz, plugin);
            } else {
                throw new BootsRegistrationException(clazz, BootsCommand.class, BukkitCommand.class);
            }
        }
    }

    @Override
    public void register(RegisteredCommand registeredCommand, Plugin plugin) {
        final Field bukkitCommandMap;
        try {
            bukkitCommandMap = plugin.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
            commandMap.register(registeredCommand.getCommand().getName(), registeredCommand.getCommand());
            Boots.getBootsLogger().info(getPluginName(plugin) + "Registered command \'/" + registeredCommand.getCommand().getName() + "\' to [" + registeredCommand.getClazz().getName() + "]");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void load(Class<?> clazz, Plugin plugin) {
        BukkitCommand command = clazz.equals(plugin.getClass()) ?
                (BukkitCommand) plugin :
                (BukkitCommand) newInstanceFromName(clazz.getName());
        BootsManager.getRegisteredCommands().put(new RegisteredCommand(clazz, command), plugin);
    }

}
