package jenkins.plugins.evolution.graph;

import java.math.BigDecimal;

/**
 * This class is representing a point in the evolution graph. All graphpoints
 * exist of a certain calculated score and build identifier. Also a tooltip and
 * url can be generated for each of the graph points. The tooltips will show
 * additional information about the score.
 * 
 * @author leon
 */
public class GraphPoint
{
	private int buildNumber;
	
	private double value;
	
	private String tooltip;
	
	public GraphPoint(int buildNumber, double value, String tooltip)
	{
		this.buildNumber = buildNumber;
		this.value = value;
		this.tooltip = tooltip;
	}
	
	public int getBuildNumber()
	{
		return buildNumber;
	}
	
	public String getToolTip()
	{
		return tooltip;
	}
	
	public String getUrl()
	{
		return buildNumber + "";
	}
	
	public double getValue()
	{
		return round(value);
	}
	
	private double round(double value)
	{
		return new BigDecimal(value).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
}
