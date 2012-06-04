package jenkins.plugins.evolution;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import jenkins.plugins.evolution.calculator.ScoreCalculator;
import jenkins.plugins.evolution.config.DataProviderConfig;
import jenkins.plugins.evolution.config.EvolutionConfig;
import jenkins.plugins.evolution.config.InvalidConfigException;
import jenkins.plugins.evolution.domain.Build;
import jenkins.plugins.evolution.domain.Result;
import org.junit.Test;

public class EvolutionScoreCalculatorTest
{
	
	@Test
	public void testWithoutWeight() throws InvalidConfigException
	{
		Build build = getBuild(1, 2, 8, 9);
		
		EvolutionConfig config = new EvolutionConfig();
		
		config.getDataProviders().put("A", getDataProviderConfig("10", "0", "1"));
		config.getDataProviders().put("B", getDataProviderConfig("10", "0", "1"));
		config.getDataProviders().put("C", getDataProviderConfig("10", "0", "1"));
		config.getDataProviders().put("D", getDataProviderConfig("10", "0", "1"));
		
		ScoreCalculator calc = new ScoreCalculator(config);
		
		assertEquals(5, calc.calculate(build).get("total"), 0);
	}
	
	@Test
	public void testWithWeight() throws InvalidConfigException
	{
		Build build = getBuild(1, 2, 8, 9);
		
		EvolutionConfig config = new EvolutionConfig();
		
		config.getDataProviders().put("A", getDataProviderConfig("10", "0", "1"));
		config.getDataProviders().put("B", getDataProviderConfig("10", "0", "2"));
		config.getDataProviders().put("C", getDataProviderConfig("10", "0", "2"));
		config.getDataProviders().put("D", getDataProviderConfig("10", "0", "1"));
		
		ScoreCalculator calc = new ScoreCalculator(config);
		
		assertEquals(5, calc.calculate(build).get("total"), 0);
		
		EvolutionConfig config2 = new EvolutionConfig();
		
		config2.getDataProviders().put("A", getDataProviderConfig("10", "0", "1"));
		config2.getDataProviders().put("B", getDataProviderConfig("10", "0", "2"));
		config2.getDataProviders().put("C", getDataProviderConfig("10", "0", "2"));
		config2.getDataProviders().put("D", getDataProviderConfig("10", "0", "1"));
		
		ScoreCalculator calc2 = new ScoreCalculator(config2);
		
		if(calc2.calculate(build).get("total") != 5)
		{
			fail();
		}
	}
	
	@Test
	public void testInvalidWeight() throws InvalidConfigException
	{
		Build build = getBuild(1, 2, 8, 9);
		
		EvolutionConfig config = new EvolutionConfig();
		
		config.getDataProviders().put("A", getDataProviderConfig("10", "0", "1"));
		config.getDataProviders().put("B", getDataProviderConfig("10", "0", "a"));
		config.getDataProviders().put("C", getDataProviderConfig("10", "0", "-1"));
		config.getDataProviders().put("D", getDataProviderConfig("10", "0", "1"));
		
		ScoreCalculator calc = new ScoreCalculator(config);
		
		assertEquals(5, calc.calculate(build).get("total"), 0);
	}
	
	@Test(expected = InvalidConfigException.class)
	public void testWeightZero() throws InvalidConfigException
	{
		Build build = getBuild(1, 2, 8, 9);
		
		EvolutionConfig config = new EvolutionConfig();
		
		config.getDataProviders().put("A", getDataProviderConfig("10", "0", "0"));
		config.getDataProviders().put("B", getDataProviderConfig("10", "0", "0"));
		config.getDataProviders().put("C", getDataProviderConfig("10", "0", "0"));
		config.getDataProviders().put("D", getDataProviderConfig("10", "0", "0"));
		
		ScoreCalculator calc = new ScoreCalculator(config);
		
		calc.calculate(build);
	}
	
	@Test
	public void testMinMax() throws InvalidConfigException
	{
		Build build = getBuild(1, 2, 8, 9);
		
		EvolutionConfig config = new EvolutionConfig();
		
		config.getDataProviders().put("A", getDataProviderConfig("10", "0", "1"));
		config.getDataProviders().put("B", getDataProviderConfig("0", "10", "1"));
		config.getDataProviders().put("C", getDataProviderConfig("0", "10", "1"));
		config.getDataProviders().put("D", getDataProviderConfig("10", "0", "1"));
		
		ScoreCalculator calc = new ScoreCalculator(config);
		
		assertEquals(5, calc.calculate(build).get("total"), 0);
	}
	
