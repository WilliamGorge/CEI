import java.util.ArrayList;

/**
 * Class for the use of the BitWeavingV Method, more specifically for the columns.<br>
 * An instance of this class represents a column of data where a query with the BitWeavingV method can be used.<br>
 * It deals with the data in the column: creation, addition, return the size.<br>
 * It uses the interface BitWeavingVInterface.<br>
 * @author Benoit Sordet
 */

public class BWVColumn extends BWColumn
{
	private ArrayList<BWVSegment> column; 	// Colomn on witch perform the scan
	
	// Name of the column
	private String name;

	// Constants of the column
	private int k;				// Size of one datum
	private int w;				// Size of processor word
	
	// Variables of the column
	private int Sno;			// Number of segments
	private int nbData;			// Number of data in the column
	private int nbDataLastSegment; 		// Number of data in the last segment


	/**
	 * Constructor of a BWV column with a processor word and a datum size given (format of the data).
	 * @param sizeOfOneDatum size (in bits) of one data in the column, depends on the format of the data. It is the number of bits with which the data is encoded.
	 * @param widthOfWord size of the processor word
	 */
	public BWVColumn(String name, int sizeOfOneDatum, int widthOfWord)
	{
		// Instaciation of the arguments
		k = sizeOfOneDatum;
		w = widthOfWord;
		this.name = name;
		nbData = 0;
		column = new ArrayList<BWVSegment>();
		nbDataLastSegment = 0;
		Sno = 0;
	}
	
	/**
	 * Gets the attatched column, as an array of BWVSegment<br><br>
	 * !!! USE FOR DEBUG ONLY !!!<br>
	 * It will be deleted for the relase version<br>
	 * @return column
	 */
	public ArrayList<BWVSegment> getColumn()
	{
		return column;
	}
	
	/**
	 * Returns the size of the column
	 * @return column.length
	 */
	public int size()
	{
		return nbData;
	}

	/*** ADD ONE DATUM FUNCTION ***/
	/**
	 * Adds one datum to the column
	 * @param datum: datum to add<br><br>
	 * @author Benoit Sordet
	 * @throws Exception throws an exeption when you try to add a datum to a full segment. Can happen when the segment is not handled in a good way.
	 */
	public void add(long nb)
	{
		if ((k < 63 && nb >= (1L << k)) || (k >= 63 && nb > (1L << 62) - 1 + (1L << 62)) || (k > 64))
		{
			throw new IllegalArgumentException(nb + " ne peut pas être codé en " + k + " bits.\n");
		}
		
		// If the number of data is not a multiple of w, this means the last segment is not full and we can add a datum in it.
		if (nbData % w != 0)
		{
			// Adding the datum to the segment
			column.get(Sno-1).add(nb);
			// Refreshing nbDataLastSegment
			++nbDataLastSegment;
		}
		
		// Else it means the number of data is a multiple of w, and all segments are full. We have to create another segment in order for us to add the datum.
		else
		{	
			// Creation of the segment		
			BWVSegment someSegment = new BWVSegment(k, w);
			// Adding the datum to the segment
			someSegment.add(nb);
			// Appending the newly created segment to the column
			column.add(someSegment);
			// Refreshing nbDataLastSegment
			nbDataLastSegment = 1;
			// Refreshing Sno
			++Sno;
		}
	
		// Refreshing nbData
		++nbData;
	}
	/*
	public void add(long nb[])
	{
		int n = nb.length;
		int roomLeft = (nbData % w == 0) ? 0 : nbData - (nbData % w);
		
		if (n <= roomLeft)
		{
			column.get(Sno-1).add(nb);
		}
		else
		{
			long[] tab = new long[roomLeft];
			int i;
			
			for (i = 0; i < roomLeft; ++i)
				tab[i] = nb[i];
			
			column.get(Sno-1).add(tab);
			
			int remainingDataToStore = n - roomLeft;
			int newFullSegmentCreatedNb = remainingDataToStore/w;
			int j;
			
			for (j = 0; j < newFullSegmentCreatedNb; ++j)
			{
				tab = new long[w];
				
				for (i = 0; i < w; ++i)
					tab[i] = nb[roomLeft+j*w+i];
				
				BWVSegment someSegment = new BWVSegment(tab, k, w);
				column.add(someSegment);
			}
			
			
			remainingDataToStore %= w;
			tab = new long[remainingDataToStore];
			
			for (i = 0; i < remainingDataToStore; ++i)
				tab[i] = nb[roomLeft+j*w+i];
			
			BWVSegment someSegment = new BWVSegment(tab, k, w);
			column.add(someSegment);
			Sno += (newFullSegmentCreatedNb+1);
		}
		
		nbData += n;
	}*/

