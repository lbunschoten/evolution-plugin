package jenkins.plugins.evolution.util;

/**
 * This exception is thrown whenever a certain object could not be found in
 * Jenkins. For instance whenever there is searched for a non-existing build.
 * 
 * @author leon
 */
public class ItemNotFoundException extends Exception
{
	
	private static final long serialVersionUID = 1L;
	
	public ItemNotFoundException(String message)
	{
		super(message);
	}
}
