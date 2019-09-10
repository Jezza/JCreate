package me.jezza.jc.interfaces;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * TODO See if I can move this into parameter support as well...
 *
 * @author Jezza
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Parameter {
	/**
	 * An array of allowed command line parameters (e.g. "-d", "--outputdir", etc...).
	 * If this attribute is omitted, the field name will be used with '--'
	 */
	String[] names() default {};

	/**
	 * A description of this option.
	 */
	String description() default "";

	/**
	 * Whether this option is required.
	 */
	boolean required() default false;

	/**
	 * How many parameter values this parameter will consume. For example,
	 * an arity of 2 will allow "-pair value1 value2".
	 */
	int arity() default -1;
}
