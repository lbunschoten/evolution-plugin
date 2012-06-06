package jenkins.plugins.evolution.graph;

import java.awt.BasicStroke;
import java.awt.Color;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.urls.CategoryURLGenerator;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;

/**
 * This line displays the score data of the evolution graph. This class
 * mostly describes what this line should look like.
 * 
 * @author leon
 */
public class ScoreLine extends AbstractLine
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
	
	public ScoreLine(GraphPointList graphPoints)
	{
		super(graphPoints);
	}
	
	@Override
	protected NumberAxis getYAxis()
	{
		NumberAxis yAxis = new NumberAxis();
		
		yAxis.setLabel("Score (higher is better)");
		yAxis.setRange(new Range(0, 10), true, false);
		yAxis.setTickUnit(new NumberTickUnit(1), false, true);
		
		return yAxis;
	}
	
	@Override
	protected LineAndShapeRenderer getLineAndShapeRenderer(LineAndShapeRenderer renderer)
	{
		int id = getId();
		
		renderer.setBaseFillPaint(Color.white);
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
		
		renderer.setSeriesShapesVisible(id, true);
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
