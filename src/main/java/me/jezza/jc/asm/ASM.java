package me.jezza.jc.asm;

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import me.jezza.jc.JCreate;
import me.jezza.jc.lib.Utils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

/**
 * Annotation Storage Management
 *
 * @author Jezza
 */
public class ASM {
	private static Map<String, ASM> cache = new HashMap<>();

	private List<Class<?>> classes = new ArrayList<>();
	private List<Method> methods = new ArrayList<>();
	private List<Field> fields = new ArrayList<>();
	private List<Constructor<?>> constructors = new ArrayList<>();

	private ASM(String packageName) {
		this(input -> input.getPackageName().startsWith(packageName));
	}

	public ASM(Predicate<ClassInfo> predicate) {
		requireNonNull(predicate);
		try {
			ClassPath path = ClassPath.from(ASM.class.getClassLoader());
			path.getAllClasses().parallelStream().filter(predicate).map(ClassInfo::load).forEach(this::processClass);
		} catch (IOException e) {
			throw JCreate.error("Failed to read Classes from ClassLoader.", e);
		}
	}

	private void processClass(Class<?> clazz) {
		classes.add(clazz);
		Collections.addAll(methods, clazz.getDeclaredMethods());
		Collections.addAll(fields, clazz.getDeclaredFields());
		Collections.addAll(constructors, clazz.getDeclaredConstructors());
	}

	public ClassStream classes() {
		return new ClassStream(classes.stream());
	}

	public List<Class<?>> classesIn(String packageName) {
		return classes().in(packageName, true).toList();
	}

	public List<Class<?>> classesIn(String packageName, boolean direct) {
		return classes().in(packageName, direct).toList();
	}

	public <A extends Annotation> AnnotatedClassStream<A> classesWith(Class<A> annotation) {
		return classes().with(annotation);
	}

	public ClassStream classesFrom(Class<?> type) {
		return classes().from(type);
	}

	public MethodStream methods() {
		return new MethodStream(methods.stream());
	}

	public <A extends Annotation> AnnotatedMethodStream<A> methodsWith(Class<A> annotation) {
		return methods().with(annotation);
	}

	public FieldStream fields() {
		return new FieldStream(fields.stream());
	}

	public <A extends Annotation> AnnotatedFieldStream<A> fieldsWith(Class<A> annotation) {
		return fields().with(annotation);
	}

	public ConstructorStream constructors() {
		return new ConstructorStream(constructors.stream());
	}

	public <A extends Annotation> AnnotatedConstructorStream<A> constructorsWith(Class<A> annotation) {
		return constructors().with(annotation);
	}

	public static ASM create(String packageName) {
		if (!Utils.useable(packageName))
			throw JCreate.error("Invalid packageName.");
		ASM asm = cache.get(packageName);
		if (asm == null) {
			asm = new ASM(packageName);
			cache.put(packageName, asm);
		}
		return asm;
	}
}
