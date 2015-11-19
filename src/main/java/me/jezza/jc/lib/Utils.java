package me.jezza.jc.lib;

import com.google.common.base.Splitter;

import java.util.Arrays;
import java.util.List;
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
}
