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
		// TODO Remplir le tableau de zéros
		// TODO Faire les requêtes test
		
		// Définit de quel test il s'agit
		int example = 0;
		
		
		
		/*************** Exemple sur une colonne de chiffres aléatoires ************/
		if(example == 0) {
			int cst = 3;
			int k = 5;
			int w = 8;
			int N = w/(k+1);
			int Ls = N*(k+1);
			int column_length = 10;
			
			System.out.println("cst=" + cst + "  k=" + k + "  w="+ w + "  N=" + N + "  Ls=" + Ls + "\n");

			// Cumpute stuff for display
			int NbFullSegments = column_length/Ls;
			int rest = column_length % Ls;
			System.out.println("\nNbFullSegments=" + NbFullSegments + "  rest=" + rest + "\n");
			
			long[] column = new long[column_length];
			
			System.out.println("Column generated: ");
			for(int i = 0; i < column.length; ++i) {
				column[i] = (long) (Math.random()*Math.pow(2, k));
				System.out.println("	" + longtobitsString(column[i]).substring(64-k));
			}
			
			
			BitWeavingH BWH = new BitWeavingH(column,k,w);
			BWH_Segment[] column_out = BWH.getColumn();
			
			// Affichage des mots processeurs
			System.out.println("\nProcessor words: \n");
			for(int n = 0; n < column_out.length; ++n) {
				System.out.println("	Segment" + (n+1));
				BWH_Segment s = column_out[n];
				for(int i = 0; i<s.getProcessorWords().length; ++i) {
					System.out.println("	" + longtobitsString(s.getProcessorWords()[i]).substring(64-w));
				}
				System.out.println("");
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
			System.out.println("\nResults of query c<"+cst+": \n");
			for(int n = 0; n < NbFullSegments; ++n) {
				System.out.println("	Segment" + (n+1));
				System.out.println("	Wanted  :" + longtobitsString(BVoutWanted[n]).substring(64-Ls));
				System.out.println("	Obtained:" + longtobitsString(BVout[n]).substring(64-Ls) + "\n");
			}
			if(rest>0) {
				System.out.println("	Segment" + (NbFullSegments+1) + " (incomplete)");
				System.out.println("	Wanted  :" + longtobitsString(BVoutWanted[NbFullSegments]).substring(64-rest));
				System.out.println("	Obtained:" + longtobitsString(BVout[NbFullSegments]).substring(64-rest) + "\n");
			}
			
		}
				
			
		/********* Cas des exemples traités dans les slides ***************/
		// Exemple 1 (k = 3)
		if(example == 1) {
			int k = 3;
			int w = 8;
			int N = w/(k+1);
			int Ls = N*(k+1);

			long[] column_segment = new long[Ls];
			column_segment[0] = 1;
			column_segment[1] = 5;
			column_segment[2] = 6;
			column_segment[3] = 1;
			column_segment[4] = 6;
			column_segment[5] = 4;
			column_segment[6] = 0;
			column_segment[7] = 7;
			BWH_Segment s = new BWH_Segment(column_segment, k, w);
			
			// Affichage
			for(int i = 0; i<s.getProcessorWords().length; ++i) {
				System.out.println("v" + (i+1)+ ": " + longtobitsString(s.getProcessorWords()[i]).substring(64-8));
			}
		}
			
		// Exemple 2 (k = 4)
		if(example == 2) {
			int k = 4;
			int w = 8;
			int N = w/(k+1);
			int Ls = N*(k+1);

			long[] column_segment = new long[Ls];
			column_segment[0] = 9;
			column_segment[1] = 5;
			column_segment[2] = 8;
			column_segment[3] = 1;
			column_segment[4] = 15;
			BWH_Segment s = new BWH_Segment(column_segment, k, w);
			
			// Affichage
			for(int i = 0; i<s.getProcessorWords().length; ++i) {
				System.out.println("v" + (i+1)+ ": " + longtobitsString(s.getProcessorWords()[i]).substring(64-8));
			}
		}
		// Example 3 (k = 2)
		if(example == 3) {
			int k = 2;
			int w = 8;
			int N = w/(k+1);
			int Ls = N*(k+1);

			long[] column_segment = new long[Ls];
			column_segment[0] = 0;
			column_segment[1] = 2;
			column_segment[2] = 3;
			column_segment[3] = 0;
			column_segment[4] = 3;
			column_segment[5] = 2;
			BWH_Segment s = new BWH_Segment(column_segment, k, w);
			
			// Affichage
			for(int i = 0; i<s.getProcessorWords().length; ++i) {
				System.out.println("v" + (i+1)+ ": " + longtobitsString(s.getProcessorWords()[i]).substring(64-8));
			}
		}
		// Exemple 4 (comme example 1 mais avec un ségment incomplet (comme le segment 2 de la figure 3)
		if(example == 4) {
			int k = 2;
			int w = 8;
			int N = w/(k+1);
			int Ls = 2;

			long[] column_segment = new long[Ls];
			column_segment[0] = 4;
			column_segment[1] = 3;
			BWH_Segment s = new BWH_Segment(column_segment, k, w);
			
			// Affichage
			for(int i = 0; i<s.getProcessorWords().length; ++i) {
				System.out.println("v" + (i+1)+ ": " + longtobitsString(s.getProcessorWords()[i]).substring(64-8));
			}
		}

	}

}
