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

import org.bukkit.entity.Player;

import java.util.UUID;

public interface EconomyManager {

    boolean deposit(Player player, double amount);

    boolean deposit(UUID player, double amount);

    boolean withdrawal(Player player, double amount);

    boolean withdrawal(UUID player, double amount);

    boolean withdrawal(Player player, double amount, boolean overdraw);

    boolean withdrawal(UUID player, double amount, boolean overdraw);

    boolean transfer(Player player, Player target, double amount);

    boolean transfer(UUID player, UUID target, double amount);

    double getBalance(Player player);

    double getBalance(UUID player);

    boolean isLocked(Player player);

    boolean isLocked(UUID player);

    boolean exists(Player player);

    boolean exists(UUID player);

}
