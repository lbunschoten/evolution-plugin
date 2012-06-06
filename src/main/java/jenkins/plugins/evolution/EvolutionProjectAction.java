package jenkins.plugins.evolution;

import hudson.model.Action;
import hudson.model.AbstractProject;
import hudson.model.Actionable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;
import jenkins.plugins.evolution.calculator.DerivativeCalculator;
import jenkins.plugins.evolution.calculator.ScoreCalculator;
import jenkins.plugins.evolution.config.EvolutionConfig;
import jenkins.plugins.evolution.config.InvalidConfigException;
import jenkins.plugins.evolution.domain.Build;
import jenkins.plugins.evolution.graph.DerivativeLine;
import jenkins.plugins.evolution.graph.EvolutionGraph;
import jenkins.plugins.evolution.graph.GraphPoint;
import jenkins.plugins.evolution.graph.GraphPointList;
import jenkins.plugins.evolution.graph.ScoreLine;
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
	
	public File getEvolutionFile()
	{
		return new EvolutionUtil().getEvolutionFile(project);
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
	 */
	public void doScoreGraph(StaplerRequest req, StaplerResponse rsp) throws IOException, ItemNotFoundException
	{
		EvolutionGraph scoreGraph = new EvolutionGraph(Messages.EvolutionProjectAction_scoreGraphName());
		
		scoreGraph.addLine(new ScoreLine(getScoreGraphPoints(new ScoreCalculator(getConfig()))));
		
		scoreGraph.doPng(req, rsp);
	}
	
	/**
	 * First calculates the graph points and then generates the map for the
	 * graph.
	 * 
	 * @param req
	 * @param rsp
	 * @throws IOException
	 * @throws ItemNotFoundException
	 */
	public void doScoreGraphMap(StaplerRequest req, StaplerResponse rsp) throws IOException, ItemNotFoundException
	{
		EvolutionGraph scoreGraph = new EvolutionGraph(Messages.EvolutionProjectAction_scoreGraphName());
		
		scoreGraph.addLine(new ScoreLine(getScoreGraphPoints(new ScoreCalculator(getConfig()))));
		
		scoreGraph.doMap(req, rsp);
	}
	
	/**
	 * First calculates the graph points and then generates a graph.
	 * 
	 * @param req
	 * @param rsp
	 * @throws IOException
	 * @throws ItemNotFoundException
	 */
	public void doDerivativeGraph(StaplerRequest req, StaplerResponse rsp) throws IOException, ItemNotFoundException
	{
		EvolutionGraph derivativeGraph = new EvolutionGraph(Messages.EvolutionProjectAction_derivativateGraphName());
		
		DerivativeLine line = new DerivativeLine(getDerivativeGraphPoints());
		
		derivativeGraph.addLine(line);
		
		derivativeGraph.doPng(req, rsp);
	}
	
	/**
	 * @return A list of points which should be displayed in the graph.
	 */
	private GraphPointList getScoreGraphPoints(ScoreCalculator calculator)
	{
		GraphPointList graphPoints = new GraphPointList(project.getFullName());
		
		ArrayList<Build> builds = loadBuilds();
		
		for(Build build : builds)
		{
			graphPoints.add(getScoreGraphPointForBuild(calculator, build));
		}
		
		return graphPoints;
	}
	
	/**
	 * Create a new GraphPoint for a build. Also the total score is added to the
	 * GraphPoint.
	 * 
	 * @param build
	 * @return GraphPoint
	 * @throws InvalidConfigException
	 */
	private GraphPoint getScoreGraphPointForBuild(ScoreCalculator calculator, Build build)
	{
		try
		{
			HashMap<String, Double> scores = calculator.calculate(build);
			
			double totalScore = scores.get("total");
			
			String tooltip = generateScoreTooltip(build.getId(), scores);
			
			return new GraphPoint(build.getId(), totalScore, tooltip);
		}
		catch(InvalidConfigException e)
		{
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).warning(e.getMessage());
		}
		
		return null;
	}
	
	/**
	 * Generate a tooltip for a build. This tooltip contains the scores of all
	 * individual data providers.
	 * 
	 * @param buildNumber
	 * @param scores
	 * @return a tooltip
	 */
	private String generateScoreTooltip(int buildNumber, HashMap<String, Double> scores)
	{
		return scoreTooltipGenerator.generateTooltip(buildNumber, scores);
	}
	
	/**
	 * @return A list of points which should be displayed in the graph.
	 * @throws ItemNotFoundException
	 */
	private GraphPointList getDerivativeGraphPoints() throws ItemNotFoundException
	{
		ArrayList<Build> builds = loadBuilds();
		
		ScoreCalculator scoreCalculator = new ScoreCalculator(getConfig());
		DerivativeCalculator derivativeCalculator = new DerivativeCalculator(builds.size());
		
		GraphPointList derivativeGraphPoints = new GraphPointList(project.getFullName());
		
		for(Build build : builds)
		{
			GraphPoint scoreGraphPoint = getScoreGraphPointForBuild(scoreCalculator, build);
			
			if(scoreGraphPoint != null)
			{
				derivativeGraphPoints.add(getDerivativeGraphPointForBuild(derivativeCalculator, build, scoreGraphPoint.getValue()));
			}
		}
		
		return derivativeGraphPoints;
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
			if(isUsefullBuild(build))
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
			EvolutionReader reader = new EvolutionReader(new FileInputStream(getEvolutionFile()));
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
	public boolean isUsefullBuild(Build build)
	{
		if(build.getResults().size() < 1)
		{
			return false;
		}
		if(!isExistingBuild(build.getId()))
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
	public boolean isExistingBuild(int buildNumber)
	{
		return project.getBuildByNumber(buildNumber) != null;
	}
	
	/**
	 * Retrieves the config from the publisher.
	 * 
	 * @return config
	 * @throws ItemNotFoundException
	 */
	public EvolutionConfig getConfig() throws ItemNotFoundException
	{
		EvolutionUtil util = new EvolutionUtil();
		
		return util.getEvolutionRecorder(project).getConfig();
	}
}
