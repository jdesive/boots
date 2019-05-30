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

package net.sw4pspace.mc.boots.exception;

import java.lang.reflect.Method;

public class BootsRegistrationException extends RuntimeException {

    public BootsRegistrationException(Method method, Class<?> annotation, Class<?> clazz) {
        super("Method [" + method.getName() + "] has the " + annotation.getClass().getName() + " annotation but does not return a type of " + clazz.getName());
    }

    public BootsRegistrationException(Class<?> scanClazz, Class<?> annotation, Class<?> clazz) {
        super("Class [" + scanClazz.getName() + "] has the " + annotation.getClass().getName() + " but does not extend a type of " + clazz.getName());
    }

    public BootsRegistrationException(Class<?> scanClazz, Class<?> annotation) {
        super("Class [" + scanClazz.getName() + "] has the " + annotation.getClass().getName() + " but is not of the correct type");
    }
}
