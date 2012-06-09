package jenkins.plugins.evolution.graph;

import hudson.util.ChartUtil;
import hudson.util.ChartUtil.NumberOnlyBuildLabel;
import hudson.util.DataSetBuilder;
import java.util.logging.Logger;
import jenkins.plugins.evolution.util.EvolutionUtil;
import jenkins.plugins.evolution.util.ItemNotFoundException;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;

/**
 * This line can be implemented and used in the evolutiongraph. This class
 * provides some common methods required for displaying the data on the graph.
 * 
 * @author leon
 */
public abstract class AbstractLine
{
	
	private int id;
	
	private String label = "";
	
	private GraphPointList graphPoints;
	
	public AbstractLine(String label, GraphPointList graphPoints)
	{
		this.label = label;
		
		this.graphPoints = graphPoints;
	}
	
	/**
	 * Returns a DataSetBuilder, which is used to add the graph points to the
	 * actual graph.
	 * 
	 * @return DataSetBuilder
	 */
	protected CategoryDataset createDataset(String label)
	{
		DataSetBuilder<String, NumberOnlyBuildLabel> dsb = new DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel>();
		
		for(GraphPoint graphPoint : graphPoints)
		{
			try
			{
				dsb.add(graphPoint.getValue(), label, getBuildLabel(graphPoints.getProjectName(), graphPoint.getBuildNumber()));
			}
			catch(ItemNotFoundException e)
			{
				Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).warning(e.getMessage());
			}
		}
		
		return dsb.build();
	}
	
	protected int getId()
	{
		return id;
	}
	
	protected void setId(int id)
	{
		this.id = id;
	}
	
	protected String getLabel()
	{
		return label;
	}
	
	protected GraphPointList getGraphPoints()
	{
		return graphPoints;
	}
	
	protected NumberOnlyBuildLabel getBuildLabel(String project, int build) throws ItemNotFoundException
	{
		return new ChartUtil.NumberOnlyBuildLabel(new EvolutionUtil().getBuild(project, build));
	}
	
	protected abstract NumberAxis getYAxis();
	
	protected abstract LineAndShapeRenderer getLineAndShapeRenderer(LineAndShapeRenderer renderer);
}
