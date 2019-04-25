# Boots
Boots is a Spigot/Bukkit plugin for plugin developers. It provides an annotation based API, extending off
of the existing plugin management. 

The goal with Boots is to simplify your plugin development lifecycle, we hope to achieve this 
providing the most simplistic, stable, and powerful plugin framework available. 

## Features
* Command register with @BootsCommand
* Listener register with @BootsListener
* Task scheduling with @BootsScheduledTask

## Project Setup
Maven 
```xml
<repositories>
    <repository>
        <id>spigot-repo</id>
        <url>https://hub.spigotmc.org/nexus/content/groups/public/</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>org.spigotmc</groupId>
        <artifactId>spigot-api</artifactId>
        <version>1.13.2-R0.1-SNAPSHOT</version>
        <scope>provided</scope>
    </dependency>
    <dependency>
        <groupId>net.sw4pspace.mc</groupId>
        <artifactId>boots</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```
plugin.yml
```yaml
name: <plugin name>
description: <plugin description>
version: <plugin version>
main: <plugin main class>
depend: [ "Boots" ]
```

Boots listens for plugin's onEnable and with initialize its framework. This will only happen if 
your plugin depends on Boots.

## How it works
Boots scans the class path for class/methods annotated with its annotations when your plugin is enabled. 
It will then register any commands, listeners, etc. so that you dont have to worry about it.