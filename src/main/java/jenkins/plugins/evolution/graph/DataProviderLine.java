package jenkins.plugins.evolution.graph;

import java.awt.BasicStroke;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;

/**
 * This line displays the derivative data of the evolution graph. This class
 * mostly describes what this line should look like.
 * 
 * @author leon
 */
public class DataProviderLine extends AbstractLine
{
	
	public DataProviderLine(String label, GraphPointList graphPoints)
	{
		super(label, graphPoints);
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
		
		//renderer.setBaseFillPaint(Color.white);
		renderer.setDrawOutlines(true);
		
		renderer.setSeriesStroke(id, new BasicStroke(1F));
		renderer.setSeriesOutlineStroke(id, new BasicStroke(1F));
		
		renderer.setSeriesShapesVisible(id, false);
		renderer.setBaseShapesVisible(false);
		renderer.setAutoPopulateSeriesShape(false);
		renderer.setAutoPopulateSeriesFillPaint(false);
		
		return renderer;
	}

	@Override
	protected NumberAxis getYAxis()
	{
		return null;
	}
	
}
