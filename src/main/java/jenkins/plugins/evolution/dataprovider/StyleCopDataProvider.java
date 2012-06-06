package jenkins.plugins.evolution.dataprovider;

import java.io.InputStream;
import jenkins.plugins.evolution.domain.Result;
import jenkins.plugins.evolution.persistence.PersistenceException;
import jenkins.plugins.evolution.persistence.Reader;
import jenkins.plugins.evolution.persistence.XPathReader;

/**
 * This class can be used to retrieve data from StyleCop result files. The
 * amount of violations will be returned.
 * 
 * @author leon
 */
public class StyleCopDataProvider extends DataProvider
{
	public static final String NAME = "StyleCop";
	
	public static final String ID = NAME.toLowerCase();
	
	public static final String DEFAULT_PATH = "";
	
	public StyleCopDataProvider(InputStream inputStream)
	{
		super(inputStream);
	}
	
	@Override
	public String getName()
	{
		return NAME;
	}
	
	@Override
	public String getId()
	{
		return ID;
	}
	
	@Override
	protected Reader<XPathReader> getReader()
	{
		return new XPathReader(getInputStream(), "count(//Violation)");
	}
	
	@Override
	public Result getResult() throws PersistenceException
	{
		return new Result(ID, readResult());
	}
	
	@Override
	protected double readResult() throws PersistenceException
	{
		return ((XPathReader) getReader()).read().doubleValue();
	}
}
