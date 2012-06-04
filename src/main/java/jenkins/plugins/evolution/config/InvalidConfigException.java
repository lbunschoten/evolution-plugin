package jenkins.plugins.evolution.config;

/**
 * This exception will be thrown whenever there is a invalid configuration. This
 * exception is most likely to be thrown during retrieval of results or
 * performing calculations.
 * 
 * @author leon
 */
public class InvalidConfigException extends Exception
{
	private static final long serialVersionUID = -2868689631986198966L;
	
	public InvalidConfigException(String message)
	{
		super(message);
	}
	
}
