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

package net.sw4pspace.mc.boots.utilities.impl;

import net.sw4pspace.mc.boots.utilities.FileUtility;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtilityImpl implements FileUtility {

    @Override
    public void writeToPluginDirectory(Plugin plugin, String name, String content) throws IOException {
        Path path = new File(plugin.getDataFolder(), name).toPath();
        Files.write(path, content.getBytes());
    }

    @Override
    public String readFromPluginDirectory(Plugin plugin, String name) throws IOException {
        Path path = new File(plugin.getDataFolder(), name).toPath();
        return new String(Files.readAllBytes(path));
    }

    @Override
    public boolean existsInPluginDirectory(Plugin plugin, String name) {
        return new File(plugin.getDataFolder(), name).exists();
    }

}
