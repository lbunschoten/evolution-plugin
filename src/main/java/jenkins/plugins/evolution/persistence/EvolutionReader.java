package jenkins.plugins.evolution.persistence;

import hudson.util.XStream2;
import java.io.InputStream;
import jenkins.plugins.evolution.domain.Build;
import jenkins.plugins.evolution.domain.Job;
import jenkins.plugins.evolution.domain.Result;
import com.thoughtworks.xstream.XStreamException;

/**
 * Can be used to read the results in the evolution XML file.
 * 
 * @author leon
 */
public class EvolutionReader extends XStreamReader
{
	
	/**
	 * EvolutionReader constructor.
	 * 
	 * @param inputStream - InputStream used for parsing evolution data.
	 */
	public EvolutionReader(InputStream inputStream)
	{
		super(inputStream);
	}
	
	/**
	 * Reads an evolution XML file and parses its data to a Job object
	 * containing all XML data.
	 * 
	 * @return A Job object containing the data provided in the XML file.
	 * @throws PersistenceException, XStreamException
	 */
	@Override
	public Job read() throws PersistenceException
	{
		Job job = null;
		
		try
		{
			openReader();
			
			job = (Job) getXStream().fromXML(getInputStream());
		}
		catch(XStreamException e)
		{
			throw new PersistenceException("Could not parse file");
		}
		finally
		{
			closeReader();
		}
		
		return job;
	}
	
	@Override
	protected XStream2 prepareXStream(XStream2 xstream)
	{
		xstream.autodetectAnnotations(false);
		
		xstream.alias("job", Job.class);
		xstream.alias("build", Build.class);
		xstream.alias("result", Result.class);
		
		return xstream;
	}
}
