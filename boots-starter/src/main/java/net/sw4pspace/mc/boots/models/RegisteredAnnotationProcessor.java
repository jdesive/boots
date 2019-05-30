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

package net.sw4pspace.mc.boots.models;

import lombok.*;
import net.sw4pspace.mc.boots.init.Initializer;
import org.bukkit.plugin.Plugin;

import java.lang.annotation.Annotation;
import java.util.HashMap;

@Getter
@Setter
@AllArgsConstructor
public class RegisteredAnnotationProcessor<T> {
    private final Class<? extends Annotation> annotation;
    private final Initializer<T> initializer;
    private final HashMap<T, Plugin> targetMap;
    private final Plugin ownerPlugin;
    private final int priority;
}
