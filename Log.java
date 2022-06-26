import java.io.BufferedWriter;
import java.io.FileWriter;
import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime;    

public class Log {
	public static void logerr(String message) {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(".log", true))) {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm:ss ");  
			LocalDateTime now = LocalDateTime.now();
			bw.append(dtf.format(now) + message);
			bw.newLine();
			System.err.println(message);
		} catch (Exception e) {
			System.err.println("Fatal error: Could not open .log file");
		}
	}
}