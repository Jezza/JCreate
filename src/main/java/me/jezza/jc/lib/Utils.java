package me.jezza.jc.lib;

import com.google.common.base.Splitter;
import com.google.common.collect.ArrayListMultimap;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;

/**
 * @author Jezza
 */
public enum Utils {
	;

	public static final Splitter PARAM_SPLITTER = Splitter.on(" ").trimResults().omitEmptyStrings();

	public static boolean useable(CharSequence charSequence) {
		if (charSequence == null || charSequence.length() == 0)
			return false;
		for (int i = 0; i < charSequence.length(); i++)
			if (charSequence.charAt(i) > ' ')
				return true;
		return false;
	}

	public static String[] split(String target) {
		List<String> strings = PARAM_SPLITTER.splitToList(target);
		return strings.toArray(new String[strings.size()]);
	}

	public static boolean startsWith(String[] target, String[] with) {
		if (with.length > target.length)
			return false;
		for (int i = 0; i < with.length; i++)
			if (!with[i].equals(target[i]))
				return false;
		return true;
	}

	public static boolean checkArrayFor(String[] params, String... values) {
		if (params == null || values == null || params.length == 0 || values.length == 0)
			return false;
		List<String> against = Arrays.asList(values);
		return Stream.of(params).anyMatch(against::contains);
	}

	public static String packageName(Class<?> clazz) {
		return clazz.getPackage().getName();
	}

	public static <T, K, U> Collector<T, ?, ArrayListMultimap<K, U>> toArrayListMultimap(Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U[]> valueMapper) {
		BiConsumer<ArrayListMultimap<K, U>, T> accumulator = (map, t) -> {
			K key = keyMapper.apply(t);
			for (U value : valueMapper.apply(t))
				map.put(key, value);
		};
		BinaryOperator<ArrayListMultimap<K, U>> merger = (map1, map2) -> {
			map1.putAll(map2);
			return map1;
		};
		return Collector.of(ArrayListMultimap::create, accumulator, merger, Collector.Characteristics.IDENTITY_FINISH);
	}
}
