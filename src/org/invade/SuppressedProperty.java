/*
 * SuppressedProperty.java
 *
 * Created on August 17, 2005, 3:02 PM
 *
 */

package org.invade;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SuppressedProperty {
  boolean value() default true;
}