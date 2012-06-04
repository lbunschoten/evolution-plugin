package jenkins.plugins.evolution;

import static org.junit.Assert.assertEquals;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import jenkins.plugins.evolution.config.InvalidConfigException;
import jenkins.plugins.evolution.dataprovider.CPDDataProvider;
import jenkins.plugins.evolution.dataprovider.CheckStyleDataProvider;
import jenkins.plugins.evolution.dataprovider.CoberturaDataProvider;
import jenkins.plugins.evolution.dataprovider.FindBugsDataProvider;
import jenkins.plugins.evolution.dataprovider.FxCopDataProvider;
import jenkins.plugins.evolution.dataprovider.NCoverDataProvider;
import jenkins.plugins.evolution.dataprovider.PMDDataProvider;
import jenkins.plugins.evolution.dataprovider.SimianDataProvider;
import jenkins.plugins.evolution.dataprovider.StyleCopDataProvider;
import jenkins.plugins.evolution.persistence.PersistenceException;
import org.junit.Test;

public class DataProviderTest
{
	@Test
	public void testValidCheckStyleFile() throws FileNotFoundException, InvalidConfigException, PersistenceException
	{
		CheckStyleDataProvider dataProvider = new CheckStyleDataProvider(new FileInputStream("src/test/resources/checkstyle-result.xml"));
		
		System.out.println("Start testing " + dataProvider.getId() + " : " + dataProvider.getName());
		
		assertEquals(50, dataProvider.getResult().getData(), 0);
	}
	
	@Test(expected = PersistenceException.class)
	public void testInvalidCheckStyleFile() throws FileNotFoundException, PersistenceException, InvalidConfigException
	{
		CheckStyleDataProvider dataProvider = new CheckStyleDataProvider(new FileInputStream("src/test/resources/invalid.xml"));
		dataProvider.getResult().getData();
	}
	
	@Test(expected = FileNotFoundException.class)
	public void testNonExistingCheckStyleFile() throws FileNotFoundException
	{
		new CheckStyleDataProvider(new FileInputStream("nonExistingFile.xml"));
	}
	
	@Test
	public void testValidFindBugsFile() throws FileNotFoundException, InvalidConfigException, PersistenceException
	{
		FindBugsDataProvider dataProvider = new FindBugsDataProvider(new FileInputStream("src/test/resources/findbugsXML.xml"));
		
		System.out.println("Start testing " + dataProvider.getId() + " : " + dataProvider.getName());
		
		assertEquals(2, dataProvider.getResult().getData(), 0);
	}
	
	@Test(expected = PersistenceException.class)
	public void testInvalidFindBugsFile() throws FileNotFoundException, PersistenceException, InvalidConfigException
	{
		FindBugsDataProvider dataProvider = new FindBugsDataProvider(new FileInputStream("src/test/resources/invalid.xml"));
		dataProvider.getResult().getData();
	}
	
	@Test(expected = FileNotFoundException.class)
	public void testNonExistingFindBugsFile() throws FileNotFoundException
	{
		new FindBugsDataProvider(new FileInputStream("nonExistingFile.xml"));
	}
	
	@Test
	public void testValidCoberturaFile() throws FileNotFoundException, InvalidConfigException, PersistenceException
	{
		CoberturaDataProvider dataProvider = new CoberturaDataProvider(new FileInputStream("src/test/resources/cobertura.xml"));
		
		System.out.println("Start testing " + dataProvider.getId() + " : " + dataProvider.getName());
		
		assertEquals(24.761904761904763, dataProvider.getResult().getData(), 0);
	}
	
	@Test(expected = PersistenceException.class)
	public void testInvalidCoberturaFile() throws FileNotFoundException, PersistenceException, InvalidConfigException
	{
		CoberturaDataProvider dataProvider = new CoberturaDataProvider(new FileInputStream("src/test/resources/invalid.xml"));
		dataProvider.getResult().getData();
	}
	
	@Test(expected = FileNotFoundException.class)
	public void testNonExistingCoberturaFile() throws FileNotFoundException
	{
		new CoberturaDataProvider(new FileInputStream("nonExistingFile.xml"));
	}
	
	@Test
	public void testValidPMDFile() throws FileNotFoundException, InvalidConfigException, PersistenceException
	{
		PMDDataProvider dataProvider = new PMDDataProvider(new FileInputStream("src/test/resources/pmd.xml"));
		
		System.out.println("Start testing " + dataProvider.getId() + " : " + dataProvider.getName());
		
		assertEquals(1, dataProvider.getResult().getData(), 0);
	}
	
	@Test(expected = PersistenceException.class)
	public void testInvalidPMDFile() throws FileNotFoundException, PersistenceException, InvalidConfigException
	{
		PMDDataProvider dataProvider = new PMDDataProvider(new FileInputStream("src/test/resources/invalid.xml"));
		dataProvider.getResult().getData();
	}
	
	@Test(expected = FileNotFoundException.class)
	public void testNonExistingPMDFile() throws FileNotFoundException
	{
		new PMDDataProvider(new FileInputStream("nonExistingFile.xml"));
	}
	
