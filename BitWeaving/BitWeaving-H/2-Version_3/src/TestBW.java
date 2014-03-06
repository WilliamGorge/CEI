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
	public void testQueryExample3() throws Exception {
		

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
	
	/******************************* EXAMPLE 0: QUERY PERFORMANCE ***************************/
	/**
	 * 
	 * Test for query for example 0 of the documentation.
	 * A random column is created and many queries are applied to it to measure performance
	 * @author William Gorge and Benoit Sordet
	 * @throws Exception
	 */
	@Test
	public void testQueryExample0() throws Exception {
		

		System.out.println("\n\n\n\n\n\n\n*********** Performance Query Example0 ***********\n");
		
		int k = 16;
		int w = 32;
		int cst = 10000;
		int columnInitialLength = 2100000;
		int nbQueries = 100;
		int nbQueriesWarmUp = 20;
		
		ArrayList<Long> column = new ArrayList<Long>();
		long max = (long) (Math.pow(2, k) - 1);
		
		// Initialization store
		BWStore store = new BWStore(w);
		
		// Creating column
		store.addColumn("Column1", ColumnType.BWH, k);
		
		// Create the data and load the store
		for(int i = 0; i < columnInitialLength; ++i) {
			
			// Generate the data and memorizing it with native ArrayList
			long datumGenerated = (long) (Math.random()*max);
			column.add(datumGenerated);
			
			// Loading store at the same time
			store.addDatum(datumGenerated, "Column1");
			
		}
		
		/***************** BW QUERY *****************/
		// Measuring time
		long timeTotalQuery = 0;
		BitVector result = null;
		
		// Query loop 
		for(int i = 0; i < nbQueries + nbQueriesWarmUp; ++i) {
			
			// Measuring time
			long timeBeforeQuery = System.nanoTime();
	
			// Doing the query
			result = store.query("Column1 < " + cst);
			
			// Measuring time
			if(i >= nbQueriesWarmUp) timeTotalQuery += System.nanoTime() - timeBeforeQuery;
		}
		
		/***************** NAIVE QUERY *****************/
		// Measuring time
		long timeTotalNaiveQuery = 0;
		BitVector resultWanted = new BitVector();

		// Query loop
		for(int n = 0; n < nbQueries + nbQueriesWarmUp; ++n) {

			// Clear the result for the next query
			resultWanted.clear();
			
			// Measuring time
			long timeBeforeNaiveQuery = System.nanoTime();
			
			// Naive query
			for(int i = 0; i < column.size(); ++i) {
				if(column.get(i) < cst)
					resultWanted.append(1, 1);
				else
					resultWanted.append(0, 1);
			}
			
			// Measuring time
			if(n>= nbQueriesWarmUp) timeTotalNaiveQuery += System.nanoTime() - timeBeforeNaiveQuery;
		}
		
		
		/****** CHECK THE RESULT AND DISPLAY ******/		
		// Check the result
		if(result.equals(resultWanted)) {
			System.out.println("-- Test Successful --");
		}
		else {
			System.out.println("result size=" + result.getVector().size() + "      resultWanted size=" + resultWanted.getVector().size());
			for(int i = 0; i < result.getVector().size(); ++i) {

				if(result.getVector().get(i).longValue() !=  resultWanted.getVector().get(i).longValue()) {
						System.out.println(" -- FAIL here: from result " + i*Long.SIZE + " to " + + ((i+1)*Long.SIZE-1) + " ---");
						System.out.println("	Wanted:   " + longtobitsString(resultWanted.getVector().get(i)));
						System.out.println("	Obtained: " + longtobitsString(result.getVector().get(i)));
						System.out.println();
				}
			}
			System.out.println("\n-- Test Failed --");
		}
		
		System.out.println("\n\n" + 
				   "Average time per data in column:\n\n" + 
				   
				   "	BWH Query ----------- " + (((float) timeTotalQuery)/((float)( nbQueries*columnInitialLength))) + " ns per data in column\n" +
				   
				   "	Naive Query --------- " + ((float) timeTotalNaiveQuery)/((float) nbQueries*columnInitialLength) + " ns per data in column\n");

	}
	
}
