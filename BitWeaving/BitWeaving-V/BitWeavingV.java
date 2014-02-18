import java.security.InvalidParameterException;


public class BitWeavingV implements BitWeavingVInterface
{
	private BWVSegment[] column; 			// Processor words
	private int k;							// Size of one datum
	private int Sno;
	private int w;
	private int nbData;
	private long shiftLastSegment;
	private long[] result;

	public BitWeavingV() 
	{
		k = 0;
		Sno = 0;
		w = 0;
		nbData = 0;
		shiftLastSegment = 0;
		result = null;
		column = null;
	}

	public BitWeavingV(long[] dataArray, int sizeOfOneDatum, int widthOfWord)
	{
		k = sizeOfOneDatum;
		w = widthOfWord;
		nbData = dataArray.length;

		int i, j, n = nbData, p = nbData % w;
		Sno = (p > 0) ? nbData/w+1 : nbData/w;
		shiftLastSegment = (p > 0) ?   w - p : 0;

		column = new BWVSegment[Sno];
		long[] tempArray = new long[w];

		for (i = 0; i < Sno - 1; ++i)
		{
			for (j = 0; j < w; ++j)
				tempArray[j]= dataArray[w*i+j];
			
			column[i] = new BWVSegment(tempArray, sizeOfOneDatum, widthOfWord);
		}

		p = (p != 0) ? p : w;

		tempArray = new long[p];

		for (j = 0; j < p; ++j)
			tempArray[j] = dataArray[(Sno-1)*w+j];

		column[Sno-1] = new BWVSegment(tempArray, sizeOfOneDatum, widthOfWord);
		result = new long[Sno];
	}
	
	public BWVSegment[] getColumn()
	{
		return column;
	}
	
	public int size()
	{
		return nbData;
	}
	
	public void add(long nb)
	{
		if ((k < 63 && nb >= (1L << k)) || (k >= 63 && nb > (1L << 62) - 1 + (1L << 62)) || (k > 64))
		{
			System.out.println(nb + " ne peut pas être codé en " + k + " bits.\n");
			return;
		}
		
		if (nbData % w != 0)
			column[Sno-1].add(nb);
		else
		{
			BWVSegment[] column2 = new BWVSegment[Sno+1];
			
			for (int i = 0; i < Sno; ++i)
				column2[i] = column[i];
			
			long[] tab = new long[1];
			tab[0] = nb;
			
			column2[Sno] = new BWVSegment(tab, k, w);
			++Sno;
			column = column2;
			result = new long[Sno];
		}
	
		++nbData;
		--shiftLastSegment;
		
		if (shiftLastSegment == -1)
			shiftLastSegment = w - 1;
	}

	public long[] query(Query queryName, long cst)
	{	
		long[] cstTab = new long[k];
		long mask = ( 1L << (k-1) );
		int i = 0;
		
		if ((k < 63 && cst >= (1L << k)) || (k >= 63 && cst > (1L << 62) - 1 + (1L << 62)) || (k > 64))
		{
			System.out.println(cst + " ne peut pas être codé en " + k + " bits.\n");
			return cstTab;
		}
		
		while (i < k)
		{
			if ( (mask & cst) != 0 )
				cstTab[i] = ~0;
			else
				cstTab[i] = 0;
			
			mask >>>= 1;
			++i;
		}
		
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
				System.out.println("No such query.");
				break;
		}
		
