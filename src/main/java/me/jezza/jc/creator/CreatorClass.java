package me.jezza.jc.creator;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import me.jezza.jc.JCreate;
import me.jezza.jc.interfaces.Command;
import me.jezza.jc.interfaces.Contributor;
import me.jezza.jc.interfaces.Creator;
import me.jezza.jc.interfaces.Ignore;
import me.jezza.jc.interfaces.Parameter;
import me.jezza.jc.util.Strings;
import me.jezza.jc.util.SuffixMap;

/**
 * @author Jezza
 */
final class CreatorClass {
	final Class<?> type;
	final Command classParam;

	final List<Contributor> fields;

	private Object instance;

	CreatorClass(Class<?> type, SuffixMap<String, Creator> creatorMap) {
		this.type = type;
		classParam = type.getDeclaredAnnotation(Command.class);
		fields = processFields();
		processMethods(creatorMap);
	}

	private List<Contributor> processFields() {
		Field[] fields = type.getDeclaredFields();
		List<Contributor> contributors = new ArrayList<>(fields.length);
		long start = System.nanoTime();
		for (Field field : fields) {
			if (field.isAnnotationPresent(Ignore.class))
				continue;
			Parameter parameter = field.getDeclaredAnnotation(Parameter.class);
			if (parameter != null)
				contributors.add(new CreatorField(this, field, parameter));
		}
		long end = System.nanoTime();
		System.out.println("Fields:  " + (end - start));
		return contributors;
	}

	private void processMethods(SuffixMap<String, Creator> creatorMap) {
		long start = System.nanoTime();
		for (Method method : type.getDeclaredMethods()) {
			final Command param;
			if (method.isAnnotationPresent(Ignore.class)
					|| (param = method.getDeclaredAnnotation(Command.class)) == null) {
				continue;
			}
			CreatorMethod creatorMethod = new CreatorMethod(this, classParam, method, param);
			creatorMap.put(creatorMethod.params(), creatorMethod);
		}
		long end = System.nanoTime();
		System.out.println("Methods: " + (end - start));
	}

	public Object instance() {
		if (instance == null) {
			try {
				instance = type.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw JCreate.error(Strings.format("Failed to instantiate Creator({}).", type), e);
			}
		}
		return instance;
	}

	@Override
	public String toString() {
		return type.toString() + '(' + 0 + " Methods)";
	}
}
