package com.github.darthcofferus.instance_creator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link InstanceCreator} will create instances of classes marked with this annotation.
 * @version 1.0
 * @author Darth Cofferus
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CreatingInstance {}