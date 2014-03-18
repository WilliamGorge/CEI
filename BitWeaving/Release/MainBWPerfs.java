import java.util.ArrayList;


public class MainBWPerfs {
	
	static long resultOne = 1L << (Long.SIZE - 1);
	

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		/*
		for(int k = 1; k <= 63; ++k)
			testQueryExample0(k, ColumnType.BWH, "<", 1); // Query "< 1" sur une colonne BWH avec k qui varie

		for(int k = 1; k <= 64; ++k)
			testQueryExample0(k, ColumnType.BWV, "<", 1); // Query "< 1" sur une colonne BWV avec k qui varie
		
		for(int k = 1; k <= 63; ++k)
			testAddExample0(k, ColumnType.BWH);
		
		for(int k = 1; k <= 64; ++k)
			testAddExample0(k, ColumnType.BWV);*/
		


		System.out.println("\n\n\n\n\n\n***\nPERFORMANCE TEST TOWARDS THE NUMBER OF DATA\n****\n");
		
		for(int nbDataMultipilucator = 1; nbDataMultipilucator < 50; ++nbDataMultipilucator)
			testComplexQueryExample0(nbDataMultipilucator*300, 0, ColumnType.BWH);
		
		for(int nbDataMultipilucator = 1; nbDataMultipilucator < 50; ++nbDataMultipilucator)
			testComplexQueryExample0(nbDataMultipilucator*300, 0, ColumnType.BWV);
		
		
		/*
		System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n***\nPERFORMANCE TEST TOWARDS THE SIZE OF DATA\n***\n");
		
		for(int kAdd = 0; kAdd <= 31; ++kAdd)
			testComplexQueryExample0(1000000, kAdd, ColumnType.BWH);
		
		for(int kAdd = 0; kAdd <= 31; ++kAdd)
			testComplexQueryExample0(1000000, kAdd, ColumnType.BWH);
			*/

		System.out.println("\n\n\nPERFORMANCE TEST FINISHED\n");
	}
	
	/**
	 * Returns the binary string with zeros of the long l
	 * This is a little modification of Long.toBinaryString(long)
	 * @param long l: long to convert to a string of bits
	 * @author William Gorge
	 */
	static String longtobitsString(long l){
		String s = "";
		for(int i = 0; i < Long.numberOfLeadingZeros(l); ++i) {
			s += "0";
		}
		if(l != 0) s += Long.toBinaryString(l);
		return s;
	}

	
	
	/******************************* EXAMPLE 0: QUERY PERFORMANCE ***************************/
	/**
	 * 
	 * Test for the performance of query for example 0 of the documentation.
	 * A random column is created and many queries are applied to it to measure performance
	 * @param k Size of one datum
	 * @param columnTypeTested Type of the column tested
	 * @param query query to apply to the column. Ex: "<" , "=" , "!=" , ">="
	 * @param cst constant for this query
	 * @author William Gorge and Benoit Sordet
	 * @throws Exception
	 */
	
	static void testQueryExample0(int k, ColumnType columnTypeTested, String query, int cst) throws Exception {
		

		System.out.println("\n\n\n*********** Query, k=" + k + ", columnTypeTested=" + columnTypeTested + " ***********\n");
		
		int w = 64;
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
			result = store.query("Column1 " + query + " " + cst);
			
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
		System.out.println("Average time per data in column:\n\n" + 
				   
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
			System.out.println("\n!!!!!!! Test FAILED !!!!!!!");
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
	
	static void testAddExample0(int k, ColumnType columnTypeTested) throws Exception {
		

		System.out.println("\n\n\n*********** Add, k=" + k + "  columnTypeTested=" + columnTypeTested + " ***********\n");
		
		int w = 64;
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
		
		/******** DISPLAY THE PERFORMANCE *******/
		System.out.println("Average time per data in column:\n\n" + 
				   
				   "	" + columnTypeTested + " Add one datum ------ " + (((float) timeTotalAdd)/((float)( nbAdd*columnInitialLength))) + " ns per data in column\n");
		
		
		/***************** NAIVE and BW QUERY to check if it works good *****************/
		// Measuring time
		long cst = (long) (Math.random()*Math.pow(2, k));
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
			System.out.println("\n!!!!!!! Test FAILED !!!!!!!");
		}
	}
	
	public static void testComplexQueryExample0(int nbData, int kAdd, ColumnType columnTypeTested) throws Exception {
		

		System.out.println("\n\n\n\n\n\n\n*********** Complex query  nbData=" + nbData +"  kAdd=" + kAdd + "  columnTypeTested=" + columnTypeTested  + "***********\n");
		
		int w = 64;
		String query = "Column1 < 100 or Column2 = 10 or Column3 = 0 and Column4 >= 1 or Column5 < 100";
		
		int columnInitialLength = nbData;
		int nbQueries = 100;
		int nbQueriesWarmUp = 20;
		
		int[] kTab = new int[5];
		kTab[0] = 16 + kAdd;
		kTab[1] = 8 + kAdd;
		kTab[2] = 1 + kAdd;
		kTab[3] = 3 + kAdd;
		kTab[4] = 32 + kAdd;
	
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
