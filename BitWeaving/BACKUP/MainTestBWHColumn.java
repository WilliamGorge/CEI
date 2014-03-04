
import java.util.ArrayList;
import java.util.Vector;

/**
 * Main test class for BitWeavingH.<br>
 * This class contains the main functiobn that performs tests on the implemented BitWeavingH<br>
 * @param args: Nothing (yet)
 * @author William Gorge
 */
public class MainTestBWHColumn {

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
				
		/****************** VARIABLES DE TEST MODIFIABLES A SOUHAIT ********************/
		// Nombre de queries à faire à la suite
		int nbQueries = 50;
		
		// Nombre de queries à ignorer pour le temps
		int nbQueriesIgnored = 5;
		
		// Nombre de données ajoutées à la colonne
		int nbAdd = 10000;
		
		// Nombre de add à ignorer pour le temps
		int nbAddIgnored = 50;
		
		// Définit de quel test il s'agit:
		// Exemple 1, 2 et 3 sont les exemples des slides.
		// L'exemple 1 correspond à celui de la publication
		// Exemple 0 est sur une colonne de nombres aléatoires, on peut faire varier les paramères
		int example = 0;	
		
		// Valeurs pour l'exemple 0
		// La requête est exprimée par query0 et cst0 par exemple pour la requète "<5": query = Query.LESS_THAN; cst0 = 5;
		int k0 = 16; 		// taille d'une donnée en bits
		int w0 = 64; 		// largeur du mot processeur
		int cst0 = 10000;   // columnlength0 = taille de la colonne d'entiers aléatoires codés sur k0 bits
		Query query0 = Query.LESS_THAN; // Exprime la query (cf Query.java pour les query disponibles)
		int columnlength0 = 3000000; // Taille de la colone de nombres aléatoire
		
		// Indique si la colonne, les mots processeurs et les vecteurs de bits résultats doivent être affichés
		// Si à faux, les segments (mots processeurs et résultats) donnant un résultat incorrect seront quand même affichés
		boolean display = (example !=0);
		
		/******************** FIN DES VARIABLES MODIFIABLES ***********************/
		
		// Pour l'exemple 0, si k est trop grand
		if(k0 > w0 - 1) k0 = w0 - 1;
		
		// Pour l'exemple 0, si la constante donnée est invalide (ie. trop grande et ne peut être encodée sur k bits)
		if(cst0 > Math.pow(2, k0) - 1) cst0 = (int) (Math.pow(2, k0) - 1);

		
		// Initialisation variables de test (ces valeurs vont êtres modifiées suivant les exemples
		int cst = 0;
		int k = 0;
		int w = 0;
		int N = 0;
		int Ls = 0;
		int columnlength = 0;
		Query query = null;
		long[] column = null;
		
