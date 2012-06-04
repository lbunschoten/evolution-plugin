package jenkins.plugins.evolution.domain;

import hudson.remoting.IReadResolve;
import java.util.ArrayList;

/**
 * Represents data of a build required for the Evolution Plugin. This object is
 * persisted to an XML object using the XStream library.
 * 
 * @author leon
 */
public class Build implements IReadResolve
{
	private int id;
	
	private ArrayList<Result> results = new ArrayList<Result>();
	
	/**
	 * Constructor for the Build object. Every build should have a unique ID.
	 * 
	 * @param id - A unique build identifier
	 */
	public Build(int id)
	{
		this.id = id;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object readResolve()
	{
		if(results == null)
		{
			results = new ArrayList<Result>();
		}
		return this;
	}
	
	public int getId()
	{
		return id;
	}
	
	public void setId(int id)
	{
		this.id = id;
	}
	
	/**
	 * Add a result to an existing build.
	 * 
	 * @param result - A result of a data provider
	 */
	public void addResult(Result result)
	{
		results.add(result);
	}
	
	public ArrayList<Result> getResults()
	{
		return results;
	}
	
	public void setResults(ArrayList<Result> results)
	{
		this.results = results;
	}
	
	/**
	 * Checks whether a certain result is already stored in the list.
	 * 
	 * @param dataProvider - Unique dataprovider name
	 * @return boolean
	 */
	public boolean hasResult(String dataProvider)
	{
		for(Result result : results)
		{
			if(result.getDataProviderId().equals(dataProvider))
			{
				return true;
			}
		}
		return false;
	}
}
