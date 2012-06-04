package jenkins.plugins.evolution.calculator;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Logger;
import jenkins.plugins.evolution.config.DataProviderConfig;
import jenkins.plugins.evolution.config.EvolutionConfig;
import jenkins.plugins.evolution.config.InvalidConfigException;
import jenkins.plugins.evolution.domain.Build;
import jenkins.plugins.evolution.domain.Result;

/**
 * This class is used to calculate all scores as shown in the evolution graph.
 * First all results for the individual data providers are calculated for each specific
 * build. Afterwards these results are combined into a total score.
 * 
 * @author leon
 */
public class ScoreCalculator
{
	private HashMap<String, DataProviderConfig> dataProviderConfigs;
	
	private HashMap<String, Double> scores;
	
	private static final int WORST_SCORE = 0;
	
	private static final int BEST_SCORE = 10;
	
	/**
	 * Constructor for the score calculator.
	 * 
	 * @param config
	 */
	public ScoreCalculator(EvolutionConfig config)
	{
		dataProviderConfigs = config.getDataProviders();
	}
	
	/**
	 * Performs all calculations for a build.
	 * 
	 * @param build
	 * @throws InvalidConfigException
	 */
	public HashMap<String, Double> calculate(Build build) throws InvalidConfigException
	{
		scores = new HashMap<String, Double>();
		
		HashMap<String, Double> dataProviderScores = calculateDataProviderScores(build);
		
		scores.put("total", calculateScore(build.getId(), dataProviderScores));
		scores.putAll(dataProviderScores);
		
		return scores;
	}
	
	/**
	 * Calculates the total score of a build, by combining the scores of the
	 * individual dataproviders multiplied by the weight.
	 * 
	 * @return total score
	 * @throws InvalidConfigException
	 */
	private double calculateScore(int buildNumber, HashMap<String, Double> dataProviderScores) throws InvalidConfigException
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
			throw new InvalidConfigException("No results found for build " + buildNumber);
		}
		
		return score / weightSum;
	}
	
	/**
	 * Performs calculations for all individual dataproviders.
	 * 
	 * @return Results of all calculations
	 */
	private HashMap<String, Double> calculateDataProviderScores(Build build)
	{
		scores = new HashMap<String, Double>();
		
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
	private DataProviderConfig getDataProviderConfig(String dataprovider) throws InvalidConfigException
	{
		DataProviderConfig config = dataProviderConfigs.get(dataprovider);
		
		if(config == null)
		{
			throw new InvalidConfigException("Unknown dataprovider " + dataprovider);
		}
		
		return config;
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
			throw new InvalidConfigException("Invalid weight for " + dataProviderId);
		}
		
		return weight;
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
