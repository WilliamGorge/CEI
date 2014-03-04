import java.util.ArrayList;

import org.junit.Test;

public class TestBW {
	
	
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
	
	
	
	
	
	
	
	
	/************************************* EXAMPLE 1: QUERY *****************************/
	/**
	 * 
	 * Test for query on example 1 of the documentation. This is a simple test to check if the API works good.
	 * @author William Gorge and Benoit Sordet
	 * @throws Exception
	 */
	@Test
	public void testQueryExemple1() throws Exception {
	
		System.out.println("\n\n\n\n\n\n\n*********** test Query Example1 ***********\n");
		
		int w = 8;
		int k = 3;
		ArrayList<Long> column = new ArrayList<Long>();

		// Building the different values of the column (native, NOT BW)
		column.add(1L);
		column.add(5L);
		column.add(2L);
		column.add(1L);
		column.add(6L);
		column.add(4L);
		column.add(0L);
		column.add(7L);
		column.add(4L);
		column.add(3L);
		
				
		try {
			
			// Display data
			System.out.println("Column:");
			for(int i = 0; i < column.size(); ++i) 
				System.out.println("  	" + longtobitsString(column.get(i)).substring(Long.SIZE - k));
			
			System.out.println();
		
			// Initialization store
			BWStore store = new BWStore(w);
			
			// Loading store
			store.addColumn("Column1", ColumnType.BWH, k);
			for(int i = 0; i < column.size(); ++i) {
				store.addDatum(column.get(i), "Column1");
			}
			
			// Diplay the processor words
			store.printProcessorWords("Column1");
			
			// Query with BW
			BitVector result = store.query("Column1 < 5");
			
			// Naive Query
			BitVector resultWanted = new BitVector();
			for(int i = 0; i < column.size(); ++i) {
				if(column.get(i) < 5)
					resultWanted.append(1, 1);
				else
					resultWanted.append(0, 1);
			}
			
			// Display result
			System.out.println("Result of query:");
			System.out.print("	Wanted:   ");
			resultWanted.print();
			System.out.print("	Obtained: ");
			result.print();
			
			System.out.println();
			
			// Check the result
			if(result.equals(resultWanted))
				System.out.println("-- Test Successful --");
			else
				System.out.println("-- Test Failed --");
		
		} catch(Exception ex) {
			System.out.println("Error during test \"Query Example1\"");
			ex.printStackTrace();
		}
	}
	
	
	
	
	
	
	
	
	/************************************* EXAMPLE 1: ADD AND QUERY *****************************/
	/**
	 * 
	 * Test for add for example 1 of the documentation. This is a simple test to check if the API works good.
	 * @author William Gorge and Benoit Sordet
	 * @throws Exception
	 */
	@Test
	public void testAddAndQueryExemple1() throws Exception {
		

		System.out.println("\n\n\n\n\n\n\n*********** test Add And Query Example1 ***********\n");
		
		
		try {
			
			int w = 8;
			int k = 3;
			ArrayList<Long> column = new ArrayList<Long>();

			// Building the different values of the column (native, NOT BW)
			column.add(1L);
			column.add(5L);
			column.add(2L);
			column.add(1L);
			column.add(6L);
			column.add(4L);
			column.add(0L);
			column.add(7L);
			column.add(4L);
			column.add(3L);
			
			
			ArrayList<Long> dataToAdd = new ArrayList<Long>();
			dataToAdd.add(7L);
			dataToAdd.add(1L);
			
			// Display data
			System.out.println("Column:");
			for(int i = 0; i < column.size(); ++i) 
				System.out.println("  	" + longtobitsString(column.get(i)).substring(Long.SIZE - k));
		
			// Initialization store
			BWStore store = new BWStore(w);
			
			// Loading store
			store.addColumn("Column1", ColumnType.BWH, k);
			for(int i = 0; i < column.size(); ++i) {
				store.addDatum(column.get(i), "Column1");
			}
			
			// Adding the data from dataToAdd
			for(int i = 0; i < dataToAdd.size(); ++i) {
				store.addDatum(dataToAdd.get(i), "Column1");
			}
			
			// Adding it to the normal column
			for(int i = 0; i < dataToAdd.size(); ++i) {
				column.add(dataToAdd.get(i));
			}

			// Diplay the processor words
			store.printProcessorWords("Column1");
			
			// Query with BW
			BitVector result = store.query("Column1 < 5");
			
			// Naive Query
			BitVector resultWanted = new BitVector();
			for(int i = 0; i < column.size(); ++i) {
				if(column.get(i) < 5)
					resultWanted.append(1, 1);
				else
					resultWanted.append(0, 1);
			}
			
			// Display result
			System.out.println("Result of query:");
			System.out.print("	Wanted:   ");
			resultWanted.print();
			System.out.print("	Obtained: ");
			result.print();
			
			System.out.println();
			
			// Check the result
			if(result.equals(resultWanted))
				System.out.println("-- Test Successful --");
			else
				System.out.println("-- Test Failed --");
		
		} catch(Exception ex) {
			System.out.println("Error during test \"Query and Add Example1\"");
			ex.printStackTrace();
		}
	}
	
	
	
	
	
	
	/************************************* EXAMPLE 2: QUERY *****************************/
	/**
	 * 
	 * Test for query for example 2 of the documentation. This is a simple test to check if the API works good.
	 * @author William Gorge and Benoit Sordet
	 * @throws Exception
	 */
	@Test
	public void testQueryExemple2() throws Exception {
		

		System.out.println("\n\n\n\n\n\n\n*********** test Query Example2 ***********\n");
		
		int k = 4;
		int w = 8;
		ArrayList<Long> column = new ArrayList<Long>();
		
		// Building the different values of the column (native, NOT BW)
		column.add(9L);
		column.add(5L);
		column.add(8L);
		column.add(1L);
		column.add(15L);
		column.add(4L);
		column.add(11L);
		column.add(0L);
		column.add(3L);
		column.add(4L);
		
		// Display data
		System.out.println("Column:");
		for(int i = 0; i < column.size(); ++i) 
			System.out.println("  	" + longtobitsString(column.get(i)).substring(Long.SIZE - k));
		
		System.out.println();
		
		// Initialization store
		BWStore store = new BWStore(w);
		
		// Loading store
		store.addColumn("Column1", ColumnType.BWH, k);
		for(int i = 0; i < column.size(); ++i) {
			store.addDatum(column.get(i), "Column1");
		}
		
		// Diplay the processor words
		store.printProcessorWords("Column1");
		
		// Query with BW
		BitVector result = store.query("Column1 < 5");
		
		// Naive Query
		BitVector resultWanted = new BitVector();
		for(int i = 0; i < column.size(); ++i) {
			if(column.get(i) < 5)
				resultWanted.append(1, 1);
			else
				resultWanted.append(0, 1);
		}
		
		// Display result
		System.out.println("Result of query:");
		System.out.print("	Wanted:   ");
		resultWanted.print();
		System.out.print("	Obtained: ");
		result.print();
		
		System.out.println();
		
		// Check the result
		if(result.equals(resultWanted))
			System.out.println("-- Test Successful --");
		else
			System.out.println("-- Test Failed --");

	}
	
