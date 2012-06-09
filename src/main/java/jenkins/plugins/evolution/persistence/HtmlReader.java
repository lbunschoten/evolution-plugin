package jenkins.plugins.evolution.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

/**
 * This reader uses xpath to fetch data from a file, using the Xalan-java
 * library.
 * 
 * @author leon
 */
public class HtmlReader implements Reader<HtmlReader>
{
	private HtmlCleaner htmlCleaner;
	
	private InputStream inputStream;
	
	public HtmlReader(InputStream inputStream)
	{
		this.inputStream = inputStream;
	}
	
	@Override
	public void openReader() throws PersistenceException
	{
		if(inputStream == null)
		{
			throw new PersistenceException("Could not read from file; Does the file exist?");
		}
		
		CleanerProperties props = new CleanerProperties();
		props.setOmitComments(true);
		
		htmlCleaner = new HtmlCleaner(props);
	}
	
	@Override
	public TagNode read() throws PersistenceException
	{
		TagNode result = null;
		
		openReader();
				
		try
		{
			result = htmlCleaner.clean(inputStream);
		}
		catch(IOException e)
		{
			throw new PersistenceException("Could not read HTML file");
		}
		finally
		{
			closeReader();
		}
		
		closeReader();
		
		return result;
	}
	
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