	/*** QUERY FUNCTION ***/
	/**
	 * Performs the query queryName with the constant cst on the column of the instance of the BitWeavingV object.<br>
	 * Returns the result bit vector<br>
	 * @param query among enum Query:
		<blockquote>
			DIFFERENT<br>
			EQUAL<br>
			LESS THAN<br>
			LESS THAN OR EQUAL TO<br>
			GREATER THAN<br>
			GREATER THAN OR EQUAL TO<br>
		</blockquote>
		cst1: Constant to compare<br>
		cst2: Constant to compare<br><br>
	 * @throws IllegalArgumentException thrown if query has an unknown value or if cst cannot be encoded in k bits
	 * @author Benoit Sordet
	 */
	public BitVector query(Query queryName, long cst) throws IllegalArgumentException
	{	
		// Creation of the tab containing all bits of cst
		long[] cstTab = new long[k];
		// Creation of the mask we will use to select every bit of cst once at a time
		long mask = ( 1L << (k-1) );
		// Loop iterator
		int i = 0;
		
		if ((k < 63 && cst >= (1L << k)) || (k >= 63 && cst > (1L << 62) - 1 + (1L << 62)) || (k > 64))
		{
			throw new IllegalArgumentException(cst + " ne peut pas être codé en " + k + " bits.\n");
		}
		
		// As explained in the publication and the report, we have to generate an array of cst, cstTab[0] being the repetition (Long.SIZE times) of the last bit of cst (the most important one)
		while (i < k)
		{
			// If the i_th bit of cst is 1, put Long.SIZE 1 in cstTab[i]
			if ( (mask & cst) != 0 )
				cstTab[i] = ~0;
				
			// Else the i_th bit of cst is 0, put Long.SIZE 0 in cstTab[i]
			else
				cstTab[i] = 0;
			
			// Refreshing the mask for next iteration
			mask >>>= 1;
			// Refreshing the loop iterator
			++i;
		}
		
		// Given queryName, the appropriate query is to be launched
		switch (queryName)
		{
			case LESS_THAN:
				return lessThan(cstTab);
	
			case GREATER_THAN:
				return greaterThan(cstTab);
	
			case EQUAL:
				return equalTo(cstTab);
	
			case DIFFERENT:
				return differentTo(cstTab);
	
			case LESS_THAN_OR_EQUAL_TO:
				return lessThanOrEqualTo(cstTab);
	
			case GREATER_THAN_OR_EQUAL_TO:
				return greaterThanOrEqualTo(cstTab);
	
			default:
				throw new IllegalArgumentException("No such query.");
		}
	}
	
