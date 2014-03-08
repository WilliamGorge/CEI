import java.util.ArrayList;


public interface BitWeavingVInterface {
	
	long[] query(Query queryName, long cst);
	long[] query(Query queryName, long cst1, long cst2);
	int size();
	void add(long nb);
	void add(long[] nb);
	ArrayList<BWVSegment> getColumn();
	long[] complexQuery(String query);
}