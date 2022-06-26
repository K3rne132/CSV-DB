import java.util.ArrayList;
import java.util.Queue;
import java.util.PriorityQueue;
import java.util.LinkedList;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.io.FileOutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

public class Database {
	private Shell              sh;
	private boolean            loaded;  // is database loaded
	private LinkedHashMap<String, Integer>  fields;  // columns names
	private ArrayList<Record>  records; // array of records
	private ArrayList<Integer> padding; // max number of chars in every record of fields
								// used for visual representation
	
	private ArrayList<Integer> getAllFields() {
		ArrayList<Integer> res = new ArrayList<Integer>();
		for (Integer i : this.fields.values()) {
			res.add(i);
		}
		return res;
	}
	
	private Integer paddingSum() {
		Integer sum = 0;
		for (Integer i : this.padding)
			sum += i;
		return sum;
	}
	
	private Integer paddingSum(ArrayList<Integer> padding) {
		Integer sum = 0;
		for (Integer i : padding)
			sum += this.padding.get(i);
		return sum;
	}
	
	public Database(Shell sh) {
		this.sh      = sh;
		this.fields  = new LinkedHashMap<String, Integer>();
		this.records = new ArrayList<Record>();
		this.padding = new ArrayList<Integer>();
	}
	
	public Integer getPadding(Integer index) {
		if (index == null)
			throw new NullPointerException("Not valid index.");
		if (index < 0 || index > this.padding.size())
			throw new IndexOutOfBoundsException("Not valid index.");
		return this.padding.get(index);
	}
	
	public Integer fieldToId(String field) {
		Integer i = this.fields.get(field);
		if (i == null)
				throw new NullPointerException("Field: " + field + " does not exists.");
		return i;
	}
	
	public ArrayList<Integer> fieldsToId(ArrayList<String> fields) {
		ArrayList<Integer> res = new ArrayList<Integer>();
		for (String s : fields) {
			Integer i = this.fields.get(s);
			if (i == null)
				throw new NullPointerException("Field: " + s + " does not exists.");
			res.add(i);
		}
		return res;
	}
	
