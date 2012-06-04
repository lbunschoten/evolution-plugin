package jenkins.plugins.evolution.config;

import java.io.Serializable;
import java.util.logging.Logger;

/**
 * Stores data used to configure a data provider. The data in this class will used to
 * calculate the scores for each of the data providers.
 * 
 * @author leon
 */
public class DataProviderConfig implements Cloneable, Serializable
{
	private static final long serialVersionUID = 1L;
	
	private String name;
	
	private String path;
	
	private String min;
	
	private String max;
	
	private String weight;
	
	/**
	 * Constructor for the configuration item of a data provider.
	 * 
	 * @param name
	 */
	public DataProviderConfig(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getPath()
	{
		return path;
	}
	
	public void setPath(String path)
	{
		this.path = path;
	}
	
	public String getMin()
	{
		return min;
	}
	
	public void setMin(String min)
	{
		this.min = min;
	}
	
	public String getMax()
	{
		return max;
	}
	
	public void setMax(String max)
	{
		this.max = max;
	}
	
	public String getWeight()
	{
		return weight;
	}
	
	public void setWeight(String weight)
	{
		this.weight = weight;
	}
	
	/**
	 * Checks wheter any configuration data has been entered.
	 * 
	 * @return whether the configuration item contains data
	 */
	public boolean isFullyConfigured()
	{
		return path != null && !path.isEmpty() && min != null && !min.isEmpty() && max != null && !max.isEmpty() && weight != null && !weight.isEmpty();
	}
	
	/**
	 * Clone the config item, so that it can be used in the configuration of
	 * Jenkins.
	 * 
	 * @return Copy of the DataProviderConfig
	 */
	@Override
	public DataProviderConfig clone()
	{
		DataProviderConfig config = null;
		
		try
		{
			config = (DataProviderConfig) super.clone();
		}
		catch(CloneNotSupportedException e)
		{
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).warning(e.getMessage());
			// Should never reach this exception as the Cloneable interface is
			// implemented.
		}
		
		return config;
	}
}
