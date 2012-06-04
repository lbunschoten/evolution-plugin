package jenkins.plugins.evolution.persistence;

/**
 * Interface for all reader classes. Before reading any data, the openReader
 * method will be called. Afterwards the closeReader method will be called to
 * close any remaining inputStreams.
 * 
 * @author leon
 * @param <T>
 */
public interface Reader<T>
{
	void openReader() throws PersistenceException;
	
	Object read() throws PersistenceException;
	
	void closeReader() throws PersistenceException;
}
