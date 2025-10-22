# Instance Creator
## [Docs](https://darthcofferus.github.io/Instance-Creator/)
## Description
If you have a situation where you need to create objects of certain
classes without knowing their names (for example, classes with a
common parent), then this library is for you! It will find the classes
you need (in the entire program or only in certain packages), you just
need to specify their parent class/interface. For more convenience,
I've made two instance creation modes: **GREEDY** and **LAZY**. The
**GREEDY** mode will create instances of all classes, unless you have
marked them with the **@Ignoring** annotation. **LAZY** mode, on the
contrary, will create instances of only those classes that are marked
with the **@CreatingInstance** annotation. The library works with both
compiled files and JAR files. This library is well suited for creating
commands (for example, console commands or bot commands).
## Examples
**Nested static classes are made for compactness of the example.**
```java
import com.github.darthcofferus.instance_creator.CreatingInstance;
import com.github.darthcofferus.instance_creator.Ignoring;
import com.github.darthcofferus.instance_creator.InstanceCreator;

public class Main {

    public static void main(String[] args) {
        System.out.println("GREEDY mode:");
        new InstanceCreator(InstanceCreator.Mode.GREEDY, Base.class).createInstances();
        System.out.println("----------------\n");

        System.out.println("LAZY mode:");
        new InstanceCreator(InstanceCreator.Mode.LAZY, Base.class).createInstances();
        System.out.println("----------------\n");

        System.out.println("Constructor with parameters:");
        InstanceCreator instanceCreator = new InstanceCreator(InstanceCreator.Mode.GREEDY, Test.class);
        instanceCreator.setCreatingInstanceAction(aClass -> {
            try {
                aClass.getDeclaredConstructor(String.class).newInstance("Hello, Instance Creator!");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        instanceCreator.createInstances();
        System.out.println("----------------\n");
    }

    @Ignoring
    static class Base {
        Base() {
            System.out.println("Instance created: " + getClass().getSimpleName());
        }
    }

    @CreatingInstance
    static class Inheritor1 extends Base {}

    static class Inheritor2 extends Base {}

    static class Test {
        Test(String parameter) {
            System.out.println("Instance created: " + getClass().getSimpleName() + ". Parameter: " + parameter);
        }
    }

}
```
**Execution result:**
```
GREEDY mode:
Instance created: Inheritor1
Instance created: Inheritor2
----------------

LAZY mode:
Instance created: Inheritor1
----------------

Constructor with parameters:
Instance created: Test. Parameter: Hello, Instance Creator!
----------------
```
## Maven
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.darthcofferus</groupId>
        <artifactId>instance-creator</artifactId>
        <version>1.0</version>
    </dependency>
</dependencies>
```