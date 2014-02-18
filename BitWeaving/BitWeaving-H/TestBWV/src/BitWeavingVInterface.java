
public interface BitWeavingVInterface {
	
	long[] query(Query queryName, long cst);
	int size();
	void add(long nb);
	BWVSegment[] getColumn();
	long[] complexQuery(String query);
}
