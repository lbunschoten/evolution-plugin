package jenkins.plugins.evolution;

import static org.junit.Assert.assertEquals;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import jenkins.plugins.evolution.config.InvalidConfigException;
import jenkins.plugins.evolution.dataprovider.CPDDataProvider;
import jenkins.plugins.evolution.dataprovider.CheckStyleDataProvider;
import jenkins.plugins.evolution.dataprovider.CoberturaDataProvider;
import jenkins.plugins.evolution.dataprovider.DataProvider;
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
	public ArrayList<DataProvider> getValidDataProviders() throws FileNotFoundException
	{
		ArrayList<DataProvider> dataProviders = new ArrayList<DataProvider>();
		
		dataProviders.add(new CheckStyleDataProvider(new FileInputStream("src/test/resources/example-checkstyle.xml")));
		dataProviders.add(new FindBugsDataProvider(new FileInputStream("src/test/resources/example-findbugs.xml")));
		dataProviders.add(new CoberturaDataProvider(new FileInputStream("src/test/resources/example-cobertura.xml")));
		dataProviders.add(new CPDDataProvider(new FileInputStream("src/test/resources/example-cpd.xml")));
		dataProviders.add(new PMDDataProvider(new FileInputStream("src/test/resources/example-pmd.xml")));
		dataProviders.add(new FxCopDataProvider(new FileInputStream("src/test/resources/example-fxcop.xml")));
		dataProviders.add(new NCoverDataProvider(new FileInputStream("src/test/resources/example-ncover.html")));
		dataProviders.add(new StyleCopDataProvider(new FileInputStream("src/test/resources/example-stylecop.xml")));
		dataProviders.add(new SimianDataProvider(new FileInputStream("src/test/resources/example-simian.xml")));
		
		return dataProviders;
	}
	
	public ArrayList<Double> getExpectedValues() throws FileNotFoundException
	{
		ArrayList<Double> expectedValues = new ArrayList<Double>();
		
		expectedValues.add(50.0);
		expectedValues.add(2.0);
		expectedValues.add(24.761904761904763);
		expectedValues.add(4.0);
		expectedValues.add(1.0);
		expectedValues.add(13.0);
		expectedValues.add(42.17);
		expectedValues.add(2.0);
		expectedValues.add(13.0);
		
		return expectedValues;
	}
	
	@Test
	public void testDataProviders() throws FileNotFoundException, PersistenceException
	{
		ArrayList<DataProvider> dataProviders = getValidDataProviders();
		ArrayList<Double> expectedValues = getExpectedValues(); 
		
		for(int i = 0; i < dataProviders.size(); i++)
		{
			System.out.println("[" + dataProviders.get(i).getId() + "] Test reading " + dataProviders.get(i).getName() + " results");
			
			assertEquals(expectedValues.get(i), dataProviders.get(i).getResult(null).getData(), 0);
		}
	}
	
	@Test(expected = PersistenceException.class)
	public void testInvalidCheckStyleFile() throws FileNotFoundException, PersistenceException, InvalidConfigException
	{
		CheckStyleDataProvider dataProvider = new CheckStyleDataProvider(new FileInputStream("src/test/resources/invalid.xml"));
		dataProvider.getResult(null).getData();
	}
	
	@Test(expected = FileNotFoundException.class)
	public void testNonExistingCheckStyleFile() throws FileNotFoundException
	{
		new CheckStyleDataProvider(new FileInputStream("nonExistingFile.xml"));
	}
	
	@Test(expected = PersistenceException.class)
	public void testInvalidFindBugsFile() throws FileNotFoundException, PersistenceException, InvalidConfigException
	{
		FindBugsDataProvider dataProvider = new FindBugsDataProvider(new FileInputStream("src/test/resources/invalid.xml"));
		dataProvider.getResult(null).getData();
	}
	
	@Test(expected = FileNotFoundException.class)
	public void testNonExistingFindBugsFile() throws FileNotFoundException
	{
		new FindBugsDataProvider(new FileInputStream("nonExistingFile.xml"));
	}
	
	@Test(expected = PersistenceException.class)
	public void testInvalidCoberturaFile() throws FileNotFoundException, PersistenceException, InvalidConfigException
	{
		CoberturaDataProvider dataProvider = new CoberturaDataProvider(new FileInputStream("src/test/resources/invalid.xml"));
		dataProvider.getResult(null).getData();
	}
	
	@Test(expected = FileNotFoundException.class)
	public void testNonExistingCoberturaFile() throws FileNotFoundException
	{
		new CoberturaDataProvider(new FileInputStream("nonExistingFile.xml"));
	}
	
	@Test(expected = PersistenceException.class)
	public void testInvalidPMDFile() throws FileNotFoundException, PersistenceException, InvalidConfigException
	{
		PMDDataProvider dataProvider = new PMDDataProvider(new FileInputStream("src/test/resources/invalid.xml"));
		dataProvider.getResult(null).getData();
	}
	
	@Test(expected = FileNotFoundException.class)
	public void testNonExistingPMDFile() throws FileNotFoundException
	{
		new PMDDataProvider(new FileInputStream("nonExistingFile.xml"));
	}
	
	@Test(expected = PersistenceException.class)
	public void testInvalidCPDFile() throws FileNotFoundException, PersistenceException, InvalidConfigException
	{
		CPDDataProvider dataProvider = new CPDDataProvider(new FileInputStream("src/test/resources/invalid.xml"));
		dataProvider.getResult(null).getData();
	}
	
	@Test(expected = FileNotFoundException.class)
	public void testNonExistingCPDFile() throws FileNotFoundException
	{
		new CPDDataProvider(new FileInputStream("nonExistingFile.xml"));
	}
	
	
	@Test(expected = PersistenceException.class)
	public void testInvalidNCoverFile() throws FileNotFoundException, PersistenceException, InvalidConfigException
	{
		NCoverDataProvider dataProvider = new NCoverDataProvider(new FileInputStream("src/test/resources/invalid.xml"));
		dataProvider.getResult(null).getData();
	}
	
	@Test(expected = FileNotFoundException.class)
	public void testNonExistingNCoverFile() throws FileNotFoundException
	{
		new NCoverDataProvider(new FileInputStream("nonExistingFile.xml"));
	}
	
	@Test(expected = PersistenceException.class)
	public void testInvalidSimianFile() throws FileNotFoundException, PersistenceException, InvalidConfigException
	{
		SimianDataProvider dataProvider = new SimianDataProvider(new FileInputStream("src/test/resources/invalid.xml"));
		dataProvider.getResult(null).getData();
	}
	
	@Test(expected = FileNotFoundException.class)
	public void testNonExistingSimianFile() throws FileNotFoundException
	{
		new SimianDataProvider(new FileInputStream("nonExistingFile.xml"));
	}
	
	@Test(expected = PersistenceException.class)
	public void testInvalidFxCopFile() throws FileNotFoundException, PersistenceException, InvalidConfigException
	{
		FxCopDataProvider dataProvider = new FxCopDataProvider(new FileInputStream("src/test/resources/invalid.xml"));
		dataProvider.getResult(null).getData();
	}
	
	@Test(expected = FileNotFoundException.class)
	public void testNonExistingFxCopFile() throws FileNotFoundException
	{
		new FxCopDataProvider(new FileInputStream("nonExistingFile.xml"));
	}
	
	@Test(expected = PersistenceException.class)
	public void testInvalidStyleCopFile() throws FileNotFoundException, PersistenceException, InvalidConfigException
	{
		StyleCopDataProvider dataProvider = new StyleCopDataProvider(new FileInputStream("src/test/resources/invalid.xml"));
		dataProvider.getResult(null).getData();
	}
	
	@Test(expected = FileNotFoundException.class)
	public void testNonExistingStyleCopFile() throws FileNotFoundException
	{
		new StyleCopDataProvider(new FileInputStream("nonExistingFile.xml"));
	}
}
