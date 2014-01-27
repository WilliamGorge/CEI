import java.security.InvalidParameterException;

public class BitWeavingH {
	
	private BWH_Segment[] column; // Colomn on witch perform the scan
	private int k;  // Size of one data
	private int w;  // Size of processor word
	private int N;	// Number of data that can fit in a processor word
	private int Ls; // Length of one segment
	private long mask; // Mask for the query less than
	private long maskout; // Masks the bits outside the theorical processor word
	private int NbFullSegments; // Number of segments that contains Ls data
	private int rest; // Number of data in the last segment
	private int NbSegments; // Number of segments
	private int NbProcWordsLastSegmt; // Number of processor words in the last segment
	private long maskresultLastSegmt; // Mask to obtain the result for the last segment
	private int nbZP; // Number of the zero added in the zerop padding
	
	/**
	 * Default constructor
	 */
	public BitWeavingH() {
		column = null;
	}
	
	/**
	 * Constructor with a column and a data size given
	 * @param col column attatched to the object
	 * @param sizeofonedata size (in bits) of one data in the column
	 * @param sizeofprocessorword size of the processor word
	 */
	public BitWeavingH(long[] col, int sizeofonedata, int sizeofprocessorword) {
		
		// Instaciation of the arguments
		k = sizeofonedata;
		w = sizeofprocessorword;
		N = w/(k+1);
		Ls = N*(k+1);
		nbZP = (w-N*(k+1));
		
		// masktemp is for one data in the processor word ie 0111...1 (k times 1)
		long masktemp = 1;
		for(int i = 1; i < k; ++i) {
			masktemp <<= 1;
			masktemp |= 1;
		}
		// "mask" is the mask in the article ie N times masktemp (0111...1 0111...1 0111...1 0111...1 ...)
		for(int i = 0; i < N; ++i) {
			mask <<= (k+1);
			mask |= masktemp;
		}
		// Let us do some zero padding to this mask, in case
		mask <<= nbZP;
		
		// Mask that have w times 1, it is used to mask what's outside the procesor word
		maskout =  1;
		for(int i = 1; i < w; ++i) {
			maskout <<= 1;
			maskout |= 1;
		}
		
		// Number of full segments
		NbFullSegments = col.length/Ls;
		
		// Number of remaining data in the last segment
		rest = col.length - NbFullSegments*Ls;
		
		// Creation of the segment array
		if(rest == 0) this.column = new BWH_Segment[NbFullSegments]; // Case when Ls divides the length of the column
		else this.column = new BWH_Segment[NbFullSegments+1];	// Case if not: we have to add another segment
		
		// Itterate for all the segments
		for(int i = 0; i < NbFullSegments; ++i) {
			
			// Copy from the column to the segment
			long [] segmt = new long[Ls];
			for(int j = 0; j < Ls; ++j) {
				segmt[j] = col[i*Ls+j];
			}
			
			// BWH Segment creation (creation of processor words)
			this.column[i] = new BWH_Segment(segmt, k, w, true);
		}
		
		// We sould not forget the possible rest of the segmentation: the last segment
		if(rest > 0) {
			
			long [] segmt = new long[rest];
			for(int j = 0; j < rest; ++j) {
				segmt[j] = col[NbFullSegments*Ls + j];
			}
			
			// BWH Segment creation (creation of processor words)
			this.column[NbFullSegments] = new BWH_Segment(segmt, k, w, false);
		}
		
		// Number of segments
		NbSegments = column.length; // Warning: here column.length is the number of segments
		
		// Number of processor words in the last segment 
		NbProcWordsLastSegmt = column[NbSegments-1].getProcessorWords().length;
		
		// Mask the obtain the results for the last segment
		maskresultLastSegmt = 1;
		for(int i = 1; i < NbProcWordsLastSegmt; ++i) {
			maskresultLastSegmt <<= 1;
			maskresultLastSegmt |= 1;
		}
		maskresultLastSegmt <<= (k+1-NbProcWordsLastSegmt);
		
	}
	
	/**
	 * Gets the attatched column
	 * @return column
	 */
	public BWH_Segment[] getColumn() {
		return column;
	}
	
