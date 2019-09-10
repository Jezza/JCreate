package me.jezza.jc.template;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;

/**
 * @author Jezza
 */
public interface Template {
	String DATA_FILE = "data.ret";

	String START_TOKEN = "${";
	String END_TOKEN = "}";

	default void into(File target, Function<String, String> transform) throws IOException {
		into(target.toPath(), transform);
	}

	void into(Path target, Function<String, String> transform) throws IOException;

	static Template open(String path) throws IOException {
		return open(Paths.get(path));
	}

	static Template open(Path path) throws IOException {
		return path.toString().endsWith(".zip")
				? new TemplateZip(path)
				: new TemplateDirectory(path);
	}
}