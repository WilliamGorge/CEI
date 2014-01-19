import java.sql.Time;
import java.util.Random;


public class MainTestBWH {

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
		// TODO Bug pour w > 63
		// TODO Exception pour k = 32 et w = 64
		
		// Définit de quel test il s'agit:
		// Exemple 1, 2 et 3 sont les exemples des slides.
		// L'exemple 1 correspond à celui de la publication
		// Exemple 0 est sur une colonne de nombres aléatoires, on peut faire varier les paramères
		int example = 1;	
		
		// Valeurs pour l'exemple 0
		int k_0 = 8;
		int w_0 = 32;
		int cst_0 = 10;
		int column_length_0 = 100;
		
		// Donne plus ou moins d'affichage
		boolean display = true;
		
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
		
		/*************** Exemple sur une colonne de chiffres aléatoires (par défaut) ************/
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
		
		System.out.println("cst=" + cst + "  k=" + k + "  w="+ w + "  N=" + N + "  Ls=" + Ls);

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
		
		BitWeavingH BWH = new BitWeavingH(column,k,w);
		BWH_Segment[] column_out = BWH.getColumn();
		
		// Affichage des mots processeurs
		if(display) System.out.println("\nProcessor words: \n");
		for(int n = 0; n < column_out.length; ++n) {
			if(display) System.out.println("	Segment" + (n+1));
			BWH_Segment s = column_out[n];
			for(int i = 0; i<s.getProcessorWords().length; ++i) {
				if(display) System.out.println("	" + longtobitsString(s.getProcessorWords()[i]).substring(64-w));
			}
			if(display) System.out.println("");
		}
		
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
		
		// Requête
		long[] BVout = BWH.is_column_less_than(cst);
		
		// Affichage
		if(display) System.out.println("\nResults of query c<"+cst+": \n");
		boolean testok = true;
		long maskFullSegments = ( (long) Math.pow(2, Ls) ) - 1;
		long maskIncompleteSegments = ( (long) Math.pow(2, rest) ) - 1;
		for(int n = 0; n < NbFullSegments; ++n) {
			long resultWanted = BVoutWanted[n] & maskFullSegments;
			long result = BVout[n] & maskFullSegments;
			if(display) {
				System.out.println("	Segment" + (n+1));
				System.out.println("	Wanted  :" + longtobitsString(BVoutWanted[n]).substring(64-Ls));
				System.out.println("	Obtained:" + longtobitsString(BVout[n]).substring(64-Ls) + "\n");
			}
			testok &= (result == resultWanted);
		}
		if(rest>0) {
			int n = NbFullSegments;
			if(display) {
				System.out.println("	Segment" + (n+1));
				System.out.println("	Wanted  :" + longtobitsString(BVoutWanted[n]).substring(64-rest));
				System.out.println("	Obtained:" + longtobitsString(BVout[n]).substring(64-rest) + "\n");
			}
			testok &= ((BVoutWanted[n] & maskIncompleteSegments) == (BVout[n] & maskIncompleteSegments));
		}
		if(testok) System.out.println("-- Test sucessful --");
		else  System.out.println("-- Test failed --");
			
				


	}

}
