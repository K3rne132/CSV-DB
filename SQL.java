import java.util.ArrayList;
import java.util.List;
import java.lang.IllegalArgumentException;
import java.util.Arrays;
import java.lang.StringBuilder;

public class SQL {
	private boolean SELECT;
	private boolean WHERE;
	private boolean ORDERBY;
	private boolean GROUPBY;
	private boolean READ;  // not in SQL - used for reading db from CSV file
	private boolean WRITE; // not in SQL - used for writing db to XML/JSON files
	private ArrayList<String> select; // args passed to: - select
	private ArrayList<String> where;  // - where
	private ArrayList<String> read;   // - read
	private ArrayList<String> write;  // - write
	private ArrayList<String> orderby;  // - orderby
	private ArrayList<String> groupby;  // - groupby
	private String last; // last command: select, where, read, write
	
	private String getCommands() {
		StringBuilder sb = new StringBuilder();
		if (this.SELECT)
			sb.append("'select' ");
		if (this.WHERE)
			sb.append("'where' ");
		if (this.ORDERBY)
			sb.append("'orderby' ");
		if (this.GROUPBY)
			sb.append("'groupby' ");
		if (this.READ)
			sb.append("'read' ");
		if (this.WRITE)
			sb.append("'write' ");
		return sb.toString();
	}
	
	private boolean check(boolean sel, boolean wh, boolean ord, boolean gr,
		boolean rd, boolean wr) {
		if (this.SELECT == sel &&
			this.WHERE  == wh  &&
			this.ORDERBY  == ord  &&
			this.GROUPBY  == gr  &&
			this.READ   == rd  &&
			this.WRITE  == wr)
			return true;
		return false;
	}
	
	private void append(String str) {
		switch (this.last) {
			case "select":
				this.select.add(str);
				return;
			case "where":
				this.where.add(str);
				return;
			case "orderby":
				this.orderby.add(str);
				return;
			case "groupby":
				this.groupby.add(str);
				return;
			case "read":
				this.read.add(str);
				return;
			case "write":
				this.write.add(str);
				return;
			default:
				throw new RuntimeException("Something went wrong.");
		}
	}
	
	private boolean isCommand(String command) {
		switch (command) {
			case "select":
				this.SELECT = true;
				this.last = "select";
				return true;
			case "where":
				this.WHERE = true;
				this.last = "where";
				return true;
			case "orderby":
				this.ORDERBY = true;
				this.last = "orderby";
				return true;
			case "groupby":
				this.GROUPBY = true;
				this.last = "groupby";
				return true;
			case "read":
				this.READ = true;
				this.last = "read";
				return true;
			case "write":
				this.WRITE = true;
				this.last = "write";
				return true;
			default:
				return false;
		}
	}
	
