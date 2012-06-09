package jenkins.plugins.evolution;

import hudson.model.Action;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Actionable;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Logger;
import jenkins.plugins.evolution.calculator.DataProviderCalculator;
import jenkins.plugins.evolution.calculator.DerivativeCalculator;
import jenkins.plugins.evolution.calculator.EvolutionCalculator;
import jenkins.plugins.evolution.config.EvolutionConfig;
import jenkins.plugins.evolution.config.InvalidConfigException;
import jenkins.plugins.evolution.dataprovider.DataProvider;
import jenkins.plugins.evolution.domain.Build;
import jenkins.plugins.evolution.graph.DataProviderLine;
import jenkins.plugins.evolution.graph.DerivativeLine;
import jenkins.plugins.evolution.graph.EvolutionGraph;
import jenkins.plugins.evolution.graph.EvolutionLine;
import jenkins.plugins.evolution.graph.GraphPoint;
import jenkins.plugins.evolution.graph.GraphPointList;
import jenkins.plugins.evolution.graph.ScoreTooltipGenerator;
import jenkins.plugins.evolution.persistence.EvolutionReader;
import jenkins.plugins.evolution.persistence.PersistenceException;
import jenkins.plugins.evolution.util.EvolutionUtil;
import jenkins.plugins.evolution.util.ItemNotFoundException;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * This action is executed on the project overview page of Jenkins. Its main
 * goal is to display a evolution graph.
 * 
 * @author leon
 */
public class EvolutionProjectAction extends Actionable implements Action
{
	private AbstractProject<?, ?> project;
	
	private ScoreTooltipGenerator scoreTooltipGenerator = new ScoreTooltipGenerator();
	
	/**
	 * Constructor of the EvolutionProjectAction
	 * 
	 * @param project
	 */
	public EvolutionProjectAction(AbstractProject<?, ?> project)
	{
		this.project = project;
	}
	
	@Override
	public String getDisplayName()
	{
		return "Evolution";
	}
	
	@Override
	public String getSearchUrl()
	{
		return "evolution";
	}
	
	@Override
	public String getIconFileName()
	{
		return null;
	}
	
	@Override
	public String getUrlName()
	{
		return "evolution";
	}
	
	public int getResultCount()
	{
		return loadBuilds().size();
	}
	
	/**
	 * First calculates the graph points and then generates a graph.
	 * 
	 * @param req
	 * @param rsp
	 * @throws IOException
	 * @throws ItemNotFoundException
	 * @throws InvalidConfigException
	 */
	public void doScoreGraph(StaplerRequest req, StaplerResponse rsp) throws IOException
	{
		EvolutionGraph graph = new EvolutionGraph(Messages.EvolutionProjectAction_scoreGraphName());
		
		// Load config (quit on failure)
		EvolutionConfig config = getConfig();
		if(config == null)
		{
			return;
		}
		
		addScoreGraphPoints(graph, config);
		
		graph.doPng(req, rsp);
	}
	
	/**
	 * First calculates the graph points and then generates the map for the
	 * graph.
	 * 
	 * @param req
	 * @param rsp
	 * @throws IOException
	 * @throws ItemNotFoundException
	 * @throws InvalidConfigException
	 */
	public void doScoreGraphMap(StaplerRequest req, StaplerResponse rsp) throws IOException, ItemNotFoundException, InvalidConfigException
	{
		EvolutionGraph graph = new EvolutionGraph(Messages.EvolutionProjectAction_scoreGraphName());
		
		// Load config (quit on failure)
		EvolutionConfig config = getConfig();
		if(config == null)
		{
			return;
		}
		
		addScoreGraphPoints(graph, config);
		
		graph.doMap(req, rsp);
	}
	
	/**
	 * First calculates the graph points and then generates a graph.
	 * 
	 * @param req
	 * @param rsp
	 * @throws IOException
	 * @throws ItemNotFoundException
	 * @throws InvalidConfigException
	 */
	public void doDerivativeGraph(StaplerRequest req, StaplerResponse rsp) throws IOException, ItemNotFoundException, InvalidConfigException
	{
		EvolutionGraph graph = new EvolutionGraph(Messages.EvolutionProjectAction_derivativateGraphName());
		
		// Load config (quit on failure)
		EvolutionConfig config = getConfig();
		if(config == null)
		{
			return;
		}
		
		addDerivativeGraphPoints(graph, config);
		
		graph.doPng(req, rsp);
	}
	
	private EvolutionGraph addScoreGraphPoints(EvolutionGraph scoreGraph, EvolutionConfig config)
	{
		double totalScore = 0;
		HashMap<String, Double> dataProviderScores;
		
		// Create lists
		HashMap<String, GraphPointList> dataProviderGraphPoints = new HashMap<String, GraphPointList>();
		
		for(Entry<String, DataProvider> dataProvider : config.getConfiguredDataProviders().entrySet())
		{
			dataProviderGraphPoints.put(dataProvider.getKey(), new GraphPointList(project.getFullName()));
		}
		
		GraphPointList evolutionGraphPoints = new GraphPointList(project.getFullName());
		
		// Load builds
		ArrayList<Build> builds = loadBuilds();
		
		// Create calculators
		DataProviderCalculator dataProviderCalculator = new DataProviderCalculator(config);
		EvolutionCalculator evolutionCalculator = new EvolutionCalculator(config);
		
		// Calculate scores for each build
		for(Build build : builds)
		{
			try
			{
				dataProviderScores = calculateDataProviderScoresForBuild(dataProviderCalculator, build);
				
				for(Entry<String, Double> dataProviderScore : dataProviderScores.entrySet())
				{
					GraphPointList list = dataProviderGraphPoints.get(dataProviderScore.getKey());
					
					list.add(build.getId(), dataProviderScore.getValue());
				}
				
				totalScore = calculateEvolutionScore(evolutionCalculator, dataProviderScores);
				
				evolutionGraphPoints.add(build.getId(), totalScore, generateScoreTooltip(build.getId(), totalScore, dataProviderScores));
			}
			catch(InvalidConfigException e)
			{
				Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).warning(e.getMessage());
			}
		}
		
