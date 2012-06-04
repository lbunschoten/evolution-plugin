package jenkins.plugins.evolution.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import jenkins.plugins.evolution.persistence.PersistenceException;
import jenkins.plugins.evolution.persistence.XPathReader;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

/**
 * This test tests if the files are properly parsed using XPath.
 * 
 * @author leon
 */
public class XPathReaderTest
{
	
	@Test
	public void test() throws PersistenceException, FileNotFoundException
	{
		System.setProperty("file.encoding", "UTF-8");
		
		XPathReader reader = new XPathReader(new FileInputStream(new File("src/test/resources/cobertura.xml")), "/coverage/@line-rate");
		
		assertEquals(0.24761904761904763, reader.read());
	}
	
	@Test(expected = FileNotFoundException.class)
	public void testNonExistingFile() throws PersistenceException, FileNotFoundException
	{
		XPathReader reader = new XPathReader(new FileInputStream(new File("src/test/resources/nonExistingFile.xml")), "/coverage/@line-rate");
		
		reader.read();
	}
	
	@Test(expected = PersistenceException.class)
	public void testInvalidExpression() throws PersistenceException, FileNotFoundException
	{
		XPathReader reader = new XPathReader(new FileInputStream(new File("src/test/resources/cobertura.xml")), "/covefdgfdgrage/@(line-rate");
		
		reader.read();
	}
	
	@Test(expected = PersistenceException.class)
	public void testEmptyInputStream() throws FileNotFoundException, PersistenceException
	{
		XPathReader reader = new XPathReader(null, "/covefdgfdgrage/@(line-rate");
		
		reader.read();
	}
	
	@Test
	public void testUnsupportedEncoding() throws FileNotFoundException, PersistenceException
	{
		System.setProperty("file.encoding", "utf-9");
		
		XPathReader reader = new XPathReader(new FileInputStream(new File("src/test/resources/cobertura.xml")), "/coverage/@line-rate");
		
		assertEquals(0.24761904761904763, reader.read());		
	}
	
	@Test
	@PrepareForTest(XPathReader.class)
	public void testCloseStreamFailure() throws IOException
	{
		FileInputStream mockedFileInputStream = PowerMockito.mock(FileInputStream.class);
		
		XPathReader reader = new XPathReader(mockedFileInputStream, "/coverage/@line-rate");
		
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
