package jenkins.plugins.evolution.domain;

import hudson.remoting.IReadResolve;
import java.util.ArrayList;

/**
 * Represents data of a job required for the Evolution Plugin. This object is
 * persisted to an XML object using the XStream library.
 * 
 * @author leon
 */
public class Job implements IReadResolve
{
	
	private ArrayList<Build> builds = new ArrayList<Build>();
	
	@Override
	public Job readResolve()
	{
		if(builds == null)
		{
			builds = new ArrayList<Build>();
		}
		
		return this;
	}
	
	public ArrayList<Build> getBuilds()
	{
		return builds;
	}
	
	public void setBuilds(ArrayList<Build> builds)
	{
		this.builds = builds;
	}
	
	/**
	 * Add a build object, if this build is not already listed.
	 * 
	 * @param id - A unique build id
	 * @param results - Parsed data from a dataprovider
	 */
	public void addBuild(int id, ArrayList<Result> results)
	{
		if(!hasBuild(id))
		{
			Build build = new Build(id);
			
			for(Result result : results)
			{
				if(!build.hasResult(result.getDataProviderId()))
				{
					build.addResult(result);
				}
			}
			
			builds.add(build);
		}
	}
	
	/**
	 * Checks whether a build with a certain id exists.
	 * 
	 * @param id - A unique build id
	 * @return boolean
	 */
	public boolean hasBuild(int id)
	{
		for(Build build : getBuilds())
		{
			if(build.getId() == id)
			{
				return true;
			}
		}
		return false;
	}
}
