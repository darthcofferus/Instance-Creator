package com.github.darthcofferus.instance_creator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Classes marked with this annotation will be ignored by the {@link InstanceCreator} when creating instances.
 * @version 1.0
 * @author Darth Cofferus
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Ignoring {}