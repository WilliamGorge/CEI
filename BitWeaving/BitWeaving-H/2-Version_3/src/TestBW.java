import java.util.ArrayList;
import org.junit.Test;

/**
 * Unitary tests for the BitWeaving API, using JUnit.
 * These tests are, in order:
 * 		Example 1: one column of 10 data, k = 3, w = 8, doing a query
 * 		Example 1: one column of 10 data, k = 3, w = 8, doing two adds
 * 		Example 2: one column of 10 data, k = 4, w = 8, doing a query
 * 		Example 3: one column of 10 data, k = 2, w = 8, doing a query
 * 		Example 0: one column of 2172184 data, k = 16, w = 64, doing a query
 * 		Example 0: one column of 2172184 data, k = 16, w = 64, doing a query
 * 		Example 4: 4 columns of 1000 data, k = 16, w = 64, doing a complex query
 * 		
 * @author William Gorge
 */
public class TestBW {
	
	ColumnType columnTypeTested = ColumnType.BWV;
	long resultOne = 1L << (Long.SIZE - 1);
	
	
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
		

		
		// Display data
		System.out.println("Column:");
		for(int i = 0; i < column.size(); ++i) 
			System.out.println("  	" + longtobitsString(column.get(i)).substring(Long.SIZE - k));
		
		System.out.println();
	
		// Initialization store
		BWStore store = new BWStore(w);
		
