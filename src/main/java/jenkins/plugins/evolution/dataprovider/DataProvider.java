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
	private String id;
	
	protected DataProvider(String id)
	{
		this.id = id;
	}
	
	public String getId()
	{
		return id;
	}
	
	/**
	 * @param inputStream
	 * @return Creates a new reader instance.
	 */
	protected abstract Reader<?> getReader(InputStream inputStream);
	
	/**
	 * Create a result object, containing the results of the retrieved data.
	 * @param inputStream
	 * 
	 * @return results
	 * @throws PersistenceException
	 * @throws InvalidConfigException
	 */
	public abstract Result getResult(InputStream inputStream) throws PersistenceException;
	
	protected abstract double readResult(InputStream inputStream) throws PersistenceException;
}
