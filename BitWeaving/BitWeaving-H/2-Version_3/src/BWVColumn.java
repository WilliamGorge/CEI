import java.util.ArrayList;

public class BWVColumn extends BWColumn
{
	private String name;
	private ArrayList<BWVSegment> column; 			// Processor words
	private int k;							// Size of one datum
	private int Sno;
	private int w;
	private int nbData;
	
	// Ajouts
	private long maskout; // Masks the bits outside the theorical processor word
	private int nbDataLastSegment; // Number of data in the last segment


	public BWVColumn(String name, int sizeOfOneDatum, int widthOfWord)
	{
		k = sizeOfOneDatum;
		w = widthOfWord;
		this.name = name;
		nbData = 0;
		column = new ArrayList<BWVSegment>();
		nbDataLastSegment = 0;
		
		Sno = 0;
		
		// Mask that have w times 1, it is used to mask what's outside the procesor word
		maskout =  1;
		for(int i = 1; i < w; ++i) {
			maskout <<= 1;
			maskout |= 1L;
		}

	}
	
	public ArrayList<BWVSegment> getColumn()
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
			throw new IllegalArgumentException(nb + " ne peut pas être codé en " + k + " bits.\n");
		}
		
		if (nbData % w != 0) {
			column.get(Sno-1).add(nb);
			++nbDataLastSegment;
		}
			
		else
		{				
			BWVSegment someSegment = new BWVSegment(k, w);
			someSegment.add(nb);
			column.add(someSegment);
			nbDataLastSegment = 1;
			++Sno;
		}
	
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

	public BitVector query(Query queryName, long cst) throws IllegalArgumentException
	{	
		long[] cstTab = new long[k];
		long mask = ( 1L << (k-1) );
		int i = 0;
		
		if ((k < 63 && cst >= (1L << k)) || (k >= 63 && cst > (1L << 62) - 1 + (1L << 62)) || (k > 64))
		{
			throw new IllegalArgumentException(cst + " ne peut pas être codé en " + k + " bits.\n");
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
				throw new IllegalArgumentException("No such query.");
		}
	}
	
	
	public BitVector query(Query queryName, long cst1, long cst2) throws IllegalArgumentException
	{	
		long[] cstTab1 = new long[k];
		long[] cstTab2 = new long[k];
		long mask = ( 1L << (k-1) );
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
			if ( (mask & cst1) != 0 )
				cstTab1[i] = ~0;
			else
				cstTab1[i] = 0;
			
			if ( (mask & cst2) != 0 )
				cstTab2[i] = ~0;
			else
				cstTab2[i] = 0;
			
			mask >>>= 1;
			++i;
		}
		
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

	private BitVector lessThan(long[] cstTab)
	{
		BitVector result = new BitVector();
		for (int i = 0; i < Sno; ++i)
		{
			result.append(column.get(i).lessThan(cstTab), w);
			column.get(i).recharge();
		}
		result.deleteEnd(w - nbDataLastSegment);
		return result;
	}

	private BitVector greaterThan(long[] cstTab)
	{
		BitVector result = new BitVector();
		for (int i = 0; i < Sno; ++i)
		{
			result.append(column.get(i).greaterThan(cstTab), w);
			column.get(i).recharge();
		}
		result.deleteEnd(w - nbDataLastSegment);
		return result;
	}
	
	private BitVector between(long[] cstTab1, long[] cstTab2)
	{
		BitVector result = new BitVector();
		for (int i = 0; i < Sno; ++i)
		{
			result.append(column.get(i).between(cstTab1, cstTab2), w);
			column.get(i).recharge2();
		}
		result.deleteEnd(w - nbDataLastSegment);
		return result;
	}

	private BitVector equalTo(long[] cstTab)
	{
		BitVector result = new BitVector();
		for (int i = 0; i < Sno; ++i)
		{
			result.append(column.get(i).equalTo(cstTab), w);
			column.get(i).recharge();
		}
		result.deleteEnd(w - nbDataLastSegment);
		return result;
	}

	private BitVector differentTo(long[] cstTab)
	{
		BitVector result = new BitVector();
		for (int i = 0; i < Sno; ++i)
		{
			result.append(column.get(i).differentTo(cstTab), w);
			column.get(i).recharge();
		}
		result.deleteEnd(w - nbDataLastSegment);
		return result;
	}

	private BitVector lessThanOrEqualTo(long[] cstTab)
	{
		BitVector result = new BitVector();
		for (int i = 0; i < Sno; ++i)
		{
			result.append(column.get(i).lessThanOrEqualTo(cstTab), w);
			column.get(i).recharge();
		}
		result.deleteEnd(w - nbDataLastSegment);
		return result;
	}

	private BitVector greaterThanOrEqualTo(long[] cstTab)
	{
		BitVector result = new BitVector();
		for (int i = 0; i < Sno; ++i)
		{
			result.append(column.get(i).greaterThanOrEqualTo(cstTab), w);
			column.get(i).recharge();
		}
		result.deleteEnd(w - nbDataLastSegment);
		return result;
	}

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