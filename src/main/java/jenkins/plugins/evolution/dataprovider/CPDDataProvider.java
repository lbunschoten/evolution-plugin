package jenkins.plugins.evolution.dataprovider;

import java.io.InputStream;
import jenkins.plugins.evolution.domain.Result;
import jenkins.plugins.evolution.persistence.PersistenceException;
import jenkins.plugins.evolution.persistence.XPathReader;

/**
 * This class can be used to retrieve data from CPD result files. CPD measures
 * the amount of code duplications.
 * 
 * @author leon
 */
public class CPDDataProvider extends DataProvider
{
	public CPDDataProvider(String id)
	{
		super(id);
	}
	
	@Override
	protected XPathReader getReader(InputStream inputStream)
	{
		return new XPathReader(inputStream, "count(//file) - count(//duplication)");
	}
	
	@Override
	public Result getResult(InputStream inputStream) throws PersistenceException
	{
		return new Result(getId(), readResult(inputStream));
	}
	
	@Override
	protected double readResult(InputStream inputStream) throws PersistenceException
	{
		return getReader(inputStream).read().intValue();
	}
}