		// Loading store
		store.addColumn("Column1", columnTypeTested, k);
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
				resultWanted.append(resultOne, 1);
			else
				resultWanted.append(0, 1);
		}
		
		// Display result
		System.out.println("Result of query Column1 < 5:");
		System.out.print("	Wanted:   ");
		resultWanted.print();
		System.out.print("	Obtained: ");
		result.print();
		
		System.out.println();
		
		// Check the result
		if(result.equals(resultWanted))
			System.out.println("-- Test Successful --");
		else {
			System.out.println("-- Test Failed --");
			throw new Exception("Test failed");
		}
		
	}
	
	

	
	/************************************* EXAMPLE 1: ADD AND QUERY *****************************/
	/**
	 * 
	 * Test for add for example 1 of the documentation. This is a simple test to check if the API works good.
	 * @author William Gorge and Benoit Sordet
	 * @throws Exception
	 */

	
	public void testAddAndQueryExemple1() throws Exception {
		

		System.out.println("\n\n\n\n\n\n\n*********** test Add And Query Example1 ***********\n");
		

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
	
		// Initialization store
		BWStore store = new BWStore(w);
		
		// Loading store
		store.addColumn("Column1", columnTypeTested, k);
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
		
		// Display data
		System.out.println("Column:");
		for(int i = 0; i < column.size(); ++i) 
			System.out.println("  	" + longtobitsString(column.get(i)).substring(Long.SIZE - k));

		// Diplay the processor words
		store.printProcessorWords("Column1");
		
		// Query with BW
		BitVector result = store.query("Column1 < 5");
		
		// Naive Query
		BitVector resultWanted = new BitVector();
		for(int i = 0; i < column.size(); ++i) {
			if(column.get(i) < 5)
				resultWanted.append(resultOne, 1);
			else
				resultWanted.append(0, 1);
		}
		
		// Display result
		System.out.println("Result of query Column1 < 5:");
		System.out.print("	Wanted:   ");
		resultWanted.print();
		System.out.print("	Obtained: ");
		result.print();
		
		System.out.println();
		
		// Check the result
		if(result.equals(resultWanted))
			System.out.println("-- Test Successful --");
		else {
			System.out.println("-- Test Failed --");
			throw new Exception("Test failed");
		}
	

	}
	
	
	
	
	/************************************* EXAMPLE 2: QUERY *****************************/
	/**
	 * 
	 * Test for query for example 2 of the documentation. This is a simple test to check if the API works good.
	 * @author William Gorge and Benoit Sordet
	 * @throws Exception
	 */

	
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
		store.addColumn("Column1", columnTypeTested, k);
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
				resultWanted.append(resultOne, 1);
			else
				resultWanted.append(0, 1);
		}
		
		// Display result
		System.out.println("Result of query Column1 < 5:");
		System.out.print("	Wanted:   ");
		resultWanted.print();
		System.out.print("	Obtained: ");
		result.print();
		
		System.out.println();
		
		// Check the result
		if(result.equals(resultWanted))
			System.out.println("-- Test Successful --");
		else {
			System.out.println("-- Test Failed --");
			throw new Exception("Test failed");
		}

	}
	
	
	
	
	/************************************* EXAMPLE 3: QUERY  *****************************/
	/**
	 * 
	 * Test for query for example 3 of the documentation. This is a simple test to check if the API works good.
	 * @author William Gorge and Benoit Sordet
	 * @throws Exception
	 */

	
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
		store.addColumn("Column1", columnTypeTested, k);
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
				resultWanted.append(resultOne, 1);
			else
				resultWanted.append(0, 1);
		}
		
		// Display result
		System.out.println("Result of query Column1 < 2:");
		System.out.print("	Wanted:   ");
		resultWanted.print();
		System.out.print("	Obtained: ");
		result.print();
		
		System.out.println();
		
		// Check the result
		if(result.equals(resultWanted))
			System.out.println("-- Test Successful --");
		else {
			System.out.println("-- Test Failed --");
			throw new Exception("Test failed");
		}
	}
	
	
	
	
	/******************************* EXAMPLE 0: QUERY  ***************************/
	/**
	 * 
	 * Test for the query for example 0 of the documentation.
	 * A random column is created and many queries are applied to it. Performance is measured
	 * @author William Gorge and Benoit Sordet
	 * @throws Exception
	 */

	@Test
	public void testQueryExample0() throws Exception {
		

		System.out.println("\n\n\n\n\n\n\n*********** Performance Query Example0 ***********\n");
		
		int k = 36;
		int w = 64;
		int cst = 1;
		int columnInitialLength = 2172184;
		int nbQueries = 100;
		int nbQueriesWarmUp = 20;
		
		ArrayList<Long> column = new ArrayList<Long>();
		long max = (long) (Math.pow(2, k) - 1);
		
		// Initialization store
		BWStore store = new BWStore(w);
		
		// Creating column
		store.addColumn("Column1", columnTypeTested, k);
		
		// Create the data and load the store
		for(int i = 1; i < columnInitialLength; ++i) {
			
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
				if(column.get(i)  < cst)
					resultWanted.append(resultOne, 1);
				else
					resultWanted.append(0, 1);
			}
			
			// Measuring time
			if(n>= nbQueriesWarmUp) timeTotalNaiveQuery += System.nanoTime() - timeBeforeNaiveQuery;
		}
		
		/******** DISPLAY THE PERFORMANCE *******/
		System.out.println("\n\n" + 
				   "Average time per data in column:\n\n" + 
				   
				   "	" + columnTypeTested + " Query ----------- " + (((float) timeTotalQuery)/((float)( nbQueries*columnInitialLength))) + " ns per data in column\n" +
				   
				   "	Naive Query --------- " + ((float) timeTotalNaiveQuery)/((float) nbQueries*columnInitialLength) + " ns per data in column\n");

		
		/****** CHECK THE RESULT ******/		
		// Check the result
		if(result.equals(resultWanted)) {
			System.out.println("-- Test Successful --");
		}
		else {
			for(int i = 0; i < result.getVector().size(); ++i) {

				if(result.getVector().get(i).longValue() !=  resultWanted.getVector().get(i).longValue()) {
						System.out.println(" -- FAIL here: from result " + i*Long.SIZE + " to " + + ((i+1)*Long.SIZE-1) + " ---");
						System.out.println("	Wanted:   " + longtobitsString(resultWanted.getVector().get(i)));
						System.out.println("	Obtained: " + longtobitsString(result.getVector().get(i)));
						System.out.println();
				}
			}
			System.out.println("\n-- Test Failed --");
			throw new Exception("Test failed");
		}
	}
	
	
	
	
	
	/******************************* EXAMPLE 0: ADD PERFORMANCE ***************************/
	/**
	 * 
	 * Test for the performance of add for example 0 of the documentation.
	 * A random column is created and many queries are applied to it to measure performance
	 * @author William Gorge and Benoit Sordet
	 * @throws Exception
	 */

	
	public void testAddExample0() throws Exception {
		

		System.out.println("\n\n\n\n\n\n\n*********** Performance Add Example0 ***********\n");
		
		int k = 16;
		int w = 64;
		int cst = 10000;
		int columnInitialLength = 2172184;
		int nbAdd = 100;
		int nbAddWarmUp = 20;
		
		ArrayList<Long> column = new ArrayList<Long>();
		long max = (long) (Math.pow(2, k) - 1);
		
		// Initialization store
		BWStore store = new BWStore(w);
		
		// Creating column
		store.addColumn("Column1", columnTypeTested, k);
		
		// Create the data and load the store
		for(int i = 1; i < columnInitialLength; ++i) {
			
			// Generate the data and memorizing it with native ArrayList
			long datumGenerated = (long) (Math.random()*max);
			column.add(datumGenerated);
			
			// Loading store at the same time
			store.addDatum(datumGenerated, "Column1");
			
		}
		
		/***************** BW ADD *****************/
		// Measuring time
		long timeTotalAdd = 0;
		
		// Query loop 
		for(int i = 0; i < nbAdd + nbAddWarmUp; ++i) {
			
			// Generate the data and memorizing it with native ArrayList
			long datumGenerated = (long) (Math.random()*max);
			column.add(datumGenerated);
			
			// Measuring time
			long timeBeforeAdd = System.nanoTime();
	
			// Doing the query
			store.addDatum(datumGenerated, "Column1");
			
			// Measuring time
			if(i >= nbAddWarmUp) timeTotalAdd += System.nanoTime() - timeBeforeAdd;
		}
		
		// Measuring time
		BitVector result = store.query("Column1 < " + cst);
		BitVector resultWanted = new BitVector();

		// Clear the result for the next query
		resultWanted.clear();
		
		// Naive query
		for(int i = 0; i < column.size(); ++i) {
			if(column.get(i)  < cst)
				resultWanted.append(resultOne, 1);
			else
				resultWanted.append(0, 1);
		}
		
		
		/******** DISPLAY THE PERFORMANCE *******/
		System.out.println("Average time per data in column:\n\n" + 
				   
				   "	" + columnTypeTested + " Add one datum ------ " + (((float) timeTotalAdd)/((float)( nbAdd*columnInitialLength))) + " ns per data in column\n");

		/****** CHECK THE RESULT ******/		
		// Check the result
		if(result.equals(resultWanted)) {
			System.out.println("-- Test Successful --");
		}
		else {
			for(int i = 0; i < result.getVector().size(); ++i) {

				if(result.getVector().get(i).longValue() !=  resultWanted.getVector().get(i).longValue()) {
						System.out.println(" -- FAIL here: from result " + i*Long.SIZE + " to " + + ((i+1)*Long.SIZE-1) + " ---");
						System.out.println("	Wanted:   " + longtobitsString(resultWanted.getVector().get(i)));
						System.out.println("	Obtained: " + longtobitsString(result.getVector().get(i)));
						System.out.println();
				}
			}
			System.out.println("\n-- Test Failed --");
			throw new Exception("Test failed");
		}
	}
	
	
	
	
	
	
	/******************************* EXAMPLE 0: ADD PERFORMANCE ***************************/
	/**
	 * 
	 * Test for the performance of add for example 0 of the documentation.
	 * A random column is created and many queries are applied to it to measure performance
	 * @author William Gorge and Benoit Sordet
	 * @throws Exception
	 */

	@Test
	public void testComplexQueryExample0() throws Exception {
		

		System.out.println("\n\n\n\n\n\n\n*********** Complex query Example0 ***********\n");
		
		int w = 64;
		String query = "Column1 < 100 or Column2 = 10 or Column3 = 0 and Column4 >= 1 or Column5 < 100";
		
		int columnInitialLength = 1000;
		int nbQueries = 100;
		int nbQueriesWarmUp = 20;
		
		int[] kTab = new int[5];
		kTab[0] = 16;
		kTab[1] = 8;
		kTab[2] = 1;
		kTab[3] = 3;
		kTab[4] = 32;
	
		ArrayList<ArrayList<Long>> columns = new ArrayList<ArrayList<Long>>();
			
		// Initialization store
		BWStore store = new BWStore(w);
		
		for(int n = 0; n < kTab.length; ++n) {
			
			long max = (long) (Math.pow(2, kTab[n]) - 1);
			
			// Creating column
			store.addColumn("Column" + (n+1), columnTypeTested, kTab[n]);
			
			// Creating raw column
			ArrayList<Long> column = new ArrayList<Long>();
			
			// Create the data and load the store
			for(int i = 0; i < columnInitialLength - 1; ++i) {
				
				// Generate the data and memorizing it with native ArrayList
				long datumGenerated = (long) (Math.random()*max);
				column.add(datumGenerated);
				
				// Loading store at the same time
				store.addDatum(datumGenerated, "Column" + (n+1));
			}
			columns.add(column);
		}
		
		/***************** BW Complex Query *****************/
		// Measuring time
		long timeTotalQuery = 0;
		BitVector result = new BitVector();
		
		// Query loop 
		for(int i = 0; i < nbQueries + nbQueriesWarmUp; ++i) {
			
			// Measuring time
			long timeBeforeQuery = System.nanoTime();
	
			// Doing the query
			result = store.query(query);
			
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
			// Column1 < 100 or Column2 = 10 or Column3 = 0 and Column4 >= 1 or Column5 < 100"
			for(int i = 0; i < columnInitialLength - 1; ++i) {
				if((((columns.get(0).get(i) < 100 
						|| columns.get(1).get(i) == 10)
						|| columns.get(2).get(i) == 0 )
						&& columns.get(3).get(i) >=1 )
						|| columns.get(4).get(i) < 100 )
					resultWanted.append(resultOne, 1);
				else
					resultWanted.append(0, 1);
			}
			
			// Measuring time
			if(n>= nbQueriesWarmUp) timeTotalNaiveQuery += System.nanoTime() - timeBeforeNaiveQuery;
		}
		
		/******** DISPLAY THE PERFORMANCE *******/
		System.out.println("\n\n" + 
				   "Average time per data in column:\n\n" + 
				   
				   "	" + columnTypeTested + " Complex Query ------ " + (((float) timeTotalQuery)/((float)( nbQueries*columnInitialLength))) + " ns per data in column\n" +
				   
				   "	Naive Query --------- " + ((float) timeTotalNaiveQuery)/((float) nbQueries*columnInitialLength) + " ns per data in column\n");

		
		/****** CHECK THE RESULT ******/		
		// Check the result
		if(result.equals(resultWanted)) {
			System.out.println("-- Test Successful --");
		}
		else {
			for(int i = 0; i < result.getVector().size(); ++i) {

				if(result.getVector().get(i).longValue() !=  resultWanted.getVector().get(i).longValue()) {
						System.out.println(" -- FAIL here: from result " + i*Long.SIZE + " to " + + ((i+1)*Long.SIZE-1) + " ---");
						System.out.println("	Wanted:   " + longtobitsString(resultWanted.getVector().get(i)));
						System.out.println("	Obtained: " + longtobitsString(result.getVector().get(i)));
						System.out.println();
				}
			}
			System.out.println("\n-- Test Failed --");
			throw new Exception("Test failed");
		}
		
				   
	}
}
