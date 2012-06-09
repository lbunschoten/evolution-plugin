package jenkins.plugins.evolution.graph;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * This class is able to generate a tooltip for each of the graphpoints in the
 * score graph. All individual dataproviders can provide data to be displayed in
 * the tooltip. This data will be combined into a single tooltip.
 * 
 * @author leon
 */
public class ScoreTooltipGenerator
{
	
	public String generateTooltip(int buildNumber, double totalScore, HashMap<String, Double> scores)
	{
		StringBuilder stringBuilder = new StringBuilder();
		String nl = System.getProperty("line.separator");
		
		stringBuilder.append("Build #" + buildNumber + nl + nl);
		
		stringBuilder.append("Score: " + round(totalScore) + nl + nl);
		
		for(Entry<String, Double> pluginScore : scores.entrySet())
		{
			stringBuilder.append(round(pluginScore.getValue()));
			stringBuilder.append(" (" + pluginScore.getKey() + ")" + nl);
		}
		
		return stringBuilder.toString();
	}
	
	private String round(double value)
	{
		NumberFormat format = NumberFormat.getInstance();
		
		format.setMinimumFractionDigits(2);
		format.setMaximumFractionDigits(2);
		
		return format.format(value);
	}
}