		/********* Cas des exemples traités dans les slides ***************/
		// Exemple 1 (k = 3)
		if(example == 1) {
			k = 3;
			w = 8;
			cst = 5;
			query = Query.LESS_THAN;
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
			query = Query.LESS_THAN;
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
			query = Query.LESS_THAN;
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
		
		/*** Exemple sur une colonne de chiffres aléatoires (par défaut) ****/
		else {
			k = k0;
			w = w0;
			cst = cst0;
			query = query0;
			N = w/(k+1);
			Ls = N*(k+1);
			columnlength = columnlength0;
			column = new long[columnlength];
			
			for(int i = 0; i < column.length; ++i) {
				column[i] = (long) (Math.random()*(Math.pow(2, k) - 1));
			}
		}
		
		
		
		/******* INITIALIZATION OF THE BWH COLUMN *******/
		
		long timeBeforeInit = System.nanoTime(); // To measure the time elapsed during initialization
		
		BitWeavingHInterface columnBWH = new BWHColumn(k,w);
		
		try {
			for(int i = 0; i < column.length; ++i) 
				columnBWH.add(column[i]);
			
		} catch (Exception e) {
			e.printStackTrace();			
			return;
		}
		
		// To measure the time elapsed during initialization
		long timeElapsedLoading = System.nanoTime() - timeBeforeInit;
		
		
		
	
		
		/********************* TEST FOR ADD **********************/
		
		// To measure total add time
		long timeElapsedAddTotal = 0;
		
		// To memorize the added data
		Vector<Long> addedData = new Vector<Long>();
		
		
		for(int i = 0; i < nbAdd; ++i ) {
			
			// Add
			try {
			
				// Generate a random datum
				long addedDatum = (long) (Math.random()*(Math.pow(2, k) - 1));
				
				// Measuring time
				long timeBeforeAdd = System.nanoTime();
				
				/******************************* HERE IS ADD **********************************/
				columnBWH.add(addedDatum);
				
				/******************************* END OF ADD **********************************/
				
				// Measuring time
				if(i>=nbAddIgnored) timeElapsedAddTotal += System.nanoTime() - timeBeforeAdd;
				
				//Memorizing the data added to check if it works good
				addedData.add(new Long(addedDatum));
				
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
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
		
		
		
		/****************************** TEST FOR QUERY ********************************/
		// To measure total query time
		long timeElapsedQueryTotal = 0;
		

		// Result bit vector
		long[] BVout = null;
		
		for(int i = 0; i < nbQueries; ++i) {
			
			// Query
			try {
				
				// To measure the time elapsed during query
				long timeBeforeQuery = System.nanoTime();
				
				/******************************* HERE IS THE QUERY **********************************/
		
				BVout = columnBWH.query(query, cst);
	
				
				/******************************* END OF THE QUERY **********************************/
				
				// To measure the time elapsed during query
				long timeElapsedQuery = System.nanoTime() - timeBeforeQuery;

				if(i>=nbQueriesIgnored) timeElapsedQueryTotal += timeElapsedQuery;
							
			
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
		
		
		
		
		/******************** COMPUTING CONSTANTS *********************/
		ArrayList<BWHSegment> columnout = columnBWH.getColumn();
		int nbSegments = columnout.size();
		int nbFullSegments = columnBWH.size()/Ls;
		int nbDataLastSegment = columnBWH.size() - nbFullSegments*Ls;
		
		
		
		
		/******************** COMPUTING THE RESULT THAT WE WANT TO HAVE (naive method) **********************/ 
		int count = 0;
		long timeNaiveMethod = 0;
		
		long[] BVoutWanted = null;
		
		// Loop to warmup the JVM
		for(int p = 0; p < nbQueries; ++p) {
			
			// Result bit vector that we want to have
			BVoutWanted = new long[nbSegments];
			
			// Measuring time
			long parseTimeNaiveMethod = System.nanoTime();
			
			switch(query) {
				case DIFFERENT:
					for(int n = 0; n < nbSegments; ++n) {
						for(int i = 0; i < Ls && i+Ls*n < column.length; ++i) {
							BVoutWanted[n]<<=1;
							if(column[i+Ls*n] != cst) {
								BVoutWanted[n] |= 1;
							}
						}
					}
					break;
				
				case EQUAL:
					for(int n = 0; n < nbSegments; ++n) {
						for(int i = 0; i < Ls && i+Ls*n < column.length; ++i) {
							BVoutWanted[n]<<=1;
							if(column[i+Ls*n] == cst) {
								BVoutWanted[n] |= 1;
							}
						}
					}
					break;
					
				case LESS_THAN:
					for(int n = 0; n < nbSegments; ++n) {
						for(int i = 0; (i < Ls) && (i+Ls*n < column.length); ++i) {
							BVoutWanted[n]<<=1;
							if(column[i+Ls*n] < cst) {
								BVoutWanted[n] |= 1;
							}
						}
					}
					break;
					
				case LESS_THAN_OR_EQUAL_TO:
					for(int n = 0; n < nbSegments; ++n) {
						for(int i = 0; i < Ls && i+Ls*n < column.length; ++i) {
							BVoutWanted[n]<<=1;
							if(column[i+Ls*n] <= cst) {
								BVoutWanted[n] |= 1;
							}
						}
					}
					break;
					
				case GREATER_THAN:
					for(int n = 0; n < nbSegments; ++n) {
						for(int i = 0; i < Ls && i+Ls*n < column.length; ++i) {
							BVoutWanted[n]<<=1;
							if(column[i+Ls*n] > cst) {
								BVoutWanted[n] |= 1;
							}
						}
					}
					break;
					
				case GREATER_THAN_OR_EQUAL_TO:
					for(int n = 0; n < nbSegments; ++n) {
						for(int i = 0; i < Ls && i+Ls*n < column.length; ++i) {
							BVoutWanted[n]<<=1;
							if(column[i+Ls*n] >= cst) {
								BVoutWanted[n] |= 1;
							}
						}
					}
					break;
				default:
					break;
			}
			// Special treat for the last and incomplete segment
			if(nbDataLastSegment>0) {
				BVoutWanted[nbSegments - 1] <<= Ls-nbDataLastSegment;
			}
			if(p > nbQueriesIgnored) {
				timeNaiveMethod += System.nanoTime() - parseTimeNaiveMethod;
				++count;
			}
		}
		// Norlalization
		timeNaiveMethod = timeNaiveMethod/count;
		
		
		
		/************************ DISPLAY AND CHECKING THE COLUMN, THE PROCESSOR WORDS AND THE RESULTS**************************/
		// DISPLAY CONSTANTS
		System.out.println("k=" + k + "  w="+ w + "  Ls=" + Ls + "  N=" + N + "  columnlength=" + columnlength + " nbAdd=" + nbAdd + " query=" + query + " cst=" + cst);
		System.out.println("\nnbSegments=" + nbSegments + " nbFullSegments=" + nbFullSegments + "  nbDataLastSegment=" + nbDataLastSegment + "\n");
		
		// Display the column
		if(display) {
			System.out.println("Column: ");
			for(int i = 0; i < column.length; ++i) {
				System.out.println("	" + longtobitsString(column[i]).substring(64-k));
			}
		}
		
		// Processor words display
		if(display) System.out.println("\nProcessor words: \n");
		for(int n = 0; n < columnout.size(); ++n) {
			if(display) System.out.println("	Processor words for segment" + (n+1));
			BWHSegment s = columnout.get(n);
			for(int i = 0; i<s.getProcessorWords().length; ++i) {
				if(display) System.out.println("	" + longtobitsString(s.getProcessorWords()[i]).substring(64-w));
			}
			if(display) System.out.println("");
		}
		
		if(display) System.out.println("\nResults of query " + query + " " + cst + ": \n");
		
		// Boolean which indicates if the results obtained are correct
		boolean testok = true;
		
		// Itteration on the segments
		for(int n = 0; n < nbSegments; ++n) {
			
			// Obtaining the result of the segment
			long resultWanted = BVoutWanted[n];
			long result = BVout[n];
			
			// Display it
			if(display || result != resultWanted) {
				if(result != resultWanted) System.out.println("----- FAIL ----");
				System.out.println("	Results of query on segment " + n);
				System.out.println("	Wanted  : " + longtobitsString(resultWanted).substring(Long.SIZE - Ls));
				System.out.println("	Obtained: " + longtobitsString(result).substring(Long.SIZE - Ls) + "\n");
				if(result != resultWanted) {
					System.out.println("	Processor words for segment " + n);
					BWHSegment s = columnout.get(n);
					for(int i = 0; i<s.getProcessorWords().length; ++i) {
						System.out.println("	" + longtobitsString(s.getProcessorWords()[i]));
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
						   
						   "	BWH Loading --------- " + ((float)timeElapsedLoading)/((float)columnlength) + " ns per data in column\n" + 

						   "	BWH Add one datum --- " + (((float) timeElapsedAddTotal)/((float)addedData.size()*columnlength)) + " ns per data in column\n" +
						   
						   "	****\n" +
						   
						   "	BWH Query ----------- " + (((float) timeElapsedQueryTotal)/((float)(nbQueries - nbQueriesIgnored)*columnlength)) + " ns per data in column\n" +
						   
						   "	Naive Query --------- " + ((float)timeNaiveMethod)/((float)columnlength) + " ns per data in column\n");
						   
	}
}
