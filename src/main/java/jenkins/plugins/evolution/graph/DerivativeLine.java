package jenkins.plugins.evolution.graph;

import java.awt.BasicStroke;
import java.awt.Color;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;

/**
 * This line displays the derivative data of the evolution graph. This class
 * mostly describes what this line should look like.
 * 
 * @author leon
 */
public class DerivativeLine extends AbstractLine
{
	
	public DerivativeLine(GraphPointList graphPoints)
	{
		super(graphPoints);
	}
	
	@Override
	protected NumberAxis getYAxis()
	{
		NumberAxis yAxis = new NumberAxis();
		
		yAxis.setLabel("Derivative");
		yAxis.setAutoRange(true);
		yAxis.setAutoRangeIncludesZero(false);
		
		return yAxis;
	}
	
	/**
	 * Creates a new line and shape renderer used in a graph.
	 * 
	 * @param renderer
	 * @return LineAndShapeRenderer used in the plot.
	 */
	@Override
	protected LineAndShapeRenderer getLineAndShapeRenderer(LineAndShapeRenderer renderer)
	{
		int id = getId();
		
		if(renderer == null)
		{
			renderer = new LineAndShapeRenderer();
		}
		
		renderer.setBaseFillPaint(Color.white);
		renderer.setDrawOutlines(true);
		
		renderer.setSeriesStroke(id, new BasicStroke(1F));
		renderer.setSeriesOutlineStroke(id, new BasicStroke(1F));
		renderer.setSeriesPaint(id, Color.BLUE);
		
		renderer.setUseFillPaint(true);
		renderer.setSeriesShapesVisible(id, false);
		
		return renderer;
	}
	
}
