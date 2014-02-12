/**
 * Class for the use of the BitWeavingH Method, more specifically for the segments.<br>
 * An instance of this class represents a segmement of data according to the BitWeaving method.<br>
 * It deals with the processor words: they are construced, returned, updated, etc...<br>
 * @author William Gorge
 */

public class BWHSegment {
 
	
	private long[] v; // Processor words
	private int k;	// Size of one data
	private int w;	// Size of processor word
	private int Ls; // Length of one segment
	private int nbProcessorWords; // Number of processor words in each segment
	private int nbZP; // Number of zeros added by zero padding
	
	
	/**
	 * Default constructor
	 */
	public BWHSegment() {
		k = 0;
		w = 0;
		v = null;
	}
	
	/** 
	 * Constructs the segment given its elements in columnsegment and other parameters and indicating
	 * if it is the last segment. This information is used to optimize the construction time
	 * @param columnsegement elements of the segment to create
	 * @param sizeofonedata size (in bits) of one data in the column
	 * @param sizeofprocessorword size of the processor word
	 * @param isFullSegment indicates if it is a full segment or not
	 */
	public BWHSegment(long[] columnsegment, int sizeofonedata, int sizeofprocessorword, boolean isFullSegment) {
		
		// Coping the arguments
		k = sizeofonedata;
		w = sizeofprocessorword;
		Ls = columnsegment.length;
				
		// Calclulating the number of data that you can fit in a processor word
		int N = w/(k+1);
		
		// Calculating the zero padding
		nbZP = w - N*(k+1);
		
		// Calulating the number of processor words needed
		nbProcessorWords = Ls/N;
		if(Ls > N*nbProcessorWords) nbProcessorWords += 1;
		
		// Declaration of the array of processor words
		v = new long[nbProcessorWords];
		
		if(isFullSegment) {
			
			// Making the processor words
			// Itteration on the processor words
			for(int i=0; i < nbProcessorWords; ++i) {

				v[i] = columnsegment[i];
				
				// Itteration on the data in one processor word
				for(int j=1; j < N ; ++j) {
					v[i] <<= k+1;
					v[i] |= columnsegment[i+j*nbProcessorWords];
				}
				
				// Let us do some zero padding
				if(N*(k+1) < w) v[i] <<= nbZP;
			}
		}
		else {
			
			// Making the processor words
			// Itteration on the processor words
			for(int i=0; i < nbProcessorWords; ++i) {
				
				// Adding zeros if we're outside of the column (can happen if we are at the last segment)
				if(i < Ls) {
					v[i] = columnsegment[i];
				}
				else v[i] = 0;
				
				
				// Itteration on the data in one processor word
				for(int j=1; j < N ; ++j) {
					
					v[i] <<= k+1;
					
					// Adding the data if we are inside the column
					if(i+j*nbProcessorWords < Ls) {
						v[i] |= columnsegment[i+j*nbProcessorWords];
					}
					
					// Adding zeros if we're outside of the column (can happen if we are at the last segment)
					
				}
				
				// Let us do some zero padding
				if(N*(k+1) < w) v[i] <<= nbZP;
			}
		}
	}
	
	/**
	 * Gets the processor words for this segment
	 * @return processor words
	 */
	public long[] getProcessorWords() {
		return v;
	}
	
}
