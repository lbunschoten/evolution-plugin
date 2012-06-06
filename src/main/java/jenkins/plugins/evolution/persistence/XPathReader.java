package jenkins.plugins.evolution.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import jenkins.plugins.evolution.util.EncodingUtil;
import org.xml.sax.InputSource;

/**
 * This reader uses xpath to fetch data from a file, using the Xalan-java
 * library.
 * 
 * @author leon
 */
public class XPathReader implements Reader<XPathReader>
{
	private XPathFactory factory;
	
	private InputSource inputSource;
	
	private InputStream inputStream;
	
	private String expression;
	
	public XPathReader(InputStream inputStream, String expression)
	{
		this.inputStream = inputStream;
		this.expression = expression;
	}
	
	@Override
	public void openReader() throws PersistenceException
	{
		if(inputStream == null)
		{
			throw new PersistenceException("Could not read from file; Does the file exist?");
		}
		
		factory = XPathFactory.newInstance();
		
		inputSource = new InputSource(inputStream);
		inputSource.setEncoding(new EncodingUtil().getEncodingAsString());
	}
	
	@Override
	public Number read() throws PersistenceException
	{
		Number result = null;
		
		openReader();
				
		try
		{
			result = (Number) factory.newXPath().evaluate(expression, inputSource, XPathConstants.NUMBER);
		}
		catch(XPathExpressionException e)
		{
			e.printStackTrace();
			throw new PersistenceException("Invalid XPath expression: " + expression);
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
		factory = null;
		
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
