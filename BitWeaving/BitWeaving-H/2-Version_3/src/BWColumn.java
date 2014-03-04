import java.security.InvalidParameterException;


public abstract class BWColumn {
	
	
	/**
	 * Adds one datum to the column<br>
	 * @param datum: datum to add<br>
	 * @author William Gorge
	 * @throws Exception  throws an exeption when you try to add a datum to a full segment.
	 *  Can happen when the segment is not handled in a good way.
	 */
	abstract void add(long datum) throws Exception;
	
	
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
	abstract BitVector query(Query query, long cst);
	
	/**
	 * Returns the number of data in the column<br>
	 * @return integer: size of the column
	 */
	abstract int size();
	
	/**
	 * Returns the name of the column
	 * @return column.name
	 */
	abstract String getName();

	/**
	 * Returns the size of processor word for this column
	 * @return column.w
	 */
	abstract int getSizeOfProcessorWord();
}