	/*** EXTENDED QUERY FUNCTION FOR THE BETWEEN QUERY ***/
	/**
	 * Performs only the query BETWEEN with the constants cst1 and cst2 on the column of the instance of the BitWeavingV object.<br>
	 * Returns the result bit vector<br>
	 * @param query among enum Query:
		<blockquote>
			DIFFERENT<br>
			EQUAL<br>
			LESS THAN<br>
			LESS THAN OR EQUAL TO<br>
			GREATER THAN<br>
			GREATER THAN OR EQUAL TO<br>
		</blockquote>
		cst1: Constant to compare<br><br>
	 * @throws IllegalArgumentException thrown if query has an unknown value or if cst cannot be encoded in k bits
	 * @author Benoit Sordet
	 */
	public BitVector query(Query queryName, long cst1, long cst2) throws IllegalArgumentException
	{	
		// Creation of the tab containing all bits of cst1
		long[] cstTab1 = new long[k];
		// Creation of the tab containing all bits of cst2
		long[] cstTab2 = new long[k];
		// Creation of the mask we will use to select every bit of cst once at a time
		long mask = ( 1L << (k-1) );
		// Loop iterator
		int i = 0;
		
		if (cst1 > cst2)
		{
			throw new IllegalArgumentException(cst1 + " est strictement plus grand que " + cst2 + ".");
		}
		
		if ((k < 63 && cst1 >= (1L << k)) || (k >= 63 && cst1 > (1L << 62) - 1 + (1L << 62)) || (k > 64))
		{
			throw new IllegalArgumentException(cst1 + " ne peut pas être codé en " + k + " bits.\n");
		}

		if ((k < 63 && cst2 >= (1L << k)) || (k >= 63 && cst2 > (1L << 62) - 1 + (1L << 62)) || (k > 64))
		{
			throw new IllegalArgumentException(cst2 + " ne peut pas être codé en " + k + " bits.\n");
		}
		
		while (i < k)
		{
			// If the i_th bit of cst1 is 1, put Long.SIZE 1 in cstTab1[i]
			if ( (mask & cst1) != 0 )
				cstTab1[i] = ~0;
				
			// Else the i_th bit of cst1 is 0, put Long.SIZE 0 in cstTab1[i]
			else
				cstTab1[i] = 0;
			
			// If the i_th bit of cst2 is 1, put Long.SIZE 1 in cstTab2[i]
			if ( (mask & cst2) != 0 )
				cstTab2[i] = ~0;
				
			// Else the i_th bit of cst2 is 0, put Long.SIZE 0 in cstTab2[i]
			else
				cstTab2[i] = 0;
			
			// Refreshing the mask for next iteration
			mask >>>= 1;
			// Refreshing the loop iterator
			++i;
		}
		
		// Given queryName, the appropriate query (BETWEEN here) is to be launched
		switch (queryName)
		{
			case BETWEEN:
				return between(cstTab1, cstTab2);
	
			default:
				System.out.println("No such query.");
				break;
		}

		return null;
	}

	/*** CORE FUNCTION OF THE QUERY "X < cst" ***/
	private BitVector lessThan(long[] cstTab)
	{
		// Creation of the Bit Vector that will contain all results
		BitVector result = new BitVector();
		
		for (int i = 0; i < Sno; ++i)
		{
			// Append the result of the query on the i_th segment to the Bit Vector
			result.append(column.get(i).lessThan(cstTab), w);
			// Re-initialize all variables of the i_th segment
			column.get(i).recharge();
		}
		
		// Remove the useless zero-padding
		result.deleteEnd(w - nbDataLastSegment);
		// Return the result of the query
		return result;
	}

	/*** CORE FUNCTION OF THE QUERY "X > cst" ***/
	private BitVector greaterThan(long[] cstTab)
	{
		// Creation of the Bit Vector that will contain all results
		BitVector result = new BitVector();
		
		for (int i = 0; i < Sno; ++i)
		{
			// Append the result of the query on the i_th segment to the Bit Vector
			result.append(column.get(i).greaterThan(cstTab), w);
			// Re-initialize all variables of the i_th segment
			column.get(i).recharge();
		}
		
		// Remove the useless zero-padding
		result.deleteEnd(w - nbDataLastSegment);
		// Return the result of the query
		return result;
	}
	
	/*** CORE FUNCTION OF THE QUERY "cst1 <= X <= cst2" ***/
	private BitVector between(long[] cstTab1, long[] cstTab2)
	{
		// Creation of the Bit Vector that will contain all results
		BitVector result = new BitVector();
		
		for (int i = 0; i < Sno; ++i)
		{
			// Append the result of the query on the i_th segment to the Bit Vector
			result.append(column.get(i).between(cstTab1, cstTab2), w);
			// Re-initialize all variables of the i_th segment
			column.get(i).recharge();
		}
		
		// Remove the useless zero-padding
		result.deleteEnd(w - nbDataLastSegment);
		// Return the result of the query
		return result;
	}

