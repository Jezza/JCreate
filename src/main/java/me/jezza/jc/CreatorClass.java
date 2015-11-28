package me.jezza.jc;

import com.google.common.base.Joiner;
import me.jezza.jc.annotations.CreatorArgument;
import me.jezza.jc.annotations.CreatorParam;
import me.jezza.jc.lib.Utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static me.jezza.jc.lib.Utils.useable;

/**
 * @author Jezza
 */
public class CreatorClass {
	private static final Joiner ARRAY_JOINER = Joiner.on(" ");

	protected final Class<?> clazz;
	protected final CreatorParam classParam;
	protected transient Object instance = null;

	protected final List<Creator> creators = new ArrayList<>();
	protected final List<Argument> arguments = new ArrayList<>();

	public CreatorClass(Class<?> clazz, CreatorParam classParam) {
		this.clazz = clazz;
		this.classParam = classParam;
	}

	public Class<?> clazz() {
		return clazz;
	}

	public Object instance() {
		if (instance == null) {
			try {
				instance = clazz.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw JCreate.error(JCreate.format("Failed to instantiate Creator({}).", clazz), e);
			}
		}
		return instance;
	}

	@Override
	public String toString() {
		return clazz.toString() + '(' + creators.size() + " Methods)";
	}

	public Creator creator(Method method) {
		if (classParam == null)
			return null;
		Creator creator = new Creator(method, Utils.split(classParam.value()));
		creators.add(creator);
		return creator;
	}

	public Creator creator(Method method, CreatorParam param) {
		Creator creator = new Creator(method, Utils.split(value(param)));
		creators.add(creator);
		return creator;
	}

	private String value(CreatorParam param) {
		if (classParam == null)
			return param.value();
		String joiner = classParam.joiner();
		return classParam.value() + (useable(joiner) ? joiner : " ") + param.value();
	}

	public List<Creator> creators() {
		Collections.sort(creators);
		return creators;
	}

	public Argument argument(Field field, CreatorArgument param) {
		Argument argument = new Argument(field, param.value());
		arguments.add(argument);
		return argument;
	}

	public List<Argument> arguments() {
		Collections.sort(arguments);
		return arguments;
	}

	public class Creator implements Comparable<Creator> {
		protected final Method method;
		protected final String[] params;

		private Creator(Method method, String[] params) {
			this.method = method;
			this.params = params;
			method.setAccessible(true);
		}

		public void create(String[] params) {
			arguments().forEach(argument -> argument.check(params));
			try {
				Object[] parameters = new Object[]{params};
				method.invoke(instance(), parameters);
			} catch (IllegalAccessException | InvocationTargetException e) {
				throw JCreate.error(JCreate.format("Failed to execute creator method({}) in {}", method, clazz), e);
			}
		}

		public String[] params() {
			return params;
		}

		public int length() {
			return params.length;
		}

		@Override
		public int compareTo(Creator o) {
			int compare = Integer.compare(o.length(), length());
			return compare != 0 ? compare : ARRAY_JOINER.join(o.params()).compareTo(ARRAY_JOINER.join(params()));
		}

		@Override
		public String toString() {
			return clazz.getCanonicalName() + '.' + method.getName() + '(' + Arrays.asList(params) + ')';
		}
	}

	public class Argument implements Comparable<Argument> {
		protected final Field field;
		protected final String[] params;

		public Argument(Field field, String[] params) {
			this.field = field;
			this.params = params;
			field.setAccessible(true);
		}

		public void check(String[] params) {
			try {
				field.set(instance(), Utils.checkArrayFor(params, params()));
			} catch (IllegalAccessException e) {
				throw JCreate.error("Failed to set field with value!", e);
			}
		}

		public String[] params() {
			return params;
		}

		public int length() {
			return params.length;
		}

		@Override
		public int compareTo(Argument o) {
			int compare = Integer.compare(o.length(), length());
			return compare != 0 ? compare : ARRAY_JOINER.join(o.params()).compareTo(ARRAY_JOINER.join(params()));
		}
	}
}
