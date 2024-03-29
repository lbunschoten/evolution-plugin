package jenkins.plugins.evolution.config;

import java.io.Serializable;
import java.util.HashMap;
import java.util.logging.Logger;
import jenkins.plugins.evolution.dataprovider.CPDDataProvider;
import jenkins.plugins.evolution.dataprovider.CheckStyleDataProvider;
import jenkins.plugins.evolution.dataprovider.CoberturaDataProvider;
import jenkins.plugins.evolution.dataprovider.DataProvider;
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
	
	private boolean displayPoints;
	
	private boolean displayIndividualResults;
	
	private HashMap<String, DataProviderConfig> dataProviders = new HashMap<String, DataProviderConfig>();
	
	public enum DataProviderDescriptor
	{
		//formatter:off
		CHECKSTYLE	("CheckStyle", 	"**/checkstyle-result.xml", 	new CheckStyleDataProvider("CHECKSTYLE")),
		FINDBUGS	("FindBugs", 	"**/findbugsXml.xml", 			new FindBugsDataProvider("FINDBUGS")),
		CPD			("CPD", 		"**/cpd.xml", 					new CPDDataProvider("CPD")),
		PMD			("PMD",			"**/pmd.xml", 					new PMDDataProvider("PMD")),
		COBERTURA	("Cobertura", 	"**/coverage.xml", 				new CoberturaDataProvider("COBERTURA")),
		NCOVER		("NCover", 		"**/fullcoverage.html",			new NCoverDataProvider("NCOVER")),
		SIMIAN		("Simian", 		"", 							new SimianDataProvider("SIMIAN")),
		STYLECOP	("StyleCop", 	"", 							new StyleCopDataProvider("STYLECOP")),
		FXCOP		("FxCop", 		"", 							new FxCopDataProvider("FXCOP"));
		//formatter:on
		
		String name;
		
		String defaultPath;
		
		DataProvider dataProvider;
				
		DataProviderDescriptor(String name, String defaultPath, DataProvider dataProvider)
		{
			this.name = name;
			this.defaultPath = defaultPath;
			this.dataProvider = dataProvider;
		}
		
		public String getName()
		{
			return name;
		}
		
		public String getDefaultPath()
		{
			return defaultPath;
		}
		
		public DataProvider getDataProvider()
		{
			return dataProvider;
		}
	}
	
	public EvolutionConfig()
	{
		for(int i = 0; i < DataProviderDescriptor.values().length; i++)
		{
			DataProviderDescriptor dataProvider = DataProviderDescriptor.values()[i];
			
			dataProviders.put(dataProvider.toString(), new DataProviderConfig(dataProvider.getName(), dataProvider.getDefaultPath()));
		}
	}
	
	public HashMap<String, DataProvider> getDataProviders()
	{
		HashMap<String, DataProvider> dataProviders = new HashMap<String, DataProvider>();
		
		for(int i = 0; i < DataProviderDescriptor.values().length; i++)
		{
			dataProviders.put(DataProviderDescriptor.values()[i].toString(), DataProviderDescriptor.valueOf(DataProviderDescriptor.values()[i].toString()).getDataProvider());
		}
		
		return dataProviders;
	}
	
	public HashMap<String, DataProviderConfig> getDataProviderConfigs()
	{
		HashMap<String, DataProviderConfig> configs = new HashMap<String, DataProviderConfig>();
		
		for(int i = 0; i < DataProviderDescriptor.values().length; i++)
		{
			DataProviderConfig config = dataProviders.get(DataProviderDescriptor.values()[i].toString());

			configs.put(DataProviderDescriptor.values()[i].toString(), config);
		}
		
		return configs;
	}
	
	public HashMap<String, DataProvider> getConfiguredDataProviders()
	{
		HashMap<String, DataProvider> configuredDataProviders = new HashMap<String, DataProvider>();
		
		for(int i = 0; i < DataProviderDescriptor.values().length; i++)
		{
			DataProviderConfig config = dataProviders.get(DataProviderDescriptor.values()[i].toString());
			
			if(config.isFullyConfigured())
			{
				configuredDataProviders.put(DataProviderDescriptor.values()[i].toString(), DataProviderDescriptor.values()[i].getDataProvider());
			}
		}
		
		return configuredDataProviders;
	}
	
	public DataProviderConfig getDataProviderConfig(String id)
	{
		return dataProviders.get(id);
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

	public boolean getDisplayPoints()
	{
		return displayPoints;
	}

	public void setDisplayPoints(boolean displayPoints)
	{
		this.displayPoints = displayPoints;
	}

	public boolean getDisplayIndividualResults()
	{
		return displayIndividualResults;
	}

	public void setDisplayIndividualResults(boolean displayIndividualResults)
	{
		this.displayIndividualResults = displayIndividualResults;
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
