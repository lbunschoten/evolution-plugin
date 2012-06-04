package jenkins.plugins.evolution.graph;

import hudson.model.Result;
import hudson.model.AbstractBuild;
import java.util.ArrayList;
import java.util.logging.Logger;
import jenkins.plugins.evolution.util.EvolutionUtil;
import jenkins.plugins.evolution.util.ItemNotFoundException;

/**
 * A list containing graphpoint objects identified by a build label. This class
 * provides a more robust way of adding graphpoints. All keys and values are
 * checked for null values to make sure no useless data is shown in the final
 * graph.
 * 
 * @author leon
 */
public class GraphPointList extends ArrayList<GraphPoint>
{
	private static final long serialVersionUID = 1L;
	
	private String projectName;
	
	public GraphPointList(String projectName)
	{
		this.projectName = projectName;
	}
	
	@Override
	public boolean add(GraphPoint graphPoint)
	{
		try
		{
			if(graphPoint != null && isSuccesfulBuild(graphPoint))
			{
				return super.add(graphPoint);
			}
		}
		catch(ItemNotFoundException e)
		{
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).warning(e.getMessage());
		}
		
		return false;
	}
	
	public String getProjectName()
	{
		return projectName;
	}
	
	private AbstractBuild<?, ?> getBuild(int number) throws ItemNotFoundException
	{
		return new EvolutionUtil().getBuild(projectName, number);
	}
	
	private boolean isSuccesfulBuild(GraphPoint graphPoint) throws ItemNotFoundException
	{
		AbstractBuild<?, ?> build = getBuild(graphPoint.getBuildNumber());
		
		return build.getResult().isBetterOrEqualTo(Result.UNSTABLE);
	}
	
}
