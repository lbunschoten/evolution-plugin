package jenkins.plugins.evolution.dataprovider;

import java.io.InputStream;
import jenkins.plugins.evolution.domain.Result;
import jenkins.plugins.evolution.persistence.PersistenceException;
import jenkins.plugins.evolution.persistence.XPathReader;

/**
 * This class can be used to retrieve data from FxCop result files. The
 * amount of issues will be returned.
 * 
 * @author leon
 */
public class FxCopDataProvider extends DataProvider
{	
	public static final String NAME = "FxCop";
	
	public static final String ID = NAME.toLowerCase();
	
	public static final String DEFAULT_PATH = "";
	
	
	public FxCopDataProvider(InputStream inputStream)
	{
		super(inputStream);
	}
	
	@Override
	public String getName()
	{
		return FxCopDataProvider.NAME;
	}
	
	@Override
	public String getId()
	{
		return ID;
	}
	
	@Override
	protected XPathReader getReader()
	{
		return new XPathReader(getInputStream(), "count(//Issue)");
	}
	
	@Override
	public Result getResult() throws PersistenceException
	{
		return new Result(ID, readResult());
	}
	
	@Override
	protected double readResult() throws PersistenceException
	{
		return getReader().read().intValue();
	}
}
