# Boots Quartz
Boots-Quartz is an implementation of the Quartz Scheduler in the Boots framework. 

## Features
* @BootsScheduledJob
* Quartz Asynchronous Scheduling

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
    <dependency>
        <groupId>net.sw4pspace.mc</groupId>
        <artifactId>boots-quartz</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </dependency>
    <!-- This is only for the editor, the dep will be added by the boots-quartz plugin -->
    <dependency>
        <groupId>org.quartz-scheduler</groupId>
        <artifactId>quartz</artifactId>
        <version>2.3.1</version>
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
    implementation "net.sw4pspace.mc:boots-quartz:0.0.1-SNAPSHOT"
    implementation "org.quartz-scheduler:quartz:2.3.1"
    ...
}
```