package me.jezza.jc.lib;

import com.google.common.base.Splitter;

import java.util.Arrays;
import java.util.List;

/**
 * @author Jezza
 */
public class Utils {

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
		System.out.println(Arrays.asList(with));
		if (with.length > target.length)
			return false;
		for (int i = 0; i < with.length; i++)
			if (!with[i].equals(target[i]))
				return false;
		return true;
	}
}
