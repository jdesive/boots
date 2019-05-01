# @BootsCommand
The BootsCommand annotation registers your BukkitCommand. This does not require you to specify anything
in your plugin.yml, all properties are set in the class. 

Create a new class that extends the Bukkit class BukkitCommand. Make sure this class has a public 
no argument constructor. After adding the @BootsCommand annotation your command will be automatically 
registered. 

## Example
```java
@BootsCommand
public class LocationCommand extends BukkitCommand {

    public LocationCommand() {
        super("location");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(commandSender instanceof Player && commandSender.hasPermission(getPermission())) {
            Location location = ((Player) commandSender).getLocation();
            commandSender.sendMessage("Current Location: ");
            commandSender.sendMessage("    World: " + location.getWorld().getName());
            commandSender.sendMessage("    X: " + location.getX());
            commandSender.sendMessage("    Y: " + location.getY());
            commandSender.sendMessage("    Z: " + location.getZ());
            commandSender.sendMessage("    Yaw: " + location.getYaw());
            commandSender.sendMessage("    Pitch: " + location.getPitch());
        } else {
            commandSender.sendMessage(getPermissionMessage());
        }
        return false;
    }

    @Override
    public String getPermission() {
        return "boots.test.location";
    }

    @Override
    public List<String> getAliases() {
        return Lists.newArrayList("loc");
    }

    @Override
    public String getPermissionMessage() {
        return RED + "You do not have permission to use this command";
    }

    @Override
    public String getDescription() {
        return "Display your current location";
    }
}
```