import java.security.InvalidParameterException;

/**
 * Class for the use of the BitWeavingH Method.<br>
 * An instance of this class represents a column of data where a query with the BitWeavingH method can be used.<br>
 * It deals with the data in the column: creation, addition, return the size.<br>
 * It uses the interface BitWeavingHInterface.<br>
 * @author William Gorge
 */
public class BitWeavingH implements BitWeavingHInterface {
	
	private BWHSegment[] column; // Colomn on witch perform the scan
	private int nbData; // Number of data in the column
	private int k;  // Size of one data
	private int w;  // Size of processor word
	private int N;	// Number of data that can fit in a processor word
	private int Ls; // Length of one segment
	private long mask; // Mask for the query less than
	private long maskout; // Masks the bits outside the theorical processor word
	private long maskDatum; // Mask to obtain one datum in one processor word
	private int NbFullSegments; // Number of segments that contains Ls data
	private int rest; // Number of data in the last segment
	private int NbSegments; // Number of segments
	private int NbProcWordsLastSegmt; // Number of processor words in the last segment
	private long maskresultLastSegmt; // Mask to obtain the result for the last segment
	private int nbZP; // Number of the zero added in the zerop padding
	private long[] lastSegmentData; // array of the data in the last segment
	
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
		nbData = col.length;
		NbFullSegments = col.length/Ls;
		rest = col.length - NbFullSegments*Ls;
		
		// maskDatum is for one data in the processor word ie 0111...1 (k times 1)
		maskDatum = 1;
		for(int i = 1; i < k; ++i) {
			maskDatum <<= 1;
			maskDatum |= 1;
		}
		
		// "mask" is the mask in the article ie N times masktemp (0111...1 0111...1 0111...1 0111...1 ...)
		for(int i = 0; i < N; ++i) {
			mask <<= (k+1);
			mask |= maskDatum;
		}
		// Let us do some zero padding to this mask, in case
		mask <<= nbZP;
		
		// Mask that have w times 1, it is used to mask what's outside the procesor word
		maskout =  1;
		for(int i = 1; i < w; ++i) {
			maskout <<= 1;
			maskout |= 1;
		}
		
		// Creation of the segment array
		if(rest == 0) this.column = new BWHSegment[NbFullSegments]; // Case when Ls divides the length of the column
		else this.column = new BWHSegment[NbFullSegments+1];	// Case if not: we have to add another segment
		
		// Itterate for all the segments
		for(int i = 0; i < NbFullSegments; ++i) {
			
			// Copy from the column to the segment
			long [] segmt = new long[Ls];
			for(int j = 0; j < Ls; ++j) {
				segmt[j] = col[i*Ls+j];
			}
			
			// BWH Segment creation (creation of processor words)
			this.column[i] = new BWHSegment(segmt, k, w, true);
		}
		
