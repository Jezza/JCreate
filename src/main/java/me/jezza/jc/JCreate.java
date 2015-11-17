package me.jezza.jc;

import me.jezza.jc.CreatorClass.Creator;

import java.io.File;
import java.util.Arrays;

/**
 * @author Jezza
 */
public class JCreate {
	public static final String OBJ_REP = "{}";
	private static File CWD;
	private static String[] params;

	public static void main(String[] args) {
		if (args.length < 2)
			throw error("Valid first tier parameters: [project]");
		CWD = new File(args[0]);
		// Remove the Current Working Directory. We don't want to resolve that.
		params = Arrays.copyOfRange(args, 1, args.length);
		print("Attempting to resolve {}", Arrays.asList(params));
		Creator creator = Creators.resolve(params);
		if (creator == null)
			throw error("Unknown parameters: " + Arrays.asList(params));
		params = Arrays.copyOfRange(params, creator.length(), params.length);
		print("Creator ({}) launched with {} in {}", creator, Arrays.asList(params), CWD);
		creator.create(params);
	}

	public static File CWD() {
		return CWD;
	}

	public static String[] params() {
		return params;
	}

	/**
	 * This method shouldn't actually return, but this means the code can throw which allows flow analysis to better understand what's happening..
	 *
	 * @param message - CharSequence to print out before exiting.
	 * @return - returns null, but shouldn't matter.
	 */
	public static RuntimeException error(CharSequence message) {
		return exit(message, -1);
	}

	public static RuntimeException error(CharSequence message, Exception e) {
		return exit(message, -1, e);
	}

	public static RuntimeException exit(CharSequence message) {
		return exit(message, 0);
	}

	public static RuntimeException exit(CharSequence message, Exception e) {
		return exit(message, 0, e);
	}

	public static RuntimeException exit(CharSequence message, int status) {
		print(message);
		System.exit(status);
		return null;
	}

	public static RuntimeException exit(CharSequence message, int status, Exception e) {
		print(message);
		catching(e);
		System.exit(status);
		return null;
	}

	public static void print(CharSequence target) {
		System.out.println(target);
	}

	public static void print(CharSequence target, Object... objects) {
		print(format(target, objects));
	}

	public static void catching(Exception e) {
		e.printStackTrace();
	}

	public static void catching(CharSequence target, Exception e) {
		System.out.println(target);
		e.printStackTrace();
	}

	public static CharSequence format(CharSequence target, Object... params) {
		if (params == null || params.length == 0)
			return target;
		StringBuilder builder = new StringBuilder(target);
		for (Object param : params) {
			int index = builder.indexOf(OBJ_REP);
			builder.replace(index, index + 2, String.valueOf(param));
		}
		return builder.toString();
	}
}
