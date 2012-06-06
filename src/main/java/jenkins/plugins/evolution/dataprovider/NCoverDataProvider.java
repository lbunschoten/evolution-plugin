package jenkins.plugins.evolution.dataprovider;

import java.io.InputStream;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.regex.MatchResult;
import jenkins.plugins.evolution.domain.Result;
import jenkins.plugins.evolution.persistence.PersistenceException;
import jenkins.plugins.evolution.persistence.RegExpReader;

/**
 * This class can be used to retrieve data from ncover result files. The total
 * test coverage will be read, returning a value between 0 and 100.
 * 
 * @author leon
 */
public class NCoverDataProvider extends DataProvider
{
	public static final String NAME = "NCover";
	
	public static final String ID = NAME.toLowerCase();
	
	public static final String DEFAULT_PATH = "**/fullcoveragereport.html";
	
	public NCoverDataProvider(InputStream inputStream)
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
	protected RegExpReader getReader()
	{
		return new RegExpReader(getInputStream(), "Symbol Coverage: <span class=\"[\\w\\s]+\">([\\d,]+)%<\\/span>");
	}
	
	@Override
	public Result getResult() throws PersistenceException
	{
		return new Result(ID, readResult());
	}
	
	@Override
	public double readResult() throws PersistenceException
	{
		double score = 0;
		MatchResult result = null;
		
		try
		{
			 result = getReader().read();
			
			if(result != null)
			{
				NumberFormat format = NumberFormat.getInstance(Locale.GERMAN);
				score = format.parse(result.group(1)).doubleValue();
			}
		}
		catch(ParseException e)
		{
			throw new PersistenceException("Could not parse result: " + result.group(1));
		}
		
		return score;
	}
}
