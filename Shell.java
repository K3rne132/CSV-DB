import java.util.Scanner;

public class Shell {
	private String   prompt;
	private Database db;
	
	private boolean execute(String line) {
		String lower = line.toLowerCase();
		if (lower.equals("exit") || lower.equals("quit"))
			return false;
		try {
			SQL sql = new SQL(line);
			if (sql.verify()) // if sql query is valid
				sql.printQuery();
				this.db.execute(sql);
		} catch (Exception e) {
			Log.logerr("Error: " + e.getMessage());
			return true;
		}
		return true;
	}
	
	public Shell() {
		this.prompt = new String("Database> ");
		this.db     = new Database(this);
	}
	
	public void setPrompt(String prompt) {
		this.prompt = prompt + "> ";
	}
	
	public void launch() {
		Scanner sc = new Scanner(System.in);
		System.out.print(this.prompt);
		try {
		String input = sc.nextLine();
			while (execute(input)) {
				System.out.print(this.prompt);
				input = sc.nextLine();
			}
		} catch (Exception e) {
			System.out.println();
		}
		System.out.println("Bye!");
	}
	
	public static void main(String[] args) {
		Shell sh = new Shell();
		sh.launch();
	}
}
