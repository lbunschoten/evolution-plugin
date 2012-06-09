package jenkins.plugins.evolution.dataprovider;

import java.io.InputStream;
import jenkins.plugins.evolution.domain.Result;
import jenkins.plugins.evolution.persistence.PersistenceException;
import jenkins.plugins.evolution.persistence.XPathReader;

/**
 * This class can be used to retrieve data from FindBugs result files. The
 * amount of bug instances will be returned.
 * 
 * @author leon
 */
public class FindBugsDataProvider extends DataProvider
{	
	public FindBugsDataProvider(String id)
	{
		super(id);
	}
	
	@Override
	protected XPathReader getReader(InputStream inputStream)
	{
		return new XPathReader(inputStream, "count(//BugInstance)");
	}
	
	@Override
	public Result getResult(InputStream inputStream) throws PersistenceException
	{
		return new Result(getId(), readResult(inputStream));
	}
	
	/**
	 * @return The total amount of bug instances
	 * @throws PersistenceException
	 */
	@Override
	protected double readResult(InputStream inputStream) throws PersistenceException
	{
		return getReader(inputStream).read().intValue();
	}
}
