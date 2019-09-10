package me.jezza.jc.creator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import me.jezza.jc.JCreate;
import me.jezza.jc.interfaces.Command;
import me.jezza.jc.interfaces.Contributor;
import me.jezza.jc.interfaces.Creator;
import me.jezza.jc.util.Strings;

/**
 * @author Jezza
 */
final class CreatorMethod implements Creator {
	private final CreatorClass creatorClass;
	private final Method method;
	private final String[] params;

	public CreatorMethod(CreatorClass creatorClass, Command classParam, Method method, Command methodParam) {
		method.setAccessible(true);
		this.creatorClass = creatorClass;
		this.method = method;

		List<String> parts = new ArrayList<>();
		if (classParam != null)
			parts.add(classParam.value());
		parts.add(methodParam.value());
		params = parts.toArray(new String[0]);
		System.out.println(parts);
	}

	String[] params() {
		return params;
	}

	@Override
	public void create(String[] params) {
		for (Contributor contributor : creatorClass.fields)
			params = contributor.process(params);
		try {
			Object[] parameters = new Object[]{params};
			method.invoke(creatorClass.instance(), parameters);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw JCreate.error(Strings.format("Failed to execute creator method({}) in {}", method, creatorClass.type), e);
		}
	}

	@Override
	public String toString() {
		return method.toString();
	}
}