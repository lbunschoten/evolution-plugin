package jenkins.plugins.evolution;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import java.util.Map.Entry;
import jenkins.plugins.evolution.config.DataProviderConfig;
import jenkins.plugins.evolution.config.EvolutionConfig;
import jenkins.plugins.evolution.util.ItemNotFoundException;
import org.junit.Test;

public class EvolutionConfigTest
{
	
	public void setDataProviderConfig(EvolutionConfig config, String name)
	{
		config.getDataProviderConfigs().get(name).setMin("0");
		config.getDataProviderConfigs().get(name).setMax("5");
		config.getDataProviderConfigs().get(name).setPath("**/" + name);
		config.getDataProviderConfigs().get(name).setWeight("3");
	}
	
	@Test
	public void testGetConfiguredDataProviders() throws ItemNotFoundException
	{
		EvolutionConfig config = new EvolutionConfig();
		
		config.getDataProviderConfig("CHECKSTYLE").setPath("test");
		config.getDataProviderConfig("CHECKSTYLE").setMin("test");
		config.getDataProviderConfig("CHECKSTYLE").setMax("test");
		config.getDataProviderConfig("CHECKSTYLE").setWeight("test");
		
		config.getDataProviderConfig("FINDBUGS").setPath("");
		config.getDataProviderConfig("FINDBUGS").setMin(null);
		config.getDataProviderConfig("FINDBUGS").setMax("test");
		config.getDataProviderConfig("FINDBUGS").setWeight("test");
		
		assertEquals(1, config.getConfiguredDataProviders().size());
	}
	
	@Test
	public void testClone()
	{
		EvolutionConfig config = new EvolutionConfig();
		
		config.getDataProviderConfig("CHECKSTYLE").setPath("test");
		config.getDataProviderConfig("CHECKSTYLE").setMin("test");
		config.getDataProviderConfig("CHECKSTYLE").setMax("test");
		config.getDataProviderConfig("CHECKSTYLE").setWeight("test");
		
		EvolutionConfig clonedConfig = config.clone();
		
		assertNotNull(clonedConfig);
		assertNotSame(config, clonedConfig);
		
		assertEquals(1, clonedConfig.getConfiguredDataProviders().size());
		
		DataProviderConfig clonedDataProviderConfig;
		
		for(Entry<String, DataProviderConfig> dataProviderConfig : clonedConfig.getDataProviderConfigs().entrySet())
		{
			clonedDataProviderConfig = dataProviderConfig.getValue().clone();
			
			assertNotNull(clonedDataProviderConfig);
			assertNotSame(dataProviderConfig.getValue(), clonedDataProviderConfig);
		}
	}
}
