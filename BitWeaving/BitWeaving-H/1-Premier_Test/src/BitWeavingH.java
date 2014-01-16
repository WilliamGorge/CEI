public class BitWeavingH {
	
	// Colomn on witch perform the scan
	private long[] column;
	private int k;
	private int w;
	private int Ls;
	
	// Default constructor
	public BitWeavingH() {
		column = null;
	}
	
	// Constructor with a column and a data size given
	public BitWeavingH(long[] column, int size_of_one_data, int size_of_processor_word) {
		this.column = column;
		int k = size_of_one_data;
		int w = size_of_processor_word;
		int Ls = w/(k+1);
	}
	
	// get the current column
	public long[] getColumn() {
		return column;
	}
	
	// Set a new column
	public void setColumn(long[] col) {
		column = col;
	}
	
	public long is_column_less_than(long cst) {
		long BVout = 0L;
		int index = 0;
		return BVout;
	}
	
}
