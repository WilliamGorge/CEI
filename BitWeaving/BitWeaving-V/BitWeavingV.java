
public class BitWeavingV 
{
	private BWV_Segment[] column; 	// Processor words
	private int k;					// Size of one datum
	private int w;					// Size of processor word
	private int Sno;
	private int bit_group_no;
	private int word_per_bit_group_no;
	private int real_width;

	public BitWeavingV() 
	{
		k = 0;
		w = 0;
		Sno = 0;
		bit_group_no = 0;
		real_width = 0;
		word_per_bit_group_no = 0;
		column = null;
	}

	public BitWeavingV(long[] data_array, int size_of_one_datum, int size_of_processor_word, int bit_group_no_)
	{
		k = size_of_one_datum;
		w = size_of_processor_word;
		bit_group_no = bit_group_no_;
		word_per_bit_group_no = k/bit_group_no;
		real_width = w/(word_per_bit_group_no);

		int i, j, n = data_array.length, p = n % real_width;
		Sno = (p > 0) ? n/real_width + 1 : n/real_width;
	
		column = new BWV_Segment[Sno];
		long[] temp_array = new long[real_width];

		for (i = 0; i < Sno - 1; ++i)
		{
			for (j = 0; j < real_width; ++j)
				temp_array[j]= data_array[i*real_width + j];
			
			column[i] = new BWV_Segment(temp_array, k, w, bit_group_no);
		}
		
		p = (p != 0) ? p : real_width;
		
		temp_array = new long[p];

		for (j = 0; (Sno - 1)*real_width + j < n; ++j)
			temp_array[j] = data_array[(Sno - 1)*real_width + j];

		column[Sno-1] = new BWV_Segment(temp_array, k, w, bit_group_no);
	}
	
	public long[] query(Query queryName, long nb)
	{	
		switch (queryName)
		{
			case LESS_THAN:
				return less_than(nb);
			
			case GREATER_THAN:
				return greater_than(nb);
			
			case EQUAL_TO:
				return equal_to(nb);
			
			case DIFFERENT_TO:
				return different_to(nb);
			
			case LESS_THAN_OR_EQUAL_TO:
				return less_than_or_equal_to(nb);
			
			case GREATER_THAN_OR_EQUAL_TO:
				return greater_than_or_equal_to(nb);
			
			default:
				System.out.println("No such query.");
				break;
		}
		
		return null;
	}
	
	public long[] less_than(long nb)
	{
		long[] result = new long[Sno];
		
		for (int i = 0; i < Sno; ++i)
			result[i] = column[i].less_than(nb);

		return result;
	}
	
	public long[] greater_than(long nb)
	{
		long[] result = new long[Sno];
		
		for (int i = 0; i < Sno; ++i)
			result[i] = column[i].greater_than(nb);

		return result;
	}
	
	public long[] equal_to(long nb)
	{
		long[] result = new long[Sno];
		
		for (int i = 0; i < Sno; ++i)
			result[i] = column[i].equal_to(nb);

		return result;
	}
	
	public long[] different_to(long nb)
	{
		long[] result = new long[Sno];
		
		for (int i = 0; i < Sno; ++i)
			result[i] = column[i].different_to(nb);

		return result;
	}
	
	public long[] less_than_or_equal_to(long nb)
	{
		long[] result = new long[Sno];
		
		for (int i = 0; i < Sno; ++i)
			result[i] = column[i].less_than_or_equal_to(nb);

		return result;
	}
	
	public long[] greater_than_or_equal_to(long nb)
	{
		long[] result = new long[Sno];
		
		for (int i = 0; i < Sno; ++i)
			result[i] = column[i].greater_than_or_equal_to(nb);

		return result;
	}
		
	public void display(long result, int length)
	{
		long mask;
		
		for (int j = 0; j < length; ++j)
		{
			mask = result & (1L << j);
			mask >>>= j;
			System.out.print(mask);
		}
	}
}
