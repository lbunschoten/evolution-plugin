package jenkins.plugins.evolution.persistence;

/**
 * This exception is thrown whenever an error occurred while reading or writing
 * data.
 * 
 * @author leon
 */
public class PersistenceException extends Exception
{
	private static final long serialVersionUID = -2341476697632931169L;
	
	public PersistenceException(String message)
	{
		super(message);
	}
	
}
