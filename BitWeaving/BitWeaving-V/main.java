
public class main 
{
	public static void main(String[] args) 
	{
		int i, j, N = 3000000, bit_group_no = 4;
		long[] tab = new long[N];
		int k = 16, w = 64;
		long power = (1L << k); 
		long time;
			
		for (i = 0; i < N; ++i)
			tab[i] = ((int) (Math.random()*power)) % power;
		
			BitWeavingV chose = new BitWeavingV(tab, 16, w, 1);
			
			time = System.nanoTime();
			long[] result = chose.less_than_or_equal_to(120);
			System.out.println((System.nanoTime() - time)*1./N);
			eval(tab, result, Query.LESS_THAN_OR_EQUAL_TO, 200, k, w, bit_group_no);
		
	
	}
	
	public static void eval(long tab[], long[]result, Query queryName, long nb, int k, int w, int bit_group_no)
	{
		int real_width = w/(k/bit_group_no);
		
		for (int n = 0; n < tab.length; ++n)
		{
			if (queryName == Query.LESS_THAN)
				if ((tab[n] < nb && ( result[n/real_width] & (1L << (n % real_width) ) ) == 0) || (tab[n] >= nb && ( result[n/real_width] & (1L << (n % real_width) ) ) != 0))
					System.out.println(n);
			
			else if (queryName == Query.GREATER_THAN)
				if ((tab[n] > nb && ( result[n/real_width] & (1L << (n % real_width) ) ) == 0) || (tab[n] < nb && ( result[n/real_width] & (1L << (n % real_width) ) ) != 0))
					System.out.println(n);
			
			else if (queryName == Query.EQUAL_TO)
				if ((tab[n] == nb && ( result[n/real_width] & (1L << (n % real_width) ) ) == 0) || (tab[n] != nb && ( result[n/real_width] & (1L << (n % real_width) ) ) != 0))
					System.out.println(n);
			
			else if (queryName == Query.DIFFERENT_TO)
				if ((tab[n] != nb && ( result[n/real_width] & (1L << (n % real_width) ) ) == 0) || (tab[n] == nb && ( result[n/real_width] & (1L << (n % real_width) ) ) != 0))
					System.out.println(n);
			
			else if (queryName == Query.LESS_THAN_OR_EQUAL_TO)
				if ((tab[n] <= nb && ( result[n/real_width] & (1L << (n % real_width) ) ) == 0) || (tab[n] > nb && ( result[n/real_width] & (1L << (n % real_width) ) ) != 0))
					System.out.println(n);
			
			else if (queryName == Query.GREATER_THAN_OR_EQUAL_TO)
				if ((tab[n] >= nb && ( result[n/real_width] & (1L << (n % real_width) ) ) == 0) || (tab[n] < nb && ( result[n/real_width] & (1L << (n % real_width) ) ) != 0))
					System.out.println(n);
		}
		
		System.out.println("Fin");
	}
}
