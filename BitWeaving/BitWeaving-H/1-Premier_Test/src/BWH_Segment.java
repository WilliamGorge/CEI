

public class BWH_Segment {
 
	
	private long[] v; // Processor words
	private int k;	// Size of one data
	private int w;	// Size of processor word
	private int Ls; // Length of one segment
	
	/**
	 * Default constructor
	 */
	public BWH_Segment() {
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
	public BWH_Segment(long[] columnsegment, int sizeofonedata, int sizeofprocessorword, boolean isFullSegment) {
		
		// Coping the arguments
		k = sizeofonedata;
		w = sizeofprocessorword;
		Ls = columnsegment.length;
		
		// Calclulating the number of data that you can fit in a processor word
		int N = w/(k+1);
		
		// Calulating the number of processor words needed
		int NbProcessorWords = Ls/N;
		if(Ls != N*(k+1)) NbProcessorWords += 1;
		
		// Declaration of the array of processor words
		v = new long[NbProcessorWords];
		
		if(isFullSegment) {
			
			// Making the processor words
			// Itteration on the processor words
			for(int i=0; i < NbProcessorWords; ++i) {

				v[i] = columnsegment[i];
				
				// Itteration on the data in one processor word
				for(int j=1; j < N ; ++j) {
					v[i] <<= k+1;
					v[i] |= columnsegment[i+j*NbProcessorWords];
				}
				
				// Let us do some zero padding
				if(N*(k+1) < w) v[i] <<= (w - N*(k+1));
			}
		}
		else {
			
			// Making the processor words
			// Itteration on the processor words
			for(int i=0; i < NbProcessorWords; ++i) {
				
				// Adding zeros if we're outside of the column (can happen if we are at the last segment)
				if(i < Ls) v[i] = columnsegment[i];
				else v[i] = 0;
				
				// Itteration on the data in one processor word
				for(int j=1; j < N ; ++j) {
					v[i] <<= k+1;
					// Adding zeros if we're outside of the column (can happen if we are at the last segment)
					if(i+j*NbProcessorWords < Ls) v[i] |= columnsegment[i+j*NbProcessorWords];
				}
				
				// Let us do some zero padding
				if(N*(k+1) < w) v[i] <<= (w - N*(k+1));
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
