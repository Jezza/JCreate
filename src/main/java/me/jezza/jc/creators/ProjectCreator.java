package me.jezza.jc.creators;

import me.jezza.jc.CreatorClass;
import me.jezza.jc.CreatorClass.Creator;
import me.jezza.jc.Creators;
import me.jezza.jc.JCreate;
import me.jezza.jc.annotations.CreatorArgument;
import me.jezza.jc.annotations.CreatorClassInstance;
import me.jezza.jc.annotations.CreatorError;
import me.jezza.jc.annotations.CreatorParam;
import me.jezza.jc.lib.Files;
import me.jezza.jc.lib.Rename;

import java.io.File;
import java.util.Arrays;
import java.util.zip.ZipEntry;

import static me.jezza.jc.lib.Utils.useable;

/**
 * @author Jezza
 */
@CreatorParam("project")
public class ProjectCreator {
	public static final String TEMPLATE_ZIP = "Template.zip";
	public static final String TEMPLATE_IML = "Template.iml";
	public static final String TEMPLATE_NAME_FILE = ".idea/.name";
	public static final String TEMPLATE_WORKSPACE = ".idea/workspace.xml";
	public static final String TEMPLATE_MODULES = ".idea/modules.xml";

	@CreatorClassInstance
	private CreatorClass creatorClass;

	@CreatorArgument({"-o", "-overwrite"})
	private boolean overwrite = false;

	@CreatorArgument({"-g", "-github"})
	private boolean github = false;

	@CreatorError
	public void error(String[] params) {
		if (params.length == 0)
			throw JCreate.error("No creator specified. Available options: [TODO]");
		throw JCreate.error("Failed to locate creator with '" + params[0] + '\'');
	}

	@CreatorParam("java")
	public void create(String[] params) {
		if (params.length == 0)
			throw JCreate.error("Requires a project name.");
		String projectName = params[0];
		if (!useable(projectName))
			throw JCreate.error("Invalid project name.");
		System.out.println("Creating Java Project ['" + projectName + "']");
		File project = new File(JCreate.CWD(), projectName);
		if (project.exists()) {
			if (!overwrite) {
				System.out.println("Project already exists with that name, if you wish to overwrite it use the flags: [-o or -overwrite]");
				System.out.println("Exiting...");
				return;
			}
			System.out.println("Attempting to delete project: " + projectName);
			if (!Files.delete(project))
				throw JCreate.error("Failed to delete previous project. Aborting...");
			System.out.println("Resuming...");
		}
		if (github) {
			System.out.println("Currently not a thing.");
		}
		Files.openZip(TEMPLATE_ZIP, project, ((name, entry, file) -> processFile(projectName, name, entry, file)));
	}

	private String processFile(String projectName, String name, ZipEntry entry, File file) {
		switch (name) {
			case TEMPLATE_IML:
				Rename.in(file, Rename.PROJECT_NAME, projectName);
				return projectName + ".iml";
			case TEMPLATE_NAME_FILE:
			case TEMPLATE_WORKSPACE:
			case TEMPLATE_MODULES:
				Rename.in(file, Rename.PROJECT_NAME, projectName);
			default:
				return null;
		}
	}

	@CreatorParam("help")
	public void help(String[] params) {
		for (Creator creator : Creators.creatorClass(getClass()).creators())
			System.out.println("Possible values: " + Arrays.asList(creator.params()));
	}
}