		scoreGraph.addLine(new EvolutionLine(evolutionGraphPoints));
		
		for(Entry<String, GraphPointList> list : dataProviderGraphPoints.entrySet())
		{
			scoreGraph.addLine(new DataProviderLine(list.getValue()));
		}
		
		return scoreGraph;
	}
	
	public EvolutionGraph addDerivativeGraphPoints(EvolutionGraph graph, EvolutionConfig config)
	{
		double totalScore = 0;
		HashMap<String, Double> dataProviderScores;
		
		GraphPointList graphPointList = new GraphPointList(project.getFullName());
		
		// Load builds
		ArrayList<Build> builds = loadBuilds();
		
		// Create calculators
		DataProviderCalculator dataProviderCalculator = new DataProviderCalculator(config);
		EvolutionCalculator evolutionCalculator = new EvolutionCalculator(config);
		DerivativeCalculator derivativeCalculator = new DerivativeCalculator(builds.size());
		
		// Calculate scores for each build
		for(Build build : builds)
		{
			try
			{
				dataProviderScores = calculateDataProviderScoresForBuild(dataProviderCalculator, build);
				
				totalScore = calculateEvolutionScore(evolutionCalculator, dataProviderScores);
				
				graphPointList.add(getDerivativeGraphPointForBuild(derivativeCalculator, build, totalScore));
			}
			catch(InvalidConfigException e)
			{
				Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).warning(e.getMessage());
			}
		}
		
		graph.addLine(new DerivativeLine(graphPointList));
		
		return graph;
	}
	
	private HashMap<String, Double> calculateDataProviderScoresForBuild(DataProviderCalculator calculator, Build build)
	{
		return calculator.calculate(build);
	}
	
	private double calculateEvolutionScore(EvolutionCalculator calculator, HashMap<String, Double> dataProviderScores) throws InvalidConfigException
	{
		return calculator.calculate(dataProviderScores);
	}
	
	/**
	 * Generate a tooltip for a build. This tooltip contains the scores of all
	 * individual data providers.
	 * 
	 * @param buildNumber
	 * @param scores
	 * @return a tooltip
	 */
	private String generateScoreTooltip(int buildNumber, double totalScore, HashMap<String, Double> scores)
	{
		return scoreTooltipGenerator.generateTooltip(buildNumber, totalScore, scores);
	}
	
	/**
	 * Create a new GraphPoint for a build. Also the total score is added to the
	 * GraphPoint.
	 * 
	 * @param build
	 * @return GraphPoint
	 * @throws InvalidConfigException
	 */
	private GraphPoint getDerivativeGraphPointForBuild(DerivativeCalculator calculator, Build build, double score)
	{
		double derivative = calculator.calculate(score);
		
		if(derivative != 0)
		{
			return new GraphPoint(build.getId(), derivative, "");
		}
		
		return null;
	}
	
	public ArrayList<Build> loadBuilds()
	{
		ArrayList<Build> builds = readBuilds();
		ArrayList<Build> usefulBuilds = new ArrayList<Build>();
		
		for(Build build : builds)
		{
			if(isUsefulBuild(build))
			{
				usefulBuilds.add(build);
			}
		}
		
		return usefulBuilds;
	}
	
	/**
	 * Loads evolution data from XML.
	 * 
	 * @return a Job object containing all required data from the XML file.
	 */
	private ArrayList<Build> readBuilds()
	{
		ArrayList<Build> builds = new ArrayList<Build>();
		
		try
		{
			EvolutionReader reader = new EvolutionReader(new FileInputStream(EvolutionRecorder.getEvolutionFile(project)));
			builds = reader.read().getBuilds();
		}
		catch(PersistenceException e)
		{
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).warning(e.getMessage());
		}
		catch(FileNotFoundException e)
		{
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).warning(e.getMessage());
		}
		
		return builds;
	}
	
	/**
	 * Checks whether a build (still) exists.
	 * 
	 * @param buildNumber
	 * @return build existence
	 */
	public boolean isUsefulBuild(Build build)
	{
		if(build.getResults().size() < 1)
		{
			return false;
		}
		if(!isSuccesfulBuild(build.getId()))
		{
			return false;
		}
		
		return true;
	}
	
	/**
	 * Checks whether a build (still) exists.
	 * 
	 * @param buildNumber
	 * @return build existence
	 */
	public boolean isSuccesfulBuild(int buildNumber)
	{
		AbstractBuild<?, ?> build = project.getBuildByNumber(buildNumber);
		
		if(build == null)
		{
			return false;
		}
		
		if(build.getResult().isBetterOrEqualTo(Result.UNSTABLE))
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Retrieves the config from the publisher.
	 * 
	 * @return config
	 * @throws ItemNotFoundException
	 */
	public EvolutionConfig getConfig()
	{
		EvolutionUtil util = new EvolutionUtil();
		
		try
		{
			return util.getEvolutionRecorder(project).getConfig();
		}
		catch(ItemNotFoundException e)
		{
			Logger.getLogger("Could not load config");
		}
		
		return null;
	}
}
