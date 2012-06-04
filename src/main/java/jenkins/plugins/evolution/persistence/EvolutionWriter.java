package jenkins.plugins.evolution.persistence;

import hudson.util.XStream2;
import java.io.File;
import java.io.IOException;
import jenkins.plugins.evolution.domain.Build;
import jenkins.plugins.evolution.domain.Job;
import jenkins.plugins.evolution.domain.Result;
import jenkins.plugins.evolution.util.EncodingUtil;
import org.apache.commons.io.output.FileWriterWithEncoding;
import com.thoughtworks.xstream.XStreamException;

/**
 * Can be used to write the results of a build to the evolution XML file.
 * 
 * @author leon
 */
public class EvolutionWriter
{
	private File file;
	
	private FileWriterWithEncoding writer;
	
	private XStream2 xstream = new XStream2();
	
	public EvolutionWriter(File file)
	{
		this.file = file;
	}
	
	/**
	 * Returns an existing file reader if avaiable, otherwise opens a new
	 * reader. Throws a file not found exception if the given path could not be
	 * found.
	 * 
	 * @param path
	 * @return A File Reader
	 * @throws PersistenceException
	 * @throws IOException
	 */
	private void openWriter(File file) throws PersistenceException
	{
		try
		{
			writer = new FileWriterWithEncoding(file, new EncodingUtil().getEncoding());
		}
		catch(IOException e)
		{
			throw new PersistenceException("Could not open file writer.");
		}
	}
	
	/**
	 * Reads an evolution XML file and parses its data to a Job object
	 * containing all XML data.
	 * 
	 * @throws PersistenceException
	 */
	public void write(Job job) throws PersistenceException
	{
		try
		{
			openWriter(file);
			
			prepareXStream(xstream);
			
			xstream.toXML(job, writer);
		}
		catch(XStreamException e)
		{
			throw new PersistenceException("Could not write file.");
		}
		finally
		{
			closeWriter();
		}
	}
	
	protected XStream2 prepareXStream(XStream2 xstream)
	{
		xstream.autodetectAnnotations(false);
		
		xstream.alias("job", Job.class);
		xstream.alias("build", Build.class);
		xstream.alias("result", Result.class);
		
		return xstream;
	}
	
	/**
	 * Closes the file writer if its open.
	 * 
	 * @throws PersistenceException
	 */
	private void closeWriter() throws PersistenceException
	{
		if(writer != null)
		{
			try
			{
				writer.close();
			}
			catch(IOException e)
			{
				throw new PersistenceException("Could not close file writer.");
			}
		}
	}
}
