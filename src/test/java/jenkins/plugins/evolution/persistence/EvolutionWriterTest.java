package jenkins.plugins.evolution.persistence;

import static org.junit.Assert.assertEquals;
import hudson.util.XStream2;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import jenkins.plugins.evolution.domain.Job;
import jenkins.plugins.evolution.domain.Result;
import jenkins.plugins.evolution.persistence.EvolutionReader;
import jenkins.plugins.evolution.persistence.EvolutionWriter;
import jenkins.plugins.evolution.persistence.PersistenceException;
import jenkins.plugins.evolution.util.EncodingUtil;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import com.thoughtworks.xstream.XStreamException;

/**
 * This test tests if the evolution file is propery parsed.
 * 
 * @author leon
 */
@RunWith(PowerMockRunner.class)
public class EvolutionWriterTest
{
	
	@Test
	public void testWriter() throws XStreamException, IOException, PersistenceException
	{
		new File("src/test/resources/evolution2.xml").delete();
		
		EvolutionWriter writer = new EvolutionWriter(new File("src/test/resources/evolution2.xml"));
		
		ArrayList<Result> results1 = new ArrayList<Result>();
		results1.add(new Result("CheckStyle",  244));
		results1.add(new Result("FindBugs", 17));
		results1.add(new Result("Cobertura", 0.25222423473));
		
		ArrayList<Result> results2 = new ArrayList<Result>();
		results2.add(new Result("CheckStyle", 642));
		results2.add(new Result("FindBugs", 22));
		results2.add(new Result("Cobertura", 0.12222423473));
		
		ArrayList<Result> results3 = new ArrayList<Result>();
		results3.add(new Result("CheckStyle", 312));
		results3.add(new Result("FindBugs", 4));
		results3.add(new Result("Cobertura", 0.43222423473));
		
		Job job = new Job();
		job.addBuild(1, results1);
		job.addBuild(2, results2);
		job.addBuild(3, results3);
		
		writer.write(job);
		
		EvolutionReader reader = new EvolutionReader(new FileInputStream("src/test/resources/evolution2.xml"));
		Job job2 = reader.read();
		assertEquals(3, job2.getBuilds().size());
		assertEquals(3, job2.getBuilds().get(0).getResults().size());
	}
	
	@Test(expected = PersistenceException.class)
	@PrepareForTest(EvolutionWriter.class)
	public void testOpenStreamFailure() throws Exception
	{
		File file = new File("src/test/resources/evolution3.xml");
		
		PowerMockito.whenNew(FileWriterWithEncoding.class).withArguments(file, new EncodingUtil().getEncoding()).thenThrow(new IOException());
		
		EvolutionWriter writer = new EvolutionWriter(file);
		
		writer.write(new Job());
	}
	
	@Test(expected = PersistenceException.class)
	@PrepareForTest(EvolutionWriter.class)
	public void testCloseStreamFailure() throws Exception
	{
		FileWriterWithEncoding mockedFileWriter = PowerMockito.mock(FileWriterWithEncoding.class);
		File file = new File("src/test/resources/evolution3.xml");
		EvolutionWriter writer = new EvolutionWriter(file);
		
		PowerMockito.whenNew(FileWriterWithEncoding.class).withArguments(file, new EncodingUtil().getEncoding()).thenReturn(mockedFileWriter);
		Mockito.doThrow(new IOException()).when(mockedFileWriter).close();
		
		writer.write(new Job());
	}
	
	@Test(expected = PersistenceException.class)
	@PrepareForTest(EvolutionWriter.class)
	public void testWriteFailure() throws Exception
	{
		File file = new File("src/test/resources/evolution4.xml");
		
		XStream2 xstream = PowerMockito.mock(XStream2.class);
		Mockito.doThrow(new XStreamException("")).when(xstream).alias("job", Job.class);
		
		EvolutionWriter writer = new EvolutionWriter(file);
		Whitebox.setInternalState(writer, "xstream", xstream);
		
		writer.write(new Job());
	}
}
