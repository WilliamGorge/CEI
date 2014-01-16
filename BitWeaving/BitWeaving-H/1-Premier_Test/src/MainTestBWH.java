
public class MainTestBWH {

	public static String longtobyteString(long l){
		String s = "";
		for(int i = 0; i < Long.numberOfLeadingZeros(l); ++i) {
			s += "0";
		}
		s += Long.toBinaryString(l);
		return s;
	}
	
	/**
	 * @param args: Nothing (yet)
	 * @author William Gorge
	 */
	public static void main(String[] args) {
		// TODO Remplir le tableau de zŽros
		// TODO Faire les requtes test
		//long[] column = new long[1000];
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
		column_segment[7] = 1;
		
		BWH_Segment s = new BWH_Segment(column_segment, k, w);
		for(int i = 0; i<s.getProcessorWords().length; ++i) {
			System.out.println("v" + (i+1)+ ": " + longtobyteString(s.getProcessorWords()[i]).substring(64-8));
		}

	}

}
