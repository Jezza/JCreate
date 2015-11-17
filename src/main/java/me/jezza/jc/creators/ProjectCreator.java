package me.jezza.jc.creators;

import me.jezza.jc.CreatorClass.Creator;
import me.jezza.jc.Creators;
import me.jezza.jc.JCreate;
import me.jezza.jc.annotations.CreatorError;
import me.jezza.jc.annotations.CreatorParam;

import java.util.Arrays;

import static me.jezza.jc.lib.Utils.useable;

/**
 * @author Jezza
 */
@CreatorParam("project")
public class ProjectCreator {

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


	}

	@CreatorParam("help")
	public void help(String[] params) {
		for (Creator creator : Creators.creatorClass(getClass()).creators())
			System.out.println("Possible values: " + Arrays.asList(creator.params()));
	}
}
