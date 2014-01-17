import java.util.Vector;

public class BitWeavingH {
	
	
	private BWH_Segment[] column; // Colomn on witch perform the scan
	private int k;  // Size of one data
	private int w;  // Size of processor word
	private int N;	// Number of data that can fit in a processor word
	private int Ls; // Length of one segment
	private long mask; // Mask for the query less than
	
	private long f_less_than(long X, long cst) {
		
		// Construcion of the comparaison vector
		long Y = cst;
		Y <<= k+1;
		Y |= cst;
		long Z = X ^ mask; //ok
		Z = Y + Z;
		Z = Z  & (~mask);
		return 	Z;
	}
	
	// Default constructor
	public BitWeavingH() {
		column = null;
	}
	
	// Constructor with a column and a data size given
	public BitWeavingH(long[] col, int size_of_one_data, int size_of_processor_word) {
		
		// Instaciation of the arguments
		k = size_of_one_data;
		w = size_of_processor_word;
		N = w/(k+1);
		Ls = N*(k+1);
		mask = 0;
		for(int i = 0; i < N; ++i) {
			mask<<=k+1;
			mask |= ( (long) Math.pow(2, k) ) - 1;
		}
		// Let us do some zero padding to this mask, in case
		if(N*(k+1) < w) {
			mask<<=w-N*(k+1);
		}
		
		int NbFullSegments = col.length/Ls;
		int rest = col.length % Ls;
		
		// Creation of the segment array
		if(rest == 0) this.column = new BWH_Segment[NbFullSegments]; // Case when Ls divides the length of the column
		else this.column = new BWH_Segment[NbFullSegments+1];	// Case if not: we have to add another segment
		
		// Itterate for all the segments
		for(int i = 0; i < col.length/Ls; ++i) {
			
			// Copy from the column to the segment
			long [] segmt = new long[Ls];
			for(int j = 0; j < Ls; ++j) {
				segmt[j] = col[i+j];
			}
			
			// BWH Segment creation (creation of processor words)
			this.column[i] = new BWH_Segment(segmt, k, w);
		}
		
		// We sould not forget the possible rest of the segmentation: the last segment
		if(rest > 0) {
			
			long [] segmt = new long[rest];
			for(int j = 0; j < rest; ++j) {
				segmt[j] = col[NbFullSegments*Ls + j];
			}
			
			// BWH Segment creation (creation of processor words)
			this.column[NbFullSegments] = new BWH_Segment(segmt, k, w);
		}
		
	}
	
	// get the current column
	public BWH_Segment[] getColumn() {
		return column;
	}
	
	// Set a new column
	public void setColumn(long[] col) {
		
	}
	
	public long[] is_column_less_than(long cst) {
		int NbSegments = column.length;
		long[] BVout = new long[NbSegments]; // Warning: here column.length is the number of segments
		for(int n = 0; n < NbSegments; ++n) {
			long ms = 0;
			for(int i = 0; i < column[n].getProcessorWords().length; ++i) {
				long mw = f_less_than(column[n].getProcessorWords()[i], cst);
				mw >>= i;
				ms |= mw;
			}
			BVout[n] = ms;
		}
		return BVout;
	}
	
}
