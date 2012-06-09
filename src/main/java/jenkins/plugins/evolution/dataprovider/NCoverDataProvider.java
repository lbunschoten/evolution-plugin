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
	public NCoverDataProvider(String id)
	{
		super(id);
	}
	
	@Override
	protected RegExpReader getReader(InputStream inputStream)
	{
		return new RegExpReader(inputStream, "Symbol Coverage: <span class=\"[\\w\\s]+\">([\\d,]+)%<\\/span>");
	}
	
	@Override
	public Result getResult(InputStream inputStream) throws PersistenceException
	{
		return new Result(getId(), readResult(inputStream));
	}
	
	@Override
	public double readResult(InputStream inputStream) throws PersistenceException
	{
		double score = 0;
		MatchResult result = null;
		
		try
		{
			 result = getReader(inputStream).read();
			
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
