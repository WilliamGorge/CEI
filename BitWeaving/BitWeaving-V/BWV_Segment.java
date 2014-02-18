
public class BWVSegment 
{

	private long[] word;	 		// Processor words
	private int k;					// Size of one datum
	private int w;
	private int wordItinerator;
	private int Ls;
	private long mEq;
	private long res;

	// Default constructor
	public BWVSegment() 
	{
		k = 0;
		Ls = 0;
		w = 0;
		word = null;
	}

	// Real constructor
	public BWVSegment(long[] dataArray, int sizeOfOneDatum, int widthOfWord)
	{
		k = sizeOfOneDatum;
		Ls = dataArray.length;
		res = 0;
		w = widthOfWord;
		wordItinerator = 0;
		
		if (Ls == 1)
			mEq = ( 1L << (w-1) );
		else if (Ls < w)
			mEq = ~( ( 1L << (w - Ls) ) - 1 );
		else
			mEq = ~0;

		int i, j;
		word = new long[k];
		long mask, move = ( 1L << (k - 1) );

		// Pour les mots processeurs de 1 à k.
		for (i = 0; i < k; ++i)
		{ 	
			word[i] = 0;	// On remplit word[i] de 0.

			for (j = 0; j < Ls; ++j)
			{
				// On met à 0 tous les bits de dataArray[j] sauf le (k-1-i) ième, et l'on affecte le tout à mask.
				mask = dataArray[j] & move;
				// On divise par 2 autant qu'il le faut pour que le résultat soit dans le premier bit.
				mask = (mask != 0) ? 1 : 0;
				// On déplace ensuite le bit vers l'emplacement approprié dans le but de l'ajouter à v[i].
				mask <<= (w-j-1);
				// On ajoute le résultat à v[i].
				word[i] |= mask;
			}
			
			move >>>= 1;
		}
	}
	
	public void add(long nb)
	{
		int i;
		long mask, move = ( 1L << (k - 1) );
		
		for (i = 0; i < k; ++i)
		{
			mask = nb & move;
			mask = (mask != 0) ? 1 : 0;
			mask <<= (w-Ls-1);
			word[i] |= mask;
			move >>>= 1;
		}
		
		++Ls;
		
		if (Ls < w)
			mEq = ~( ( 1L << (w - Ls) ) - 1 );
		else
			mEq = ~0;
	}
	
	public long[] getProcessorWords()
	{
		return word;
	}
	
	private void display(long result, int length)
	{
		long mask;

		for (int j = 0; j < length; ++j)
		{
			mask = result & (1L << (w-1-j));
			mask = (mask != 0) ? 1 : 0;
			System.out.print(mask);
		}
		
		System.out.println();
	}
	
	public void recharge()
	{
		res = 0;
		mEq = (Ls < w) ? (1L << Ls) - 1 : ~0;
		wordItinerator = 0;
	}	

	public long lessThan(long[] cst)
	{		
		while (mEq != 0 && wordItinerator < k)
		{
			res |= ( mEq & (~word[wordItinerator] & cst[wordItinerator]) );
			mEq &= ~(cst[wordItinerator] ^ word[wordItinerator]);
			++wordItinerator;
		}

		return res;
	}

	public long greaterThan(long[] cst)
	{
		while (mEq != 0 && wordItinerator < k)
		{
			res |= ( mEq & ( ~cst[wordItinerator] & word[wordItinerator] ) );
			mEq &= ~(cst[wordItinerator] ^ word[wordItinerator]);
			++wordItinerator;
		}

		return res;
	}

	public long equalTo(long[] cst)
	{
		while (mEq != 0 && wordItinerator < k)
		{
			mEq &= ~(cst[wordItinerator] ^ word[wordItinerator]);
			++wordItinerator;
		}

		return mEq;
	}

	public long differentTo(long[] cst)
	{
		while (mEq != 0 && wordItinerator < k)
		{
			mEq &= ~(cst[wordItinerator] ^ word[wordItinerator]);
			++wordItinerator;
		}

		return ~mEq;
	}

	public long lessThanOrEqualTo(long[] cst)
	{
		while (mEq != 0 && wordItinerator < k)
		{
			res |= ( mEq & ( ~cst[wordItinerator] & word[wordItinerator] ) );
			mEq &= ~( cst[wordItinerator] ^ word[wordItinerator] );
			++wordItinerator;
		}

		return ~res;
	}

	public long greaterThanOrEqualTo(long[] cst)
	{
		while (mEq != 0 && wordItinerator < k)
		{
			res |= ( mEq & ( ~word[wordItinerator] & cst[wordItinerator] ) );
			mEq &= ~(cst[wordItinerator] ^ word[wordItinerator]);
			++wordItinerator;
		}
		
		return ~res;
	}
}
