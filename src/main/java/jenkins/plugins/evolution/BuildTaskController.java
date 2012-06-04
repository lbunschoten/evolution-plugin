package jenkins.plugins.evolution;

import hudson.model.AbstractBuild;
import hudson.plugins.analysis.util.PluginLogger;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import jenkins.plugins.evolution.config.ConfigManager;
import jenkins.plugins.evolution.dataprovider.DataProvider;
import jenkins.plugins.evolution.domain.Job;
import jenkins.plugins.evolution.domain.Result;
import jenkins.plugins.evolution.persistence.EvolutionReader;
import jenkins.plugins.evolution.persistence.EvolutionWriter;
import jenkins.plugins.evolution.persistence.PersistenceException;
import jenkins.plugins.evolution.util.EvolutionUtil;
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
	
	private ArrayList<DataProvider> dataProviders = new ArrayList<DataProvider>();
	
	private PluginLogger logger;
	
	private ConfigManager configManager;
	
	public BuildTaskController(AbstractBuild<?, ?> build, PluginLogger logger) throws ItemNotFoundException
	{
		configManager = new ConfigManager(build.getProject().getFullName(), build.getWorkspace(), logger);
		
		dataProviders = configManager.getConfiguredDataProviders();
		
		this.build = build;
		this.logger = logger;
	}
	
	protected boolean build()
	{
		ArrayList<Result> results = readDataProviderResults();
		
		logger.log("Existing results have been read");
		
		if(results.size() == 0)
		{
			logger.log("Invalid configuration: No data provider results available.");
		}
		
		logger.log("Writing new results");
		
		return writeEvolutionFile(results);
	}
	
	private ArrayList<Result> readDataProviderResults()
	{
		ArrayList<Result> results = new ArrayList<Result>();
		
		for(DataProvider p : dataProviders)
		{
			try
			{
				logger.log("Reading " + p.getName() + " results");
				
				results.add(p.getResult());
			}
			catch(PersistenceException e)
			{
				logger.log(e.getMessage());
			}
		}
		
		return results;
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
		Job job = readEvolutionFile(getEvolutionFile());
		
		job.addBuild(build.getNumber(), results);
		
		try
		{
			logger.log("Writing to " + getEvolutionFile().getName());
			EvolutionWriter writer = new EvolutionWriter(getEvolutionFile());
			writer.write(job);
		}
		catch(PersistenceException e)
		{
			logger.log(e.getMessage());
			
			return false;
		}
		
		return true;
	}
	
	private File getEvolutionFile()
	{
		EvolutionUtil util = new EvolutionUtil();
		
		return util.getEvolutionFile(build.getProject());
	}
}
