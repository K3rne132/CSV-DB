import java.util.ArrayList;

public class Record {
	private ArrayList<String> entries;
	private Database db;
	
	private int intCompare(String s1, String s2) {
		Integer ai = 0;
		Integer bi = 0;
		try  {
			ai = Integer.parseInt(s1);
		} catch (Exception e) {
			throw new NumberFormatException("Values in column are not integers.");
		}
		try  {
			bi = Integer.parseInt(s2);
		} catch (Exception e) {
			throw new NumberFormatException("Value " + s2 + " is not an integer.");
		}
		return ai.compareTo(bi);
	}
	
	public Record(Database db) {
		this.entries = new ArrayList<String>();
		this.db      = db;
	}
	
	public void insert(String value) {
		if (value == null || value.isEmpty()) {
			throw new NullPointerException("Tried to load empty value. " +
				"Maybe there is a doubled delimiter somewhere?");
		}
		this.entries.add(value);
	}
	
	public boolean addToResult(SQL query) {
		ArrayList<String> where = query.getWhere();
		if (where.size() == 0)
			return true;
		boolean res = false;
		String arg1 = null;
		String op = null;
		boolean and = false;
		for (int i = 0; i < where.size(); i++) {
			if (i % 4 == 0)
				arg1 = this.entries.get(this.db.fieldToId(where.get(i)));
			else if (i % 4 == 1)
				op = where.get(i);
			else if (i % 4 == 2) {
				String arg2 = where.get(i);
				if (arg1 == null || arg1.isEmpty())
					throw new NullPointerException("Invalid arg1.");
				if (op == null || op.isEmpty())
					throw new NullPointerException("Invalid operator.");
				if (arg2 == null || arg2.isEmpty())
					throw new NullPointerException("Invalid arg2.");
				boolean temp = false;
				switch (op) {
					case "=":
						if (arg1.compareTo(arg2) == 0)
							temp = true;
						break;
					case ">":
						if (arg1.compareTo(arg2) > 0)
							temp = true;
						break;
					case ">=":
						if (arg1.compareTo(arg2) >= 0)
							temp = true;
						break;
					case "<":
						if (arg1.compareTo(arg2) < 0)
							temp = true;
						break;
					case "<=":
						if (arg1.compareTo(arg2) <= 0)
							temp = true;
						break;
					case "i=":
						if (intCompare(arg1, arg2) == 0)
							temp = true;
						break;
					case "i>":
						if (intCompare(arg1, arg2) > 0)
							temp = true;
						break;
					case "i>=":
						if (intCompare(arg1, arg2) >= 0)
							temp = true;
						break;
					case "i<":
						if (intCompare(arg1, arg2) < 0)
							temp = true;
						break;
					case "i<=":
						if (intCompare(arg1, arg2) <= 0)
							temp = true;
						break;
					default: throw new IllegalArgumentException(
						"Unknown operator " + op + ".");
				}
				if (and)
					res = res && temp;
				else
					res = res || temp;
			}
			else {
				String join = where.get(i).toLowerCase();
				switch (join) {
					case "and": and = true;
						break;
					case "or": and = false;
						break;
					default: throw new IllegalArgumentException(
						"Should be 'and/or' instead of " + join + ".");
				}
			}
		}
		return res;
	}
	
	public void print(ArrayList<Integer> fields) {
		for (Integer i : fields) {
			Integer padding = this.db.getPadding(i);
			padding -= this.entries.get(i).length();
			System.out.print("|");
			System.out.print(" ".repeat(padding / 2));
			System.out.print(this.entries.get(i));
			System.out.print(" ".repeat(padding / 2 + padding % 2));
		}
		System.out.println("|");
	}
	
	public String getEntry(String key) {
		Integer i = this.db.fieldToId(key);
		if (i == null)
			throw new NullPointerException("Field: " + key + " does not exists.");
		return this.entries.get(i);
	}
	
	public String getEntry(Integer i) {
		return this.entries.get(i);
	}
}
