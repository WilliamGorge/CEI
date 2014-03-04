
import java.security.InvalidParameterException;
import java.util.ArrayList;

/**
 * Class for the use of the BitWeavingH Method, more specifically for the columns.<br>
 * An instance of this class represents a column of data where a query with the BitWeavingH method can be used.<br>
 * It deals with the data in the column: creation, addition, return the size.<br>
 * It uses the interface BitWeavingHInterface.<br>
 * @author William Gorge
 */
public class BWHColumn extends BWColumn {
	
	private ArrayList<BWHSegment> column; // Colomn on witch perform the scan
	
	// Name of the column
	private String name;
	
	// Constants of the column
	private int k;  // Size of one data
	private int w;  // Size of processor word
	private int N;	// Number of data that can fit in a processor word
	private int Ls; // Length of one segment
	private int nbZP; // Number of the zero added in the zerop padding
	private long maxValue; // The highest value that can have a datum with k bits
	
	// Variables of the column
	private int nbData; // Number of data in the column
	private int nbDataLastSegment; // Number of data in the last segment of the column	
	private int nbSegments; // Number of segments
	
	// Masks used for bit processing
	private long mask; // Mask for the query less than
	private long maskout; // Masks the bits outside the theorical processor word
	private long maskResultLastSegment; // Mask that delete the wring results of the added "0" data of the last segment
	private long maskOneBit; // Mask used to build the maskResultLastSegment
	
