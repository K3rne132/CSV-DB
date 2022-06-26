import java.util.Comparator;

public class RecordDescComparator
implements Comparator<Record> {
	private String column;
	
	public RecordDescComparator(String column) {
		this.column = column;
	}
	
	@Override
	public int compare(Record a, Record b) {
		String as = a.getEntry(column);
		String bs = b.getEntry(column);
		if (as == null || bs == null) {
			throw new NullPointerException("Value not found in column: " +
				this.column + ".");
		}
		return bs.compareTo(as);
	}
}
