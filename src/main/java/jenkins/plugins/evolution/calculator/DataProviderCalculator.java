package jenkins.plugins.evolution.calculator;

import java.util.HashMap;
import java.util.logging.Logger;
import jenkins.plugins.evolution.config.DataProviderConfig;
import jenkins.plugins.evolution.config.EvolutionConfig;
import jenkins.plugins.evolution.config.InvalidConfigException;
import jenkins.plugins.evolution.domain.Build;
import jenkins.plugins.evolution.domain.Result;

public class DataProviderCalculator
{
	EvolutionConfig config;
	
	private static final int WORST_SCORE = 0;
	
	private static final int BEST_SCORE = 10;
	
	public DataProviderCalculator(EvolutionConfig config)
	{
		this.config = config;
	}
	
	/**
	 * Performs calculations for all individual dataproviders.
	 * 
	 * @return Results of all calculations
	 */
	public HashMap<String, Double> calculate(Build build)
	{
		HashMap<String, Double> scores = new HashMap<String, Double>();
		
		for(Result result : build.getResults())
		{
			try
			{
				scores.put(result.getDataProviderId(), calculateDataProviderScore(result));
			}
			catch(InvalidConfigException e)
			{
				Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).warning(e.getMessage());
			}
		}
		
		return scores;
	}
	
	/**
	 * Checks whether a higher or a lower value is better and calculates the
	 * score accordingly.
	 * 
	 * @param value
	 * @return score
	 * @throws InvalidConfigException
	 */
	private double calculateDataProviderScore(Result result) throws InvalidConfigException
	{
		DataProviderConfig config = getDataProviderConfig(result.getDataProviderId());
		double value = 0;
		double min = 0;
		double max = 0;
		
		try
		{
			if(!config.isFullyConfigured())
			{
				throw new NumberFormatException();
			}
			
			value = validatePositiveDouble(result.getData());
			min = validatePositiveDouble(Double.parseDouble(config.getMin()));
			max = validatePositiveDouble(Double.parseDouble(config.getMax()));
		}
		catch(NumberFormatException e)
		{
			throw new InvalidConfigException("Minimum or maximum value has not been configured properly for " + result.getDataProviderId());
		}
		
		if(min == max)
		{
			throw new InvalidConfigException("Minimum and maximum value are equal for " + result.getDataProviderId());
		}
		
		return calculateDataProviderScore(value, min, max);
	}
	
	/**
	 * Calculates the score for a dataprovider. The score will be a value
	 * between 0 and 10. All score lower then 0, or highere then 10 will be
	 * reset to 0 or 10.
	 * 
	 * @param value
	 * @param min
	 * @param max
	 * @return a score between 0 and 10
	 */
	private double calculateDataProviderScore(double value, double min, double max)
	{
		double score = (value - min) / (max - min) * 10;
		
		if(score > BEST_SCORE)
		{
			return BEST_SCORE;
		}
		else if(score < WORST_SCORE)
		{
			return WORST_SCORE;
		}
		
		return score;
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
		DataProviderConfig dataProviderConfig = config.getDataProviderConfigs().get(dataProvider);
		
		if(dataProviderConfig == null)
		{
			throw new InvalidConfigException("[Evolution] [ " + dataProvider + "] Unknown dataprovider: " + dataProvider);
		}
		
		return dataProviderConfig;
	}
	
	/**
	 * Validates if a value if is a positive double value. Throws an exception
	 * otherwise.
	 * 
	 * @param value
	 * @return The initial value
	 * @throws InvalidConfigException
	 */
	private double validatePositiveDouble(double value) throws InvalidConfigException
	{
		if(value < 0)
		{
			throw new InvalidConfigException("Negative double value");
		}
		
		return value;
	}
}
