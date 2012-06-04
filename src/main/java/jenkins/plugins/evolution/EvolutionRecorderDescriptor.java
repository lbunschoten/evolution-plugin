package jenkins.plugins.evolution;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import java.util.Map.Entry;
import jenkins.plugins.evolution.config.DataProviderConfig;
import jenkins.plugins.evolution.config.EvolutionConfig;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Descriptor for {@link EvolutionRecorder}. Used as a singleton. The class is
 * marked as public so that it can be accessed from views.
 * <p>
 * See
 * <tt>src/main/resources/jenkins/plugins/evolution/EvolutionRecorder/config.jelly</tt>
 * for the actual HTML fragment for the configuration screen.
 */
@Extension
public final class EvolutionRecorderDescriptor extends BuildStepDescriptor<Publisher>
{
	/**
	 * Creates a new empty descriptor.
	 */
	public EvolutionRecorderDescriptor()
	{
		super(EvolutionRecorder.class);
		load();
	}
	
	/**
	 * Creates a new EvolutionRecorder instance, binding the configuration
	 * values.
	 * 
	 * @param req
	 * @param formData
	 * @return EvolutionRecorder
	 */
	@Override
	public EvolutionRecorder newInstance(StaplerRequest req, JSONObject formData)
	{
		EvolutionRecorder recorder = new EvolutionRecorder();
		
		for(Entry<String, DataProviderConfig> dataProvider : recorder.getConfig().getDataProviders().entrySet())
		{
			req.bindParameters(recorder.getConfig().getDataProviders().get(dataProvider.getKey()), "dataprovider." + dataProvider.getKey() + ".");
		}
		
		req.bindParameters(recorder.getConfig(), "evolution.");
		
		return recorder;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDisplayName()
	{
		return Messages.EvolutionRecorderDescriptor_displayName();
	}
	
	/**
	 * Create a new Evolution configuration.
	 * 
	 * @return EvolutionConfig
	 */
	public EvolutionConfig getConfig()
	{
		return new EvolutionConfig();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public boolean isApplicable(final Class<? extends AbstractProject> jobType)
	{
		return true;
	}
}