		return null;
	}

	private long[] lessThan(long[] cstTab)
	{
		for (int i = 0; i < Sno; ++i)
		{
			result[i] = column[i].lessThan(cstTab);
			column[i].recharge();
		}
		
		result[Sno - 1] >>>= shiftLastSegment;
		return result;
	}

	private long[] greaterThan(long[] cstTab)
	{
		for (int i = 0; i < Sno; ++i)
		{
			result[i] = column[i].greaterThan(cstTab);
			column[i].recharge();
		}
		
		result[Sno - 1] >>>= shiftLastSegment;
		return result;
	}

	private long[] equalTo(long[] cstTab)
	{
		for (int i = 0; i < Sno; ++i)
		{
			result[i] = column[i].equalTo(cstTab);
			column[i].recharge();
		}

		result[Sno - 1] >>>= shiftLastSegment;
		return result;
	}

	private long[] differentTo(long[] cstTab)
	{
		for (int i = 0; i < Sno; ++i)
		{
			result[i] = column[i].differentTo(cstTab);
			column[i].recharge();
		}

		result[Sno - 1] >>>= shiftLastSegment;
		return result;
	}

	private long[] lessThanOrEqualTo(long[] cstTab)
	{
		for (int i = 0; i < Sno; ++i)
		{
			result[i] = column[i].lessThanOrEqualTo(cstTab);
			column[i].recharge();
		}

		result[Sno - 1] >>>= shiftLastSegment;
		return result;
	}

	private long[] greaterThanOrEqualTo(long[] cstTab)
	{
		for (int i = 0; i < Sno; ++i)
		{
			result[i] = column[i].greaterThanOrEqualTo(cstTab);
			column[i].recharge();
		}

		result[Sno - 1] >>>= shiftLastSegment;
		return result;
	}

	private void display(long result, int length)
	{
		long mask;

		for (int j = 0; j < length; ++j)
		{
			mask = result & ( 1L << (w-1-j) );
			mask = (mask != 0) ? 1 : 0;
			System.out.print(mask);
		}
		
		System.out.println();
	}

	/**
	 * Decodes complex queries into elementary queries and performs them on the column of the instance of the BitWeavingH object.<br>
	 * Decodes the String "query" into elements of the enum Query.<br>
	 * Returns the result bit vector<br>
	 * @param query: String that represents the complex query ej: "LESS THAN 1000 OR EQUAL 2000 AND DIFFERENT 0"
	 * @throws InvalidParameterException
	 * @author William Gorge and Benoit Sordet
	 */
	public long[] complexQuery(String query) throws InvalidParameterException 
	{
		// Result bit vector
		long[] BVout = null;

		// Operator for the next instruction
		Operator op = null;

		// Query of the next instruction
		Query q = null;

		// Constant of the next instruction
		Integer cst = null;

		// Index of this constant in the query string
		int cstIndex = 0;

		// Boolean that indicates if it is the first instruction
		boolean first = true;

		// Splits the query into sets of instructions. The separator is the constants numbers given
		String[] instructions = query.split("\\d+");

		// Itterating on this instructions
		for(int i = 0; i < instructions.length; ++i){

			cstIndex += instructions[i].length();

			// Decoding operator
			if(instructions[i].startsWith(" AND") && !first) {
				op = Operator.AND;
			}
			else if(instructions[i].startsWith(" OR") && !first) {
				op = Operator.OR;
			}

			// Decoding query
			if(instructions[i].contains("DIFFERENT")) {
				q = Query.DIFFERENT;
			}
			else if(instructions[i].contains("EQUAL")) {
				q = Query.EQUAL;
			}
			else if(instructions[i].contains("LESS THAN")) {
				q = Query.LESS_THAN;
			}
			else if(instructions[i].contains("LESS THAN OR EQUAL TO")) {
				q = Query.LESS_THAN_OR_EQUAL_TO;
			}
			else if(instructions[i].contains("GREATER THAN")) {
				q = Query.GREATER_THAN;
			}
			else if(instructions[i].contains("GREATER THAN OR EQUAL TO")) {
				q = Query.GREATER_THAN_OR_EQUAL_TO;
			}
			else throw new InvalidParameterException("\"" + instructions[i] + "\"" + ": Syntax error for query");

			// Find the constant with its begin and end index in the String
			int endCstIndex = query.length();
			if(i+1 < instructions.length) endCstIndex = query.indexOf(instructions[i+1]);

			cst = Integer.decode(query.substring(cstIndex, endCstIndex));

			cstIndex += cst.toString().length();

			// Case when we have to do a query and add it logically (AND, OR) to the global result
			// We check if an operator, a constant and a query has been found
			if(op!=null && q!= null && cst != null && BVout != null) {

				// Performing query
				long[] newBVout = query(q, cst);

				// Adding logically this query to the global result
				switch(op) {

				case AND:
					for(int n = 0; n < BVout.length; ++n) {
						BVout[n] &= newBVout[n];
					}
					break;

				case OR:
					for(int n = 0; n < BVout.length; ++n) {
						BVout[n] |= newBVout[n];
					}
					break;

				default:
					throw new InvalidParameterException("\"" + instructions[i] + "\"" + ": Error for this query");
				}

				// Invalidate the used query, operator and constant
				op = null;
				q = null;
				cst = null;
			}
			// Case when just a query has to be done (first instruction)
			// We check if a constant and a query has been found
			else if(q!= null && cst != null && first) {

				// Perform the first query
				BVout = query(q, cst);

				// Invalidate the used query and constant
				first = false;
				op = null;
				q = null;
				cst = null;
			}
		}

		return BVout;
	}
}
