package jenkins.plugins.evolution.dataprovider;

import java.io.InputStream;
import jenkins.plugins.evolution.domain.Result;
import jenkins.plugins.evolution.persistence.PersistenceException;
import jenkins.plugins.evolution.persistence.Reader;
import jenkins.plugins.evolution.persistence.XPathReader;

/**
 * This class can be used to retrieve data from CheckStyle result files. The
 * amount of errors will be returned.
 * 
 * @author leon
 */
public class CheckStyleDataProvider extends DataProvider
{	
	public CheckStyleDataProvider(String id)
	{
		super(id);
	}
	
	@Override
	protected Reader<XPathReader> getReader(InputStream inputStream)
	{
		return new XPathReader(inputStream, "count(//error)");
	}
	
	@Override
	public Result getResult(InputStream inputStream) throws PersistenceException
	{
		return new Result(getId(), readResult(inputStream));
	}
	
	@Override
	protected double readResult(InputStream inputStream) throws PersistenceException
	{
		return ((XPathReader) getReader(inputStream)).read().doubleValue();
	}
}
