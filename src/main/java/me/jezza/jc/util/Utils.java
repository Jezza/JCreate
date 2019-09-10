package me.jezza.jc.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Jezza
 */
public final class Utils {

	private Utils() {
		throw new IllegalStateException();
	}

	public static String[] split(String target, char split) {
		int next;
		int off = 0;
		ArrayList<String> list = new ArrayList<>();
		while ((next = target.indexOf(split, off)) != -1) {
			list.add(target.substring(off, next));
			off = next + 1;
		}
		// If no match was found, return this
		if (off == 0)
			return new String[]{target};
		// Add remaining segment
		list.add(target.substring(off, target.length()));
		// Construct result
		int resultSize = list.size();
		while (resultSize > 0 && list.get(resultSize - 1).length() == 0)
			resultSize--;
		String[] result = new String[resultSize];
		return list.subList(0, resultSize).toArray(result);
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
}