	private ArrayList<String> parse(String command) {
		boolean escape = false;
		StringBuilder sb = new StringBuilder(command);
		for (int i = 0; i < sb.length(); i++) {
			if (sb.charAt(i) == ' ' && escape == true)
				sb.setCharAt(i, '\uDFFF');
			else if (sb.charAt(i) == '"' && escape == false)
				escape = true;
			else if (sb.charAt(i) == '"' && escape == true)
				escape = false;
		}
		
		ArrayList<String> result =
			new ArrayList<String>(Arrays.asList(sb.toString().split(" ")));
		while (result.remove(null));
		for (int i = 0; i < result.size(); i++) {
			if (result.get(i).isEmpty()) {
				result.remove(i);
				i--;
			}
			else {
				String s = result.get(i);
				s = s.replaceAll("\uDFFF", " ");
				s = s.trim();
				String tmp = s.replaceAll(",$|^,", "");
				// ,,,exa,mple,,,, => exa,mple
				while (!s.equals(tmp)) {
					s = tmp;
					tmp = s.replaceAll(",$|^,", "");
				}
				if (s.length() > 1) {
					// "example" => example, 'example' => example, " " => <spacebar>
					if (s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"')
						s = s.substring(1, s.length() - 1);
					else if (s.charAt(0) == '\'' && s.charAt(s.length() - 1) == '\'')
						s = s.substring(1, s.length() - 1);
				}
				result.set(i, s);
				if (result.get(i).isEmpty()) {
					result.remove(i);
					i--;
				}
			}
		}
		return result;
	}
	
	public SQL(String command) {
		if (command == null || command.isEmpty())
			throw new NullPointerException("Invalid command.");
		this.select  = new ArrayList<String>();
		this.where   = new ArrayList<String>();
		this.orderby = new ArrayList<String>();
		this.groupby = new ArrayList<String>();
		this.read    = new ArrayList<String>();
		this.write   = new ArrayList<String>();
		this.last    = new String();
		List<String> parts = parse(command);
		
		for (String s : parts) {
			if (!isCommand(s.toLowerCase())) {
				if (this.last.isEmpty())
					throw new IllegalArgumentException("Unknown command: " + s);
				append(s);
			}
		}
	}
	
	public void printQuery() {
		StringBuilder sb = new StringBuilder();
		sb.append("Query properties:\n");
		if (this.SELECT) {
			sb.append("SELECT\n");
			for (String s : this.select)
				sb.append("\t" + s + "\n");
		}
		if (this.WHERE) {
			sb.append("WHERE\n");
			Integer arg = 0;
			boolean connect = false;
			sb.append("\t");
			for (String s : this.where) {
				if (connect == true) {
					sb.append("\n\t" + s + "\n");
					sb.append("\t");
					connect = false;
				}
				else {
					sb.append(s + " ");
					arg++;
					if (arg == 3) {
						connect = true;
						arg = 0;
					}
				}
			}
			System.out.println(sb);
		}
		if (this.ORDERBY) {
			sb = new StringBuilder();
			if (this.orderby.size() != 2)
				throw new IllegalArgumentException("Unchecked 'orderby'");
			sb.append("ORDER BY " + this.orderby.get(0) + " column in ");
			if (this.orderby.get(1).toLowerCase().equals("desc"))
				sb.append("descending");
			else if (this.orderby.get(1).toLowerCase().equals("asc"))
				sb.append("ascending");
			else if (this.orderby.get(1).toLowerCase().equals("idesc"))
				sb.append("integer-descending");
			else if (this.orderby.get(1).toLowerCase().equals("iasc"))
				sb.append("integer-ascending");
			else
				sb.append("unknown");
			sb.append(" order");
			System.out.println(sb);
		}
		if (this.GROUPBY) {
			if (this.groupby.size() != 1)
				throw new IllegalArgumentException("Unchecked 'groupby'");
			System.out.println("GROUP BY " + this.groupby.get(0));
		}
		if (this.READ) {
			if (this.read.size() != 2)
				throw new IllegalArgumentException("Unchecked 'read'");
			System.out.println("READ CSV file from " + this.read.get(0) +
				" with delimiter \"" + this.read.get(1) + "\"");
		}
		if (this.WRITE) {
			if (this.write.size() != 2)
				throw new IllegalArgumentException("Unchecked 'write'");
			System.out.println("WRITE to " + this.write.get(0) +
				" file in " + this.write.get(1) + " path");
		}
	}
	
	public boolean verify() {
		if (!(check(true, false, false, false, false, false) ||
			check(true, true, false, false, false, false) ||
			check(true, false, true, false, false, false) ||
			check(true, false, false, true, false, false) ||
			check(true, true, true, false, false, false) ||
			check(true, true, false, true, false, false) ||
			check(true, false, true, true, false, false) ||
			check(true, true, true, true, false, false) ||
			check(false, false, false, false, true, false) ||
			check(false, false, false, false, false, true))) {
			throw new IllegalArgumentException("Cannot " + getCommands() +
				"in the same time.");
			}
		if (this.SELECT && this.select.size() == 0)
			throw new IllegalArgumentException("'select': <column> ...");
		if (this.WHERE && this.where.size() % 4 != 3) {
			throw new IllegalArgumentException("'where': <column> <operator>" +
				"<value> [join] ...");
		}
		if (this.ORDERBY && this.orderby.size() != 2)
			throw new IllegalArgumentException("'orderby': <column> <[i]asc/[i]desc>");
		if (this.GROUPBY && this.groupby.size() != 1)
			throw new IllegalArgumentException("'groupby': <column>");
		if (this.READ && this.read.size() != 2)
			throw new IllegalArgumentException("'read': <path> <delim>");
		if (this.WRITE && this.write.size() != 2)
			throw new IllegalArgumentException("'write': <xml/json> <path>");
		return true;
	}
	
	public ArrayList<String> getSelect() {
		return this.select;
	}
	
	public ArrayList<String> getWhere() {
		return this.where;
	}
	
	public ArrayList<String> getOrderby() {
		return this.orderby;
	}
	
	public ArrayList<String> getGroupby() {
		return this.groupby;
	}
	
	public ArrayList<String> getRead() {
		return this.read;
	}
	
	public ArrayList<String> getWrite() {
		return this.write;
	}
}
