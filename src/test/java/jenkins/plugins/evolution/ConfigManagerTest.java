package jenkins.plugins.evolution;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import hudson.FilePath;
import hudson.plugins.analysis.util.PluginLogger;
import java.io.IOException;
import java.util.Map.Entry;
import jenkins.model.Jenkins;
import jenkins.plugins.evolution.config.ConfigManager;
import jenkins.plugins.evolution.config.DataProviderConfig;
import jenkins.plugins.evolution.config.EvolutionConfig;
import jenkins.plugins.evolution.util.ItemNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Jenkins.class, FilePath.class })
public class ConfigManagerTest
{
	Jenkins jenkins;
	
	PluginLogger logger;
	
	FilePath workspace;
	
	ConfigManager manager;
	
	@Before
	public void before() throws Exception
	{
		jenkins = PowerMockito.mock(Jenkins.class);
		manager = PowerMockito.mock(ConfigManager.class);
		workspace = PowerMockito.mock(FilePath.class);
	}
	
	public EvolutionConfig getEvolutionConfig()
	{
		EvolutionConfig config = new EvolutionConfig();
		
		setDataProviderConfig(config, "checkstyle");
		setDataProviderConfig(config, "findbugs");
		setDataProviderConfig(config, "ncover");
		setDataProviderConfig(config, "cobertura");
		setDataProviderConfig(config, "cpd");
		setDataProviderConfig(config, "pmd");
		setDataProviderConfig(config, "fxcop");
		setDataProviderConfig(config, "simian");
		setDataProviderConfig(config, "stylecop");
		
		return config;
	}
	
	public void setDataProviderConfig(EvolutionConfig config, String name)
	{
		config.getDataProviders().get(name).setMin("0");
		config.getDataProviders().get(name).setMax("5");
		config.getDataProviders().get(name).setPath("**/" + name);
		config.getDataProviders().get(name).setWeight("3");
	}
	
	@Test
	public void testGetConfiguredDataProviders() throws ItemNotFoundException
	{
		Mockito.when(manager.getConfiguredDataProviders()).thenCallRealMethod();
		
		Whitebox.setInternalState(manager, "config", getEvolutionConfig());
		Whitebox.setInternalState(manager, "workspace", workspace);
		
		assertEquals(9, manager.getConfiguredDataProviders().size());
	}
	
	@Test
	public void testLoadNonExistingDataProvider() throws ItemNotFoundException, IOException, InterruptedException
	{
		Mockito.when(manager.getConfiguredDataProviders()).thenCallRealMethod();
		
		EvolutionConfig config = getEvolutionConfig();
		
		config.getDataProviders().put("a", null);
		
		FilePath[] filePaths = { workspace };
		Mockito.when(workspace.list("**/checkstyle")).thenReturn(filePaths);
		
		Whitebox.setInternalState(manager, "config", config);
		Whitebox.setInternalState(manager, "workspace", workspace);
		
		assertEquals(9, manager.getConfiguredDataProviders().size());
	}
	
	@Test
	public void testClone()
	{
		EvolutionConfig config = getEvolutionConfig();
		
		EvolutionConfig clonedConfig = config.clone();

		assertNotNull(clonedConfig);
		assertNotSame(config, clonedConfig);
		
		assertEquals(9, clonedConfig.getDataProviders().size());
		
		DataProviderConfig clonedDataProviderConfig;
		
		for(Entry<String, DataProviderConfig> dataProviderConfig : clonedConfig.getDataProviders().entrySet())
		{
			clonedDataProviderConfig = dataProviderConfig.getValue().clone();
			
			assertNotNull(clonedDataProviderConfig);
			assertNotSame(dataProviderConfig.getValue(), clonedDataProviderConfig);
		}
	}
}
