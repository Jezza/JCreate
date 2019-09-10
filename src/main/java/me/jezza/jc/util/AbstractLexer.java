package me.jezza.jc.util;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;

/**
 * A very simple extension on the Reader classes.
 * Provides a good base for lexers, and the like, by unifying various input formats. (Files, Strings, InputStreams, etc)
 * And it does so by adding so little overhead.
 *
 * @author Jezza
 */
public abstract class AbstractLexer {
	private static final int DEFAULT_BUFFER_SIZE = 4096;

	private static final int START_COLUMN = 1;
	private static final int START_ROW = 1;

	private static final int MODE_KILL = 3;
	private static final int MODE_DRAIN = 2;
	private static final int MODE_READ = 1;
	private static final int MODE_UNINITIALISED = 0;

	protected static final int EOS = -1;

	/**
	 * The input from which to read from.
	 * <p>
	 * While not actually final, this should be treated as such.
	 * The only reason this isn't final is because we need a nice, fast, and clean way of knowing when we closed it.
	 * So, null is used as a "There's no more input" internally, beside the mode state.
	 */
	private /*final*/ Reader in;

	/**
	 * The internal buffer with a capacity as defined by the numerous constructors,
	 * or if no such constructor was used, {@link #DEFAULT_BUFFER_SIZE}.
	 */
	private final char[] buffer;

	protected final int[] pos;

	private int mode;

	private int index;
	private int limit;

	protected AbstractLexer(final String input) {
		this(new StringReader(input), DEFAULT_BUFFER_SIZE);
	}

	protected AbstractLexer(final String input, int bufferSize) {
		this(new StringReader(input), bufferSize);
	}

	protected AbstractLexer(final File file) throws FileNotFoundException {
		this(new FileReader(file), DEFAULT_BUFFER_SIZE);
	}

	protected AbstractLexer(final File file, int bufferSize) throws FileNotFoundException {
		this(new FileReader(file), bufferSize);
	}

	protected AbstractLexer(final InputStream in) {
		this(new InputStreamReader(in), DEFAULT_BUFFER_SIZE);
	}

	protected AbstractLexer(final InputStream in, int bufferSize) {
		this(new InputStreamReader(in), bufferSize);
	}

	protected AbstractLexer(final Reader in) {
		this(in, DEFAULT_BUFFER_SIZE);
	}

	protected AbstractLexer(final Reader in, int length) {
		if (in == null)
			throw new NullPointerException("Input cannot be null.");
		if (length < 1)
			throw new IllegalArgumentException("Buffer size must be > 0");
		this.in = in;
		this.buffer = new char[length];
		mode = MODE_UNINITIALISED;
		pos = new int[]{START_ROW, START_COLUMN};
		index = 0;
		limit = -1;
	}

	private boolean nextChunk() throws IOException {
		int count = in.read(buffer);
		if (count == EOS) {
			in.close();
			in = null;
			mode = MODE_KILL;
			return true;
		} else if (count < buffer.length) {
			index = 0;
			limit = count;
			in.close();
			in = null;
			mode = MODE_DRAIN;
		} else {
			index = 0;
		}
		return false;
	}

	protected final int advance() throws IOException {
		if (mode == MODE_UNINITIALISED) {
			mode = MODE_READ;
			if (nextChunk())
				return EOS;
		} else if (mode == MODE_KILL)
			return EOS;
		final int c = buffer[index++];
		if (limit != -1 && index >= limit)
			mode = MODE_KILL;
		else if (in != null && buffer.length - index == 0)
			nextChunk();
		if (c == '\n') {
			pos[0]++;
			pos[1] = START_COLUMN;
		} else if (c != '\r') {
			pos[1]++;
		}
		return c;
	}

	protected final String inputBuffer() {
		return Arrays.toString(buffer);
	}

	protected final int peek() throws IOException {
		if (mode == MODE_KILL) {
			return EOS;
		} else if (mode == MODE_UNINITIALISED) {
			nextChunk();
			mode = MODE_READ;
		}
		return buffer[index];
	}
}