	/**
	 * Constructor of a BWH column with a processor word and a data size given (format of the data).
	 * @param sizeofonedata size (in bits) of one data in the column, depends on the format of the data. It is the number of bits with which the data is encoded.
	 * @param sizeofprocessorword size of the processor word
	 * @throws InvalidParameterException Is thrown when sizeOfOneData is higher than Long.SIZE-1 (63). It is then impossible to create the column because the processor words are at least k + 1 bits
	 */
	public BWHColumn(String name, int sizeOfOneData, int sizeOfProcessorWord) {
		
		// Instaciation of the arguments
		this.name = name;
		k = sizeOfOneData;
		w = sizeOfProcessorWord;
		N = w/(k+1);
		Ls = N*(k+1);
		nbZP = (w-N*(k+1));
		if(k < Long.SIZE)
			maxValue = (long) (Math.pow(2, k) - 1);
		else 
			throw new InvalidParameterException("Cannot create a BWH column with data encoded in " + k + "bits");
		nbData = 0;
		nbDataLastSegment = 0;
		nbSegments = 0; 
		
		// maskDatum is for one data in the processor word ie 0111...1 (k times 1)
		long maskDatum = 1;
		for(int i = 1; i < k; ++i) {
			maskDatum <<= 1;
			maskDatum |= 1L;
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
			maskout |= 1L;
		}
		
		// Mask that delete the wring results of the added "0" data of the last segment
		maskResultLastSegment = 0;
		maskOneBit = 1L << (Ls - 1);
		
		// Initialization of the Column
		column = new ArrayList<BWHSegment>();
		
		
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
	
	/**
	 * Gets the attatched column, as an array of BWHSegment<br><br>
	 * !!! USE FOR DEBUG ONLY !!!<br>
	 * It will be deleted for the relase version<br>
	 * @return column
	 */
	public ArrayList<BWHSegment> getColumn() {
		return column;
	}
	
	/**
	 * Returns the size of the column
	 * @return column.length
	 */
	public int size() {
		return nbData;
	}
	
	/**
	 * Returns the name of the column
	 * @return column.name
	 */
	public String getName() {
		return name;
	}
	
	public int getSizeOfProcessorWord() {
		return w;
	}
	
	public int getSizeOfOneDatum() {
		return k;
	}
	
	/*** QUERY FUNCTION ***/
	/**
	 * Performs the query queryName with the constant cst on the column of the instance of the BitWeavingH object.<br>
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
		cst: Constant to compare<br><br>
	 * @throws InvalidParameterException thrown if query has an unknown value or if cst cannot be encoded in k bits
	 * @author William Gorge
	 */
	public BitVector query(Query query, long cst) throws InvalidParameterException {
		
		if(cst > maxValue) 
			throw new InvalidParameterException("Invalid value for the comparaison constant (value=" + cst + "): Too high to be encoded in " + k + " bits, maximum value for this format is " + maxValue);
	
		/*** INITIALIATION ***/

		// Result vector
		BitVector BVout = new BitVector();

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
				
				// Itterating on all the segments
				for(int n = 0; n < nbSegments; ++n) {
					long ms = 0;
					for(int i = 0; i < column.get(n).getProcessorWords().length; ++i) { // Warning: column.get(n).getProcessorWords().length returns the number of processor words for one segment
						long mw = fDifferent(column.get(n).getProcessorWords()[i], Y);
						mw >>>= i;
						ms |= mw;
					}
					ms >>>= nbZP; // Deleting the zero padding
					BVout.append(ms, Ls);
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
				
				for(int n = 0; n < nbSegments; ++n) {
					long ms = 0;
					for(int i = 0; i < column.get(n).getNbProcessorWords(); ++i) {
						long mw = fEqual(column.get(n).getProcessorWords()[i], Y);
						mw >>>= i;
						ms |= mw;
					}
					ms >>>= nbZP; // Deleting the zero padding
					BVout.append(ms & maskResultLastSegment, Ls);
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
				
				for(int n = 0; n < nbSegments; ++n) {
					long ms = 0;
					for(int i = 0; i < column.get(n).getNbProcessorWords(); ++i) {
						long mw = fLessThan(column.get(n).getProcessorWords()[i], Y);
						mw >>>= i;
						ms |= mw;
					}
					ms >>>= nbZP; // Deleting the zero padding
					BVout.append(ms, Ls);
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
				
				for(int n = 0; n < nbSegments; ++n) {
					long ms = 0;
					for(int i = 0; i < column.get(n).getNbProcessorWords(); ++i) { 
						long mw = fLessThan(column.get(n).getProcessorWords()[i], Y);
						mw >>>= i;
						ms |= mw;
					}
					ms >>>= nbZP; // Deleting the zero padding
					BVout.append(ms, Ls);
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
				
				for(int n = 0; n < nbSegments; ++n) {
					long ms = 0;
					for(int i = 0; i < column.get(n).getNbProcessorWords(); ++i) { 
						long mw = fLessThan(Y, column.get(n).getProcessorWords()[i]);
						mw >>>= i;
						ms |= mw;
					}
					ms >>>= nbZP; // Deleting the zero padding
					BVout.append(ms, Ls);
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
				
				for(int n = 0; n < nbSegments; ++n) {

					long ms = 0;
					for(int i = 0; i < column.get(n).getNbProcessorWords(); ++i) { 
						long mw = fLessThan(Y, column.get(n).getProcessorWords()[i]);
						mw >>>= i;
						ms |= mw;
					}
					ms >>>= nbZP; // Deleting the zero padding
					BVout.append(ms, Ls);
				}
				break;
			
			default:
				throw new InvalidParameterException("Invalid parameter for query");
		}
		BVout.deleteEnd(Ls - nbDataLastSegment);
		return BVout;
	}
	
	/*** ADD ONE DATUM FUNCTION ***/
	/**
	 * Adds one datum to the column
	 * @param datum: datum to add<br><br>
	 * @author William Gorge
	 * @throws Exception throws an exeption when you try to add a datum to a full segment. Can happen when the segment is not handled in a good way.
	 */
	public void add(long datum) {
		
		try {
			if(nbDataLastSegment >= Ls || nbSegments == 0) {
				
				// Creating a new segment
				BWHSegment newSegment = new BWHSegment(k,w);
				
				// Adding it to the segment array
				column.add(newSegment);
				
				// Updating the segment infos
				++nbSegments;
				nbDataLastSegment = 0;
				
				// Updating the mask for the result of the last segment (to delete the wrong added 0 data)
				maskResultLastSegment = 0;
				maskOneBit = 1L << (Ls - 1);
			}
			
			// Add the datum to the last segment
			column.get(nbSegments-1).add(datum);
			
			// Update the number of data
			++nbData;
			++nbDataLastSegment;
			
			// Updating the mask for the result of the last segment (to delete the wrong added 0 data)
			maskResultLastSegment |= maskOneBit;
			maskOneBit >>>= 1;

		} catch(Exception e) {
			System.out.println("Error during add on the column " + name + ": ");
			e.printStackTrace();
		}
	}


	public void printProcessorWords() {
		
		// Itteration on all the segment
		for(int i = 0; i < column.size(); ++i) {
			
			System.out.println("Processor Words for segment " + (i + 1) + ":");
			
			// Gets the processor words
			long[] processorWords = column.get(i).getProcessorWords();
			
			// Display them
			for(int j = 0; j < processorWords.length; ++j)
				System.out.println("	" + longtobitsString(processorWords[j]).substring(Long.SIZE - w));
			
			System.out.println();
		}
		
	}
}
