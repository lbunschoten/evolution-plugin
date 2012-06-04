package jenkins.plugins.evolution.domain;

/**
 * Represents data of a result required for the Evolution Plugin. This object is
 * persisted to an XML object using the XStream library.
 * 
 * @author leon
 */
public class Result
{
	private String dataProviderId;
	
	private double data;
	
	/**
	 * Constructor of the Result class.
	 * 
	 * @param dataProviderId
	 * @param data
	 */
	public Result(String dataProviderId, double data)
	{
		this.dataProviderId = dataProviderId;
		this.data = data;
	}
	
	public double getData()
	{
		return data;
	}
	
	public void setData(double data)
	{
		this.data = data;
	}
	
	public String getDataProviderId()
	{
		return dataProviderId;
	}
	
	public void setDataProviderId(String dataProviderId)
	{
		this.dataProviderId = dataProviderId;
	}
}
