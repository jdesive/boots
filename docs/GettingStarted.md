# Getting Started
Welcome to the Boots plugin, this guide will help you get started wearing boots ;). 

This guide assumes you have a decent understanding of using annotation based frameworks in Java.

## Setting up your plugin
First things first, your going to need a workspace. Setup a project in your favorite IDE, mine's Intellij.

Create a file named `pom.xml` if not created already and make sure you have the following dependencies:

**Note:** *Make sure the version is the latest build, we may forget to update the docs*
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>net.sw4pspace.mc.boots</groupId>
    <artifactId>test-boots</artifactId>
    <version>1.0-SNAPSHOT</version>

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
            <version>1.14-R0.1-SNAPSHOT</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>net.sw4pspace.mc</groupId>
            <artifactId>boots</artifactId>
            <version>0.0.1</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.5</version>
                <configuration>
                    <source>8</source>
                    <target>8</target>
                </configuration>
            </plugin>
        </plugins>
    </build>

</project>
```

Now navigate to `src > main > resources` and create a file called plugin.yml:
```yaml
name: TestBoots
description: Boots test plugin
version: 1.0.0
main: net.sw4pspace.mc.boots.testboots.TestBootsPlugin
depend: [ "Boots" ]
```
It's important that your depend on Boots! This makes sure that your plugin is loaded after the Boots plugin has 
initialized and loaded all the classes. The Boots plugin also uses this in the auto registration to determine
which plugins contain Boots code. 

Navigate to `src > main > java` and create your package, our example will be `net.sw4pspace.mc.boots.testboots`. 
Your project structure should now look like the below:
```text
testboots
  - src
    - main
      - java
        - net
          - sw4pspace
            - mc
              - boots
                - testboots
      - resources
        - plugin.yml
  - pom.xml
```

Now your finally get you create your plugin's main class. Under the new package your just created, create a
new Java class named after your plugin. Example: `TestBootsPlugin.java`

Below is an example template to get you started, it includes the @BootsPlugin annotation to auto register 
your plugin and the @OnRegister annotation to hook into the registration of your plugin. 
```java
@BootsPlugin
public class TestBootsPlugin extends JavaPlugin {

    @Override
    public void onLoad() {
        // Plugin is loading -- Bukkit method
    }

    @Override
    public void onEnable() {
        // Plugin is enabling -- Bukkit method
    }
    
    @Override
    public void onDisable() {
        // Plugin is disabling -- Bukkit method
    }
    
    @OnRegister
    public void onRegister() {
        // Plugin is registering -- Boots method
    }

}
```

And... that's it. Now you have a Boots plugin, run `mvn clean package` to generate the jar file and drop 
it in your plugins folder with Boots. 

If all checks out head over to the other guides to learn how to use the rest of your Boots. 