package me.jezza.jc;

import me.jezza.jc.CreatorClass.Creator;
import me.jezza.jc.annotations.*;
import me.jezza.jc.asm.ASM;
import me.jezza.jc.lib.Utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Jezza
 */
public class Creators {
	public static final String CREATOR_PACKAGE = "me.jezza.jc.creators";
	private static final ASM data = ASM.create(CREATOR_PACKAGE);

	public static Creators instance() {
		if (INSTANCE == null)
			INSTANCE = new Creators();
		return INSTANCE;
	}

	public static Creator resolve(String[] params) {
		return instance()._resolve(params);
	}

	public static CreatorClass creatorClass(Class<?> clazz) {
		return instance().classMap.get(clazz);
	}

	private static Creators INSTANCE;

	private final Map<Class<?>, CreatorClass> classMap = new HashMap<>();
	private final Map<String[], Creator> creatorMap = new LinkedHashMap<>();

	private Creators() {
		data.classes().forEach(this::processClass);
	}

	private Creator _resolve(String[] params) {
		for (Entry<String[], Creator> entry : creatorMap.entrySet())
			System.out.println(Arrays.asList(entry.getKey()) + ":" + entry.getValue());
		for (Entry<String[], Creator> entry : creatorMap.entrySet())
			if (Utils.startsWith(params, entry.getKey()))
				return entry.getValue();
		return null;
	}

	private void processClass(Class<?> clazz) {
		if (clazz.isAnnotationPresent(CreatorIgnore.class))
			return;
		CreatorParam classParam = clazz.getDeclaredAnnotation(CreatorParam.class);
		CreatorClass creatorClass = new CreatorClass(clazz, classParam);
		classMap.put(clazz, creatorClass);

		boolean errorMethod = false;
		for (Method method : clazz.getDeclaredMethods()) {
			if (method.isAnnotationPresent(CreatorIgnore.class))
				continue;
			if (!errorMethod) {
				CreatorError error = method.getDeclaredAnnotation(CreatorError.class);
				if (error != null) {
					if (classParam == null)
						throw JCreate.error(JCreate.format("Discovered a @CreatorError when no @CreatorParam exists for the class. ({}). Exiting...", clazz));
					creatorClass.creator(method);
					errorMethod = true;
					continue;
				}
			}

			CreatorParam param = method.getDeclaredAnnotation(CreatorParam.class);
			if (param != null)
				creatorClass.creator(method, param);
		}
		for (Field field : clazz.getDeclaredFields()) {
			if (field.isAnnotationPresent(CreatorIgnore.class))
				continue;
			CreatorArgument argument = field.getDeclaredAnnotation(CreatorArgument.class);
			if (argument != null)
				creatorClass.argument(field, argument);
			CreatorClassInstance classInstance = field.getDeclaredAnnotation(CreatorClassInstance.class);
			if (classInstance != null) {
				try {
					field.setAccessible(true);
					field.set(creatorClass.instance(), creatorClass);
				} catch (IllegalAccessException e) {
					throw JCreate.error("Failed to inject the CreatorClass instance into the field.", e);
				}
			}
		}
		for (Creator creator : creatorClass.creators())
			creatorMap.put(creator.params(), creator);
	}
}
