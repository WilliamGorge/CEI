
public class MainTestBW {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		int example = 0;
		
		// Valeurs pour l'exemple 0
		int k0 = 16;
		int w0 = 64;
		int columnlength0 = 3000000;
		
		// Initialisation variables de test (ces valeurs vont tres modifiŽes suivant les exemple
		int w = 0;
		int k = 0;
		int columnlength = 0;
		long[] column = null;
		
		// Exemple 1 (k = 3)
		if(example == 1) {
			w = 8;
			k = 3;
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
		else {
			k = k0;
			w = w0;
			columnlength = columnlength0;
		}
		
		
		try {
						
			// Initialisation store
			BWStore store = new BWStore(w);
			
			// Loading store
			store.addColumn("Column1", ColumnType.BWH, 5);
			for(int i = 0; i < columnlength; ++i) {
				store.addDatum((long) (Math.random()*(Math.pow(2, k) - 1)), "Column1");
			}
			
			store.addColumn("Column2", ColumnType.BWH, 20);
			for(int i = 0; i < columnlength+1000; ++i) {
				store.addDatum((long) (Math.random()*(Math.pow(2, k) - 1)), "Column2");
			}
			
			// Query
			BitVector result = store.query("Column1 < 5 or Column2 < 3");
			result.print();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

}
