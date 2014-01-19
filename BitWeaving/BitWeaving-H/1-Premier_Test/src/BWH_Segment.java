

public class BWH_Segment {
 
	
	private long[] v; // Processor words
	private int k;	// Size of one data
	private int w;	// Size of processor word
	private int Ls; // Length of one segment
	
	// Default constructor
	public BWH_Segment() {
		k = 0;
		w = 0;
		v = null;
	}
	
	// Real constructor
	public BWH_Segment(long[] column_segement, int size_of_one_data, int size_of_processor_word) {
		
		// Coping the arguments
		k = size_of_one_data;
		w = size_of_processor_word;
		Ls = column_segement.length;
		
		// Calclulating the number of data that you can fit in a processor word
		int N = w/(k+1);
		
		// Calulating the number of processor words needed
		int NbProcessorWords = Ls/N;
		if(Ls != N*(k+1)) NbProcessorWords += 1;
		
		// Declaration of the array of processor words
		v = new long[NbProcessorWords];
		
		// Making the processor words
		int i,j;
		// Itteration on the processor words
		for(i=0; i < NbProcessorWords; ++i) {
			
			// Adding zeros if we're outside of the column (can happen if we are at the last segment)
			if(i < Ls) v[i] = column_segement[i];
			else v[i] = 0;
			
			// Itteration on the data in one processor word
			for(j=1; j < N ; ++j) {
				v[i] <<= k+1;
				// Adding zeros if we're outside of the column (can happen if we are at the last segment)
				if(i+j*NbProcessorWords < Ls) v[i] |= column_segement[i+j*NbProcessorWords];
			}
			
			// Let us do some zero padding
			if(N*(k+1) < w) v[i] <<= (w - N*(k+1));
		}
	}
	
	public long[] getProcessorWords() {
		return v;
	}
}
