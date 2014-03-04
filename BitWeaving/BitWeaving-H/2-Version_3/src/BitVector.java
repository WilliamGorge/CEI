import java.util.ArrayList;

/**
 * Class that represents Bit Vectors (ie bit arrays of unknown dimention).<br>
 * @author William Gorge and Benoit Sordet
 */
public class BitVector {

	private ArrayList<Long> vector;
	private int size;
	private int index;
	
	/**
	 * Constructor for BitVector.
	 */
	BitVector() {
		vector = new ArrayList<Long>();
		index = 0;
	}
	
	/**
	 * Returns the binary string with zeros of the long l
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
	
	private void setSize(int newSize) {
		size = newSize;
	}
	
	/**
	 * Appends bits to the bit vector, the length of the bit word to append has to be specified (when lower than 64).
	 * @param bits bits to append to the bit vector
	 * @param length length of the bit word to append
	 * @author William Gorge and Benoit Sordet
	 */
	public void append(long bits, int length) {
		
		// gets the old datum, remove it and update it
		long oldDatum = 0;
		if(!vector.isEmpty()) {
			oldDatum = vector.get(vector.size() - 1);
			vector.remove(vector.size() - 1);
		}
		long add = (bits << ((long)Long.SIZE - length - index));
		vector.add(oldDatum | add);
		
		// Adds the folowing datum to the next slot
		if(index + length > Long.SIZE) {
			
			// Adding the rest of the datum
			vector.add(bits << ((long) Long.SIZE - index));
			
			// Updating the index
			index = index + length - Long.SIZE;
		}
		// Normal incrementation otherwise
		else index += length;
		
		size += length;

	}
	
	/**
	 * Appends bits to the bit vector assuming the length of the datum to add is Long.SIZE (typically 64)<br>
	 * This method has to be used only when the length of the bit word to append is 64 (Long.SIZE).
	 * @param bits bits to append to the bit vector
	 * @see Long
	 * @author William Gorge and Benoit Sordet
	 */
	public void append(long bits) {
		vector.add(bits);
		index = 0;
		size += Long.SIZE;
	}
	
	/**
	 * Deletes the last nbBitsToDelete bits in the vector. It cannot delete more than Long.SIZE (64) bits.<br>
	 * Only used in BW(H/V)Column but can be used elsewhere.
	 * @param nbBitsToDelete Number of bits to delete from the end of the vector. If greater than Long.SIZE (64), it takes the value Long.SIZE (64).
	 * @author William Gorge and Benoit Sordet
	 * @see BWHColumn
	 * @see Long
	 */
	public void deleteEnd(int nbBitsToDelete) {
		
		// Check if the number of bits to delete is too high
		// We did this to symplify the code
		if(nbBitsToDelete > Long.SIZE) 
			nbBitsToDelete = Long.SIZE;
		
		
		// Case when a slot has to be removed
		if(nbBitsToDelete > index) {
			vector.remove(vector.size() - 1);
			index = Long.SIZE;
		}
		
		
		// Updating arguments
		size -= nbBitsToDelete;
		index -= nbBitsToDelete;
		
		// Building a mask to put to zero the ze
		long mask =  1;
		for(int i = 1; i < index; ++i) {
			mask <<= 1;
			mask |= 1L;
		}
		mask <<= (Long.SIZE - index);
		
		long maskedLong = vector.get(vector.size() - 1) & mask;
		vector.set(vector.size() - 1, maskedLong);
	}

	
	/**
	 * Gets the bit vector as an array of long
	 * @return bit vector
	 * @author William Gorge and Benoit Sordet
	 */
	public ArrayList<Long> getVector() {
		return vector;
	}
	
	/**
	 * Returns the size of the vector
	 * @return size
	 * @author William Gorge and Benoit Sordet
	 */
	public int size() {
		return size;
	}
	
	/**
	 * Gets the bit of the specified index
	 * @return bit vector
	 * @author William Gorge and Benoit Sordet
	 */
	public boolean getBit(int index) {
		
		long mask = 1;
		mask <<= (Long.SIZE - index);
		
		boolean value = ((vector.get(index/Long.SIZE) & mask) != 0);
		
		return value;
	}
	
	
	/**
	 * Displays the bit vector
	 */
	public void print() {
		
		// Computes the number of data in the last slot
		int rest = size - (size/Long.SIZE) * Long.SIZE;
		
		// Display...
		for(int i = 0; i < vector.size() - 1; ++i) {
			System.out.print(longtobitsString(vector.get(i)));
		}
		System.out.print(longtobitsString(vector.get(vector.size() - 1)).substring(0, rest));
		System.out.print("\n");
	}
	
	/**
	 * Performs the logical operation "and" between the object and the argument
	 * @param bv Other bit vector to perform the operation with
	 * @return the result of the operation
	 * @author William Gorge and Benoit Sordet
	 */
	public BitVector and(BitVector bvOther) {
		
		// Result returned
		BitVector bvResult = new BitVector();
		
		// Itteration on all the slots
		for(int i = 0; i < vector.size() - 1 && i < bvOther.getVector().size() - 1; ++i) {
			
			// Compute and add it to the bit vector
			long tempResult = this.vector.get(i) & bvOther.getVector().get(i);
			bvResult.append(tempResult);
		}
		bvResult.setSize(Math.max(size, bvOther.size()));
		return bvResult;
	}
	
	
	/**
	 * Performs the logical operation "or" between the object and the argument
	 * @param bv Other bit vector to perform the operation with
	 * @return the result of the operation
	 * @author William Gorge and Benoit Sordet
	 */
	public BitVector or(BitVector bvOther) {
		
		// Result returned
		BitVector bvResult = new BitVector();
		
		// Itteration on all the slots
		for(int i = 0; i < vector.size() && i < bvOther.getVector().size(); ++i) {
			
			// Compute and add it to the bit vector
			long tempResult = this.vector.get(i) | bvOther.getVector().get(i);
			bvResult.append(tempResult);
		}
		bvResult.setSize(Math.max(size, bvOther.size()));
		return bvResult;
	}
}
