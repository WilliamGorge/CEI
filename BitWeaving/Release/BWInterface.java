import java.security.InvalidParameterException;


/**
 * Interface for the use of BitWeaving to use with a BWStore<br>
 * @author William Gorge and Benoit Sordet
 */
public interface BWInterface {
	
	/**
	 * Adds one datum to the specified column
	 * @param datum datum to add to the column
	 * @param columnIndex index of column to witch add the datum
	 * @author William Gorge and Benoit Sordet
	 * @throws Exception an exception can be thrown when the column name is not found
	 */
	public void addDatum(long datum, String columnName) throws Exception;
	
	/**
	 * Adds one column to the store, given its type, its name and the size of one datum.
	 * @param columnName Name of the column, used to manipulate it (query, add ...)
	 * @param columnType Type of the column, has to be from the enum ColumnType: BWH or BWV
	 * @param sizeOfOneDatum Size of one datum in the column, depends on the data format of the column. Example: if the type is int, sizeOfOneDatum = 32.
	 * @author William Gorge and Benoit Sordet
	 */
	public void addColumn(String columnName, ColumnType columnType, int sizeOfOneDatum);
	
	/**
	 * Adds one column to the store giv.
	 * @param column Column to add, has to match the length of the processor word of the store
	 * @author William Gorge and Benoit Sordet
	 * @throws Exception thrown when the size of the processor word of the column given does not match the one of the store.
	 */
	public void addColumn(BWColumn column) throws Exception;
	
	/**
	 * Performs the query given the arguments
	 * @param arg query
	 * @author William Gorge and Benoit Sordet
	 * @throws Exception Thrown when there is a syntax error
	 * @throws InvalidParameterException Thrown when there is a illegal value for one parameter
	 */
	public BitVector query(String arg) throws Exception;
	
}	
