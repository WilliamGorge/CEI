
/**
 * Class for the use of the BitWeavingV Method, more specifically for the column segments.<br>
 * An instance of this class represents a segmement of data according to the BitWeaving method.<br>
 * It deals with the processor words: they are construced, returned, updated, etc...<br>
 * @author Benoit Sordet
 */

public class BWVSegment 
{
	private long[] word;		// Processor words
	
	// Constants of the segment
	private int k;			// Size of one datum
	private int w;			// Size of processor word
	
	// Variables of the segment
	private int wordItinerator;	// Iterator for words
	private int Ls;			// Number of data in the segment
	private long mEq;		// long to determine the data already treated
	private long res;		// long to be returned

	/** 
	 * Constructs the segment given its elements in columnsegment and other parameters.
	 * @param sizeOfOneDatum size (in bits) of one data in the column
	 * @param widthOfWord size of the processor word
	 */
	public BWVSegment(int sizeOfOneDatum, int widthOfWord)
	{
		// Copying the arguments
		k = sizeOfOneDatum;
		w = widthOfWord;
		
		// Initializing the variables.
		// There is no datum in the segment yet, so Ls = 0
		Ls = 0;
		// Default value of the returned result
		res = 0;
		// Word iterator starts at 0
		wordItinerator = 0;
		// As there is no datum in the segment yet, mEq has no 1
		mEq = 0;

		// Creating the word array
		word = new long[k];
	}
	
	/**
	 * Adds one datum to the segment
	 * @param datum: datum to add<br><br>
	 * @author Benoit Sordet
	 */
	public void add(long nb)
	{
		int i;
		// Masks that will be used to select the appropriate bit at every iteration
		long mask, move = ( 1L << (k - 1) );
		
		for (i = 0; i < k; ++i)
		{
			// mask takes the i_th bit of move
			mask = nb & move;
			// If that bit is 1, we assign 1 to mask, otherwise 0. This is faster than bit shifting
			mask = (mask != 0) ? 1 : 0;
			// We shift 1 or 0 to the appropriate position (that is, the location of the new datum in the word, which happens to be Long.SIZE-Ls-1)
			mask <<= (Long.SIZE-Ls-1);
			// We put the bit on word[i]
			word[i] |= mask;
			// Refreshing move for the next iteration
			move >>>= 1;
		}
		
		// Refreshing Ls
		++Ls;
		
		// Refreshing mEq
		// mEq has to be equal to 1^(Ls)0^(Long.SIZE-Ls)
		// Rather than putting all 1s on the left of mEq, it's easier to put (Long.SIZE-Ls) 1 on the right, then use the ~ operator
		// If the segment isn't full
		if (Ls < w)
			// Do what we just said : put (Long.SIZE-Ls) 1 on the right, then use the ~ operator
			mEq = ~( ( 1L << (Long.SIZE - Ls) ) - 1 );
		
		// Else the segment is full
		else
			// If w happens to be Long.SIZE, we must put 1 everywhere
			// Else we only put 1 w times on the left, once again by putting (Long.SIZE-Ls) 1 on the right first, then using the ~ operator
			mEq = (w == Long.SIZE) ? ~0 : ~( ( 1L << (Long.SIZE - Ls) ) - 1 );
	}
	
	/*
	public void add(long[] nb)
	{
		int i, j, Lss = Ls + nb.length;
		long mask, move = ( 1L << (k - 1) );
		
		for (i = 0; i < k; ++i)
		{ 	
			for (j = Ls; j < Lss; ++j)
			{
<<<<<<< HEAD
				// On met a 0 tous les bits de dataArray[j] sauf le (k-1-i) ieme, et l'on affecte le tout a mask.
				mask = nb[j-Ls] & move;
				// On divise par 2 autant qu'il le faut pour que le resultat soit dans le premier bit.
				mask = (mask != 0) ? 1 : 0;
				// On deplace ensuite le bit vers l'emplacement approprie dans le but de l'ajouter a v[i].
				mask <<= (Long.SIZE-j-1);
				// On ajoute le resultat a v[i].
=======
				// On met � 0 tous les bits de dataArray[j] sauf le (k-1-i) i�me, et l'on affecte le tout � mask.
				mask = nb[j-Ls] & move;
				// On divise par 2 autant qu'il le faut pour que le r�sultat soit dans le premier bit.
				mask = (mask != 0) ? 1 : 0;
				// On d�place ensuite le bit vers l'emplacement appropri� dans le but de l'ajouter � v[i].
				mask <<= (Long.SIZE-j-1);
				// On ajoute le r�sultat � v[i].
>>>>>>> FETCH_HEAD
				word[i] |= mask;
			}
			
			move >>>= 1;
		}
		
		Ls = Lss;
		
		if (Ls < w)
			mEq = ~( ( 1L << (Long.SIZE - Ls) ) - 1 );
		else
			mEq = (w == Long.SIZE) ? ~0 : ~( ( 1L << (Long.SIZE - Ls) ) - 1 );
	}
	*/
	
