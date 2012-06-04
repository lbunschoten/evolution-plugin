package jenkins.plugins.evolution.config;

import java.io.Serializable;
import java.util.HashMap;
import java.util.logging.Logger;
import jenkins.plugins.evolution.dataprovider.CPDDataProvider;
import jenkins.plugins.evolution.dataprovider.CheckStyleDataProvider;
import jenkins.plugins.evolution.dataprovider.CoberturaDataProvider;
import jenkins.plugins.evolution.dataprovider.FindBugsDataProvider;
import jenkins.plugins.evolution.dataprovider.FxCopDataProvider;
import jenkins.plugins.evolution.dataprovider.NCoverDataProvider;
import jenkins.plugins.evolution.dataprovider.PMDDataProvider;
import jenkins.plugins.evolution.dataprovider.SimianDataProvider;
import jenkins.plugins.evolution.dataprovider.StyleCopDataProvider;

/**
 * Stores configuration data for the evolution plugin.
 * 
 * @author leon
 */
public class EvolutionConfig implements Cloneable, Serializable
{
	private static final long serialVersionUID = 1L;
	
	private boolean scoreGraphEnabled;
	
	private boolean derivativeGraphEnabled;
	
	private HashMap<String, DataProviderConfig> dataProviders = new HashMap<String, DataProviderConfig>();
	
	/**
	 * Constructor for a new Evolution config. All available dataProviders, used to
	 * provide data for the evolution plugin, are defined here.
	 */
	public EvolutionConfig()
	{
		dataProviders.put(CheckStyleDataProvider.ID, new DataProviderConfig(CheckStyleDataProvider.NAME));
		dataProviders.put(FindBugsDataProvider.ID, new DataProviderConfig(FindBugsDataProvider.NAME));
		dataProviders.put(CoberturaDataProvider.ID, new DataProviderConfig(CoberturaDataProvider.NAME));
		dataProviders.put(FxCopDataProvider.ID, new DataProviderConfig(FxCopDataProvider.NAME));
		dataProviders.put(PMDDataProvider.ID, new DataProviderConfig(PMDDataProvider.NAME));
		dataProviders.put(CPDDataProvider.ID, new DataProviderConfig(CPDDataProvider.NAME));
		dataProviders.put(SimianDataProvider.ID, new DataProviderConfig(SimianDataProvider.NAME));
		dataProviders.put(NCoverDataProvider.ID, new DataProviderConfig(NCoverDataProvider.NAME));
		dataProviders.put(StyleCopDataProvider.ID, new DataProviderConfig(StyleCopDataProvider.NAME));
	}
	
	public HashMap<String, DataProviderConfig> getDataProviders()
	{
		return dataProviders;
	}
	
	public boolean getScoreGraphEnabled()
	{
		return scoreGraphEnabled;
	}
	
	public void setScoreGraphEnabled(boolean enabled)
	{
		scoreGraphEnabled = enabled;
	}
	
	public boolean getDerivativeGraphEnabled()
	{
		return derivativeGraphEnabled;
	}
	
	public void setDerivativeGraphEnabled(boolean enabled)
	{
		derivativeGraphEnabled = enabled;
	}
	
	/**
	 * Clones the EvolutionConfig, so that it can be used for the configuration
	 * page.
	 * 
	 * @return A copy of the configuration data
	 */
	@Override
	public EvolutionConfig clone()
	{
		EvolutionConfig config = null;
		
		try
		{
			config = (EvolutionConfig) super.clone();
		}
		catch(CloneNotSupportedException e)
		{
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).warning(e.getMessage());
			// Should never reach this exception as the Cloneable interface is
			// implemented.
		}
		
		return config;
	}
}
