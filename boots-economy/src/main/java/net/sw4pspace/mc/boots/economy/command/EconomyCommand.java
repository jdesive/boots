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

package net.sw4pspace.mc.boots.economy.command;

import com.google.common.collect.Lists;
import net.sw4pspace.mc.boots.annotations.BootsCommand;
import net.sw4pspace.mc.boots.economy.manager.EconomyManager;
import net.sw4pspace.mc.boots.economy.EconomyPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.util.List;

import static org.bukkit.ChatColor.*;

@BootsCommand
public class EconomyCommand extends BukkitCommand {

    private EconomyManager economyManager;

    public EconomyCommand() {
        super("economy");
        economyManager = EconomyPlugin.economyManager;
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(!commandSender.hasPermission(this.getPermission())) {
            commandSender.sendMessage(this.getPermissionMessage());
            return false;
        }
        if(strings.length == 2) {
            if("add".equalsIgnoreCase(strings[0])) {
                if(this.checkArguments(commandSender, strings)) return false;
                Double amt = this.parseAmount(strings[1]);
                this.economyManager.deposit((Player) commandSender, amt);
                commandSender.sendMessage(GREEN + "\'" + WHITE + amt + GREEN + "\' has been deposited to your account");
                return true;
            } else if("remove".equalsIgnoreCase(strings[0])) {
                if(this.checkArguments(commandSender, strings)) return false;
                Double amt = this.parseAmount(strings[1]);
                this.economyManager.withdrawal((Player) commandSender, amt);
                commandSender.sendMessage(RED + "\'" + WHITE + amt + RED + "\' has been withdrawn from your account");
                return true;
            }
        } else if(strings.length >= 3) {

            if(strings.length == 4) {
                if("transfer".equalsIgnoreCase(strings[0])) {

                    Player player = Bukkit.getPlayer(strings[1]);
                    Player target = Bukkit.getPlayer(strings[2]);
                    if(player == null || target == null) {
                        commandSender.sendMessage(RED + "Was unable to find one of the supplied players");
                        return false;
                    }

                    Double amt = this.parseAmount(strings[3]);
                    if(amt == null) {
                        commandSender.sendMessage(RED + "\'" + WHITE + strings[3] + RED + "\' is not a number");
                    }

                    this.economyManager.transfer(player, target, amt);
                    commandSender.sendMessage(GREEN + "Transfer complete");
                    player.sendMessage(RED + "\'" + WHITE + amt + RED + "\' has been withdrawn from your account");
                    target.sendMessage(GREEN + "\'" + WHITE + amt + GREEN + "\' has been transferred to your account");
                    return true;
                }
            } else if(strings.length == 3) {



            }


        }

        return false;
    }

    @Override
    public String getPermission() {
        return "boots.economy";
    }

    @Override
    public List<String> getAliases() {
        return Lists.newArrayList("econ", "eco");
    }

    @Override
    public String getPermissionMessage() {
        return RED + "You do not have permission to use this command";
    }

    @Override
    public String getDescription() {
        return "Main economy command. Allows you to add/remove money to players";
    }

    private boolean checkArguments(CommandSender commandSender, String[] strings) {
        if(!(commandSender instanceof Player)) {
            commandSender.sendMessage(RED + "Only players can use this command");
            return true;
        }

        if(this.parseAmount(strings[1]) == null) {
            commandSender.sendMessage(RED + "\'" + WHITE + strings[1] + RED + "\' is not a number");
            return true;
        }
        return false;
    }

    private Double parseAmount(String amtStr) {
        try {
            return Double.parseDouble(amtStr);
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
