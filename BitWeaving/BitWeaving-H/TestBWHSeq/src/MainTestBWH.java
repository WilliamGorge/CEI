
import java.util.Vector;

/**
 * Main test class for BitWeavingH.<br>
 * This class contains the main functiobn that performs tests on the implemented BitWeavingH<br>
 * @param args: Nothing (yet)
 * @author William Gorge
 */
public class MainTestBWH {

	/**
	 * Returns the binary string with zeros of the long l
	 * This is a little modification of Long.toBinaryString(long)
	 * @param long l: long to convert to a string of bits
	 * @author William Gorge
	 */
	public static String longtobitsString(long l){
		String s = "";
		for(int i = 0; i < Long.numberOfLeadingZeros(l); ++i) {
			s += "0";
		}
		if(l != 0) s += Long.toBinaryString(l);
		return s;
	}
	
	/**
	 * Main test function for BitWeavingH
	 * Performs some tests on the classes implemented to check if it works good and to see the performance
	 * @param args: Nothing (yet)
	 * @author William Gorge
	 */
	public static void main(String[] args) {
	for(int kk = 1; kk < 36; ++kk) {
				
		/****************** VARIABLES DE TEST MODIFIABLES A SOUHAIT ********************/
		// Nombre de queries a faire a la suite
		int nbQueries = 300;
		
		// Nombre de queries a ignorer
		int nbQueriesIgnored = 50;
		
		// Proba. d'ajouter une donnee a chaque query
		float pbAdd = 0;
		
		// Definit de quel test il s'agit:
		// Exemple 1, 2 et 3 sont les exemples des slides.
		// L'exemple 1 correspond a celui de la publication
		// Exemple 0 est sur une colonne de nombres aleatoires, on peut faire varier les parameres
		int example = 0;	
		
		// Valeurs pour l'exemple 0
		// k0 = taille d'une donnee en bits
		// w0 = largeur du mot processeur
		// columnlength0 = taille de la colonne d'entiers aleatoires codes sur k0 bits
		// La requ�te est exprimee par queryName0 pour la constante cst0
		// 	ex: pour avoir toutes les donnees inferieures a 5: cst0 = 5 et queryName = "LESS THAN"
		// Differentes requ�tes disponibles: "DIFFERENT", "EQUAL", "LESS THAN", "LESS THAN OR EQUAL TO", "GREATER THAN", "GREATER THAN OR EQUAL TO"
		int k0 = kk;
		int w0 = 64;
		int cst0 = 1000;
		String queryName0 = "LESS THAN OR EQUAL TO";
		int columnlength0 = 3000000;
		
		// Indique si la colonne, les mots processeurs et les vecteurs de bits resultats doivent �tre affiches
		// Si a faux, les segments (mots processeurs et resultats) donnant un resultat incorrect seront quand m�me affiches
		boolean display = false;
		
		/******************** FIN DES VARIABLES MODIFIABLES ***********************/
		
		// Pour l'exemple 0, si k est trop grand
		if(k0 > w0 - 1) k0 = w0 - 1;
		
		// Pour l'exemple 0, si la constante donnee est invalide (ie. trop grande et ne peut �tre encodee sur k bits)
		if(cst0 > Math.pow(2, k0) - 1) cst0 = (int) (Math.pow(2, k0) - 1);

		
		// Initialisation variables de test (ces valeurs vont �tres modifiees suivant les exemples
		int cst = 0;
		int k = 0;
		int w = 0;
		int N = 0;
		int Ls = 0;
		int columnlength = 0;
		String queryName = null;
		long[] column = null;
		
		/********* Cas des exemples traites dans les slides ***************/
		// Exemple 1 (k = 3)
		if(example == 1) {
			k = 3;
			w = 8;
			cst = 5;
			queryName = "LESS THAN";
			N = w/(k+1);
			Ls = N*(k+1);
			columnlength = 10;
			column = new long[columnlength];

			column[0] = 1;
			column[1] = 5;
			column[2] = 6;
			column[3] = 1;
			column[4] = 6;
			column[5] = 4;
			column[6] = 0;
			column[7] = 7;
			column[8] = 4;
			column[9] = 3;
		}
			
		// Exemple 2 (k = 4)
		else if(example == 2) {
			k = 4;
			w = 8;
			cst = 5;
			queryName = "LESS THAN";
			N = w/(k+1);
			Ls = N*(k+1);
			columnlength = 10;
			column = new long[columnlength];
			
			column[0] = 9;
			column[1] = 5;
			column[2] = 8;
			column[3] = 1;
			column[4] = 15;
			column[5] = 4;
			column[6] = 11;
			column[7] =	0;
			column[8] = 3;
			column[9] = 4;
			
		}
		// Example 3 (k = 2)
		else if(example == 3) {
			k = 2;
			w = 8;
			cst = 2;
			queryName = "LESS THAN";
			N = w/(k+1);
			Ls = N*(k+1);
			columnlength = 10;
			column = new long[columnlength];

			column[0] = 0;
			column[1] = 2;
			column[2] = 3;
			column[3] = 0;
			column[4] = 3;
			column[5] = 2;
			column[6] = 0;
			column[7] = 3;
			column[8] = 2;
			column[9] = 1;
		}
		
		/*** Exemple sur une colonne de chiffres aleatoires (par defaut) ****/
		else {
			k = k0;
			w = w0;
			cst = cst0;
			queryName = queryName0.toString();
			N = w/(k+1);
			Ls = N*(k+1);
			columnlength = columnlength0;
			column = new long[columnlength];
			
			for(int i = 0; i < column.length; ++i) {
				column[i] = (long) (Math.random()*(Math.pow(2, k) - 1));
			}
		}
		
		// To measure the time elapsed during initialization
		long timeBeforeInit = System.nanoTime();
		
		/*** ENCODE THE QUERY TO AN INTEGER ***/
		Query query = null;
		if(queryName == "DIFFERENT") query = Query.DIFFERENT;
		else if(queryName == "EQUAL") query = Query.EQUAL;
		else if(queryName == "LESS THAN") query = Query.LESS_THAN;
		else if(queryName == "LESS THAN OR EQUAL TO") query = Query.LESS_THAN_OR_EQUAL_TO;
		else if(queryName == "GREATER THAN") query = Query.GREATER_THAN;
		else if(queryName == "GREATER THAN OR EQUAL TO") query = Query.GREATER_THAN_OR_EQUAL_TO;
		
		/*** INITIALIZATION OF THE BWH COLUMN ***/
		BitWeavingHInterface BWH = new BitWeavingH(column,k,w);
		
		// To measure the time elapsed during initialization
		long timeElapsedInit = System.nanoTime() - timeBeforeInit;
		
		// To measure total query time
		long timeElapsedQueryTotal = 0;
		
		// To measure total add time
		long timeElapsedAddTotal = 0;
		
		// To memorize the added data
		Vector<Long> addedData = new Vector<Long>();
		
		// Result bit vector
		long[] BVout = null;
		
		for(int i= 0; i < nbQueries; ++i) {
			
			// Query
			try {
				
				// Adding a random number of data in the column
				double toss = Math.random();
				if(toss <= pbAdd) {
					// Generate a random datum
					long addedDatum = (long) (Math.random()*(Math.pow(2, k) - 1));
					
					// Measuring time
					long timeBeforeAdd = System.nanoTime();
					
					/******************************* HERE IS ADD **********************************/
					BWH.add(addedDatum);
					
					/******************************* END OF ADD **********************************/
					
					// Measuring time
					if(i>=nbQueriesIgnored) timeElapsedAddTotal += System.nanoTime() - timeBeforeAdd;
					
					//Memorizing the data added to check if it works good
					addedData.add(new Long(addedDatum));
					
				}

			
				// To measure the time elapsed during query
				long timeBeforeQuery = System.nanoTime();
				
				/******************************* HERE IS THE QUERY **********************************/
		
				BVout = BWH.query(query, cst);
	
				
				/******************************* END OF THE QUERY **********************************/
				
				// To measure the time elapsed during query
				long timeElapsedQuery = System.nanoTime() - timeBeforeQuery;

				if(i>=nbQueriesIgnored) timeElapsedQueryTotal += timeElapsedQuery;
							
			
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
		
		BWH.complexQuery("LESS THAN 1000 OR EQUAL 2000 AND DIFFERENT 0");
		
		// Updating the colomn with the data added
		columnlength += addedData.size();
		long[] newColumn = new long[columnlength];
		
		for(int i = 0; i < column.length; ++i) {
			newColumn[i] = column[i];
		}
		for(int i = column.length, j = 0; i < columnlength; ++i) {
			newColumn[i] = addedData.get(j);
			++j;
		}
		column = newColumn;
		
		/************************ DISPLAY AND CHECKING THE RESULTS**************************/
		BWHSegment[] columnout = BWH.getColumn();
		int NbFullSegments = BWH.size()/Ls;
		int rest = BWH.size() - NbFullSegments*Ls;
		System.out.println("nbQueries=" + nbQueries + " nbQueriesIgnored=" + nbQueriesIgnored + " columnlength=" + columnlength + " queryName=\"" + queryName + "\" query=" + query + " cst=" + cst + "  k=" + k + "  w="+ w + "  N=" + N + "  Ls=" + Ls);
		System.out.println("\nNbFullSegments=" + NbFullSegments + "  rest=" + rest + "\n");
		
		// Display the column
		if(display) {
			System.out.println("Column: ");
			for(int i = 0; i < column.length; ++i) {
				System.out.println("	" + longtobitsString(column[i]).substring(64-k));
			}
		}
		
		// Processor words display
		if(display) System.out.println("\nProcessor words: \n");
		for(int n = 0; n < columnout.length; ++n) {
			if(display) System.out.println("	Processor words for segment" + (n+1));
			BWHSegment s = columnout[n];
			for(int i = 0; i<s.getProcessorWords().length; ++i) {
				if(display) System.out.println("	" + longtobitsString(s.getProcessorWords()[i]).substring(64-w));
			}
			if(display) System.out.println("");
		}
		
		/*** COMPUTING THE RESULT THAT WE WANT TO HAVE (naive method) ***/ 
		// Result bit vector that we want to have
		long[] BVoutWanted;
		if(rest >0) BVoutWanted = new long[NbFullSegments + 1];
		else BVoutWanted = new long[NbFullSegments];
		
		int count = 0;
		long timeNaiveMethod = 0;
		
		// Loop to warmup the JVM
		for(int p = 0; p < nbQueries; ++p) {
			
			// Measuring time
			long parseTimeNaiveMethod = System.nanoTime();
			
			for(int n = 0; n < NbFullSegments; ++n) {
				for(int i = 0; i < Ls; ++i) {
					BVoutWanted[n]<<=1;
					if(column[i+Ls*n] != cst && query== Query.DIFFERENT) {
						BVoutWanted[n] |= 1;
					}
					else if(column[i+Ls*n] == cst && query == Query.EQUAL) {
						BVoutWanted[n] |= 1;
					}
					else if(column[i+Ls*n] < cst && query == Query.LESS_THAN) {
						BVoutWanted[n] |= 1;
					}
					else if(column[i+Ls*n] <= cst && query == Query.LESS_THAN_OR_EQUAL_TO) {
						BVoutWanted[n] |= 1;
					}
					else if(column[i+Ls*n] > cst && query == Query.GREATER_THAN) {
						BVoutWanted[n] |= 1;
					}
					else if(column[i+Ls*n] >= cst && query == Query.GREATER_THAN_OR_EQUAL_TO) {
						BVoutWanted[n] |= 1;
					}
				}
			}
			// Special treat for the last and incomplete segment
			if(rest>0) {
				int n = NbFullSegments;
				for(int i = 0; i < rest; ++i) {
					BVoutWanted[n]<<=1;
					if(column[i+Ls*n] != cst && query== Query.DIFFERENT) {
						BVoutWanted[n] |= 1;
					}
					else if(column[i+Ls*n] == cst && query == Query.EQUAL) {
						BVoutWanted[n] |= 1;
					}
					else if(column[i+Ls*n] < cst && query == Query.LESS_THAN) {
						BVoutWanted[n] |= 1;
					}
					else if(column[i+Ls*n] <= cst && query == Query.LESS_THAN_OR_EQUAL_TO) {
						BVoutWanted[n] |= 1;
					}
					else if(column[i+Ls*n] > cst && query == Query.GREATER_THAN) {
						BVoutWanted[n] |= 1;
					}
					else if(column[i+Ls*n] >= cst && query == Query.GREATER_THAN_OR_EQUAL_TO) {
						BVoutWanted[n] |= 1;
					}
				}
			}
			if(p > nbQueriesIgnored) {
				timeNaiveMethod += System.nanoTime() - parseTimeNaiveMethod;
				++count;
			}
		}
		// Norlalization
		timeNaiveMethod = timeNaiveMethod/count;
			
		if(display) System.out.println("\nResults of query " + queryName + " " + cst + ": \n");
		
		// Boolean which indicates if the results obtained are correct
		boolean testok = true;
		
		// Masks to get the relevent result for the full segments and for the last incomplete segment
		long maskFullSegments = ( (long) Math.pow(2, Ls) ) - 1;
		long maskIncompleteSegments = ( (long) Math.pow(2, rest) ) - 1;
		
		// Itteration on the segments
		for(int n = 0; n < NbFullSegments; ++n) {
			
			// Obtaining the result of the segment
			long resultWanted = BVoutWanted[n] & maskFullSegments;
			long result = BVout[n] & maskFullSegments;
			
			// Display it
			if(display || result != resultWanted) {
				if(result != resultWanted) System.out.println("----- FAIL ----");
				System.out.println("	Results of query on segment" + (n+1));
				System.out.println("	Wanted  : " + longtobitsString(resultWanted).substring(64-Ls));
				System.out.println("	Obtained: " + longtobitsString(result).substring(64-Ls) + "\n");
				if(result != resultWanted) {
					System.out.println("	Processor words for segment" + (n+1));
					BWHSegment s = columnout[n];
					for(int i = 0; i<s.getProcessorWords().length; ++i) {
						System.out.println("	" + longtobitsString(s.getProcessorWords()[i]).substring(64-w));
					}
					System.out.println("\n");
				}
			}
			// Checks the result
			testok &= (result == resultWanted);
		}
		// Same for the last incomplete segment
		if(rest>0) {
			int n = NbFullSegments;
			long resultWanted = BVoutWanted[n] & maskIncompleteSegments;
			long result = BVout[n] & maskIncompleteSegments;
			if(display || result != resultWanted) {
				if(result != resultWanted) System.out.println("----- FAILURE ----");
				System.out.println("	Results of query on last segment");
				System.out.println("	Wanted  : " + longtobitsString(resultWanted).substring(64-rest));
				System.out.println("	Obtained: " + longtobitsString(result).substring(64-rest) + "\n");
				if(result != resultWanted) {
					System.out.println("	Processor words for the last segment");
					BWHSegment s = columnout[n];
					for(int i = 0; i<s.getProcessorWords().length; ++i) {
						System.out.println("	" + longtobitsString(s.getProcessorWords()[i]).substring(64-w));
					}
					System.out.println("\n");
				}
			}
			// Checks the result
			testok &= (result == resultWanted);
		}
		if(testok) System.out.println("-- Test sucessful --");
		else  {
			System.out.println("-- Test failed --");
		}

		System.out.println("\n\n" + 
						   "Average time per data in column:\n\n" + 
						   
						   "	BWH Initialization -- " + ((float)timeElapsedInit)/((float)columnlength) + " ns per data in column\n" + 

						   "	BWH Add one datum --- " + (((float) timeElapsedAddTotal)/((float)addedData.size()*columnlength)) + " ns per data in column\n" +
						   
						   "	****\n" +
						   
						   "	BWH Query ----------- " + (((float) timeElapsedQueryTotal)/((float)(nbQueries - nbQueriesIgnored)*columnlength)) + " ns per data in column\n" +
						   
						   "	Naive Query --------- " + ((float)timeNaiveMethod)/((float)columnlength) + " ns per data in column\n");
						   
	}
	}
}
