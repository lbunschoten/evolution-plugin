package jenkins.plugins.evolution;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import jenkins.plugins.evolution.calculator.DataProviderCalculator;
import jenkins.plugins.evolution.calculator.DerivativeCalculator;
import jenkins.plugins.evolution.calculator.EvolutionCalculator;
import jenkins.plugins.evolution.config.DataProviderConfig;
import jenkins.plugins.evolution.config.EvolutionConfig;
import jenkins.plugins.evolution.config.InvalidConfigException;
import jenkins.plugins.evolution.domain.Build;
import jenkins.plugins.evolution.domain.Result;
import org.junit.Test;

public class CalculatorTest
{
	
	EvolutionCalculator evolutionCalculator;
	
	DataProviderCalculator dataProviderCalculator;
	
	@Test
	public void testWithoutWeight() throws InvalidConfigException
	{
		Build build = getBuild(1, 2, 8, 9);
		
		EvolutionConfig config = new EvolutionConfig();
		
		getDataProviderConfig(config.getDataProviderConfig("NCOVER"), "10", "0", "1");
		getDataProviderConfig(config.getDataProviderConfig("COBERTURA"), "10", "0", "1");
		getDataProviderConfig(config.getDataProviderConfig("CHECKSTYLE"), "10", "0", "1");
		getDataProviderConfig(config.getDataProviderConfig("FINDBUGS"), "10", "0", "1");
		
		evolutionCalculator = new EvolutionCalculator(config);
		dataProviderCalculator = new DataProviderCalculator(config);
		
		assertEquals(5, evolutionCalculator.calculate(dataProviderCalculator.calculate(build)), 0);
	}
	
	@Test
	public void testWithWeight() throws InvalidConfigException
	{
		Build build = getBuild(1, 2, 8, 9);
		
		EvolutionConfig config = new EvolutionConfig();
		
		getDataProviderConfig(config.getDataProviderConfig("NCOVER"), "10", "0", "1");
		getDataProviderConfig(config.getDataProviderConfig("COBERTURA"), "10", "0", "2");
		getDataProviderConfig(config.getDataProviderConfig("CHECKSTYLE"), "10", "0", "2");
		getDataProviderConfig(config.getDataProviderConfig("FINDBUGS"), "10", "0", "1");
		
		evolutionCalculator = new EvolutionCalculator(config);
		dataProviderCalculator = new DataProviderCalculator(config);
		
		assertEquals(5, evolutionCalculator.calculate(dataProviderCalculator.calculate(build)), 0);
		
		EvolutionConfig config2 = new EvolutionConfig();
		
		getDataProviderConfig(config2.getDataProviderConfig("NCOVER"), "10", "0", "1");
		getDataProviderConfig(config2.getDataProviderConfig("COBERTURA"), "10", "0", "2");
		getDataProviderConfig(config2.getDataProviderConfig("CHECKSTYLE"), "10", "0", "2");
		getDataProviderConfig(config2.getDataProviderConfig("FINDBUGS"), "10", "0", "1");
		
		EvolutionCalculator evolutionCalculator2 = new EvolutionCalculator(config2);
		DataProviderCalculator dataProviderCalculator2 = new DataProviderCalculator(config);
		
		if(evolutionCalculator2.calculate(dataProviderCalculator2.calculate(build)) != 5)
		{
			fail();
		}
	}
	
	@Test
	public void testInvalidWeight() throws InvalidConfigException
	{
		Build build = getBuild(1, 2, 8, 9);
		
		EvolutionConfig config = new EvolutionConfig();
				
		getDataProviderConfig(config.getDataProviderConfig("NCOVER"), "10", "0", "1");
		getDataProviderConfig(config.getDataProviderConfig("COBERTURA"), "10", "0", "a");
		getDataProviderConfig(config.getDataProviderConfig("CHECKSTYLE"), "10", "0", "-1");
		getDataProviderConfig(config.getDataProviderConfig("FINDBUGS"), "10", "0", "1");
		
		evolutionCalculator = new EvolutionCalculator(config);
		dataProviderCalculator = new DataProviderCalculator(config);
		
		assertEquals(5, evolutionCalculator.calculate(dataProviderCalculator.calculate(build)), 0);
	}
	
	@Test()
	public void testWeightZero() throws InvalidConfigException
	{
		Build build = getBuild(1, 2, 8, 9);
		
		EvolutionConfig config = new EvolutionConfig();
		
		getDataProviderConfig(config.getDataProviderConfig("NCOVER"), "10", "0", "0");
		getDataProviderConfig(config.getDataProviderConfig("COBERTURA"), "10", "0", "0");
		getDataProviderConfig(config.getDataProviderConfig("CHECKSTYLE"), "10", "0", "0");
		getDataProviderConfig(config.getDataProviderConfig("FINDBUGS"), "10", "0", "0");
		
		evolutionCalculator = new EvolutionCalculator(config);
		dataProviderCalculator = new DataProviderCalculator(config);
		
		assertEquals(5, evolutionCalculator.calculate(dataProviderCalculator.calculate(build)), 0);
	}
	
