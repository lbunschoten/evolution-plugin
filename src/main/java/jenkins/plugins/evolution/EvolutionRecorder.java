package jenkins.plugins.evolution;

import jenkins.model.Jenkins;
import jenkins.plugins.evolution.config.EvolutionConfig;
import jenkins.plugins.evolution.util.ItemNotFoundException;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.plugins.analysis.util.PluginLogger;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;

/**
 * <p>
 * When the user configures the project and enables this builder,
 * {@link EvolutionRecorderDescriptor} is invoked and a new
 * {@link EvolutionRecorder} is created. The created instance is persisted to
 * the project configuration XML by using XStream.
 * </p>
 * <p>
 * When a build is performed, the
 * {@link #perform(AbstractBuild, Launcher, BuildListener)} method will be
 * invoked.
 * </p>
 * 
 * @author Leon Bunschoten
 */
public class EvolutionRecorder extends Recorder
{
	private final EvolutionConfig config = new EvolutionConfig();
	
	private PluginLogger logger;
	
	private BuildTaskController controller;
	
	/**
	 * @return config
	 */
	public EvolutionConfig getConfig()
	{
		return config;
	}
	
	/**
	 * Get a copy of the existing config, used for the configuration page.
	 * 
	 * @return EvolutionConfig
	 */
	public EvolutionConfig getExistingConfig()
	{
		return config.clone();
		
	}
	
	/**
	 * This method is started when the actual post build task is being
	 * performed.
	 * 
	 * @return boolean
	 */
	@Override
	public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
	{
		logger = new PluginLogger(listener.getLogger(), "[EVOLUTION] ");
		logger.log(Messages.EvolutionRecorder_performStart());
		
		boolean result = false;
		
		try
		{
			controller = new BuildTaskController(build, logger);
			
			result = controller.build();
		}
		catch(ItemNotFoundException e)
		{
			logger.log(e.getMessage());
			
			result = false;
		}
		
		logger.log(Messages.EvolutionRecorder_performEnd());
		
		return result;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public BuildStepMonitor getRequiredMonitorService()
	{
		return BuildStepMonitor.NONE;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public EvolutionProjectAction getProjectAction(AbstractProject<?, ?> project)
	{
		return new EvolutionProjectAction(project);
	}
	
	/**
	 * Load the descriptor
	 * 
	 * @return The buildstepdescriptor.
	 */
	@Override
	@SuppressWarnings({ "unchecked" })
	public BuildStepDescriptor<Publisher> getDescriptor()
	{
		return (BuildStepDescriptor<Publisher>) Jenkins.getInstance().getDescriptorOrDie(getClass());
	}
}
