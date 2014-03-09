import java.security.InvalidParameterException;
import java.util.ArrayList;


public class BWStore implements BWInterface{

	private ArrayList<BWColumn> columns; // Columns of the store
	private int lastAddIndex; // Index of the last column where an 'addDatum' has been performed, or last column added
	private int w; // Size of the processor word of the store
	
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
	 * @throws IllegalArgumentException Thrown if the specified sizeOfProcessorWord is larger than Long.SIZE (typically 64).
	 * @see Long
	 * @author William Gorge and Benoit Sordet
	 */
	BWStore(int sizeOfProcessorWord) throws Exception {
		
		if(sizeOfProcessorWord == 0) 
			throw new IllegalArgumentException("The size of the processor word given (value=" + sizeOfProcessorWord + ") cannot be 0");
		
		
		if(sizeOfProcessorWord > Long.SIZE) 
			throw new IllegalArgumentException("This API cannot handle a processor word bigger than " + Long.SIZE + " bits (here " + sizeOfProcessorWord + " bits)");
		
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
	 * @throws Exception Thrown when there is a syntax error
	 * @throws InvalidParameterException Thrown when there is a illegal value for one parameter
	 */
	public BitVector query(String arg) throws Exception {
		
		// Splits the string around spaces
		String[] args = arg.split(" ");
		
		// Result variable that is returned
		BitVector result = null;
		
		try {
			
			// Initializing arguments
			Query query = null;
			Operator op = null;
			int columnIndex = -1;
			long cst = -1;
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
				cst = Long.parseLong(args[i]);
				

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
		catch (IllegalArgumentException e) {
			throw new InvalidParameterException("Error in query \"" + arg + "\": " + e.getMessage());
		}
		catch (Exception e) {
			throw new Exception("Syntax error in query: \"" + arg + "\": " +  e.getMessage());
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
		
		// Get the column index from its name in the array columnNames
		if(columns.get(lastAddIndex).getName().toString() == columnName) 
			columnIndex = lastAddIndex;
		
		else
			columnIndex = indexOf(columnName);
		
		if(columnIndex == -1) throw new Exception("Column \"" + columnName + "\" not found");
		
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
		
		// Check if the name already exists
		for(int i=0; i < columns.size(); ++i) {
			if(columns.get(i).getName() == columnName)
				throw new IllegalArgumentException("The name " + columnName + " is aldready used in this store");
		}
		
		// Initializing the new column
		BWColumn newColumn = null;
		switch(columnType) {
			case BWH:
				newColumn = new BWHColumn(columnName, sizeOfOneDatum, w);
				break;
			
			case BWV:
				newColumn = new BWVColumn(columnName, sizeOfOneDatum, w);
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
	
	public void printProcessorWords(String columnName) {
		
		try {
			
			int columnIndex = indexOf(columnName);
			
			columns.get(columnIndex).printProcessorWords();
			
			
		} catch (Exception e) {
			System.out.println("Error during display of the processor words");
			e.printStackTrace();
		}
		
	}
}
