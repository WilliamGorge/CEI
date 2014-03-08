import java.util.LinkedList;

/**
 * Class that represents Bit Vectors (ie bit arrays of unknown dimention).<br>
 * @author William Gorge and Benoit Sordet
 */
public class BitVector {
	
	// Vector of bits, made of an arrayList of slots of the format "Long" ie slots of 64 bits
	private LinkedList<Long> vector;
	
	// Size of the vector
	private int size;
	
	// Index that indicates the next free bit in a slot of the vector. Always lover than 64.
	private int index;
	
	/**
	 * Constructor for BitVector.
	 */
	BitVector() {
		vector = new LinkedList<Long>();
		size = 0;
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
	
	/**
	 * Sets the size of the bit vector, used for logical operations in the bit vector
	 * @param newSize new size of the bit vector
	 * @author William Gorge and Benoit Sordet
	 */
	private void setSize(int newSize) {
		size = newSize;
	}
	
	/**
	 * Appends bits to the bit vector, the length of the bit word to append has to be specified (when lower than Long.SIZE ie 64).
	 * @param bits bits to append to the bit vector
	 * @param length length of the bit word to append
	 * @throws IllegalArgumentException when the length given is greater than Long.SIZE bits (ie 64)
	 * @author William Gorge and Benoit Sordet
	 */
	public void append(long bits, int length) {
		
		if(length > Long.SIZE) throw new IllegalArgumentException("Cannot append word that is longer than " + Long.SIZE +" bits, length given is " + length);
		
		// Init of the old slot, if it doesnt exists, is 0
		long oldSlot = 0;
		
		// Gets the old slot and remove it
		if(!vector.isEmpty()) oldSlot = vector.removeLast();
		
		// Adds the bits data to this slot, when the data can fit entirely in the slot
		if(index + length <= Long.SIZE) {
			
			// Building the long to add to the current slot
			long addSlot = (bits >>> index);
			
			// Adding it to the current slot
			vector.add(oldSlot | addSlot);
			
			// Updating the index
			index += length;
			
			// If the index gets outside a slot
			if(index == 64) {
				index = 0;
				vector.add(0L);
			}
		}
		
		// Adds the bits data to this slot and to the next one, when the data cannot fit entirely in the slot
		else  {
			
			// Building the long to add to the current slot
			long addSlot = (bits >>> index);
			
			// Adding it to the current slot
			vector.add(oldSlot | addSlot);
			
			// Computes the over taking
			long overtaking = length + index - Long.SIZE;
			
			// Adding the rest of the datum to the next slot
			vector.add(bits << (length - overtaking));
			
			// Updating the index
			index += length - Long.SIZE;
		}
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
	 * @param nbBitsToDelete Number of bits to delete from the end of the vector. If greater than Long.SIZE (64), 
	 * it takes the value Long.SIZE (64).If greater than the size of the vector, clears the vector.
	 * @author William Gorge and Benoit Sordet
	 * @see BWHColumn
	 * @see Long
	 */
	public void deleteEnd(int nbBitsToDelete) {
		
		if(nbBitsToDelete == 0) return;
		
		if(nbBitsToDelete > size) {
			clear();
			return;
		}
		
		// Check if the number of bits to delete is too high
		// We did this to symplify the code
		if(nbBitsToDelete > Long.SIZE) 
			nbBitsToDelete = Long.SIZE;

		// Updating the size argument
		size -= nbBitsToDelete;
		
		// Case when a slot has to be removed
		if(nbBitsToDelete > index) {
			vector.removeLast();
			index = index - nbBitsToDelete + Long.SIZE;
		}
		else
			index -= nbBitsToDelete;
			
		
		// Building a mask to put to zero the remaining bits of the slot
		long mask =  1L;
		for(int i = 1; i < index; ++i) {
			mask <<= 1;
			mask |= 1L;
		}
		mask <<= (Long.SIZE - index);
		
		// Applying this mask to the slot
		long maskedLong = vector.removeLast() & mask;
		vector.add(maskedLong);
	}

	
	/**
	 * Gets the bit vector as an array of long
	 * @return bit vector
	 * @author William Gorge and Benoit Sordet
	 */
	public LinkedList<Long> getVector() {
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
		mask <<= (Long.SIZE - this.index);
		
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
		System.out.print(longtobitsString(vector.getLast()).substring(0, rest));
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
		for(int i = 0; i < size && i < bvOther.getVector().size(); ++i) {
			
			// Compute and add it to the bit vector
			long tempResult = this.vector.get(i) | bvOther.getVector().get(i);
			bvResult.append(tempResult);
		}
		bvResult.setSize(Math.max(size, bvOther.size()));
		return bvResult;
	}
	
	/**
	 * Compares two bit vectors (this and bvOther) and returns true if they are equal.
	 * @param bvOther other bit vector to compare with this bitVector
	 * @return the result of this comparaison
	 * @author William Gorge and Benoit Sordet
	 */
	public boolean equals(BitVector bvOther) {
		
		// If the vectos are not the same size the result is false
		if(size != bvOther.size())
			return false;
		
		return vector.equals(bvOther.getVector());
	}
	
	/**
	 * Removes all the elements from the vector.
	 * @author William Gorge and Benoit Sordet
	 */
	public void clear() {
		
		vector.clear();
		size = 0;
		index = 0;
	}
}
