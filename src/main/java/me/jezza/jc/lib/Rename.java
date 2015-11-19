package me.jezza.jc.lib;

import me.jezza.jc.JCreate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author Jezza
 */
public class Rename {
	public static final String STARTING_CHAR = "${";
	public static final String ENDING_CHAR = "}";

	public static final String PROJECT_NAME = STARTING_CHAR + "name" + ENDING_CHAR;

	public static void in(File file, String target, String with) {
		if (file == null)
			return;
		Path path = file.toPath();
		try {
			String content = new String(Files.readAllBytes(path), UTF_8);
			Files.write(path, content.replace(target, with).getBytes(UTF_8));
		} catch (IOException e) {
			throw JCreate.error("Failed to read/write file: " + file, e);
		}
	}
}
