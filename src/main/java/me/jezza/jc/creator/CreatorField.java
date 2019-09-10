package me.jezza.jc.creator;

import java.lang.reflect.Field;
import java.util.Arrays;

import me.jezza.jc.JCreate;
import me.jezza.jc.interfaces.Contributor;
import me.jezza.jc.interfaces.Parameter;
import me.jezza.jc.util.Utils;

/**
 * @author Jezza
 */
final class CreatorField implements Contributor {
	private final CreatorClass creator;
	private final Field field;
	private final Parameter parameter;

	public CreatorField(CreatorClass creator, Field field, Parameter parameter) {
		field.setAccessible(true);
		this.creator = creator;
		this.field = field;
		this.parameter = parameter;
	}

	@Override
	public String[] process(String[] params) {
		System.out.println("Preprocess: " + Arrays.toString(params));
		try {
			final Object value;
			Class<?> type = field.getType();
			if (type == boolean.class || type == Boolean.class) {
				value = Utils.checkArrayFor(parameter.names(), params);
			} else if (type == String.class) {
//				value = "";
				return params;
			} else {
				throw new IllegalStateException("Unsupported field type: " + type + ", " + field);
			}
			field.set(creator.instance(), value);
		} catch (IllegalAccessException e) {
			throw JCreate.error("Failed to set field with value!", e);
		}
		return params;
	}

	@Override
	public String toString() {
		return field.toString();
	}
}

