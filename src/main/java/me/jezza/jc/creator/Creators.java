package me.jezza.jc.creator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import me.jezza.jc.interfaces.Creator;
import me.jezza.jc.interfaces.Ignore;
import me.jezza.jc.util.SuffixMap;
import me.jezza.jc.util.SuffixMap.Pack;

/**
 * @author Jezza
 */
public final class Creators {
	public static final String CREATOR_PACKAGE = "me.jezza.jc.creators";

	private static final String CREATOR_DATA = "/data/classes.data";

	private static final SuffixMap<String, Creator> creatorMap;

	static {
		creatorMap = readMap();
	}

	private Creators() {
		throw new IllegalStateException();
	}

	public static Pack<Creator> resolve(String[] params) {
		return creatorMap.closest(params);
	}

	private static SuffixMap<String, Creator> readMap() {
		InputStream in = Creators.class.getResourceAsStream(CREATOR_DATA);
		if (in == null)
			throw new IllegalStateException("Class data not found.");
		SuffixMap<String, Creator> creatorMap = new SuffixMap<>();
		long s = System.nanoTime();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
			ClassLoader classLoader = Creators.class.getClassLoader();
			String line;
			while ((line = reader.readLine()) != null) {
				try {
					System.out.println(line);
					Class<?> type = Class.forName(line, true, classLoader);
					long start = System.nanoTime();
					processClass(type, creatorMap);
					// TODO: 20/03/2017 Do stuff with creatorClass
					long end = System.nanoTime();
					System.out.println("O:       " + (end - start));
				} catch (ClassNotFoundException e) {
					System.out.println("Legal class definition: " + line);
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			throw new IllegalStateException("Failed to read class data.", e);
		}
		long e = System.nanoTime();
		System.out.println(creatorMap);
		System.out.println(e - s);
		return creatorMap;
	}

	private static CreatorClass processClass(Class<?> type, SuffixMap<String, Creator> creatorMap) {
		if (type.isAnnotationPresent(Ignore.class))
			return null;
		long wholeS = System.nanoTime();
		CreatorClass creatorClass = new CreatorClass(type, creatorMap);
		long wholeE = System.nanoTime();
		System.out.println("T:       " + (wholeE - wholeS));
		return creatorClass;
	}
}