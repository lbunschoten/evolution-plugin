package jenkins.plugins.evolution;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.tasks.Publisher;
import hudson.util.DescribableList;
import jenkins.model.Jenkins;
import jenkins.plugins.evolution.config.EvolutionConfig;
import jenkins.plugins.evolution.util.EvolutionUtil;
import jenkins.plugins.evolution.util.ItemNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@PowerMockIgnore("jenkins.model.TopLevelItem")
@RunWith(PowerMockRunner.class)
@PrepareForTest(Jenkins.class)
public class EvolutionUtilTest
{
	Jenkins mockedJenkins;
	
	EvolutionUtil mockedUtil;
	
	AbstractProject mockedProject;
	
	DescribableList<Publisher, Descriptor<Publisher>> mockedPublisherList;
	
	EvolutionConfig mockedConfig;
	
	AbstractBuild mockedBuild;
	
	EvolutionRecorder mockedRecorder;
	
	@Before
	public void before()
	{
		mockedJenkins = Mockito.mock(Jenkins.class);
		mockedUtil = PowerMockito.mock(EvolutionUtil.class);
		mockedProject = PowerMockito.mock(AbstractProject.class);
		mockedPublisherList = PowerMockito.mock(DescribableList.class);
		mockedConfig = PowerMockito.mock(EvolutionConfig.class);
		mockedBuild = PowerMockito.mock(AbstractBuild.class);
		mockedRecorder = PowerMockito.mock(EvolutionRecorder.class);
		
		PowerMockito.mockStatic(Jenkins.class);
		
		Mockito.when(Jenkins.getInstance()).thenReturn(mockedJenkins);
	}
	
	@Test
	public void testGetBuildOnNonExistingProject() throws ItemNotFoundException
	{
		try
		{
			new EvolutionUtil().getBuild("test", 1);
		}
		catch(ItemNotFoundException e)
		{
			assertEquals("Project test was not found.", e.getMessage());
			
			return;
		}
		
		fail("Should have reached catch clause.");
	}
	
	@Test
	public void testGetBuildOnNonExistingBuild() throws ItemNotFoundException
	{
		Mockito.when(mockedUtil.getProjectByName("test")).thenReturn(mockedProject);
		Mockito.when(mockedProject.getBuildByNumber(1)).thenReturn(null);
		Mockito.when(mockedUtil.getBuild("test", 1)).thenCallRealMethod();
		
		try
		{
			mockedUtil.getBuild("test", 1);
		}
		catch(Exception e)
		{
			assertEquals("Build 1 was not found for project test", e.getMessage());
			
			return;
		}
		
		fail("Should have reached catch clause.");
	}
	
	@Test
	public void testGetBuild() throws ItemNotFoundException
	{
		Mockito.when(mockedJenkins.getItemByFullName("test", AbstractProject.class)).thenReturn(mockedProject);
		Mockito.when(mockedProject.getBuildByNumber(1)).thenReturn(mockedBuild);
		Mockito.when(mockedUtil.getBuild("test", 1)).thenCallRealMethod();
		Mockito.when(mockedUtil.getProjectByName("test")).thenCallRealMethod();
		
		assertNotNull(mockedUtil.getBuild("test", 1));
	}
	
	@Test
	public void testGetConfig() throws ItemNotFoundException
	{
		Mockito.when(mockedProject.getPublishersList()).thenReturn(mockedPublisherList);
		Mockito.when(mockedPublisherList.get(EvolutionRecorder.class)).thenReturn(mockedRecorder);
		Mockito.when(mockedUtil.getEvolutionRecorder(mockedProject)).thenCallRealMethod();
		Mockito.when(mockedRecorder.getConfig()).thenReturn(mockedConfig);
		
		try
		{
			assertNotNull(mockedUtil.getEvolutionRecorder(mockedProject).getConfig());
		}
		catch(ItemNotFoundException e)
		{
			e.printStackTrace();
			
			fail("Should not reach catch clause.");
		}
	}
	
	@Test(expected = ItemNotFoundException.class)
	public void testGetConfigOnNonExistingProject() throws ItemNotFoundException
	{
		Mockito.when(mockedProject.getPublishersList()).thenReturn(mockedPublisherList);
		Mockito.when(mockedPublisherList.get(EvolutionRecorder.class)).thenReturn(null);
		Mockito.when(mockedUtil.getEvolutionRecorder(mockedProject)).thenCallRealMethod();
		
		mockedUtil.getEvolutionRecorder(mockedProject).getConfig();
	}
}