	/**
	 * Gets the processor words for this segment
	 * @return processor words
	 */
	public long[] getProcessorWords()
	{
		return word;
	}
	
	/**
	 * For display.
	 * @param result: the long to display
	 * @param length: the number of bits to display (starting from the left)
	 * @author Benoit Sordet
	 */
	private void display(long result, int length)
	{
		long mask;

		for (int j = 0; j < length; ++j)
		{
			mask = result & (1L << (Long.SIZE-1-j));
			mask = (mask != 0) ? 1 : 0;
			System.out.print(mask);
		}
		
		System.out.println();
	}
	
	/**
	 * Re-initialize variables.
	 * @author Benoit Sordet
	 */
	public void recharge()
	{
		res = 0;
		wordItinerator = 0;		
			
		// Refreshing mEq
		// mEq has to be equal to 1^(Ls)0^(Long.SIZE-Ls)
		// Rather than putting all 1s on the left of mEq, it's easier to put (Long.SIZE-Ls) 1 on the right, then use the ~ operator
		// If the segment isn't full
		if (Ls < w)
			// Do what we just said : put (Long.SIZE-Ls) 1 on the right, then use the ~ operator
			mEq = ~( ( 1L << (Long.SIZE - Ls) ) - 1 );
		
		// Else the segment is full
		else
			// If w happens to be Long.SIZE, we must put 1 everywhere
			// Else we only put 1 w times on the left, once again by putting (Long.SIZE-Ls) 1 on the right first, then using the ~ operator
			mEq = (w == Long.SIZE) ? ~0 : ~( ( 1L << (Long.SIZE - Ls) ) - 1 );
	}	

	/*** CORE FUNCTION OF THE QUERY "X < cst" ***/
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

	/*** CORE FUNCTION OF THE QUERY "X > cst" ***/
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
	
	/*** CORE FUNCTION OF THE QUERY "cst1 <= X <= cst2" ***/
	public long between(long[] cst1, long[] cst2)
	{
		long res2 = 0;
		long mEq2 = mEq;
		
		while ((mEq != 0 || mEq2 != 0) && wordItinerator < k)
		{
			res |= ( mEq & (~word[wordItinerator] & cst1[wordItinerator]) );
			mEq &= ~(cst1[wordItinerator] ^ word[wordItinerator]);
			res2 |= ( mEq2 & ( ~cst2[wordItinerator] & word[wordItinerator] ) );
			mEq2 &= ~(cst2[wordItinerator] ^ word[wordItinerator]);
			++wordItinerator;
		}
		
		return ~(res|res2);
	}

	/*** CORE FUNCTION OF THE QUERY "X = cst" ***/
	public long equalTo(long[] cst)
	{
		while (mEq != 0 && wordItinerator < k)
		{
			mEq &= ~(cst[wordItinerator] ^ word[wordItinerator]);
			++wordItinerator;
		}

		return mEq;
	}

	/*** CORE FUNCTION OF THE QUERY "X != cst" ***/
	public long differentTo(long[] cst)
	{
		while (mEq != 0 && wordItinerator < k)
		{
			mEq &= ~(cst[wordItinerator] ^ word[wordItinerator]);
			++wordItinerator;
		}

		return ~mEq;
	}

	/*** CORE FUNCTION OF THE QUERY "X <= cst" ***/
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

	/*** CORE FUNCTION OF THE QUERY "X >= cst" ***/
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
