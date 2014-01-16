public class BitWeavingH {
	
	
	private BWH_Segment[] column; // Colomn on witch perform the scan
	private int k;  // Size of one data
	private int w;  // Size of processor word
	private int Ls; // Length of one segment
	
	// Default constructor
	public BitWeavingH() {
		column = null;
	}
	
	// Constructor with a column and a data size given
	public BitWeavingH(long[] col, int size_of_one_data, int size_of_processor_word) {
		
		k = size_of_one_data;
		w = size_of_processor_word;
		Ls = w/(k+1);
		
		// Creation of the segment array
		this.column = new BWH_Segment[col.length/Ls];
		
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
		
	}
	
	// get the current column
	public BWH_Segment[] getColumn() {
		return column;
	}
	
	// Set a new column
	public void setColumn(long[] col) {
		
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

	}
	
	public long is_column_less_than(long cst) {
		long BVout = 0L;
		int index = 0;
		return BVout;
	}
	
}