	@Test
	public void testValidCPDFile() throws FileNotFoundException, InvalidConfigException, PersistenceException
	{
		CPDDataProvider dataProvider = new CPDDataProvider(new FileInputStream("src/test/resources/cpd.xml"));
		
		System.out.println("Start testing " + dataProvider.getId() + " : " + dataProvider.getName());
		
		assertEquals(4, dataProvider.getResult().getData(), 0);
	}
	
	@Test(expected = PersistenceException.class)
	public void testInvalidCPDFile() throws FileNotFoundException, PersistenceException, InvalidConfigException
	{
		CPDDataProvider dataProvider = new CPDDataProvider(new FileInputStream("src/test/resources/invalid.xml"));
		dataProvider.getResult().getData();
	}
	
	@Test(expected = FileNotFoundException.class)
	public void testNonExistingCPDFile() throws FileNotFoundException
	{
		new CPDDataProvider(new FileInputStream("nonExistingFile.xml"));
	}
	
	@Test
	public void testValidNCoverFile() throws FileNotFoundException, InvalidConfigException, PersistenceException
	{
		NCoverDataProvider dataProvider = new NCoverDataProvider(new FileInputStream("src/test/resources/ncover.html"));
		
		System.out.println("Start testing " + dataProvider.getId() + " : " + dataProvider.getName());
		
		assertEquals(42.17, dataProvider.getResult().getData(), 0);
	}
	
	@Test(expected = PersistenceException.class)
	public void testInvalidNCoverFile() throws FileNotFoundException, PersistenceException, InvalidConfigException
	{
		NCoverDataProvider dataProvider = new NCoverDataProvider(new FileInputStream("src/test/resources/invalid.xml"));
		dataProvider.getResult().getData();
	}
	
	@Test(expected = FileNotFoundException.class)
	public void testNonExistingNCoverFile() throws FileNotFoundException
	{
		new NCoverDataProvider(new FileInputStream("nonExistingFile.xml"));
	}
	
	@Test
	public void testValidSimianFile() throws FileNotFoundException, InvalidConfigException, PersistenceException
	{
		SimianDataProvider dataProvider = new SimianDataProvider(new FileInputStream("src/test/resources/simian-result.xml"));
		
		System.out.println("Start testing " + dataProvider.getId() + " : " + dataProvider.getName());
		
		assertEquals(13, dataProvider.getResult().getData(), 0);
	}
	
	@Test(expected = PersistenceException.class)
	public void testInvalidSimianFile() throws FileNotFoundException, PersistenceException, InvalidConfigException
	{
		SimianDataProvider dataProvider = new SimianDataProvider(new FileInputStream("src/test/resources/invalid.xml"));
		dataProvider.getResult().getData();
	}
	
	@Test(expected = FileNotFoundException.class)
	public void testNonExistingSimianFile() throws FileNotFoundException
	{
		new SimianDataProvider(new FileInputStream("nonExistingFile.xml"));
	}
	
	@Test
	public void testValidFxCopFile() throws FileNotFoundException, InvalidConfigException, PersistenceException
	{
		FxCopDataProvider dataProvider = new FxCopDataProvider(new FileInputStream("src/test/resources/fxcop.xml"));
		
		System.out.println("Start testing " + dataProvider.getId() + " : " + dataProvider.getName());
		
		assertEquals(13, dataProvider.getResult().getData(), 0);
	}
	
	@Test(expected = PersistenceException.class)
	public void testInvalidFxCopFile() throws FileNotFoundException, PersistenceException, InvalidConfigException
	{
		FxCopDataProvider dataProvider = new FxCopDataProvider(new FileInputStream("src/test/resources/invalid.xml"));
		dataProvider.getResult().getData();
	}
	
	@Test(expected = FileNotFoundException.class)
	public void testNonExistingFxCopFile() throws FileNotFoundException
	{
		new FxCopDataProvider(new FileInputStream("nonExistingFile.xml"));
	}
	
	@Test
	public void testValidStyleCopFile() throws FileNotFoundException, InvalidConfigException, PersistenceException
	{
		StyleCopDataProvider dataProvider = new StyleCopDataProvider(new FileInputStream("src/test/resources/stylecop.xml"));
		
		System.out.println("Start testing " + dataProvider.getId() + " : " + dataProvider.getName());
		
		assertEquals(2, dataProvider.getResult().getData(), 0);
	}
	
	@Test(expected = PersistenceException.class)
	public void testInvalidStyleCopFile() throws FileNotFoundException, PersistenceException, InvalidConfigException
	{
		StyleCopDataProvider dataProvider = new StyleCopDataProvider(new FileInputStream("src/test/resources/invalid.xml"));
		dataProvider.getResult().getData();
	}
	
	@Test(expected = FileNotFoundException.class)
	public void testNonExistingStyleCopFile() throws FileNotFoundException
	{
		new StyleCopDataProvider(new FileInputStream("nonExistingFile.xml"));
	}
}
