package jenkins.plugins.evolution.graph;

import java.math.BigDecimal;
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
	
	public String generateTooltip(int buildNumber, HashMap<String, Double> scores)
	{
		StringBuilder stringBuilder = new StringBuilder();
		String nl = System.getProperty("line.separator");
		
		stringBuilder.append("Build #" + buildNumber + nl + nl);
		
		for(Entry<String, Double> pluginScore : scores.entrySet())
		{
			if(pluginScore.getKey().equals("total"))
			{
				stringBuilder.append("Score: " + round(scores.get("total")) + nl + nl);
			}
			else
			{
				stringBuilder.append("(" + pluginScore.getKey() + ") - ");
				stringBuilder.append(round(pluginScore.getValue()) + nl);
			}
		}
		
		return stringBuilder.toString();
	}
	
	private double round(double value)
	{
		return new BigDecimal(value).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
}
