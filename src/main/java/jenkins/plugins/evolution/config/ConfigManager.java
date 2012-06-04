package jenkins.plugins.evolution.config;

import hudson.FilePath;
import hudson.plugins.analysis.util.PluginLogger;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map.Entry;
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
import jenkins.plugins.evolution.util.EvolutionUtil;
import jenkins.plugins.evolution.util.ItemNotFoundException;

/**
 * This class retrieves the configuration for the data providers. All
 * dataproviders are checked if they are properly configured. If it is not
 * properly configured, it is ignored.
 * 
 * @author leon
 */
public class ConfigManager
{
	private String project;
	
	private FilePath workspace;
	
	private PluginLogger logger;
	
	private EvolutionConfig config;
	
	/**
	 * List of all data providers.
	 * 
	 * @author leon
	 */
	private enum DataProviders
	{
		CHECKSTYLE, FINDBUGS, PMD, CPD, COBERTURA, NCOVER, FXCOP, SIMIAN, STYLECOP
	}
	
	public ConfigManager(String project, FilePath workspace, PluginLogger logger) throws ItemNotFoundException
	{
		this.project = project;
		this.workspace = workspace;
		this.logger = logger;
		
		config = getConfig();
	}
	
	private EvolutionConfig getConfig() throws ItemNotFoundException
	{
		EvolutionUtil util = new EvolutionUtil();
		
		return util.getEvolutionRecorder(util.getProjectByName(project)).getConfig();
	}
	
	public ArrayList<DataProvider> getConfiguredDataProviders()
	{
		ArrayList<DataProvider> dataProviders = new ArrayList<DataProvider>();
		
		for(Entry<String, DataProviderConfig> configItem : config.getDataProviders().entrySet())
		{
			DataProvider dataProvider = loadDataProvider(configItem.getKey(), configItem.getValue());
			
			if(dataProvider != null)
			{
				dataProviders.add(dataProvider);
			}
		}
		
		return dataProviders;
	}
	
	/**
	 * A new data provider object is created, based on the id.
	 * 
	 * @param id
	 * @param path
	 * @return
	 * @throws FileNotFoundException
	 * @throws InvalidConfigException
	 */
	private DataProvider getDataProviderById(String id, String path) throws FileNotFoundException, InvalidConfigException
	{
		switch(DataProviders.valueOf(id.toUpperCase()))
		{
			case CHECKSTYLE:
				return new CheckStyleDataProvider(findDataProviderResultsFile(path));
			case FINDBUGS:
				return new FindBugsDataProvider(findDataProviderResultsFile(path));
			case PMD:
				return new PMDDataProvider(findDataProviderResultsFile(path));
			case CPD:
				return new CPDDataProvider(findDataProviderResultsFile(path));
			case COBERTURA:
				return new CoberturaDataProvider(findDataProviderResultsFile(path));
			case NCOVER:
				return new NCoverDataProvider(findDataProviderResultsFile(path));
			case FXCOP:
				return new FxCopDataProvider(findDataProviderResultsFile(path));
			case SIMIAN:
				return new SimianDataProvider(findDataProviderResultsFile(path));
			case STYLECOP:
				return new StyleCopDataProvider(findDataProviderResultsFile(path));
			default:
		}
		
		return null;
	}
	
	private DataProvider loadDataProvider(String dataProviderId, DataProviderConfig configItem)
	{
		DataProvider dataProvider = null;
		
		if(configItem != null && configItem.isFullyConfigured())
		{
			try
			{
				dataProvider = getDataProviderById(dataProviderId, configItem.getPath());
			}
			catch(FileNotFoundException e)
			{
				logger.log(e.getMessage());
			}
			catch(InvalidConfigException e)
			{
				logger.log(e.getMessage());
			}
		}
		
		return dataProvider;
	}
	
	private InputStream findDataProviderResultsFile(String path) throws InvalidConfigException
	{
		if(path != null && !path.isEmpty())
		{
			try
			{
				FilePath[] paths = workspace.list(path);
				
				if(paths != null && paths.length > 0)
				{
					return paths[0].read();
				}
			}
			catch(IOException e)
			{
				throw new InvalidConfigException(e.getMessage());
			}
			catch(InterruptedException e)
			{
				throw new InvalidConfigException(e.getMessage());
			}
		}
		
		return null;
	}
}