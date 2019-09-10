package me.jezza.jc.template;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import me.jezza.jc.util.AbstractLexer;
import me.jezza.jc.util.Strings;

/**
 * @author Jezza
 */
public class TemplateRules extends AbstractLexer {
	private static final int BUFFER_SIZE = 4096;

	private final StringBuilder text;

	{
		text = new StringBuilder(32);
	}

	private final Map<Path, String> data;

	// .idea/modules/${vice|name}_test.iml

	public TemplateRules(Reader in) throws IOException {
		super(in, BUFFER_SIZE);

		data = new HashMap<>();

		int c;
		int[] pos = this.pos.clone();
		while ((c = advance()) != EOS) {
			switch (c) {
				case '@':
					// name decl
					throw new IllegalStateException("NYI");
				case '#':
					// Comment
					while ((c = advance()) != EOS && c != '\n') ;
					continue;
				default:
					if (Character.isWhitespace(c)) {
						while (Character.isWhitespace(peek()))
							advance();
						pos = this.pos.clone();
						continue;
					}
					text.setLength(0);
					do {
						text.append((char) c);
					} while ((c = advance()) != EOS && c != '\n' && c != '\r');
					final String line;
					final String target;
					if (text.indexOf(Template.START_TOKEN) != -1) {
						String _line = text.toString();
						int[] _pos = pos;
						target = Strings.formatToken(_line, Template.START_TOKEN, Template.END_TOKEN, token -> {
							int index = token.indexOf('|');
							if (index == -1) {
								throw new IllegalStateException("Illegal line @ " + Arrays.toString(_pos) + ", no pipe.");
							}
							return token.substring(0, index);
						});
						line = Strings.formatToken(_line, Template.START_TOKEN, Template.END_TOKEN, token -> {
							int index = token.indexOf('|');
							if (index == -1) {
								throw new IllegalStateException("Illegal line @ " + Arrays.toString(_pos) + ", no pipe.");
							}
							return Template.START_TOKEN + token.substring(index + 1) + Template.END_TOKEN;
						});
					} else {
						target = line = text.toString();
					}
					data.put(Paths.get(target), line);

			}
		}
	}

	public Path checkDirectory(Path directory, Function<String, String> transform) throws IOException {
		return directory;
	}

	public boolean filterFile(Path file) {
		return data.get(file) != null;
	}

	public Path checkFile(Path file, Function<String, String> transform) throws IOException {
		String target = data.get(file);
		if (target == null)
			return file;
		if (target.contains(Template.START_TOKEN))
			target = Strings.formatToken(target, Template.START_TOKEN, Template.END_TOKEN, transform);
		return Paths.get(target);
	}
}
