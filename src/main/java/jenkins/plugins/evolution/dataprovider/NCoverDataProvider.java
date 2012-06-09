package jenkins.plugins.evolution.dataprovider;

import java.io.InputStream;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import jenkins.plugins.evolution.domain.Result;
import jenkins.plugins.evolution.persistence.HtmlReader;
import jenkins.plugins.evolution.persistence.PersistenceException;
import org.htmlcleaner.TagNode;

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
	protected HtmlReader getReader(InputStream inputStream)
	{
		return new HtmlReader(inputStream);
	}
	
	@Override
	public Result getResult(InputStream inputStream) throws PersistenceException
	{
		return new Result(getId(), readResult(inputStream));
	}
	
	@Override
	public double readResult(InputStream inputStream) throws PersistenceException
	{
		TagNode tagNode = getReader(inputStream).read();
		
		if(tagNode != null)
		{
			TagNode[] tagNodes = tagNode.getElementsByName("p", true);
			
			for(int i = 0; i < tagNodes.length; i++)
			{
				if(tagNodes[i].getText().toString().contains("Symbol Coverage"))
				{
					return formatResult(tagNodes[i].getChildTags()[0].getText().toString());
				}
			}
		}
			
		throw new PersistenceException("Could not parse result");
	}
	
	private double formatResult(String result) throws PersistenceException
	{
		result = result.replace("%", "");
		double score = 0;
		
		try
		{	
			NumberFormat format = NumberFormat.getInstance(Locale.GERMAN);
			score = format.parse(result).doubleValue();
		}
		catch(NumberFormatException e)
		{
			throw new PersistenceException("Could not parse result " + result);
		}
		catch(ParseException e)
		{
			throw new PersistenceException("Could not parse result " + result);
		}
		
		return score;
	}
}