	public void loadFromCSV(String path, String delim) throws IOException, Exception {
		if (path == null)
			throw new NullPointerException("Cannot read path to file.");
		try (RandomAccessFile file = new RandomAccessFile(path, "r")) {
			this.fields  = new LinkedHashMap<String, Integer>();
			this.records = new ArrayList<Record>();
			this.padding = new ArrayList<Integer>();
			this.loaded  = false;
			this.sh.setPrompt("Database");
			Integer line = 0;
			String tmp = file.readLine();
			if (tmp != null && !tmp.isEmpty()) {
				String[] str = tmp.split(delim);
				for (int i = 0; i < str.length; i++) {
					this.fields.put(str[i], i);
					this.padding.add(str[i].length());
				}
			}
			else {
				throw new IOException("Cannot read data. File " +
					path + " is empty.");
			}
			while ((tmp = file.readLine()) != null && !tmp.isEmpty()) {
				String[] str = tmp.split(delim);
				if (str.length != this.fields.size()) {
					throw new IOException("File " + path + " in line: " +
						line + " has inconsistent record.");
				}
				this.records.add(new Record(this));
				for (int i = 0; i < str.length; i++) {
					this.records.get(line).insert(str[i]);
					if (str[i].length() > this.padding.get(i))
						this.padding.set(i, str[i].length());
				}
				line++;
			}
			System.out.println("Query OK: " + line + " records, " +
				this.fields.size() + " columns have been loaded.");
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		this.loaded = true;
		Integer in1 = path.lastIndexOf("/");
		Integer in2 = path.lastIndexOf("\\");
		this.sh.setPrompt(path.substring((in1>in2?in1:in2) + 1, path.length()));
	}
	
	public void writeToXML(String path) throws Exception {
		if (!this.loaded)
			throw new IllegalArgumentException("First, load the database.");
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("<db>\n");
		for (Record r : this.records) {
			sb.append("\t<record>\n");
			for (Map.Entry<String, Integer> en : this.fields.entrySet()) {
				sb.append("\t\t<" + en.getKey() + ">");
				sb.append(r.getEntry(en.getValue()));
				sb.append("</" + en.getKey() + ">\n");
			}
			sb.append("\t</record>\n");
		}
		sb.append("</db>");
		try (FileOutputStream fos = new FileOutputStream(path)) {
			fos.write(sb.toString().getBytes());
			System.out.println("Success: XML file was exported to " + path + " file.");
		} catch(Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	
	public void writeToJSON(String path) throws Exception {
		if (!this.loaded)
			throw new IllegalArgumentException("First, load the database.");
		StringBuilder sb = new StringBuilder();
		sb.append("[\n");
		for (Record r : this.records) {
			sb.append("\t{\n");
			for (Map.Entry<String, Integer> en : this.fields.entrySet()) {
				sb.append("\t\t\"" + en.getKey() + "\": ");
				sb.append("\"" + r.getEntry(en.getValue()) + "\",\n");
			}
			sb.deleteCharAt(sb.length() - 2);
			sb.append("\t},\n");
		}
		sb.deleteCharAt(sb.length() - 2);
		sb.append("]");
		try (FileOutputStream fos = new FileOutputStream(path)) {
			fos.write(sb.toString().getBytes());
			System.out.println("Success: JSON file was exported to " + path + " file.");
		} catch(Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	
	public void execute(SQL sql) throws Exception {
		if (sql == null)
			throw new NullPointerException("Something went wrong.");
		
		if (sql.getRead().size() == 2) {
			try {
				loadFromCSV(sql.getRead().get(0), sql.getRead().get(1));
			} catch (Exception e) {
				throw new Exception(e.getMessage());
			}
		}
		
		if (sql.getWrite().size() == 2) {
			if (sql.getWrite().get(0).toLowerCase().equals("xml"))
				writeToXML(sql.getWrite().get(1));
			else if (sql.getWrite().get(0).toLowerCase().equals("json"))
				writeToJSON(sql.getWrite().get(1));
			else {
				throw new IllegalArgumentException(sql.getWrite().get(0) +
					" is not a valid output file type.");
			}
		}
		
		Queue<Record> results;
		if (sql.getOrderby().size() == 2) {
			if (sql.getOrderby().get(1).toLowerCase().equals("asc")) {
				results = new PriorityQueue<Record>(
					new RecordAscComparator(sql.getOrderby().get(0)));
			}
			else if (sql.getOrderby().get(1).toLowerCase().equals("desc")) {
				results = new PriorityQueue<Record>(
					new RecordDescComparator(sql.getOrderby().get(0)));
			}
			else if (sql.getOrderby().get(1).toLowerCase().equals("iasc")) {
				results = new PriorityQueue<Record>(
					new RecordAscComparatorInteger(sql.getOrderby().get(0)));
			}
			else if (sql.getOrderby().get(1).toLowerCase().equals("idesc")) {
				results = new PriorityQueue<Record>(
					new RecordDescComparatorInteger(sql.getOrderby().get(0)));
			}
			else {
				throw new IllegalArgumentException(sql.getOrderby().get(1) +
					" is not a valid order mode.");
			}
		}
		else
			results = new LinkedList<Record>();
		
		if (sql.getSelect().size() > 0) {
			if (!this.loaded)
				throw new IllegalArgumentException("First, load the database.");
			Integer padSum = 0;
			Integer rec = 0;
			for (Record r : this.records) {
				if (r.addToResult(sql))
					results.add(r);
			}
			if (sql.getSelect().get(0).equals("*")) {
				padSum = paddingSum() - 1;
				padSum += this.padding.size();
				System.out.println("|" + "-".repeat(padSum) + "|");
				for (Map.Entry<String, Integer> en : this.fields.entrySet()) {
					System.out.print("|");
					Integer padding = this.padding.get(en.getValue());
					padding -= en.getKey().length();
					System.out.print(" ".repeat(padding / 2));
					System.out.print(en.getKey());
					System.out.print(" ".repeat(padding / 2 + padding % 2));
				}
				System.out.println("|\n|" + "-".repeat(padSum) + "|");
				while (results.size() > 0) {
					results.poll().print(getAllFields());
					rec++;
				}
			}
			else {
				ArrayList<Integer> columns = fieldsToId(sql.getSelect());
				padSum = paddingSum(columns) - 1;
				padSum += sql.getSelect().size();
				System.out.println("|" + "-".repeat(padSum) + "|");
				for (int i = 0; i < columns.size(); i++) {
					System.out.print("|");
					Integer padding = this.padding.get(columns.get(i));
					padding -= sql.getSelect().get(i).length();
					System.out.print(" ".repeat(padding / 2));
					System.out.print(sql.getSelect().get(i));
					System.out.print(" ".repeat(padding / 2 + padding % 2));
				}
				System.out.println("|\n|" + "-".repeat(padSum) + "|");
				while (results.size() > 0) {
					results.poll().print(fieldsToId(sql.getSelect()));
					rec++;
				}
			}
			System.out.println("|" + "-".repeat(padSum) + "|");
			System.out.println("Query OK: " + rec + " records has been selected.");
		}
	}
}
