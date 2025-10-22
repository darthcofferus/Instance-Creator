package com.github.darthcofferus.instance_creator;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;

/**
 * @version 1.0
 * @author Darth Cofferus
 */
@FunctionalInterface
public interface CreatingInstanceAction {

    /** The standard action for creating an instance. Uses a parameterless constructor with any access modifier. */
    CreatingInstanceAction DEFAULT = c -> {
        try {
            Constructor<?> constructor = c.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    };

    /**
     * Creating an instance of the class.
     * @param c the class to create an instance of.
     */
    void createInstance(@NotNull Class<?> c);

}
