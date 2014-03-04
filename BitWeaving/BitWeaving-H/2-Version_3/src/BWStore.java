import java.util.ArrayList;


public class BWStore implements BWInterface{

	private ArrayList<BWColumn> columns;
	private int lastAddIndex;
	private int w;
	
	/**
	 * Defaut Constructor for BWStore. <br>
	 * Constructs assuming sizeOfProcessorWord = Long.SIZE (typically 64).
	 * @see Long
	 * @author William Gorge and Benoit Sordet
	 */
	BWStore() {
		w =  Long.SIZE;
		columns = new ArrayList<BWColumn>();
	}
	
	
	/**
	 * Constructor for BWStore, specifying the size of the processorword.
	 * @param sizeOfProcessorWord Size of one processor word, cannot be larger than Long.SIZE (typically 64).
	 * @throws Exception Thrown if the specified sizeOfProcessorWord is larger than Long.SIZE (typically 64).
	 * @see Long
	 * @author William Gorge and Benoit Sordet
	 */
	BWStore(int sizeOfProcessorWord) throws Exception {
		
		if(sizeOfProcessorWord > Long.SIZE) 
			throw new Exception("This API cannot handle a processor word bigger than " + Long.SIZE + " bits (here " + sizeOfProcessorWord + " bits)");
		
		w =  sizeOfProcessorWord;
		columns = new ArrayList<BWColumn>();
	}
	
	/**
	 * Gets the index of the column given its name
	 * @param columnName Name of the column
	 * @author William Gorge and Benoit Sordet
	 */
	private int indexOf(String columnName) throws Exception {
		
		int i;
		
		// Try to find it
		for(i = 0; i < columns.size(); ++i) {
			String currentName = columns.get(i).getName();
			if(columnName.matches(currentName.toString()))
				break;
		}
		
		// Check if it has been found
		if(i == columns.size()) {
			throw new Exception("Column name not found: " + columnName);
		}
		
		return i;
	}
	
	/**
	 * Performs the query given the arguments
	 * @param arg query
	 * @author William Gorge and Benoit Sordet
	 */
	public BitVector query(String arg) {
		
		String[] args = arg.split(" ");
		
		// Result variable that is returned
		BitVector result = new BitVector();
		
		try {
			
			// Initializing arguments
			Query query = null;
			Operator op = null;
			int columnIndex = -1;
			int cst = -1;
			boolean first = true;
			
			// Itterating on the arguments
			for(int i = 0; i < args.length; ++i) {
				
				// Scan of the logic operator
				if(args[i].matches("and")) {
					op = Operator.AND;
					++i;
				}
				else if(args[i].matches("or")) {
					op = Operator.OR;
					++i;
				}
				
				// Scan of the column name
				columnIndex = indexOf(args[i]);
				++i;
				
				// Scan of the query type
				if(args[i].matches("<")) query = Query.LESS_THAN;
				else if(args[i].matches(">")) query = Query.GREATER_THAN;
				else if(args[i].matches("<=")) query = Query.LESS_THAN_OR_EQUAL_TO;
				else if(args[i].matches(">=")) query = Query.GREATER_THAN_OR_EQUAL_TO;
				else if(args[i].matches("=")) query = Query.EQUAL;
				else if(args[i].matches("!=")) query = Query.DIFFERENT;
				else throw new Exception("Unknown query operator: " + args[i]);
				++i;
				
				// Scan of the constant
				cst = Integer.parseInt(args[i]);
				

				// Performs the query
				if(op == null) {
					if(first) {
						result = columns.get(columnIndex).query(query, cst);
						first = false;
					}
					else throw new Exception("Bit operator not found");
				}
				else {
					switch(op) {
						case AND:
							result = result.and(columns.get(columnIndex).query(query, cst));
							break;
							
						case OR:
							result = result.or(columns.get(columnIndex).query(query, cst));
							break;
					}
				}
			}
		}
		catch (Exception e) {
			System.out.println("Syntax error in query: " + arg);
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * Adds one datum to the specified column
	 * @param datum datum to add to the column
	 * @param columnIndex index of column to witch add the datum
	 * @author William Gorge and Benoit Sordet
	 * @throws Exception an exception can be thrown when the column name is not found
	 */
	public void addDatum(long datum, String columnName) throws Exception {
		
		int columnIndex = -1;
		
		if(columns.get(lastAddIndex).getName().toString() == columnName) 
			columnIndex = lastAddIndex;
		
		else
			columnIndex = indexOf(columnName);
		
		if(columnIndex == -1) throw new Exception("Column " + columnName + " not found");
		
		// Get the column index from its name in the array columnNames
		// and calls the add method from the Column class
		columns.get(columnIndex).add(datum);
	}
	
	
	/**
	 * Adds one column to the store, given its type, its name and the size of one datum.
	 * @param columnName Name of the column, used to manipulate it (query, add ...)
	 * @param columnType Type of the column, has to be from the enum ColumnType: BWH or BWV
	 * @param sizeOfOneDatum Size of one datum in the column, depends on the data format of the column. Example: if the type is int, sizeOfOneDatum = 32.
	 * @author William Gorge and Benoit Sordet
	 */
	public void addColumn(String columnName, ColumnType columnType, int sizeOfOneDatum) {
		
		// Initializing the new column
		BWColumn newColumn = null;
		switch(columnType) {
			case BWH:
				newColumn = new BWHColumn(columnName, sizeOfOneDatum, w);
				break;
		}
		
		// Adding it to the array
		columns.add(newColumn);
		lastAddIndex = columns.size() - 1;
	}
	
	/**
	 * Adds one column to the store giv.
	 * @param column Column to add, has to match the length of the processor word of the store
	 * @author William Gorge and Benoit Sordet
	 * @throws Exception thrown when the size of the processor word of the column given does not match the one of the store.
	 */
	public void addColumn(BWColumn column) throws Exception {
		
		if(column.getSizeOfProcessorWord() != w)
			throw new Exception("The size of the processor word of the column given (" + column.getSizeOfProcessorWord() + ") does not match the one of the store (" + w + ").");
		
		// Adding the new column to the array
		columns.add(column);
		lastAddIndex = columns.size() - 1;
	}
}
