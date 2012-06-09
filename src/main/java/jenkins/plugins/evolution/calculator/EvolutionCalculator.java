package jenkins.plugins.evolution.calculator;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Logger;
import jenkins.plugins.evolution.config.DataProviderConfig;
import jenkins.plugins.evolution.config.EvolutionConfig;
import jenkins.plugins.evolution.config.InvalidConfigException;

/**
 * This class is used to calculate all scores as shown in the evolution graph.
 * First all results for the individual data providers are calculated for each specific
 * build. Afterwards these results are combined into a total score.
 * 
 * @author leon
 */
public class EvolutionCalculator
{
	private EvolutionConfig config;
	
	/**
	 * Constructor for the score calculator.
	 * 
	 * @param config
	 */
	public EvolutionCalculator(EvolutionConfig config)
	{
		this.config = config;
	}
	
	/**
	 * Calculates the total score of a build, by combining the scores of the
	 * individual dataproviders multiplied by the weight.
	 * 
	 * @return total score
	 * @throws InvalidConfigException
	 */
	public double calculate(HashMap<String, Double> dataProviderScores) throws InvalidConfigException
	{
		double score = 0;
		double weight = 0;
		double weightSum = 0;
		
		for(Entry<String, Double> dataProvidereScore : dataProviderScores.entrySet())
		{
			try
			{
				weight = getWeight(dataProvidereScore.getKey());
				
				score += dataProvidereScore.getValue() * weight;
				
				weightSum += weight;
			}
			catch(InvalidConfigException e)
			{
				Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).warning(e.getMessage());
			}
		}
		
		if(weightSum == 0.0)
		{
			throw new InvalidConfigException("Skipping build as it has no results");
		}
		
		return score / weightSum;
	}
	
	/**
	 * Checks if the weight is a valid double value. If not, a
	 * {@link InvalidConfigException} will be thrown.
	 * 
	 * @param result
	 * @return weight
	 * @throws InvalidConfigException
	 */
	private double getWeight(String dataProviderId) throws InvalidConfigException
	{
		DataProviderConfig config = getDataProviderConfig(dataProviderId);
		
		double weight = 0.0;
		
		try
		{
			weight = Double.parseDouble(config.getWeight());
			
			if(weight <= 0)
			{
				throw new NumberFormatException();
			}
		}
		catch(NumberFormatException e)
		{
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).warning("[Evolution] [" + dataProviderId + "] Invalid weight: " + weight + "; Using 1 instead.");
			
			return 1;
		}
		
		return weight;
	}
	
	/**
	 * Retrieves the configuration data for a dataprovider, using its identifier
	 * to find it. If the configuration could not found, a
	 * {@link InvalidConfigException} will be thrown.
	 * 
	 * @param result
	 * @return DataProvider Configuration
	 * @throws InvalidConfigException
	 */
	private DataProviderConfig getDataProviderConfig(String dataProvider) throws InvalidConfigException
	{
		DataProviderConfig dataProviderConfig = config.getDataProviders().get(dataProvider);
		
		if(dataProviderConfig == null)
		{
			throw new InvalidConfigException("[Evolution] [ " + dataProvider + "] Unknown dataprovider: " + dataProvider);
		}
		
		return dataProviderConfig;
	}
}
