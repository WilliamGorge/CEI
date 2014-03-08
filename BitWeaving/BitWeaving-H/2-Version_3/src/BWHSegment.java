
/**
 * Class for the use of the BitWeavingH Method, more specifically for the column segments.<br>
 * An instance of this class represents a segmement of data according to the BitWeaving method.<br>
 * It deals with the processor words: they are construced, returned, updated, etc...<br>
 * @author William Gorge
 */

public class BWHSegment {
 
	
	private long[] v; // Processor words
	
	// Constants of the segment
	private int k;	// Size of one data
	private int w;	// Size of processor word
	private int N;  // Number of data that can fit in a processor word
	private int Ls; // Length of one segment
	private int nbProcessorWords; // Number of processor words in each segment
	
	// Variables of the segment
	private int nbData; // Number of data in the segment
	
	// Indexes
	// The processor words array can be seen as a matrix x index are the different data in one processor word
	// and y index are the different processor words 
	// See documentation
	private int xIndex; // Index for the different data in ont processor word
	private int yIndex; // Index for the processor words array	
	
	/** 
	 * Constructs the segment given its elements in columnsegment and other parameters and indicating
	 * if it is the last segment. This information is used to optimize the construction time
	 * @param columnsegement elements of the segment to create
	 * @param sizeOfOneData size (in bits) of one data in the column
	 * @param sizeofprocessorword size of the processor word
	 * @param isFullSegment indicates if it is a full segment or not
	 */
	public BWHSegment(int sizeOfOneData, int sizeOfProcessorWord) {
		
		// Coping the arguments
		k = sizeOfOneData;
		w = sizeOfProcessorWord;
		
		if(k > w) throw new IllegalArgumentException("Invalid value for sizeOfOneData (value=" + k + "): must be strictly lower than " + w); 
		
		// Calclulating the number of data that you can fit in a processor word
		N = w/(k+1);
		
		// Length of a segment
		Ls = N*(k+1);
		
		// Number of data in the segment
		nbData = 0;
		
		// Calulating the number of processor words needed
		nbProcessorWords = Ls/N;
		
		// Declaration of the array of processor words
		v = new long[nbProcessorWords];
		
		// Index for the processor words array
		xIndex = 0;
		yIndex = 0;
	}
	
	/**
	 * Gets the processor words for this segment
	 * @return processor words
	 */
	public long[] getProcessorWords() {
		return v;
	}
	
	/**
	 * Gets the number of the processor words
	 * @return number of the processor words
	 */
	public int getNbProcessorWords() {
		return nbProcessorWords;
	}
	
	/**
	 * Adds one datum to the segment
	 * @param datum: datum to add<br><br>
	 * @author William Gorge
	 * @throws Exception throws an exeption when you try to add a datum to a full segment.
	 *  Can happen when the segment is not handled in a good way.
	 */
	public void add(long datum) throws Exception {
		
		if(nbData < Ls) {
			
			// Updating the number of data
			++nbData;
			
			// Adding the datum in the processor words array
			v[yIndex] |= datum << (w - (xIndex + 1)*(k+1));
			++yIndex;
			
			// Updating the indexes
			if(yIndex >= nbProcessorWords) {
				yIndex = 0;
				++xIndex;
			}
			
			// The zero padding is done automatically since the processorwords are initially at zero
		}
		else throw new Exception("Cannot add data \"" + datum + "\", the segment is full");
	}
	
}