	@Test
	public void testMinMax() throws InvalidConfigException
	{
		Build build = getBuild(1, 2, 8, 9);
		
		EvolutionConfig config = new EvolutionConfig();
		
		getDataProviderConfig(config.getDataProviderConfig("NCOVER"), "10", "0", "1");
		getDataProviderConfig(config.getDataProviderConfig("COBERTURA"), "0", "10", "1");
		getDataProviderConfig(config.getDataProviderConfig("CHECKSTYLE"), "0", "10", "1");
		getDataProviderConfig(config.getDataProviderConfig("FINDBUGS"), "10", "0", "1");	
	
		evolutionCalculator = new EvolutionCalculator(config);
		dataProviderCalculator = new DataProviderCalculator(config);
		
		assertEquals(5, evolutionCalculator.calculate(dataProviderCalculator.calculate(build)), 0);
	}
	
	@Test
	public void testBeyondMinMax() throws InvalidConfigException
	{
		Build build = getBuild(150, 2, 8, 0);
		
		EvolutionConfig config = new EvolutionConfig();
		
		getDataProviderConfig(config.getDataProviderConfig("NCOVER"), "100", "50", "1");
		getDataProviderConfig(config.getDataProviderConfig("COBERTURA"), "0", "10", "1");
		getDataProviderConfig(config.getDataProviderConfig("CHECKSTYLE"), "0", "10", "1");
		getDataProviderConfig(config.getDataProviderConfig("FINDBUGS"), "100", "50", "1");
		
		evolutionCalculator = new EvolutionCalculator(config);
		dataProviderCalculator = new DataProviderCalculator(config);
		
		assertEquals(5, evolutionCalculator.calculate(dataProviderCalculator.calculate(build)), 0);
		
		build = getBuild(150, 2, 8, 0);
		
		EvolutionConfig config2 = new EvolutionConfig();
		
		getDataProviderConfig(config2.getDataProviderConfig("NCOVER"), "50", "100", "1");
		getDataProviderConfig(config2.getDataProviderConfig("COBERTURA"), "0", "10", "1");
		getDataProviderConfig(config2.getDataProviderConfig("CHECKSTYLE"), "0", "10", "1");
		getDataProviderConfig(config2.getDataProviderConfig("FINDBUGS"), "50", "100", "1");
		
		evolutionCalculator = new EvolutionCalculator(config2);
		dataProviderCalculator = new DataProviderCalculator(config2);
		
		assertEquals(5, evolutionCalculator.calculate(dataProviderCalculator.calculate(build)), 0);
	}
	
	@Test
	public void testInvalidMinMax() throws InvalidConfigException
	{
		Build build = getBuild(1, 2, 8, 9);
		
		EvolutionConfig config = new EvolutionConfig();
		
		getDataProviderConfig(config.getDataProviderConfig("NCOVER"), "10", "0", "1");
		getDataProviderConfig(config.getDataProviderConfig("COBERTURA"), "a", "0", "1");
		getDataProviderConfig(config.getDataProviderConfig("CHECKSTYLE"), "10", "-1", "1");
		getDataProviderConfig(config.getDataProviderConfig("FINDBUGS"), "10", "0", "1");
		
		evolutionCalculator = new EvolutionCalculator(config);
		dataProviderCalculator = new DataProviderCalculator(config);
		
		assertEquals(5, evolutionCalculator.calculate(dataProviderCalculator.calculate(build)), 0);
	}
	
	@Test
	public void testEqualMinMax() throws InvalidConfigException
	{
		Build build = getBuild(1, 2, 8, 9);
		
		EvolutionConfig config = new EvolutionConfig();
		
		getDataProviderConfig(config.getDataProviderConfig("NCOVER"), "10", "0", "1");
		getDataProviderConfig(config.getDataProviderConfig("COBERTURA"), "1", "1", "1");
		getDataProviderConfig(config.getDataProviderConfig("CHECKSTYLE"), "1", "1", "1");
		getDataProviderConfig(config.getDataProviderConfig("FINDBUGS"), "10", "0", "1");
		
		evolutionCalculator = new EvolutionCalculator(config);
		dataProviderCalculator = new DataProviderCalculator(config);
		
		assertEquals(5, evolutionCalculator.calculate(dataProviderCalculator.calculate(build)), 0);
	}
	
	private Build getBuild(double score1, double score2, double score3, double score4)
	{
		Result r1 = new Result("NCOVER", score1);
		Result r2 = new Result("COBERTURA", score2);
		Result r3 = new Result("CHECKSTYLE", score3);
		Result r4 = new Result("FINDBUGS", score4);
		
		Build b = new Build(1);
		
		b.addResult(r1);
		b.addResult(r2);
		b.addResult(r3);
		b.addResult(r4);
		
		return b;
	}
	
	private DataProviderConfig getDataProviderConfig(DataProviderConfig dataProviderConfig, String min, String max, String weight)
	{
		dataProviderConfig.setPath("test");
		dataProviderConfig.setMin(min);
		dataProviderConfig.setMax(max);
		dataProviderConfig.setWeight(weight);
		
		return dataProviderConfig;
	}
	
	@Test
	public void testDerivativeCalculator()
	{
		DerivativeCalculator calc = new DerivativeCalculator(6);
		
		System.out.println(calc.calculate(5.68));
		System.out.println(calc.calculate(5.58));
		System.out.println(calc.calculate(5.17));
		System.out.println(calc.calculate(4.94));
		System.out.println(calc.calculate(2.99));
		double result = calc.calculate(4.87);
		System.out.println(result);
		assertEquals(1.88, result, 0.0002);
	}
	
}
