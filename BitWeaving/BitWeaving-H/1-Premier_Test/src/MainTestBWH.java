import java.sql.Time;
import java.util.Random;


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
	 * Main test function
	 * @param args: Nothing (yet)
	 * @author William Gorge
	 */
	public static void main(String[] args) {
				
		/****************** VARIABLES DE TEST MODIFIABLES A SOUHAIT ********************/
		// Définit de quel test il s'agit:
		// Exemple 1, 2 et 3 sont les exemples des slides.
		// L'exemple 1 correspond à celui de la publication
		// Exemple 0 est sur une colonne de nombres aléatoires, on peut faire varier les paramères
		int example = 0;	
		
		// Valeurs pour l'exemple 0
		// k_0 = taille d'une donnée en bits
		// w_0 = largeur du mot processeur
		// column_length_0 = taille de la colonne d'entiers aléatoires codés sur k_0 bits
		// La requête est exprimée par queryName_0 pour la constante cst_0
		// 	ex: pour avoir toutes les données inférieures à 5: cst_0 = 5 et queryName = "LESS THAN"
		// Différentes requêtes disponibles: "DIFFERENT", "EQUAL", "LESS THAN", "LESS THAN OR EQUAL TO"
		int k_0 = 16;
		int w_0 = 64;
		int cst_0 = 10000;
		String queryName_0 = "LESS THAN OR EQUAL TO";
		int column_length_0 = 1000;
		
		// Indique si la colonne, les mots processeurs et les vecteurs de bits résultats doivent être affichés
		boolean display = true;
		
		/******************** FIN DES VARIABLES MODIFIABLES ***********************/
		
		// Pour l'exemple 0, si k est trop grand
		if(k_0 > w_0 - 1) k_0 = w_0 -1;
		
		// Pour l'exemple 0, si la constante donnée est invalide (ie. trop grande et ne peut être encodée sur k bits)
		if(cst_0 > Math.pow(2, k_0) -1) cst_0 = (int) (Math.pow(2, k_0) - 1);
		
		// Initialisation variables de test (ces valeurs vont êtres modifiées suivant les exemples
		int cst = 0;
		int k = 0;
		int w = 0;
		int N = 0;
		int Ls = 0;
		int column_length = 0;
		String queryName = null;
		long[] column = null;
		
		/********* Cas des exemples traités dans les slides ***************/
		// Exemple 1 (k = 3)
		if(example == 1) {
			k = 3;
			w = 8;
			cst = 5;
			queryName = "LESS THAN";
			N = w/(k+1);
			Ls = N*(k+1);
			column_length = 10;
			column = new long[column_length];

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
			column_length = 10;
			column = new long[column_length];
			
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
			column_length = 10;
			column = new long[column_length];

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
			k = k_0;
			w = w_0;
			cst = cst_0;
			queryName = queryName_0.toString();
			N = w/(k+1);
			Ls = N*(k+1);
			column_length = column_length_0;
			column = new long[column_length];
			
			for(int i = 0; i < column.length; ++i) {
				column[i] = (long) (Math.random()*Math.pow(2, k) - 1);
			}
		}
		
		/*** DISPLAY ***/
		System.out.println("column_length=" + column_length + " queryName=" + queryName + " cst=" + cst + "  k=" + k + "  w="+ w + "  N=" + N + "  Ls=" + Ls);

		// Cumpute stuff for display
		int NbFullSegments = column_length/Ls;
		int rest = column_length % Ls;
		System.out.println("\nNbFullSegments=" + NbFullSegments + "  rest=" + rest + "\n");
		
		// Display the column
		if(display) {
			System.out.println("Column: ");
			for(int i = 0; i < column.length; ++i) {
				System.out.println("	" + longtobitsString(column[i]).substring(64-k));
			}
		}
		
		/*** INITIALIZATION OF THE BWH COLUMN ***/
		BitWeavingH BWH = new BitWeavingH(column,k,w);
		BWH_Segment[] column_out = BWH.getColumn();
		
		
		/*** DISPLAY ***/
		// Processor words display
		if(display) System.out.println("\nProcessor words: \n");
		for(int n = 0; n < column_out.length; ++n) {
			if(display) System.out.println("	Processor words for segment" + (n+1));
			BWH_Segment s = column_out[n];
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
		for(int n = 0; n < NbFullSegments; ++n) {
			for(int i = 0; i < Ls; ++i) {
				BVoutWanted[n]<<=1;
				if(column[i+Ls*n] != cst && queryName == "DIFFERENT") {
					BVoutWanted[n] |= 1;
				}
				else if(column[i+Ls*n] == cst && queryName == "EQUAL") {
					BVoutWanted[n] |= 1;
				}
				else if(column[i+Ls*n] <= cst && queryName == "LESS THAN OR EQUAL TO") {
					BVoutWanted[n] |= 1;
				}
				else if(column[i+Ls*n] < cst && queryName == "LESS THAN") {
					BVoutWanted[n] |= 1;
				}
			}
		}
		// Special treat for the last and incomplete segment
		if(rest>0) {
			int n = NbFullSegments;
			for(int i = 0; i < rest; ++i) {
				BVoutWanted[n]<<=1;
				if(column[i+Ls*n] != cst && queryName == "DIFFERENT") {
					BVoutWanted[n] |= 1;
				}
				else if(column[i+Ls*n] == cst && queryName == "EQUAL") {
					BVoutWanted[n] |= 1;
				}
				else if(column[i+Ls*n] <= cst && queryName == "LESS THAN OR EQUAL TO") {
					BVoutWanted[n] |= 1;
				}
				else if(column[i+Ls*n] < cst && queryName == "LESS THAN") {
					BVoutWanted[n] |= 1;
				}
			}
		}
		
		
		/******************************* HERE IS THE QUERY **********************************/

		// Query
		long[] BVout = null;
		try {
			BVout = BWH.query(queryName, cst);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		/******************************* END OF THE QUERY **********************************/
		
		
		
		/*** DISPLAY AND CHECKING THE RESULTS***/
		
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
			if(display) {
				System.out.println("	Results of query on segment" + (n+1));
				System.out.println("	Wanted  : " + longtobitsString(resultWanted).substring(64-Ls));
				System.out.println("	Obtained: " + longtobitsString(result).substring(64-Ls) + "\n");
			}
			// Checks the result
			testok &= (result == resultWanted);
		}
		// Same for the last incomplete segment
		if(rest>0) {
			int n = NbFullSegments;
			long resultWanted = BVoutWanted[n] & maskIncompleteSegments;
			long result = BVout[n] & maskIncompleteSegments;
			if(display) {
				System.out.println("	Results of query segment" + (n+1));
				System.out.println("	Wanted  : " + longtobitsString(resultWanted).substring(64-rest));
				System.out.println("	Obtained: " + longtobitsString(result).substring(64-rest) + "\n");
			}
			// Checks the result
			testok &= (result == resultWanted);
		}
		if(testok) System.out.println("-- Test sucessful --");
		else  System.out.println("-- Test failed --");
	}
}
