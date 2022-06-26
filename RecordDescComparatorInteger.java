import java.util.Comparator;

public class RecordDescComparatorInteger
implements Comparator<Record> {
	private String column;
	
	public RecordDescComparatorInteger(String column) {
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
		Integer ai = 0;
		Integer bi = 0;
		try  {
			ai = Integer.parseInt(as);
			bi = Integer.parseInt(bs);
		} catch (Exception e) {
			throw new NumberFormatException("Values in column " + this.column +
				" are not integers.");
		}
		return bi.compareTo(ai);
	}
}
