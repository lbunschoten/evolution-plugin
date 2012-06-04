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
	public CPDDataProvider(InputStream inputStream)
	{
		super(inputStream);
	}
	
	public static final String NAME = "CPD";
	
	public static final String ID = NAME.toLowerCase();
	
	@Override
	public String getName()
	{
		return CPDDataProvider.NAME;
	}
	
	@Override
	public String getId()
	{
		return ID;
	}
	
	@Override
	protected XPathReader getReader()
	{
		return new XPathReader(getInputStream(), "count(//file) - count(//duplication)");
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
