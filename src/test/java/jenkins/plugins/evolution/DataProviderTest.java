package jenkins.plugins.evolution;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map.Entry;
import jenkins.plugins.evolution.config.EvolutionConfig;
import jenkins.plugins.evolution.dataprovider.DataProvider;
import jenkins.plugins.evolution.persistence.PersistenceException;
import org.junit.Test;

public class DataProviderTest
{
	public HashMap<String, DataProvider> getValidDataProviders() throws FileNotFoundException
	{		
		return new EvolutionConfig().getDataProviders();
	}
	
	public HashMap<String, Double> getExpectedValues() throws FileNotFoundException
	{
		HashMap<String, Double> expectedValues = new HashMap<String, Double>();
		
		expectedValues.put("CHECKSTYLE", 50.0);
		expectedValues.put("FINDBUGS", 2.0);
		expectedValues.put("COBERTURA", 24.761904761904763);
		expectedValues.put("CPD", 4.0);
		expectedValues.put("PMD", 1.0);
		expectedValues.put("FXCOP", 13.0);
		expectedValues.put("NCOVER", 42.17);
		expectedValues.put("STYLECOP", 2.0);
		expectedValues.put("SIMIAN", 13.0);
		
		return expectedValues;
	}
	
	@Test
	public void testDataProviders() throws FileNotFoundException, PersistenceException
	{
		HashMap<String, DataProvider> dataProviders = getValidDataProviders();
		HashMap<String, Double> expectedValues = getExpectedValues(); 
		
		for(Entry<String, DataProvider> dataProviderEntry : getValidDataProviders().entrySet())
		{
			String key = dataProviderEntry.getKey();
			DataProvider dataProvider = dataProviderEntry.getValue();
			
			System.out.println("[" + dataProviders.get(key) + "] Test reading " + EvolutionConfig.DataProviderDescriptor.valueOf(key).getName() + " results");

			if(key.equals("NCOVER"))
			{
				assertEquals(expectedValues.get(key), dataProvider.getResult(new FileInputStream(new File("src/test/resources/example-" + key.toLowerCase() + ".html"))).getData(), 0);
			}
			else
			{
				assertEquals(expectedValues.get(key), dataProvider.getResult(new FileInputStream(new File("src/test/resources/example-" + key.toLowerCase() + ".xml"))).getData(), 0);
			}
		}
	}
	
	@Test
	public void testNonExistingFileInputDataProviders() throws FileNotFoundException, PersistenceException
	{
		HashMap<String, DataProvider> dataProviders = getValidDataProviders();
		
		for(Entry<String, DataProvider> dataProviderEntry : getValidDataProviders().entrySet())
		{
			String key = dataProviderEntry.getKey();
			DataProvider dataProvider = dataProviderEntry.getValue();
			
			System.out.println("[" + dataProviders.get(key) + "] Test reading " + EvolutionConfig.DataProviderDescriptor.valueOf(key).getName() + " results");

			try
			{
				dataProvider.getResult(new FileInputStream(new File("nonExistingFile.xml")));
				
				fail("Should have reached catch clause");
			}
			catch(FileNotFoundException e)
			{
				System.out.println(dataProvider.getId() + " file not found");
			}
		}
	}
	
	@Test
	public void testInvalidFileInputDataProviders() throws FileNotFoundException, PersistenceException
	{
		HashMap<String, DataProvider> dataProviders = getValidDataProviders();
		
		for(Entry<String, DataProvider> dataProviderEntry : getValidDataProviders().entrySet())
		{
			String key = dataProviderEntry.getKey();
			DataProvider dataProvider = dataProviderEntry.getValue();
			
			System.out.println("[" + dataProviders.get(key) + "] Test reading " + EvolutionConfig.DataProviderDescriptor.valueOf(key).getName() + " results");

			try
			{
				System.out.println(dataProvider.getResult(new FileInputStream(new File("src/test/resources/invalid.xml"))).getData());
				
				fail("Should have reached catch clause");
			}
			catch(PersistenceException e)
			{
				System.out.println(dataProvider.getId() + " invalid input file");
			}
		}
	}
}
