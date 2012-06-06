package jenkins.plugins.evolution.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import jenkins.plugins.evolution.util.EncodingUtil;
import org.apache.commons.io.IOUtils;

/**
 * This reader uses regular expressions to fetch data from a file.
 * 
 * @author leon
 */
public class RegExpReader implements Reader<RegExpReader>
{
	private String regex;
	
	private InputStream inputStream;
	
	private String input;
	
	public RegExpReader(InputStream inputStream, String regex)
	{
		this.inputStream = inputStream;
		this.regex = regex;
	}
	
	@Override
	public void openReader() throws PersistenceException
	{
		try
		{
			if(inputStream != null)
			{
				input = IOUtils.toString(inputStream, new EncodingUtil().getEncodingAsString());
			}
			else
			{
				throw new PersistenceException("InputStream is not available");
			}
		}
		catch(IOException e)
		{
			throw new PersistenceException("Could not parse data from file using regular expressions.");
		}
	}
	
	/**
	 * Compiles the regular expression String to a Pattern object. If the
	 * pattern appears to be invalid, a PersistenceException will be thrown.
	 * 
	 * @return A compiled pattern
	 * @throws PersistenceException
	 */
	public Pattern getCompiledPattern() throws PersistenceException
	{
		try
		{
			return Pattern.compile(regex);
		}
		catch(PatternSyntaxException e)
		{
			throw new PersistenceException("Invalid regular expression pattern");
		}
	}
	
	@Override
	public MatchResult read() throws PersistenceException
	{
		try
		{
			openReader();
			
			if(input != null)
			{
				Matcher matcher = getCompiledPattern().matcher(input);
				
				if(matcher.find())
				{
					return matcher.toMatchResult();
				}
			}
		}
		finally
		{
			closeReader();
		}
		
		throw new PersistenceException("Could not parse data from file using regular expressions.");
	}
	
	/**
	 * The {@link InputStream} object will be closed. A
	 * {@link PersistenceException} will be thrown on failure.
	 */
	@Override
	public void closeReader()
	{
		try
		{
			if(inputStream != null)
			{
				inputStream.close();
			}
		}
		catch(IOException e)
		{
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).warning("Could not close inputstream");
		}
	}
	
}
