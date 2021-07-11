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

package net.sw4pspace.mc.boots.utilities;

import net.sw4pspace.mc.boots.annotations.ImplementedBy;
import net.sw4pspace.mc.boots.utilities.impl.FileUtilityImpl;
import org.bukkit.plugin.Plugin;

import java.io.IOException;

@ImplementedBy(FileUtilityImpl.class)
public interface FileUtility {

    void writeToPluginDirectory(Plugin plugin, String name, String content) throws IOException;

    String readFromPluginDirectory(Plugin plugin, String name) throws IOException;

    boolean existsInPluginDirectory(Plugin plugin, String name);

}
