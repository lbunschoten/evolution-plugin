package jenkins.plugins.evolution.util;

import hudson.model.TopLevelItem;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import java.io.File;
import jenkins.model.Jenkins;
import jenkins.plugins.evolution.EvolutionRecorder;

/**
 * This class provides easier access to Jenkins objects.
 * 
 * @author leon
 */
public class EvolutionUtil
{
	
	public AbstractBuild<?, ?> getBuild(String projectName, int number) throws ItemNotFoundException
	{
		AbstractProject<?, ?> project = getProjectByName(projectName);
		
		AbstractBuild<?, ?> build = project.getBuildByNumber(number);
		
		if(build == null)
		{
			throw new ItemNotFoundException("Build " + number + " was not found for project " + projectName);
		}
		
		return build;
	}
	
	public AbstractProject<?, ?> getProjectByName(String projectName) throws ItemNotFoundException
	{
		AbstractProject<?, ?> project = Jenkins.getInstance().getItemByFullName(projectName, AbstractProject.class);
		
		if(project == null)
		{
			throw new ItemNotFoundException("Project " + projectName + " was not found.");
		}
		
		return project;
	}
	
	public EvolutionRecorder getEvolutionRecorder(AbstractProject<?, ?> project) throws ItemNotFoundException
	{
		EvolutionRecorder recorder = project.getPublishersList().get(EvolutionRecorder.class);
		
		if(recorder == null)
		{
			throw new ItemNotFoundException("Problem loading config from publisher");
		}
		
		return recorder;
	}
	
	public File getEvolutionFile(AbstractProject<?, ?> project)
	{
		return new File(Jenkins.getInstance().getRootDirFor((TopLevelItem) project).getAbsolutePath() + "/evolution.xml");
	}
}
