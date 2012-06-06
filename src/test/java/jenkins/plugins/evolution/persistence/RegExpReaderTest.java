package jenkins.plugins.evolution.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import jenkins.plugins.evolution.persistence.PersistenceException;
import jenkins.plugins.evolution.persistence.RegExpReader;
import jenkins.plugins.evolution.util.EncodingUtil;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Tests for the RegExpReader
 * 
 * @author leon
 */
@RunWith(PowerMockRunner.class)
public class RegExpReaderTest
{
	
	@Test
	public void test() throws FileNotFoundException, PersistenceException
	{
		RegExpReader reader = new RegExpReader(new FileInputStream("src/test/resources/example-ncover.html"), "Symbol Coverage: <span class=\"[\\w\\s]+\">([\\d,]+)%<\\/span>");
		
		assertEquals("42,17", reader.read().group(1));
	}
	
	@Test(expected = FileNotFoundException.class)
	public void testNonExistingFile() throws PersistenceException, FileNotFoundException
	{
		RegExpReader reader = new RegExpReader(new FileInputStream("src/test/resources/nonExistingFile.xml"), "Symbol Coverage: <span class=\"[\\w\\s]+\">([\\d,]+)%<\\/span>");
		
		reader.read();
	}
	
	@Test(expected = PersistenceException.class)
	public void testNonExistingExpression() throws PersistenceException, FileNotFoundException
	{
		RegExpReader reader = new RegExpReader(new FileInputStream("src/test/resources/example-ncover.html"), "nonExistingExpression");
		
		reader.read();
	}
	
	@Test(expected = PersistenceException.class)
	public void testInvalidExpression() throws PersistenceException, FileNotFoundException
	{
		RegExpReader reader = new RegExpReader(new FileInputStream("src/test/resources/example-ncover.html"), "[]]][[][())&.*$");
		
		reader.read();
	}
	
	@Test(expected = PersistenceException.class)
	public void testEmptyInputStream() throws FileNotFoundException, PersistenceException
	{
		RegExpReader reader = new RegExpReader(null, "Symbol Coverage: <span class=\"[\\w\\s]+\">([\\d,]+)%<\\/span>");
		
		reader.read();
	}
	
	@Test(expected = PersistenceException.class)
	@PrepareForTest(IOUtils.class)
	public void testIOExceptionOnRead() throws IOException, PersistenceException
	{
		FileInputStream stream = new FileInputStream("src/test/resources/example-ncover.html");
		
		RegExpReader reader = new RegExpReader(stream, "Symbol Coverage: <span class=\"[\\w\\s]+\">([\\d,]+)%<\\/span>");
		
		PowerMockito.mockStatic(IOUtils.class);
		
		Mockito.when(IOUtils.toString(stream, new EncodingUtil().getEncodingAsString())).thenThrow(new IOException());
		
		reader.read();
	}
	
	@Test
	@PrepareForTest(RegExpReader.class)
	public void testCloseStreamFailure() throws IOException
	{
		FileInputStream mockedFileInputStream = PowerMockito.mock(FileInputStream.class);
		
		RegExpReader reader = new RegExpReader(mockedFileInputStream, "Symbol Coverage: <span class=\"[\\w\\s]+\">([\\d,]+)%<\\/span>");
		
		Mockito.doThrow(new IOException("Could not close stream")).when(mockedFileInputStream).close();
		
		try
		{
			reader.read();
		}
		catch(PersistenceException e)
		{
			assertEquals("Could not parse data from file using regular expressions.", e.getMessage());
			
			return;
		}
		
		fail("Should have reached catch clause");
	}
}
