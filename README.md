# Boots

## Overview
Boots is a plugin development library for Spigot/Bukkit plugins. It provides quick and simple utilities, 
annotations, and more focusing on common tasks during development.

The goal with Boots is to simplify your plugin development, we hope to achieve this 
providing the most simplistic, stable, and powerful plugin framework available. 

**It provides an opinionated library for the Spigot API which helps create faster, more efficient, production-grade code
for Spigot/Bukkit plugins.**

## Features
### Annotations
* [@BootsCommand](/docs/BootsCommand.md)
* @BootsListener
* @BootsScheduledTask
* @BootsInventory
* @BootsBossBar
* @BootsCraftingRecipe
* @DefaultGamemode
* @EnableWhitelist
* @IdleTimeout
* @SpawnRadius
* @OnRegister
### Utilities
* Inventory Registry
* Boss Bar Registry
* TPS Monitor
* Inventory Builder

## Project Setup
### Build Tool
#### Maven
```xml
...
<repositories>
    ...
    <repository>
        <id>sw4pspace-repo</id>
        <url>https://nexus.sw4pspace.net/nexus/content/groups/public/</url>
    </repository>
</repositories>

<dependencies>
    ...
    <dependency>
        <groupId>net.sw4pspace.mc</groupId>
        <artifactId>boots</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </dependency>
</dependencies>
```
#### Gradle
```groovy
...
repositories {
    mavenCentral()
    maven { url = 'https://nexus.sw4pspace.net/nexus/content/groups/public/' }
    ...
}

dependencies {
    implementation "net.sw4pspace.mc:boots:0.0.1-SNAPSHOT"
    ...
}
```

###plugin.yml
```yaml
name: <plugin name>
description: <plugin description>
version: <plugin version>
main: <plugin main class>
depend: [ "Boots" ] # Important part!
```

Boots listens for plugin's onEnable and with initialize its framework. This will only happen if 
your plugin depends on Boots.

## Documentation
All of the docs can be found [here](/docs)

## License
This project is licensed under the [MIT License](/LICENSE)

## Contributors
[Sw4pSpace](https://sw4pspace.net) Developers:
* [Sw4p](https://github.com/jdesive)