	/**
	 * Gets the number of data that remains in the last incomplete segment of the column
	 * @return rest
	 */
	public int getRest() {
		return rest;
	}
	
	/**
	 * Gets the Number of full segments
	 * @return NbFullSegments
	 */
	public int getNbFullSegments() {
		return NbFullSegments;
	}
	

	/*** CORE FUNCTION OF THE QUERY "X != cst" ***/
	// This method is the f!=(X,C) of the article (3.2.2)
	private long f_different(long X, long Y) {
		
		// Computing the result
		long Z = (X ^ Y) + mask ;
		Z = Z  & (~mask);
		
		// Return the result
		return 	Z;
	}
	
	/*** CORE FUNCTION OF THE QUERY "X = cst" ***/
	// This method is the f=(X,C) of the article (3.2.2)
	private long f_equal(long X, long Y) {
		
		// Computing the result
		long Z = (X ^ Y) + mask;
		Z = (~Z)  & (~mask) & maskout;
		
		// Return the result
		return 	Z;
	}
	
	
	/*** CORE FUNCTION OF THE QUERY "X < cst" (used for X<= cst too since it is equivalent to X < cst+1) ***/
	// This method is the f<(X,C) of the article and in the slides
	// The algorithm is explained at "Figure 4" of the article
	private long f_less_than(long X, long Y) {
		
		// Computing the result
		long Z = X ^ mask;
		Z = Y + Z;
		Z = Z  & (~mask);
		
		// Return the result
		return 	Z;
	}
	
