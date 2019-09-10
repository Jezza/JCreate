package me.jezza.jc.pattern;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Jezza
 */
public class TargetPattern implements Comparable<TargetPattern> {
	private static final String COMPILE_PATTERN = "[^*]+|(\\*)";
	private static final Pattern COMPILER = Pattern.compile(COMPILE_PATTERN);

	private final String input;
	private final Pattern pattern;
	private final long score;

	public TargetPattern(final String pattern) {
		final Matcher m = COMPILER.matcher(pattern);
		final StringBuilder b = new StringBuilder();
		long score = 0;
		int pass = 1;
		int last = 0;
		while (m.find()) {
			if (m.group(1) != null) {
				if (last != 1) {
					last = 1;
					score += pass < 4 ? 1 : pass / 2;
					b.append(".+?");
				}
			} else {
				last = 0;
				final String literal = m.group(0);
				score += literal.length() * pass;
				b.append("\\Q").append(literal).append("\\E");
			}
			pass++;
		}
		this.pattern = Pattern.compile(b.toString());
		this.score = Long.MAX_VALUE - score;
		input = pattern;
	}

	public boolean match(final String name) {
		return pattern.matcher(name).matches();
	}

	public String raw() {
		return input;
	}

	public String compiled() {
		return pattern.pattern();
	}

	public long score() {
		return score;
	}

	@Override
	public int compareTo(final TargetPattern o) {
		return Long.compare(o.score, score);
	}

	@Override
	public String toString() {
		return '{' + pattern.pattern() + "}:" + score;
	}
}