package crawler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Formatter;
import java.util.Scanner;

public class ParserDump {

	private Scanner scanner;
	private Formatter formatter;
	
	public ParserDump () {
	}
	
	public void start (String path){
		try {
			scanner = new Scanner(new File(path), "UTF-8");
			String word;
			
			while (scanner.hasNext()) {
				word = scanner.next();
				if (word.equals("URL::")){
					word = scanner.next();
					formatter = new Formatter("tmp/" + word.replace('/', '\\'));
					word = scanner.nextLine();
					while (!word.contains("Content:")) {
						word = scanner.nextLine();
					}
					word = scanner.nextLine();
					while (scanner.hasNextLine() && !word.contains("Recno::")) {
						//System.out.printf("%s", word);
						formatter.format("%s\n", word);
						if (scanner.hasNextLine()) {
							word = scanner.nextLine();
						}
					}
					formatter.close();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void main (String[] a) {
		ParserDump pd = new ParserDump();
		pd.start("datos/dump");
	}
}