	/*** CORE FUNCTION OF THE QUERY "X = cst" ***/
	private BitVector equalTo(long[] cstTab)
	{
		// Creation of the Bit Vector that will contain all results
		BitVector result = new BitVector();
		
		for (int i = 0; i < Sno; ++i)
		{
			// Append the result of the query on the i_th segment to the Bit Vector
			result.append(column.get(i).equalTo(cstTab), w);
			// Re-initialize all variables of the i_th segment
			column.get(i).recharge();
		}
		
		// Remove the useless zero-padding
		result.deleteEnd(w - nbDataLastSegment);
		// Return the result of the query
		return result;
	}

	/*** CORE FUNCTION OF THE QUERY "X != cst" ***/
	private BitVector differentTo(long[] cstTab)
	{
		// Creation of the Bit Vector that will contain all results
		BitVector result = new BitVector();
		
		for (int i = 0; i < Sno; ++i)
		{
			// Append the result of the query on the i_th segment to the Bit Vector
			result.append(column.get(i).differentTo(cstTab), w);
			// Re-initialize all variables of the i_th segment
			column.get(i).recharge();
		}
		
		// Remove the useless zero-padding
		result.deleteEnd(w - nbDataLastSegment);
		// Return the result of the query
		return result;
	}

	/*** CORE FUNCTION OF THE QUERY "X <= cst" ***/
	private BitVector lessThanOrEqualTo(long[] cstTab)
	{
		// Creation of the Bit Vector that will contain all results
		BitVector result = new BitVector();
		
		for (int i = 0; i < Sno; ++i)
		{
			// Append the result of the query on the i_th segment to the Bit Vector
			result.append(column.get(i).lessThanOrEqualTo(cstTab), w);
			// Re-initialize all variables of the i_th segment
			column.get(i).recharge();
		}
		
		// Remove the useless zero-padding
		result.deleteEnd(w - nbDataLastSegment);
		// Return the result of the query
		return result;
	}

	/*** CORE FUNCTION OF THE QUERY "X >= cst" ***/
	private BitVector greaterThanOrEqualTo(long[] cstTab)
	{
		// Creation of the Bit Vector that will contain all results
		BitVector result = new BitVector();
		
		for (int i = 0; i < Sno; ++i)
		{
			// Append the result of the query on the i_th segment to the Bit Vector
			result.append(column.get(i).greaterThanOrEqualTo(cstTab), w);
			// Re-initialize all variables of the i_th segment
			column.get(i).recharge();
		}
		
		// Remove the useless zero-padding
		result.deleteEnd(w - nbDataLastSegment);
		// Return the result of the query
		return result;
	}

	/**
	 * Returns the name of the column
	 * @return column.name
	 */
	public String getName() {
		return name;
	}

	public int getSizeOfOneDatum() {
		return k;
	}

	public int getSizeOfProcessorWord() {
		return w;
	}

	public void printProcessorWords() {
		
		// Itteration on all the segment
		for(int i = 0; i < column.size(); ++i) {
			
			System.out.println("Processor Words for segment " + (i + 1) + ":");
			
			// Gets the processor words
			long[] processorWords = column.get(i).getProcessorWords();
			
			// Display them
			for(int j = 0; j < processorWords.length; ++j)
				System.out.println("	" + longtobitsString(processorWords[j]).substring(0,w));
			
			System.out.println();
		}
	}
	
	/**
	 * For display. Returns the binary string with zeros of the long l
	 * This is a little modification of Long.toBinaryString(long)
	 * @param long l: long to convert to a string of bits
	 * @author William Gorge
	 */
	private String longtobitsString(long l){
		String s = "";
		for(int i = 0; i < Long.numberOfLeadingZeros(l); ++i) {
			s += "0";
		}
		if(l != 0) s += Long.toBinaryString(l);
		return s;
	}
}