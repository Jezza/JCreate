package me.jezza.jc.pattern;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import me.jezza.jc.util.Strings;

/**
 * @author Jezza
 */
public class PatternFile {
	private final Predicate<String> patternTest;

	public PatternFile(InputStream in) {
		final Map<String, TargetPattern> patternMap = new HashMap<>();

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.charAt(0) == '#')
					continue;
				TargetPattern newPattern = new TargetPattern(line);
				final TargetPattern oldPattern = patternMap.put(newPattern.compiled(), newPattern);
				if (oldPattern != null)
					throw new IllegalArgumentException(Strings.format("Compiled pattern ('{}') is the same. ('{}' and '{}').", newPattern.compiled(), newPattern.raw(), oldPattern.raw()));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (patternMap.isEmpty()) {
			patternTest = null;
		} else {
			final List<TargetPattern> patterns = patternMap.values().stream()
					.sorted(Comparator.comparingLong(TargetPattern::score))
					.collect(Collectors.toList());

			for (int i = 0, l = patterns.size(); i < l; i++)
				System.out.println(Integer.toString(i) + ':' + patterns.get(i).toString());
			patternTest = name -> patterns.parallelStream().filter(pat -> pat.match(name)).findAny().isPresent();
		}
	}

	public boolean matches(String name) {
		return patternTest == null || patternTest.test(name);
	}

	public static void main(String[] args) {
//		InputStream in = Files.open("test.ptn");
//		PatternFile file = new PatternFile(in);
	}
}
