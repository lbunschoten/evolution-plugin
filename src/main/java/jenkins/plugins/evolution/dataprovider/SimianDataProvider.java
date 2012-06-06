package jenkins.plugins.evolution.dataprovider;

import java.io.InputStream;
import jenkins.plugins.evolution.domain.Result;
import jenkins.plugins.evolution.persistence.PersistenceException;
import jenkins.plugins.evolution.persistence.XPathReader;

/**
 * This class can be used to retrieve data from Simian result files. Simian measures
 * the amount of code duplications.
 * 
 * @author leon
 */
public class SimianDataProvider extends DataProvider
{
	public static final String NAME = "Simian";

	public static final String ID = NAME.toLowerCase();
	
	public static final String DEFAULT_PATH = "";
	
	public SimianDataProvider(InputStream inputStream)
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
	protected XPathReader getReader()
	{
		return new XPathReader(getInputStream(), "count(//block) - count(//set)");
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
