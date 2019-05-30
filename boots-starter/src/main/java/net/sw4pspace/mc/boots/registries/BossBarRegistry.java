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

package net.sw4pspace.mc.boots.registries;

import com.google.common.collect.Maps;
import net.sw4pspace.mc.boots.models.RegisteredBossBar;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class BossBarRegistry {

    private HashMap<String, BossBar> bossBarList = Maps.newHashMap();

    public void registerBossBar(RegisteredBossBar registeredBossBar) {
        bossBarList.put(registeredBossBar.getId(), registeredBossBar.getBossBar());
    }

    public void addPlayer(Player player, String id) {
        bossBarList.get(id).addPlayer(player);
    }

    public void removePlayer(Player player, String id) {
        bossBarList.get(id).removePlayer(player);
    }

    public void setVisible(String id, boolean value) {
        bossBarList.get(id).setVisible(value);
    }

}
