package me.jezza.jc.creators;

import static me.jezza.jc.util.Strings.useable;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import me.jezza.jc.JCreate;
import me.jezza.jc.interfaces.Command;
import me.jezza.jc.interfaces.Parameter;
import me.jezza.jc.template.Template;

/**
 * @author Jezza
 */
@Command("project")
public class ProjectCreator {
	private static final String TEMPLATE_DIR = "C:\\Users\\Jezza\\Desktop\\JavaProjects\\Template";
	private static final String TEMPLATE_ZIP = "C:\\Users\\Jezza\\Desktop\\JavaProjects\\Template.zip";

	@Parameter(names = {"-o", "--overwrite"})
	private boolean overwrite = false;

	@Parameter(names = {"-t", "--template"})
	private String template = TEMPLATE_DIR;

	@Command("java")
	public void create(String[] params) throws IOException {
		if (params.length < 2)
			throw JCreate.error("Requires a project name and a group id.");
		String name = params[0];
		if (!useable(name))
			throw JCreate.error("Invalid project name.");
		String groupId = params[1];
		if (!useable(groupId))
			throw JCreate.error("Invalid groupId.");
		Path project = JCreate.cwd().resolve(name);
		if (Files.exists(project)) {
//			if (!overwrite) {
//				System.out.println("Project already exists with that name, if you wish to overwrite it use the flags: [-o or -overwrite]");
//				System.out.println("Exiting...");
//				return;
//			}
//			System.out.println("Attempting to delete project: " + projectName);
//			if (!Files.delete(project.toPath()))
//				throw JCreate.error("Failed to delete previous project. Aborting...");
//			System.out.println("Resuming...");
			Files.walkFileTree(project, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					Files.delete(file);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
					Files.delete(dir);
					return FileVisitResult.CONTINUE;
				}
			});
		}
		System.out.println("Creating Java Project ['" + name + "']");

		final Template template = Template.open(this.template);

		Map<String, String> properties = new HashMap<>();
		properties.put("name", name);
		properties.put("groupId", groupId);

		try {
			template.into(project, new TemplateProperties(properties));
		} catch (IOException e) {
			throw new IllegalStateException("Caught exception while transforming template: ", e);
		}
	}

	private static final class TemplateProperties implements Function<String, String> {
		private final Map<String, String> properties;

		TemplateProperties(Map<String, String> properties) {
			this.properties = properties;
		}

		@Override
		public String apply(String token) {
			String value = properties.get(token);
			if (value == null)
				throw new IllegalStateException("No input value: " + token);
			return value;
		}
	}

//	@Command("help")
//	public void help(String[] params) {
//		for (CreatorClass.Creator creator : Creators.creatorClass(getClass()).creators())
//			System.out.println("Possible values: " + Arrays.asList(creator.params()));
//	}
}
