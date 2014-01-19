import java.sql.Time;
import java.util.Random;


public class MainTestBWH {

	/**
	 * This is a little modification of Long.toBinaryString(long);
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
		int k_0 = 16;
		int w_0 = 64;
		int cst_0 = 5000;
		int column_length_0 = 133;
		
		// Donne plus ou moins d'affichage
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
		long[] column = null;
		
		/********* Cas des exemples traités dans les slides ***************/
		// Exemple 1 (k = 3)
		if(example == 1) {
			k = 3;
			w = 8;
			cst = 5;
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
			N = w/(k+1);
			Ls = N*(k+1);
			column_length = 7;
			column = new long[column_length];

			column[0] = 0;
			column[1] = 2;
			column[2] = 3;
			column[3] = 0;
			column[4] = 3;
			column[5] = 2;
			column[6] = 1;
		}
		
		/*** Exemple sur une colonne de chiffres aléatoires (par défaut) ****/
		else {
			k = k_0;
			w = w_0;
			cst = cst_0;
			N = w/(k+1);
			Ls = N*(k+1);
			column_length = column_length_0;
			column = new long[column_length];
			
			for(int i = 0; i < column.length; ++i) {
				column[i] = (long) (Math.random()*Math.pow(2, k));
			}
		}
		
		/*** DISPLAY ***/
		System.out.println("column_length=" + column_length + " cst=" + cst + "  k=" + k + "  w="+ w + "  N=" + N + "  Ls=" + Ls);

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
			if(display) System.out.println("	Segment" + (n+1));
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
				if(column[i+Ls*n] < cst) {
					BVoutWanted[n] |= 1;
				}
			}
		}
		if(rest>0) {
			for(int i = 0; i < rest; ++i) {
				BVoutWanted[NbFullSegments]<<=1;
				if(column[i+Ls*NbFullSegments] < cst) {
					BVoutWanted[NbFullSegments] |= 1;
				}
			}
		}
		
		
		/******************************* HERE IS THE QUERY **********************************/

		// Query
		long[] BVout = BWH.is_column_less_than(cst);
		
		/******************************* END OF THE QUERY **********************************/
		
		
		
		/*** DISPLAY AND CHECKING THE RESULTS***/
		
		if(display) System.out.println("\nResults of query c<"+cst+": \n");
		
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
				System.out.println("	Segment" + (n+1));
				System.out.println("	Wanted  :" + longtobitsString(resultWanted).substring(64-Ls));
				System.out.println("	Obtained:" + longtobitsString(result).substring(64-Ls) + "\n");
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
				System.out.println("	Segment" + (n+1));
				System.out.println("	Wanted  :" + longtobitsString(resultWanted).substring(64-rest));
				System.out.println("	Obtained:" + longtobitsString(result).substring(64-rest) + "\n");
			}
			// Checks the result
			testok &= (result == resultWanted);
		}
		if(testok) System.out.println("-- Test sucessful --");
		else  System.out.println("-- Test failed --");
	}
}
