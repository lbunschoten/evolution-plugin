package jenkins.plugins.evolution;

import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.plugins.analysis.util.PluginLogger;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map.Entry;
import jenkins.plugins.evolution.config.EvolutionConfig;
import jenkins.plugins.evolution.config.InvalidConfigException;
import jenkins.plugins.evolution.dataprovider.DataProvider;
import jenkins.plugins.evolution.domain.Job;
import jenkins.plugins.evolution.domain.Result;
import jenkins.plugins.evolution.persistence.EvolutionReader;
import jenkins.plugins.evolution.persistence.EvolutionWriter;
import jenkins.plugins.evolution.persistence.PersistenceException;
import jenkins.plugins.evolution.util.ItemNotFoundException;

/**
 * Performs the actions during a build. First it reads the results of all data
 * providers. These results are then added to the existing results found in the
 * evolution xml file. Finally both old and new results will be written to the
 * evolution xml file.
 * 
 * @author leon
 */
public class BuildTaskController
{
	private AbstractBuild<?, ?> build;
	
	private EvolutionConfig config;
	
	private PluginLogger logger;
	
	public BuildTaskController(AbstractBuild<?, ?> build, EvolutionConfig config, PluginLogger logger) throws ItemNotFoundException
	{
		this.config = config;
		this.build = build;
		this.logger = logger;
	}
	
	protected boolean build()
	{
		ArrayList<Result> results = readDataProviderResults();
		
		logger.log(Messages.BuildTaskController_readExistingResults());
		
		if(results.size() == 0)
		{
			logger.log(Messages.BuildTaskController_noResults());
		}
		
		logger.log(Messages.BuildTaskController_writeResults());
		
		return writeEvolutionFile(results);
	}
	
	private ArrayList<Result> readDataProviderResults()
	{
		ArrayList<Result> results = new ArrayList<Result>();
		
		for(Entry<String, DataProvider> dataProvider : config.getConfiguredDataProviders().entrySet())
		{
			try
			{
				logger.log(Messages.BuildTaskController_readResults(dataProvider.getKey()));
				
				String path = config.getDataProviderConfig(dataProvider.getKey()).getPath();
				
				results.add(dataProvider.getValue().getResult(getDataProviderFileStream(path)));
			}
			catch(PersistenceException e)
			{
				logger.log(Messages.BuildTaskController_readResults_PersistenceException(dataProvider.getKey(), e.getMessage()));
			}
			catch(InvalidConfigException e)
			{
				logger.log(e.getMessage());
			}
		}
		
		return results;
	}
	
	private InputStream getDataProviderFileStream(String path) throws InvalidConfigException
	{
		if(path != null && !path.isEmpty())
		{
			try
			{
				FilePath[] paths = build.getWorkspace().list(path);
				
				if(paths != null && paths.length > 0)
				{
					return paths[0].read();
				}
			}
			catch(IOException e)
			{
				throw new InvalidConfigException("Could not load/find file at " + path);
			}
			catch(InterruptedException e)
			{
				throw new InvalidConfigException("Search for file \"" + path + "\" was interrupted");
			}
		}
		
		return null;
	}
	
	private Job readEvolutionFile(File file)
	{
		Job job = null;
		try
		{
			EvolutionReader reader = new EvolutionReader(new FileInputStream(file));
			
			job = reader.read();
		}
		catch(PersistenceException e)
		{
			logger.log(e.getMessage());
			
			job = new Job();
		}
		catch(FileNotFoundException e)
		{
			logger.log(e.getMessage());
			
			job = new Job();
		}
		
		return job;
	}
	
	private boolean writeEvolutionFile(ArrayList<Result> results)
	{
		File evolutionFile = EvolutionRecorder.getEvolutionFile(build.getProject());
		
		Job job = readEvolutionFile(evolutionFile);
		
		job.addBuild(build.getNumber(), results);
		
		try
		{
			EvolutionWriter writer = new EvolutionWriter(evolutionFile);
			writer.write(job);
		}
		catch(PersistenceException e)
		{
			logger.log(e.getMessage());
			
			return false;
		}
		
		return true;
	}
}
