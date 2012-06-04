package jenkins.plugins.evolution.dataprovider;

import java.io.InputStream;
import jenkins.plugins.evolution.domain.Result;
import jenkins.plugins.evolution.persistence.PersistenceException;
import jenkins.plugins.evolution.persistence.Reader;

/**
 * An abstract data provider. Each data provider reads data from a file and
 * returns the results.
 * 
 * @author leon
 */
public abstract class DataProvider
{
	private InputStream inputStream;
	
	public DataProvider(InputStream inputStream)
	{
		this.inputStream = inputStream;
	}
	
	protected InputStream getInputStream()
	{
		return inputStream;
	}
	
	/**
	 * @return The name of a data provider.
	 */
	public abstract String getName();
	
	/**
	 * @return A unique identifier of a data provider.
	 */
	public abstract String getId();
	
	/**
	 * @return Creates a new reader instance.
	 */
	protected abstract Reader<?> getReader();
	
	/**
	 * Create a result object, containing the results of the retrieved data.
	 * 
	 * @return results
	 * @throws PersistenceException
	 * @throws InvalidConfigException
	 */
	public abstract Result getResult() throws PersistenceException;
	
	protected abstract double readResult() throws PersistenceException;
}
