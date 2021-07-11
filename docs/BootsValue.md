# @BootsValue
BootsValue injects a config value into a field in a class. At runtime it will look in the configuration file you specify 
for the path you specified and fill in the value to the annotated field. 

## Example

### config.yml
```yaml
test:
  string: "This is a test!"
```

### other-config.yml
```yaml
test:
  string2: "This is a test!"
```

### Java Class
```java
public class TestServiceImpl implements TestService {

    @BootsValue("test.string")
    public String testString;

    @BootsValue(value="test.string2", config="other-config")
    public String testString2;

    @Override
    public void test() {
        Boots.getBootsLogger().info("Test String: " + testString + " Test String 2: " + testString2);
    }
}
```
