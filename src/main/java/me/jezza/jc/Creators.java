package me.jezza.jc;

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import me.jezza.jc.CreatorClass.Creator;
import me.jezza.jc.annotations.CreatorError;
import me.jezza.jc.annotations.CreatorIgnore;
import me.jezza.jc.annotations.CreatorParam;
import me.jezza.jc.lib.Utils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;

/**
 * @author Jezza
 */
public class Creators {
	private static final Predicate<ClassInfo> PACKAGE_FILTER = input -> input.getPackageName().startsWith("me.jezza.jc.creators");

	public static Creators instance() {
		if (INSTANCE == null)
			INSTANCE = new Creators().init();
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
	}

	private Creator _resolve(String[] params) {
		for (Entry<String[], Creator> entry : creatorMap.entrySet()) {
			System.out.println(Arrays.asList(entry.getKey()) + ":" + entry.getValue());
		}
		for (Entry<String[], Creator> entry : creatorMap.entrySet())
			if (Utils.startsWith(params, entry.getKey()))
				return entry.getValue();
		return null;
	}

	private Creators init() {
		ClassPath path;
		try {
			path = ClassPath.from(Creators.class.getClassLoader());
		} catch (IOException e) {
			throw JCreate.error("Failed to load Creators.", e);
		}
		path.getAllClasses().parallelStream().filter(PACKAGE_FILTER).map(ClassInfo::load).forEach(this::processClass);
		return this;
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
						throw JCreate.error(JCreate.format("Discovered a @CreatorError when no @CreatorParam exists for {}. This isn't correct. Exiting...", clazz));
					creatorClass.creator(method);
					errorMethod = true;
					continue;
				}
			}

			CreatorParam param = method.getDeclaredAnnotation(CreatorParam.class);
			if (param != null) {
				creatorClass.creator(method, param);
			}
		}
		for (Creator creator : creatorClass.creators())
			creatorMap.put(creator.params(), creator);
	}
}
