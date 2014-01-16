

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
		
		// Declaration of the array of processor words
		v = new long[Ls/N];
		
		// Making the processor words
		int i,j;
		// Itteration on the processor words
		for(i=0; i < Ls/N; ++i) {
			v[i] = column_segement[i];
			for(j=1; j < N ; ++j) {
				v[i] <<= k+1;
				v[i] |= column_segement[i+j*Ls/N];
			}
			// Let us do some zero padding
			v[i] <<= w - N*(k+1);
		}
	}
	
	public long[] getProcessorWords() {
		return v;
	}
	
}
