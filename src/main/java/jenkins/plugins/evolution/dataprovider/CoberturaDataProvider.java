package jenkins.plugins.evolution.dataprovider;

import java.io.InputStream;
import jenkins.plugins.evolution.domain.Result;
import jenkins.plugins.evolution.persistence.PersistenceException;
import jenkins.plugins.evolution.persistence.Reader;
import jenkins.plugins.evolution.persistence.XPathReader;

/**
 * This class can be used to retrieve data from Cobertura result files. The
 * total test coverage will be returned as a value between 0 and 100.
 * 
 * @author leon
 */
public class CoberturaDataProvider extends DataProvider
{
	public CoberturaDataProvider(String id)
	{
		super(id);
	}
	
	@Override
	protected Reader<XPathReader> getReader(InputStream inputStream)
	{
		return new XPathReader(inputStream, "/coverage/@line-rate");
	}
	
	@Override
	public Result getResult(InputStream inputStream) throws PersistenceException
	{
		return new Result(getId(), readResult(inputStream));
	}
	
	@Override
	protected double readResult(InputStream inputStream) throws PersistenceException
	{
		XPathReader reader = (XPathReader) getReader(inputStream);
		
		return reader.read().doubleValue() * 100;
	}
}