	/*** QUERY FUNCTION ***/
	/**
	 * Performs the query queryName with the constant cst on the column of the instance of the BitWeavingH object
	 * @param query: number of the query:
		<blockquote>
			1: DIFFERENT<br>
			2: EQUAL<br>
			3: LESS THAN<br>
			4: LESS THAN OR EQUAL TO<br>
			5: GREATER THAN<br>
			6: GREATER THAN OR EQUAL TO<br>
		</blockquote>
		cst: Constant to compare<br><br>
	 * @throws InvalidParameterException
	 * @author William Gorge
	 */
	public long[] query(int query, long cst) throws InvalidParameterException {
	
		/*** INITIALIATION ***/

		// Result vector
		long[] BVout = new long[NbSegments]; 

		long Y;
		
		/*** COMPUTING LOOPS (same for each query) ***/
		// Itterating on all the segments
		// This algorith is the "Alogrithm 1" of the article
		switch(query) {
		
			// DIFFERENT
			case 1:
				
				// Construcion of the comparaison vector
				Y = cst;
				for(int i = 1; i < N; ++i) {
					Y <<= k+1;
					Y |= cst;
				}
				
				// Let us do some zero padding to Y, in case
				Y <<= nbZP;
				
				for(int n = 0; n < NbSegments; ++n) {
					long ms = 0;
					for(int i = 0; i < column[n].getProcessorWords().length; ++i) { // Warning: column[n].getProcessorWords().length returns the number of processor words for one segment
						long mw = f_different(column[n].getProcessorWords()[i], Y);
						mw >>>= i;
						ms |= mw;
					}
					ms >>= nbZP; // Deleting the zero padding
					BVout[n] = ms;
				}
				break;
			
			// EQUAL
			case 2:
				
				// Construcion of the comparaison vector
				Y = cst;
				for(int i = 1; i < N; ++i) {
					Y <<= k+1;
					Y |= cst;
				}
				
				// Let us do some zero padding to Y, in case
				Y <<= nbZP;
				
				for(int n = 0; n < NbSegments; ++n) {
					long ms = 0;
					for(int i = 0; i < column[n].getProcessorWords().length; ++i) { // Warning: column[n].getProcessorWords().length returns the number of processor words for one segment
						long mw = f_equal(column[n].getProcessorWords()[i], Y);
						mw >>>= i;
						ms |= mw;
					}
					ms >>= nbZP; // Deleting the zero padding
					BVout[n] = ms;
				}
				break;
				
			
			// LESS THAN
			case 3:
				
				// Construcion of the comparaison vector
				Y = cst;
				for(int i = 1; i < N; ++i) {
					Y <<= k+1;
					Y |= cst;
				}
				
				// Let us do some zero padding to Y, in case
				Y <<= nbZP;
				
				for(int n = 0; n < NbSegments; ++n) {
					long ms = 0;
					for(int i = 0; i < column[n].getProcessorWords().length; ++i) { // Warning: column[n].getProcessorWords().length returns the number of processor words for one segment
						long mw = f_less_than(column[n].getProcessorWords()[i], Y);
						mw >>>= i;
						ms |= mw;
					}
					ms >>= nbZP; // Deleting the zero padding
					BVout[n] = ms;
				}
				break;
				
				
			// LESS THAN OR EQUAL TO
			case 4:
				
				// Construcion of the comparaison vector
				Y = cst + 1; // X <= Y is equivalent to X < Y + 1
				for(int i = 1; i < N; ++i) {
					Y <<= k+1;
					Y |= cst + 1;
				}
				
				// Let us do some zero padding to Y, in case
				Y <<= nbZP;
				
				for(int n = 0; n < NbSegments; ++n) {
					long ms = 0;
					for(int i = 0; i < column[n].getProcessorWords().length; ++i) { // Warning: column[n].getProcessorWords().length returns the number of processor words for one segment
						long mw = f_less_than(column[n].getProcessorWords()[i], Y);
						mw >>>= i;
						ms |= mw;
					}
					ms >>= nbZP; // Deleting the zero padding
					BVout[n] = ms;
				}
				break;
			
			// GREATER THAN
			case 5:
				
				// Construcion of the comparaison vector
				Y = cst; 
				for(int i = 1; i < N; ++i) {
					Y <<= k+1;
					Y |= cst;
				}
				
				// Let us do some zero padding to Y, in case
				Y <<= nbZP;
				
				for(int n = 0; n < NbSegments; ++n) {
					long ms = 0;
					for(int i = 0; i < column[n].getProcessorWords().length; ++i) { // Warning: column[n].getProcessorWords().length returns the number of processor words for one segment
						long mw = f_less_than(Y, column[n].getProcessorWords()[i]);
						mw >>>= i;
						ms |= mw;
					}
					ms >>= nbZP; // Deleting the zero padding
					BVout[n] = ms;
				}
				break;
				
				
			// GREATER THAN OR EQUAL TO
			case 6:
				
				// Construcion of the comparaison vector
				Y = cst - 1; // X >= Y is equivalent to X > Y - 1
				for(int i = 1; i < N; ++i) {
					Y <<= k+1;
					Y |= cst - 1;
				}
				
				// Let us do some zero padding to Y, in case
				Y <<= nbZP;
				
				for(int n = 0; n < NbSegments; ++n) {
					long ms = 0;
					for(int i = 0; i < column[n].getProcessorWords().length; ++i) { // Warning: column[n].getProcessorWords().length returns the number of processor words for one segment
						long mw = f_less_than(Y, column[n].getProcessorWords()[i]);
						mw >>>= i;
						ms |= mw;
					}
					ms >>= nbZP; // Deleting the zero padding
					BVout[n] = ms;
				}
				break;
			
			default:
				throw new InvalidParameterException("\"" + query + "\"" + ": Invalid parameter for query");
		}
		
		// Special post-treatment for the last incomplete segment
		if(rest > 0) {
			
			long BVoutLastCorrected = 0, currentmaskresultLastSegmt = maskresultLastSegmt;
			
			for(int i = 0; i < N; ++i) {
				
				// Applying the mask to obtain the result
				long result = BVout[NbSegments - 1] & currentmaskresultLastSegmt;
				
				// Shifting the result to add it to the global result bit vector
				// This formula will be explained in the documentation
				result >>=  k+1-NbProcWordsLastSegmt + i*(k+1) - i*NbProcWordsLastSegmt;
				
				// Adding the result to the global result bit vector
				BVoutLastCorrected |= result;
				
				// Shifting the mask
				currentmaskresultLastSegmt <<= k+1;
			}
			
			// Deleting the wrong results due the the added "0" data
			BVoutLastCorrected >>= N*NbProcWordsLastSegmt - rest;
			
			// Saving the result
			BVout[NbSegments - 1] = BVoutLastCorrected;
		}
		return BVout;
	}
	
}
