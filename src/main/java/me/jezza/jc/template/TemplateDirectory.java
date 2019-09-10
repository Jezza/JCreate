/**
 * BPS Bildungsportal Sachsen GmbH<br>
 * Bahnhofstrasse 6<br>
 * 09111 Chemnitz<br>
 * Germany<br>
 * <p>
 * Copyright (c) 2005-2017 by BPS Bildungsportal Sachsen GmbH<br>
 * http://www.bps-system.de<br>
 * <p>
 * All rights reserved.
 */
package me.jezza.jc.template;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.Function;

import me.jezza.jc.util.Strings;

/**
 * @author Jezza
 */
final class TemplateDirectory implements Template {
	private final Path directory;
	private final TemplateRules rules;

	TemplateDirectory(Path directory) throws IOException {
		this.directory = directory;
		Path pattern = directory.resolve(DATA_FILE);
		Reader reader = new InputStreamReader(Files.newInputStream(pattern), StandardCharsets.UTF_8);
		rules = new TemplateRules(reader);
	}

	@Override
	public void into(Path target, Function<String, String> transform) throws IOException {
		System.out.println("Walking: " + target);
		Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				Path directory = Files.createDirectory(target.resolve(rules.checkDirectory(TemplateDirectory.this.directory.relativize(dir), transform)));
				System.out.println("Creating: " + directory);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				Path relative = directory.relativize(file);
				// We don't want to process the data file.
				if (relative.getNameCount() == 1 && relative.getFileName().toString().equals(DATA_FILE)) {
					return FileVisitResult.CONTINUE;
				}
				boolean filtering = rules.filterFile(relative);
				relative = rules.checkFile(relative, transform);

				Path targetFile = target.resolve(relative);
				Files.copy(file, targetFile, StandardCopyOption.COPY_ATTRIBUTES);
				if (filtering) {
					System.out.println("Rewriting: " + targetFile);
					// @TODO Jezza - 18 Jul 2017: Don't convert the entire file to a string
					String value = new String(Files.readAllBytes(targetFile), StandardCharsets.UTF_8);
					value = Strings.formatToken(value, "${", "}", transform);
					Files.delete(targetFile);
					Files.write(targetFile, value.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
				}
				return FileVisitResult.CONTINUE;
			}
		});
	}
}
