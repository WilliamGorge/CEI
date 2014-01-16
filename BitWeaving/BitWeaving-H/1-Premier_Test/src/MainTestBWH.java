import java.util.Random;


public class MainTestBWH {

	public static String longtobyteString(long l){
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
		// TODO Remplir le tableau de zŽros
		// TODO Faire les requtes test
		
		// DŽfinit de quel test il s'agit
		int example = 0;
		
		
		
		/*************** Exemple sur une colonne de chiffres alŽatoires ************/
		if(example == 0) {
			int k = 16;
			int w = 64;
			int N = w/(k+1);
			int column_length = 10;
			
			long[] column = new long[column_length];
			
			System.out.println("Column generated: ");
			for(int i = 0; i < column.length; ++i) {
				column[i] = (long) (Math.random()*Math.pow(2, k));
				System.out.println("	" + longtobyteString(column[i]).substring(64-k));
			}
			
			BitWeavingH BWH = new BitWeavingH(column,k,w);
			BWH_Segment[] column_out = BWH.getColumn();
			
			// Affichage
			System.out.println("\nProcessor words: \n");
			for(int n = 0; n < column_out.length; ++n) {
				System.out.println("	Segment" + (n+1));
				BWH_Segment s = column_out[n];
				for(int i = 0; i<s.getProcessorWords().length; ++i) {
					System.out.println("	" + longtobyteString(s.getProcessorWords()[i]).substring(64-w));
				}
				System.out.println("");
			}
			
		}
				
			
		/********* Cas des exemples traitŽs dans les slides ***************/
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
				System.out.println("v" + (i+1)+ ": " + longtobyteString(s.getProcessorWords()[i]).substring(64-8));
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
				System.out.println("v" + (i+1)+ ": " + longtobyteString(s.getProcessorWords()[i]).substring(64-8));
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
				System.out.println("v" + (i+1)+ ": " + longtobyteString(s.getProcessorWords()[i]).substring(64-8));
			}
		}
		// Exemple 4 (comme example 1 mais avec un sŽgment incomplet (comme le segment 2 de la figure 3)
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
				System.out.println("v" + (i+1)+ ": " + longtobyteString(s.getProcessorWords()[i]).substring(64-8));
			}
		}

	}

}