	/************************************* EXAMPLE 3: QUERY  *****************************/
	/**
	 * 
	 * Test for query for example 3 of the documentation. This is a simple test to check if the API works good.
	 * @author William Gorge and Benoit Sordet
	 * @throws Exception
	 */
	@Test
	public void testQueryExemple3() throws Exception {
		

		System.out.println("\n\n\n\n\n\n\n*********** test Query Example3 ***********\n");
		
		int k = 2;
		int w = 8;
		ArrayList<Long> column = new ArrayList<Long>();
		
		// Building the different values of the column (native, NOT BW)
		column.add(0L);
		column.add(2L);
		column.add(3L);
		column.add(1L);
		column.add(1L);
		column.add(0L);
		column.add(3L);
		column.add(2L);
		column.add(0L);
		column.add(1L);
		
			
		// Display data
		System.out.println("Column:");
		for(int i = 0; i < column.size(); ++i) 
			System.out.println("  	" + longtobitsString(column.get(i)).substring(Long.SIZE - k));
		
		System.out.println();
		
		// Initialization store
		BWStore store = new BWStore(w);
		
		// Loading store
		store.addColumn("Column1", ColumnType.BWH, k);
		for(int i = 0; i < column.size(); ++i) {
			store.addDatum(column.get(i), "Column1");
		}
		
		// Diplay the processor words
		store.printProcessorWords("Column1");
		
		// Query with BW
		BitVector result = store.query("Column1 < 2");
		
		// Naive Query
		BitVector resultWanted = new BitVector();
		for(int i = 0; i < column.size(); ++i) {
			if(column.get(i) < 2)
				resultWanted.append(1, 1);
			else
				resultWanted.append(0, 1);
		}
		
		// Display result
		System.out.println("Result of query:");
		System.out.print("	Wanted:   ");
		resultWanted.print();
		System.out.print("	Obtained: ");
		result.print();
		
		System.out.println();
		
		// Check the result
		if(result.equals(resultWanted))
			System.out.println("-- Test Successful --");
		else
			System.out.println("-- Test Failed --");
	}
	
	
}