		// We sould not forget the possible rest of the segmentation: the last segment
		if(rest > 0) {
			
			long [] segmt = new long[rest];
			for(int j = 0; j < rest; ++j) {
				segmt[j] = col[NbFullSegments*Ls + j];
			}
			
			// BWH Segment creation (creation of processor words)
			this.column[NbFullSegments] = new BWHSegment(segmt, k, w, false);
			
			// Initialize the array of the data in the last segment
			lastSegmentData = segmt;
		}
		else
			lastSegmentData = null;
		
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
	 * Gets the attatched column, as an array of HWHSegment<br><br>
	 * !!! USE FOR DEBUG ONLY !!!<br>
	 * It will be deleted for the relase version<br>
	 * @return column
	 */
	public BWHSegment[] getColumn() {
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
	
	/**
	 * Returns the size of the column
	 * @return column.length
	 */
	public int size() {
		return nbData;
	}
	

	/*** CORE FUNCTION OF THE QUERY "X != cst" ***/
	// This method is the f!=(X,C) of the article (3.2.2)
	private long fDifferent(long X, long Y) {
		
		// Computing the result
		long Z = (X ^ Y) + mask ;
		Z = Z  & (~mask);
		
		// Return the result
		return 	Z;
	}
	
	/*** CORE FUNCTION OF THE QUERY "X = cst" ***/
	// This method is the f=(X,C) of the article (3.2.2)
	private long fEqual(long X, long Y) {
		
		// Computing the result
		long Z = (X ^ Y) + mask;
		Z = (~Z)  & (~mask) & maskout;
		
		// Return the result
		return 	Z;
	}
	
	
	/*** CORE FUNCTION OF THE QUERY "X < cst" (used for X<= cst too since it is equivalent to X < cst+1) ***/
	// This method is the f<(X,C) of the article and in the slides
	// The algorithm is explained at "Figure 4" of the article
	private long fLessThan(long X, long Y) {
		
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
	 * @throws InvalidParameterException
	 * @author William Gorge
	 */
	public long[] query(Query query, long cst) throws InvalidParameterException {
	
		/*** INITIALIATION ***/

		// Result vector
		long[] BVout = new long[NbSegments]; 

		long Y;
		
		/*** COMPUTING LOOPS (same for each query) ***/
		// Itterating on all the segments
		// This algorith is the "Alogrithm 1" of the article
		switch(query) {
		
			// DIFFERENT
			case DIFFERENT:
				
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
						long mw = fDifferent(column[n].getProcessorWords()[i], Y);
						mw >>>= i;
						ms |= mw;
					}
					ms >>>= nbZP; // Deleting the zero padding
					BVout[n] = ms;
				}
				break;
			
			// EQUAL
			case EQUAL:
				
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
						long mw = fEqual(column[n].getProcessorWords()[i], Y);
						mw >>>= i;
						ms |= mw;
					}
					ms >>>= nbZP; // Deleting the zero padding
					BVout[n] = ms;
				}
				break;
				
			
			// LESS THAN
			case LESS_THAN:
				
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
						long mw = fLessThan(column[n].getProcessorWords()[i], Y);
						mw >>>= i;
						ms |= mw;
					}
					ms >>>= nbZP; // Deleting the zero padding
					BVout[n] = ms;
				}
				break;
				
				
			// LESS THAN OR EQUAL TO
			case LESS_THAN_OR_EQUAL_TO:
				
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
						long mw = fLessThan(column[n].getProcessorWords()[i], Y);
						mw >>>= i;
						ms |= mw;
					}
					ms >>>= nbZP; // Deleting the zero padding
					BVout[n] = ms;
				}
				break;
			
			// GREATER THAN
			case GREATER_THAN:
				
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
						long mw = fLessThan(Y, column[n].getProcessorWords()[i]);
						mw >>>= i;
						ms |= mw;
					}
					ms >>>= nbZP; // Deleting the zero padding
					BVout[n] = ms;
				}
				break;
				
				
			// GREATER THAN OR EQUAL TO
			case GREATER_THAN_OR_EQUAL_TO:
				
				
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
						long mw = fLessThan(Y, column[n].getProcessorWords()[i]);
						mw >>>= i;
						ms |= mw;
					}
					ms >>>= nbZP; // Deleting the zero padding
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
				result >>>=  k+1-NbProcWordsLastSegmt + i*(k+1) - i*NbProcWordsLastSegmt;
				
				// Adding the result to the global result bit vector
				BVoutLastCorrected |= result;
				
				// Shifting the mask
				currentmaskresultLastSegmt <<= k+1;
			}
			
			// Deleting the wrong results due the the added "0" data
			BVoutLastCorrected >>>= N*NbProcWordsLastSegmt - rest;
			
			// Saving the result
			BVout[NbSegments - 1] = BVoutLastCorrected;
		}
		return BVout;
	}
	
	/*** ADD ONE DATUM FUNCTION ***/
	/**
	 * Adds one datum to the column
	 * @param datum: datum to add<br><br>
	 * @author William Gorge
	 */
	public void add(long datum) {
		
		// If the last segment is not full, you can direcly add data to it
		if(rest != 0) {
			
			// Create temporary array to store the data of the "new" last segment
			long[] newLastSegmentData = new long[rest+1];
			
			// Copy the array
			for(int i = 0; i < rest; ++i) 
				newLastSegmentData[i] = lastSegmentData[i];
			
			// Add the new datum
			newLastSegmentData[newLastSegmentData.length - 1] = datum;
			
			// Update the arrray
			lastSegmentData = newLastSegmentData;
			
			// Updating rest
			rest += 1;
			
			// If the last segment gets full
			if(rest == Ls) {
				// we have to increment NbFullSegments and update rest
				NbFullSegments += 1;
				rest = 0;
				
				// And update the last segment's processor words calling his constructor this way
				column[NbSegments-1] = new BWHSegment(lastSegmentData, k, w, true);
			}
			// If the segment is still not full
			else
				// Still update the last segment's processor words calling his constructor that way
				column[NbSegments-1] = new BWHSegment(lastSegmentData, k, w, false);
		}
		
		
		// If the last segment is full, we have to reassign the segment array
		else {
			
			// Increasing this counter
			NbSegments += 1;
			
			/********************** THIS PART MUST BE OPTIMIZABLE *******************/
			// Creating the new array of segments
			BWHSegment[] newColumn = new BWHSegment[NbSegments];
			
			// Copying the segments
			for(int i = 0; i < NbSegments - 1; ++i) {
				newColumn[i] = column[i];
			}
			/*************************************************************************/
			
			// Creating the new segment
			lastSegmentData = new long[1];
			lastSegmentData[0] = datum;
			newColumn[NbSegments - 1] = new BWHSegment(lastSegmentData, k, w, false);
			
			// Upsating the column
			column = newColumn;
			
			// Updating rest
			rest = 1;
			
		}
		
		// Update some attributes:
		// Number of processor words in the last segment
		NbProcWordsLastSegmt = column[NbSegments-1].getProcessorWords().length;
		
		// Mask the obtain the results for the last segment
		maskresultLastSegmt = 1;
		for(int i = 1; i < NbProcWordsLastSegmt; ++i) {
			maskresultLastSegmt <<= 1;
			maskresultLastSegmt |= 1;
		}
		maskresultLastSegmt <<= (k+1-NbProcWordsLastSegmt);
		
		// Number of data
		nbData += 1;
	}
}
