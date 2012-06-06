package jenkins.plugins.evolution.persistence;

import hudson.util.XStream2;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

/**
 * This reader uses the XStream library to fetch data from a file. It reads an
 * entire XML file and stores the data in Java objects.
 * 
 * @author leon
 */
public abstract class XStreamReader implements Reader<XStreamReader>
{
	private InputStream inputStream;
	
	private XStream2 xstream;
	
	public XStreamReader(InputStream inputStream)
	{
		this.inputStream = inputStream;
	}
	
	@Override
	public void openReader() throws PersistenceException
	{
		xstream = new XStream2();
		
		prepareXStream(xstream);
	}
	
	public XStream2 getXStream()
	{
		return xstream;
	}
	
	public InputStream getInputStream()
	{
		return inputStream;
	}
	
	/**
	 * This method can overridden to prepare the xstream instance. For instance
	 * for adding aliases and impicit collections.
	 */
	protected abstract XStream2 prepareXStream(XStream2 xstream);
	
	@Override
	public void closeReader()
	{
		try
		{
			inputStream.close();
		}
		catch(IOException e)
		{
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).warning("Could not close inputstream");
		}
	}
}
