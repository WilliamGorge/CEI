
public class BitWeavingV implements BitWeavingVInterface
{
	private BWVSegment[] column; 			// Processor words
	private int k;							// Size of one datum
	private int Sno;
	private int w;
	private int nbData;
	private long[] result;

	public BitWeavingV() 
	{
		k = 0;
		Sno = 0;
		w = 0;
		nbData = 0;
		result = null;
		column = null;
	}

	public BitWeavingV(long[] dataArray, int sizeOfOneDatum, int wOfWord)
	{
		k = sizeOfOneDatum;
		w = wOfWord;
		nbData = dataArray.length;

		int i, j, n = nbData, p = nbData % w;
		Sno = (p > 0) ? nbData/w+1 : nbData/w;

		column = new BWVSegment[Sno];
		long[] tempArray = new long[w];

		for (i = 0; i < Sno - 1; ++i)
		{
			for (j = 0; j < w; ++j)
				tempArray[j]= dataArray[w*i+j];
			
			column[i] = new BWVSegment(tempArray, sizeOfOneDatum, wOfWord);
		}

		p = (p != 0) ? p : w;

		tempArray = new long[p];

		for (j = 0; j < p; ++j)
			tempArray[j] = dataArray[(Sno-1)*w+j];

		column[Sno-1] = new BWVSegment(tempArray, sizeOfOneDatum, wOfWord);
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
	}

	public long[] query(Query queryName, long cst)
	{	
		long[] cstTab = new long[k];
		long mask = ( 1L << (k-1) );
		int i = 0;
		
		if ( ((k < 64) &&  cst > mask - 1 + mask) || k >= 64 )
		{
			System.out.println(cst + " ne peut pas être codé en " + k + " bits.\n");
			return null;
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
		
		return result;
	}

	private long[] greaterThan(long[] cstTab)
	{
		for (int i = 0; i < Sno; ++i)
		{
			result[i] = column[i].greaterThan(cstTab);
			column[i].recharge();
		}
		
		return result;
	}

	private long[] equalTo(long[] cstTab)
	{
		for (int i = 0; i < Sno; ++i)
		{
			result[i] = column[i].equalTo(cstTab);
			column[i].recharge();
		}

		return result;
	}

	private long[] differentTo(long[] cstTab)
	{
		for (int i = 0; i < Sno; ++i)
		{
			result[i] = column[i].differentTo(cstTab);
			column[i].recharge();
		}

		return result;
	}

	private long[] lessThanOrEqualTo(long[] cstTab)
	{
		for (int i = 0; i < Sno; ++i)
		{
			result[i] = column[i].lessThanOrEqualTo(cstTab);
			column[i].recharge();
		}

		return result;
	}

	private long[] greaterThanOrEqualTo(long[] cstTab)
	{
		for (int i = 0; i < Sno; ++i)
		{
			result[i] = column[i].greaterThanOrEqualTo(cstTab);
			column[i].recharge();
		}

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
}
