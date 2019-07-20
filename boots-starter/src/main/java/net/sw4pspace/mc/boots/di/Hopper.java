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

package net.sw4pspace.mc.boots.di;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.Map;

/**
 * Hopper
 *
 * Hopper is a Dependency injection framework built on top of the boots framework.
 * It plays a role in the main structure of the framework.
 *
 * @since 1.0
 * @author Sw4p
 */
public class Hopper {

    private static final HashMap<Class, Class> hopperClassMap = Maps.newHashMap();
    private static final HashMap<Class, Object> hopperScope = Maps.newHashMap();

    /**
     * Register a Class directly to an Object.
     *
     * This can be useful if the object you want in DI does not have an interface and you can not
     * create one.
     *
     * @param iface - The class the register
     * @param object - The object to register
     */
    public void registerToObject(Class iface, Object object) {
        register(iface, iface);
        synchronized (hopperScope) {
            hopperScope.put(iface, object);
        }
    }

    /**
     * Register an interface to an implementation
     *
     * This is the main backbone of the DI framework. The same can be achieved by annotating
     * the interface with the {@link net.sw4pspace.mc.boots.annotations.ImplementedBy} annotation
     *
     * @param iface - The interface to register
     * @param impl - The implementation to register
     */
    public void register(Class iface, Class impl) {
        Preconditions.checkNotNull(iface, "Interface cannot be null");
        Preconditions.checkNotNull(impl, "Implementation cannot be null");
        synchronized (hopperClassMap) {
            hopperClassMap.put(iface, impl);
        }
    }

    /**
     * Load the implementation for the supplied interface
     *
     * This is normally only used internally by Hopper
     *
     * @param iface The interface to fetch the implementation for
     * @return The implemented interface
     * @throws IllegalAccessException If access to the implementation class is illegal
     * @throws InstantiationException If it is impossible to instigate the implementation class
     */
    public Object load(Class iface) throws IllegalAccessException, InstantiationException {
        Class implClass = hopperClassMap.get(iface);
        if(implClass == null)
            throw new NullPointerException("Cannot find impl class for interface [" + iface.getName() + "]");

        synchronized (hopperScope) {
            Object service = implClass.newInstance();
            hopperScope.put(implClass, service);
            return service;
        }
    }

    /**
     * Get the implementation for an interface
     *
     * Load the implementation if not already
     *
     * @param iface The interface to fetch a implementation for
     * @return The implementation for the interface
     * @throws IllegalAccessException If access to the implementation class is illegal
     * @throws InstantiationException If it is impossible to instigate the implementation class
     */
    public Object get(Class iface) throws IllegalAccessException, InstantiationException {
        Class implClass = hopperClassMap.get(iface);
        if(hopperScope.containsKey(iface)) {
            return  hopperScope.get(implClass);
        }
        return load(iface);
    }

    public boolean exists(Class iface) {
        return hopperClassMap.containsKey(iface) || hopperClassMap.containsValue(iface);
    }

    public Object fetchInstance(Class iface) throws InstantiationException, IllegalAccessException {
        if(exists(iface)) {
            Class ifaceClass = fetchKeyByName(iface.getName());
            if(ifaceClass != null) {
                return get(ifaceClass);
            } else if(hopperClassMap.containsValue(iface)){
                return hopperClassMap.get(fetchClassMapValue(iface));
            }
        }
        return null;
    }

    private Class fetchKeyByName(String name) {
        return hopperClassMap
                .keySet()
                .stream()
                .filter(aClass -> name.equals(aClass.getName()))
                .findFirst()
                .orElse(null);
    }

    private Class fetchClassMapValue(Class value) {
        return hopperClassMap
                .entrySet()
                .stream()
                .filter(entry -> value.getName().equals(entry.getValue().getName()))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

}
