
public interface BitWeavingVInterface {

	//void add(long datum)
	
	long[] query(Query queryName, long cst);
	int size();
	void add(long nb);
	BWVSegment[] getColumn();
}
