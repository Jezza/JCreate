package me.jezza.jc.interfaces;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Jezza
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
	String DEFAULT_JOINER = " ";

	String value();

	String joiner() default DEFAULT_JOINER;

	boolean caseInsensitive() default true;
}
