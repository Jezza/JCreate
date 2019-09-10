package me.jezza.jc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import me.jezza.jc.creator.Creators;
import me.jezza.jc.interfaces.Creator;
import me.jezza.jc.util.Strings;
import me.jezza.jc.util.SuffixMap.Pack;

/**
 * @author Jezza
 */
public final class JCreate {
	private static Path CWD;

	private JCreate() {
		throw new IllegalStateException();
	}

	public static void main(String[] params) throws IOException {
		long start = System.nanoTime();
		String directory = System.getProperty("user.dir");
		Path dir = Paths.get(directory);
		if (!Files.exists(dir))
			throw new IllegalArgumentException("Illegal working directory: " + directory);
		System.out.println("Directory: " + directory);
		CWD = dir;
		System.out.println("Parameters: " + Arrays.toString(params));

		Pack<Creator> pack = Creators.resolve(params);
		if (pack.value == null)
			throw error("Unknown parameters: " + Arrays.asList(params));
		Creator creator = pack.value;
		params = Arrays.copyOfRange(params, pack.depth, params.length);
		print("Creator ({}) launched with {} in {}", creator, Arrays.asList(params), CWD);
		try {
			creator.create(params);
		} catch (Exception e) {
			throw JCreate.error("Caught exception from " + creator, e);
		}
		long end = System.nanoTime();
		System.out.println(end - start);
	}

	public static Path cwd() {
		return CWD;
	}

	/**
	 * This method shouldn't actually return, but this means the code can throw which allows flow analysis to better understand what's happening..
	 *
	 * @param message - CharSequence to print out before exiting.
	 * @return - returns null, but shouldn't matter.
	 */
	public static RuntimeException error(String message) {
		return exit(message, -1);
	}

	public static RuntimeException error(String message, Exception e) {
		return exit(message, -1, e);
	}

	public static RuntimeException exit(String message) {
		return exit(message, 0);
	}

	public static RuntimeException exit(String message, Exception e) {
		return exit(message, 0, e);
	}

	public static RuntimeException exit(String message, int status) {
		print(message);
		System.exit(status);
		return null;
	}

	public static RuntimeException exit(String message, int status, Exception e) {
		print(message);
		catching(e);
		System.exit(status);
		return null;
	}

	public static void print(String target) {
		System.out.println(target);
	}

	public static void print(String target, Object... objects) {
		print(Strings.format(target, objects));
	}

	public static void catching(Exception e) {
		e.printStackTrace();
	}

	public static void catching(String target, Exception e) {
		System.out.println(target);
		e.printStackTrace();
	}
}
