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
import net.sw4pspace.mc.boots.annotations.BootsListener;
import net.sw4pspace.mc.boots.annotations.CraftingRecipe;
import net.sw4pspace.mc.boots.exception.BootsRegistrationException;
import net.sw4pspace.mc.boots.models.RegisteredRecipe;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

public class RecipeInitializer implements Initializer<RegisteredRecipe>{

    @Override
    public void check(Class<?> clazz, Plugin plugin) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(CraftingRecipe.class)) {
                if (method.getReturnType().isAssignableFrom(ShapedRecipe.class) || method.getReturnType().isAssignableFrom(ShapelessRecipe.class)) {
                    load(method, clazz, plugin);
                } else {
                    throw new BootsRegistrationException(method, CraftingRecipe.class, Recipe.class);
                }
            }
        }
    }

    @Override
    public void register(RegisteredRecipe registeredRecipe, Plugin plugin) {
        plugin.getServer().addRecipe(registeredRecipe.getRecipe());
        Boots.getBootsLogger().info(getPluginName(plugin) + "Registered crafting recipe [" + registeredRecipe.getMethod().getName() + "] in class [" + registeredRecipe.getClazz().getName() + "]");
    }

    private void load(Method method, Class<?> clazz, Plugin plugin) {
        Recipe recipe = clazz.equals(plugin.getClass()) ?
                (Recipe) invokeMainClassMethod(plugin, method) :
                (Recipe) invokeMethod(clazz, method);
        BootsManager.getRegisteredRecipes().put(new RegisteredRecipe(clazz, method, recipe), plugin);
    }

}
