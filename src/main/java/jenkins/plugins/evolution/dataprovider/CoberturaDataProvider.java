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
	public static final String NAME = "Cobertura";
	
	public static final String ID = NAME.toLowerCase();
	
	public static final String DEFAULT_PATH = "**/coverage.xml";
	
	public CoberturaDataProvider(InputStream inputStream)
	{
		super(inputStream);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName()
	{
		return NAME;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getId()
	{
		return ID;
	}
	
	@Override
	protected Reader<XPathReader> getReader()
	{
		return new XPathReader(getInputStream(), "/coverage/@line-rate");
	}
	
	@Override
	public Result getResult() throws PersistenceException
	{
		return new Result(ID, readResult());
	}
	
	@Override
	protected double readResult() throws PersistenceException
	{
		XPathReader reader = (XPathReader) getReader();
		
		return reader.read().doubleValue() * 100;
	}
}
