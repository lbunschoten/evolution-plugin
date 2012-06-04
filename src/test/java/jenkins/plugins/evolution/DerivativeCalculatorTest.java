package jenkins.plugins.evolution;

import static org.junit.Assert.assertEquals;
import jenkins.plugins.evolution.calculator.DerivativeCalculator;
import org.junit.Test;

public class DerivativeCalculatorTest
{
	
	@Test
	public void test()
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
