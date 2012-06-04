package jenkins.plugins.evolution.util;

import java.nio.charset.Charset;

/**
 * This class can be used by the Reader classes, to make sure a proper Charset
 * is being used.
 * 
 * @author leon
 */
public class EncodingUtil
{
	
	public Charset getEncoding()
	{
		if(Charset.isSupported(System.getProperty("file.encoding")))
		{
			return Charset.forName(System.getProperty("file.encoding"));
		}
		return Charset.forName("UTF-8");
	}
	
	public String getEncodingAsString()
	{
		return getEncoding().name();
	}
	
}