	@Test
	public void testBeyondMinMax() throws InvalidConfigException
	{
		Build build = getBuild(150, 2, 8, 0);
		
		EvolutionConfig config = new EvolutionConfig();
		
		config.getDataProviders().put("A", getDataProviderConfig("100", "50", "1"));
		config.getDataProviders().put("B", getDataProviderConfig("0", "10", "1"));
		config.getDataProviders().put("C", getDataProviderConfig("0", "10", "1"));
		config.getDataProviders().put("D", getDataProviderConfig("100", "50", "1"));
		
		ScoreCalculator calc = new ScoreCalculator(config);
		
		assertEquals(5, calc.calculate(build).get("total"), 0);
		
		build = getBuild(150, 2, 8, 0);
		
		EvolutionConfig config2 = new EvolutionConfig();
		
		config2.getDataProviders().put("A", getDataProviderConfig("50", "100", "1"));
		config2.getDataProviders().put("B", getDataProviderConfig("0", "10", "1"));
		config2.getDataProviders().put("C", getDataProviderConfig("0", "10", "1"));
		config2.getDataProviders().put("D", getDataProviderConfig("50", "100", "1"));
		
		calc = new ScoreCalculator(config2);
		
		assertEquals(5, calc.calculate(build).get("total"), 0);
	}
	
	@Test
	public void testInvalidMinMax() throws InvalidConfigException
	{
		Build build = getBuild(1, 2, 8, 9);
		
		EvolutionConfig config = new EvolutionConfig();
		
		config.getDataProviders().put("A", getDataProviderConfig("10", "0", "1"));
		config.getDataProviders().put("B", getDataProviderConfig("a", "0", "1"));
		config.getDataProviders().put("C", getDataProviderConfig("10", "-1", "1"));
		config.getDataProviders().put("D", getDataProviderConfig("10", "0", "1"));
		
		ScoreCalculator calc = new ScoreCalculator(config);
		
		assertEquals(5, calc.calculate(build).get("total"), 0);
	}
	
	@Test
	public void testEqualMinMax() throws InvalidConfigException
	{
		Build build = getBuild(1, 2, 8, 9);
		
		EvolutionConfig config = new EvolutionConfig();
		
		config.getDataProviders().put("A", getDataProviderConfig("10", "0", "1"));
		config.getDataProviders().put("B", getDataProviderConfig("1", "1", "1"));
		config.getDataProviders().put("C", getDataProviderConfig("1", "1", "1"));
		config.getDataProviders().put("D", getDataProviderConfig("10", "0", "1"));
		
		ScoreCalculator calc = new ScoreCalculator(config);
		
		assertEquals(5, calc.calculate(build).get("total"), 0);
	}
	
	@Test
	public void testUnknownDataProvider() throws InvalidConfigException
	{
		Build build = getBuild(1, 2, 8, 9);
		
		EvolutionConfig config = new EvolutionConfig();
		
		config.getDataProviders().put("B", getDataProviderConfig("10", "0", "1"));
		config.getDataProviders().put("C", getDataProviderConfig("10", "0", "1"));
		
		ScoreCalculator calc = new ScoreCalculator(config);
		
		assertEquals(5, calc.calculate(build).get("total"), 0);
	}
	
	private Build getBuild(double score1, double score2, double score3, double score4)
	{
		Result r1 = new Result("A", score1);
		Result r2 = new Result("B", score2);
		Result r3 = new Result("C", score3);
		Result r4 = new Result("D", score4);
		
		Build b = new Build(1);
		
		b.addResult(r1);
		b.addResult(r2);
		b.addResult(r3);
		b.addResult(r4);
		
		return b;
	}
	
	private DataProviderConfig getDataProviderConfig(String min, String max, String weight)
	{
		DataProviderConfig configItem = new DataProviderConfig("checkstyle");
		
		configItem.setMin(min);
		configItem.setMax(max);
		configItem.setWeight(weight);
		
		return configItem;
	}
	
}
