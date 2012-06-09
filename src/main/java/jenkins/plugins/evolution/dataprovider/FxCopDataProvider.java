package jenkins.plugins.evolution.dataprovider;

import java.io.InputStream;
import jenkins.plugins.evolution.domain.Result;
import jenkins.plugins.evolution.persistence.PersistenceException;
import jenkins.plugins.evolution.persistence.XPathReader;

/**
 * This class can be used to retrieve data from FxCop result files. The amount
 * of issues will be returned.
 * 
 * @author leon
 */
public class FxCopDataProvider extends DataProvider
{
	public FxCopDataProvider(String id)
	{
		super(id);
	}
	
	@Override
	protected XPathReader getReader(InputStream inputStream)
	{
		return new XPathReader(inputStream, "count(//Issue)");
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
