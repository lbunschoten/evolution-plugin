package jenkins.plugins.evolution.graph;

import hudson.util.Graph;
import hudson.util.ShiftedCategoryAxis;
import java.awt.Color;
import java.util.ArrayList;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.ui.RectangleInsets;

/**
 * This class is used to generate the graph for the evolution plugin. Data is
 * added using {@link GraphPoint} objects. Those will be identified by the id of
 * a build.
 * 
 * @author leon
 */
public class EvolutionGraph extends Graph
{
	
	private ArrayList<AbstractLine> lines = new ArrayList<AbstractLine>();
	
	private static final int DEFAULT_HEIGHT = 300;
	
	private static final int DEFAULT_WIDTH = 500;
	
	/**
	 * The top margin of the graph.
	 */
	private static final int INSET_TOP = 20;
	
	/**
	 * The bottom margin of the graph.
	 */
	private static final int INSET_BOTTOM = 20;
	
	/**
	 * The left margin of the graph.
	 */
	private static final int INSET_LEFT = 0;
	
	/**
	 * The right margin of the graph.
	 */
	private static final int INSET_RIGHT = 5;
	
	private String name;
	
	/**
	 * Constructor of the EvolutionGraph. The constructor requires a list of
	 * graphPoints, which will be drawn on the graph.
	 * 
	 * @param name
	 */
	public EvolutionGraph(String name)
	{
		super(-1, DEFAULT_WIDTH, DEFAULT_HEIGHT);
		
		this.name = name;
	}
	
	public void addLine(AbstractLine line)
	{
		int id = lines.size();
		
		line.setId(id);
		lines.add(id, line);
	}
	
	/**
	 * @return JFreeChart
	 */
	@Override
	protected JFreeChart createGraph()
	{
		final JFreeChart chart = ChartFactory.createLineChart(name, null, null, null, PlotOrientation.VERTICAL, true, true, true);
		chart.setBackgroundPaint(Color.WHITE);
		
		loadPlot(chart);
		loadLines(chart.getCategoryPlot());
		
		return chart;
	}
	
	/**
	 * Loads the plot form the chart object and then adds some information about
	 * the styling of the plot.
	 * 
	 * @param chart
	 */
	private void loadPlot(JFreeChart chart)
	{
		CategoryPlot plot = chart.getCategoryPlot();
		
		plot.setBackgroundPaint(Color.WHITE);
		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.BLACK);
		plot.setDomainAxis(getXAxis());
		plot.setInsets(new RectangleInsets(INSET_TOP, INSET_LEFT, INSET_BOTTOM, INSET_RIGHT));
	}
	
	private void loadLines(CategoryPlot plot)
	{
		plot.setRangeAxis(getDefaultYAxis());
		
		for(AbstractLine line : lines)
		{
			int id = line.getId();
			
			plot.setDataset(id, line.createDataset());
			
			if(line.getYAxis() != null)
			{
				plot.setRangeAxis(id, line.getYAxis());
				plot.mapDatasetToRangeAxis(id, id);
			}
			
			plot.setRenderer(id, line.getLineAndShapeRenderer((LineAndShapeRenderer) plot.getRenderer(id)));
		}
	}
	
	/**
	 * Creates a new X-Axis and adds a style to it.
	 * 
	 * @return X-Axis
	 */
	private CategoryAxis getXAxis()
	{
		CategoryAxis domainAxis = new ShiftedCategoryAxis(null);
		
		domainAxis.setLowerMargin(0.0);
		domainAxis.setUpperMargin(0.0);
		domainAxis.setCategoryMargin(0.0);
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
		
		return domainAxis;
	}
	
	protected NumberAxis getDefaultYAxis()
	{
		NumberAxis yAxis = new NumberAxis();
		
		yAxis.setLabel("Score (higher is better)");
		yAxis.setRange(new Range(0, 10), true, false);
		yAxis.setTickUnit(new NumberTickUnit(1), false, true);
		
		return yAxis;
	}
	
}
