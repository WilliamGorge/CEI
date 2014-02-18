
import java.security.InvalidParameterException;

/**
 * Interface for the use of BitWeavingH<br>
 * An instance of this interface represents a column of data where a query with the BitWeavingH method can be used<br>
 * @author William Gorge
 */
public interface BitWeavingHInterface {
	
	/**
	 * Adds one datum to the column<br>
	 * @param datum: datum to add<br>
	 * @author William Gorge
	 */
	void add(long datum);
	
	
	/**
	 * Performs the query queryName with the constant cst on the column of the instance of the BitWeavingH object<br>
	 * @param query among enum Query:
		<blockquote>
			DIFFERENT<br>
			EQUAL<br>
			LESS THAN<br>
			LESS THAN OR EQUAL TO<br>
			GREATER THAN<br>
			GREATER THAN OR EQUAL TO<br>
		</blockquote>
		cst: Constant to compare<br><br>
		
	 * @return Result bit vector.<br>
	 * The result bit vector is an array of long: each entry in the array is the result bit vector for one segment<br>
	 * Each bit in a long is the result of one datum<br>
	 * 
	 * @throws InvalidParameterException
	 * @author William Gorge
	 */
	long[] query(Query query, long cst);
	
	
	/**
	 * Returns the number of data in the column<br>
	 * @return integer: size of the column
	 */
	int size();
	
	/**
	 * Returns the attatched column (as an array of BWHSegment)<br><br>
	 * !!! USE FOR DEBUG ONLY !!!<br>
	 * It will be deleted for the relase version<br>
	 * @return column
	 */
	BWHSegment[] getColumn();


	long[] complexQuery(String string);
}	
