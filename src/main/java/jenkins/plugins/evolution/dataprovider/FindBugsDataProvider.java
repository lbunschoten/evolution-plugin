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
	public static final String NAME = "FindBugs";
	
	public static final String ID = NAME.toLowerCase();
	
	public FindBugsDataProvider(InputStream inputStream)
	{
		super(inputStream);
	}
	
	@Override
	public String getName()
	{
		return FindBugsDataProvider.NAME;
	}
	
	@Override
	public String getId()
	{
		return FindBugsDataProvider.ID;
	}
	
	@Override
	protected XPathReader getReader()
	{
		return new XPathReader(getInputStream(), "count(//BugInstance)");
	}
	
	@Override
	public Result getResult() throws PersistenceException
	{
		return new Result(ID, readResult());
	}
	
	/**
	 * @return The total amount of bug instances
	 * @throws PersistenceException
	 */
	@Override
	protected double readResult() throws PersistenceException
	{
		return getReader().read().intValue();
	}
}
