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

import com.google.common.collect.Maps;
import lombok.Getter;

import java.util.HashMap;

public class Piston {

    // Dependency Injection
    @Getter private static final HashMap<Class, Class> pistonClassMap = Maps.newHashMap();
    @Getter private static final HashMap<Class, Object> pistonScope = Maps.newHashMap();

    public void registerToObject(Class iface, Object object) {
        register(iface, iface);
        synchronized (pistonScope) {
            pistonScope.put(iface, object);
        }
    }

    public void register(Class iface, Class impl) {
        pistonClassMap.put(iface, impl);
    }

    public Object load(Class iface) throws IllegalAccessException, InstantiationException {
        Class implClass = pistonClassMap.get(iface);
        synchronized (pistonScope) {
            Object service = implClass.newInstance();
            pistonScope.put(implClass, service);
            return service;
        }
    }

    public Object get(Class iface) throws IllegalAccessException, InstantiationException {
        Class implClass = pistonClassMap.get(iface);
        if(pistonScope.containsKey(iface)) {
            return  pistonScope.get(implClass);
        }
        return load(iface);
    }

}
