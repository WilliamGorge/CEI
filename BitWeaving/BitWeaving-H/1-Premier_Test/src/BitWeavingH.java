import java.util.Vector;

public class BitWeavingH {
	
	
	private BWH_Segment[] column; // Colomn on witch perform the scan
	private int k;  // Size of one data
	private int w;  // Size of processor word
	private int N;	// Number of data that can fit in a processor word
	private int Ls; // Length of one segment
	private long mask; // Mask for the query less than
	private int NbFullSegments; // Number of segments that contains Ls data
	private int rest; // Number of data in the last segment
	
	private long f_less_than(long X, long Y) {
		
		// Computing the result
		long Z = X ^ mask;
		Z = Y + Z;
		Z = Z  & (~mask);
		
		// return the result
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
			mask <<= (k+1);
			mask |= ( (long) Math.pow(2, k) ) - 1;
		}
		// Let us do some zero padding to this mask, in case
		mask <<= (w-N*(k+1));
		
		NbFullSegments = col.length/Ls;
		rest = col.length % Ls;
		
		// Creation of the segment array
		if(rest == 0) this.column = new BWH_Segment[NbFullSegments]; // Case when Ls divides the length of the column
		else this.column = new BWH_Segment[NbFullSegments+1];	// Case if not: we have to add another segment
		
		// Itterate for all the segments
		for(int i = 0; i < col.length/Ls; ++i) {
			
			// Copy from the column to the segment
			long [] segmt = new long[Ls];
			for(int j = 0; j < Ls; ++j) {
				segmt[j] = col[i*Ls+j];
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
	
	public long[] is_column_less_than(long cst) {
		
		// Number of segments
		int NbSegments = column.length; // Warning: here column.length is the number of segments
		
		// Result vector
		long[] BVout = new long[NbSegments]; 
		
		// Construcion of the comparaison vector
		long Y = cst;
		for(int i = 1; i < N; ++i) {
			Y <<= k+1;
			Y |= cst;
		}
		
		// Let us do some zero padding to Y, in case
		Y <<= (w-N*(k+1));
		
		// Itterating on all the full the segments
		for(int n = 0; n < NbSegments; ++n) {
			long ms = 0;
			for(int i = 0; i < column[n].getProcessorWords().length; ++i) { // Warning: column[n].getProcessorWords().length returns the number of processor words for one segment
				long mw = f_less_than(column[n].getProcessorWords()[i], Y);
				mw >>= i;
				ms |= mw;
			}
			ms >>= (w - N*(k+1)); // Deleting the zero padding
			BVout[n] = ms;
		}
		// Special treat for the last incomplete segment: works with N = 2...
		if(rest > 0) {
			String SBVoutinit = longtobitsString(BVout[NbSegments - 1]);
			
			// Building a mask the obtain the right results
			long maskright = ((long) (Math.pow(2, rest/N)) -1)<<k;
			String Smaskright = longtobitsString(maskright);
			
			// Building a mask the obtain the ledtresults
			long maskleft =  maskright<<k+1;
			String Smaskleft = longtobitsString(maskleft);
			
			// Obtaining the result
			long resleft = BVout[NbSegments - 1] & maskleft;
			resleft >>= 2*k;
			String Sresleft = longtobitsString(resleft);
			
			long resright = BVout[NbSegments - 1] & maskright;
			resright >>=  k;
			String Sresright = longtobitsString(resright);
			
			BVout[NbSegments - 1] = resleft | resright;
			String SBVoutfinal = longtobitsString(BVout[NbSegments - 1]);
		}
		return BVout;
	}
	
	public static String longtobitsString(long l){
		String s = "";
		for(int i = 0; i < Long.numberOfLeadingZeros(l); ++i) {
			s += "0";
		}
		if(l != 0) s += Long.toBinaryString(l);
		return s;
	}
	
	
}
