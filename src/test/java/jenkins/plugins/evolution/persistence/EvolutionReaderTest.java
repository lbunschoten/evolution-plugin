package jenkins.plugins.evolution.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import jenkins.plugins.evolution.domain.Job;
import jenkins.plugins.evolution.persistence.EvolutionReader;
import jenkins.plugins.evolution.persistence.PersistenceException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

/**
 * This test tests if the evolution file is propery parsed.
 * 
 * @author leon
 */
public class EvolutionReaderTest
{
	
	EvolutionReader reader;
	
	Job job;
	
	@Before
	public void before() throws FileNotFoundException
	{
		reader = new EvolutionReader(new FileInputStream("src/test/resources/evolution.xml"));
		
		try
		{
			job = reader.read();
		}
		catch(PersistenceException e)
		{
			e.printStackTrace();
		}
	}
	
	@Test
	public void testJob()
	{
		assertEquals(1, job.getBuilds().size());
	}
	
	@Test
	public void testBuild()
	{
		assertEquals(9, job.getBuilds().get(0).getId());
		assertEquals(1, job.getBuilds().get(0).getResults().size());
	}
	
	@Test
	public void testResult()
	{
		assertEquals("cobertura", job.getBuilds().get(0).getResults().get(0).getDataProviderId());
		assertEquals(2.4761904761904763, job.getBuilds().get(0).getResults().get(0).getData(), 0);
	}
	
	@Test(expected = FileNotFoundException.class)
	public void testNonExistingFile() throws PersistenceException, FileNotFoundException
	{
		new EvolutionReader(new FileInputStream("nonExistingFile.xml")).read();
	}
	
	@Test(expected = PersistenceException.class)
	public void testNonEvolutionFile() throws PersistenceException, FileNotFoundException
	{
		new EvolutionReader(new FileInputStream("src/test/resources/cobertura.xml")).read();
	}
	
	@Test(expected = PersistenceException.class)
	public void testEmptyFile() throws PersistenceException, FileNotFoundException
	{
		reader = new EvolutionReader(new FileInputStream("src/test/resources/empty.xml"));
		
		reader.read();
	}
	
	
	@Test
	@PrepareForTest(EvolutionReader.class)
	public void testCloseStreamFailure() throws IOException
	{
		FileInputStream mockedFileInputStream = PowerMockito.mock(FileInputStream.class);
		
		EvolutionReader reader = new EvolutionReader(mockedFileInputStream);
		
		Mockito.doThrow(new IOException("Could not close stream")).when(mockedFileInputStream).close();
		
		try
		{
			reader.read();
		}
		catch(PersistenceException e)
		{
			return;
		}
		
		fail("Should have reached catch clause");
	}
}
