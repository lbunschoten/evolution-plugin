package jenkins.plugins.evolution.graph;

import java.awt.BasicStroke;
import java.awt.Color;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.urls.CategoryURLGenerator;
import org.jfree.data.category.CategoryDataset;

/**
 * This line displays the score data of the evolution graph. This class
 * mostly describes what this line should look like.
 * 
 * @author leon
 */
public class EvolutionLine extends AbstractLine
{
	
	/**
	 * Represents the width of a line
	 */
	private static final float STROKE_WIDTH = 3F;
	
	/**
	 * This value indicates the size of the 'points' displayed in the graph. The
	 * small point is displayed whenever there is not enough space to display
	 * the bigger 'point'.
	 */
	private static final double SERIES_SHAPE_SIZE_SMALL = 4D;
	
	/**
	 * This value represents the offset of a small 'point' in the graph. This
	 * value should be half the diameter of the actual point.
	 */
	private static final double SERIES_SHAPE_OFFSET_SMALL = SERIES_SHAPE_SIZE_SMALL / 2;
	
	/**
	 * This value indicates the size of the 'points' displayed in the graph. The
	 * big point is displayed whenever the space allows it within the graph.
	 */
	private static final double SERIES_SHAPE_SIZE_BIG = 6D;
	
	/**
	 * This value represents the offset of a big 'point' in the graph. This
	 * value should be half the diameter of the actual point.
	 */
	private static final double SERIES_SHAPE_OFFSET_BIG = SERIES_SHAPE_SIZE_BIG / 2;
	
	private static final String LABEL = "Evolution";
	
	private boolean displayPoints = true;
	
	public EvolutionLine(GraphPointList graphPoints, boolean displayPoints)
	{
		super(LABEL, graphPoints);
		
		this.displayPoints = displayPoints;
	}
	
	@Override
	protected NumberAxis getYAxis()
	{
		return null;
	}
	
	@Override
	protected LineAndShapeRenderer getLineAndShapeRenderer(LineAndShapeRenderer renderer)
	{
		int id = getId();
		
		renderer.setBaseFillPaint(Color.WHITE);
		renderer.setDrawOutlines(false);
		renderer.setSeriesStroke(id, new BasicStroke(STROKE_WIDTH));
		renderer.setSeriesPaint(id, Color.ORANGE);
		
		if(getGraphPoints().size() > 50)
		{
			renderer.setSeriesShape(id, new java.awt.geom.Ellipse2D.Double(-SERIES_SHAPE_OFFSET_SMALL, -SERIES_SHAPE_OFFSET_SMALL, SERIES_SHAPE_SIZE_SMALL, SERIES_SHAPE_SIZE_SMALL));
		}
		else
		{
			renderer.setSeriesShape(id, new java.awt.geom.Ellipse2D.Double(-SERIES_SHAPE_OFFSET_BIG, -SERIES_SHAPE_OFFSET_BIG, SERIES_SHAPE_SIZE_BIG, SERIES_SHAPE_SIZE_BIG));
		}
		
		renderer.setSeriesShapesVisible(id, displayPoints);
		renderer.setSeriesFillPaint(id, Color.BLACK);
		renderer.setSeriesToolTipGenerator(id, getToolTipGenerator());
		renderer.setSeriesItemURLGenerator(id, getUrlGenerator());
		
		renderer.setUseFillPaint(true);
		
		return renderer;
	}
	
	/**
	 * Adds a tooltip generator which provides each graph point with a tooltip.
	 * 
	 * @return tooltip
	 */
	private CategoryToolTipGenerator getToolTipGenerator()
	{
		return new CategoryToolTipGenerator()
		{
			@Override
			public String generateToolTip(CategoryDataset dataset, int row, int column)
			{
				return getGraphPoints().get(column).getToolTip();
			}
		};
	}
	
	/**
	 * Add a generator which provides each graph point with a url.
	 * 
	 * @return url
	 */
	private CategoryURLGenerator getUrlGenerator()
	{
		return new CategoryURLGenerator()
		{
			@Override
			public String generateURL(CategoryDataset dataset, int series, int category)
			{
				return getGraphPoints().get(series).getUrl();
			}
		};
	}
}
