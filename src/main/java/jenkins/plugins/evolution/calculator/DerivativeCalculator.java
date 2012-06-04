package jenkins.plugins.evolution.calculator;

/**
 * This class calculates the derivative of the score graph. To get a more global
 * view of the average derivative, it takes bigger steps then the actual score
 * graph.
 * 
 * @author leon
 */
public class DerivativeCalculator
{
	private double total = 0;
	
	private double prev = -1;
	
	private int calculationCount = 1;
	
	private int step = 1;
	
	private static final int STEP_PERCENTAGE = 5;
	
	public DerivativeCalculator(int buildCount)
	{
		this.step = getStepSize(buildCount);
	}
	
	private int getStepSize(int buildCount)
	{
		int stepSize = (int) Math.floor(buildCount / (100.0 / STEP_PERCENTAGE));
		
		if(stepSize < 1)
		{
			stepSize = 1;
		}
		
		return stepSize;
	}
	
	public double calculate(double score)
	{
		double derivative = 0;
		
		if(prev == -1)
		{
			prev = score;
			
			return 0;
		}
		
		total += score - prev;
		
		if(calculationCount % step == 0)
		{
			derivative = total / step;
			
			total = 0;
		}
		
		prev = score;
		
		calculationCount++;
		
		return derivative;
	}
}
