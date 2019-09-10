package me.jezza.jc.template;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author Jezza
 */
final class TemplateZip implements Template {
	private final Path zip;
	private final ZipFile file;
	private final TemplateRules rules;

	TemplateZip(Path zip) throws IOException {
		this.zip = zip;

		file = new ZipFile(zip.toFile(), ZipFile.OPEN_READ, StandardCharsets.UTF_8);
		ZipEntry entry = file.getEntry(DATA_FILE);
		InputStream stream = file.getInputStream(entry);
		Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
		rules = new TemplateRules(reader);
	}

	@Override
	public void into(Path target, Function<String, String> transform) {
		throw new IllegalStateException("NYI");
	}
}
