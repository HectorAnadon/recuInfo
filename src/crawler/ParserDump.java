package crawler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Formatter;
import java.util.Scanner;

public class ParserDump {

	private static Scanner scanner;
	private static Formatter formatter;
	
	
	public static void start (String path){
		try {
			scanner = new Scanner(new File(path), "UTF-8");
			String word;
			
			while (scanner.hasNext()) {
				word = scanner.next();
				if (word.equals("URL::")){
					word = scanner.next();
					formatter = new Formatter("tmp/" + word.replace('/', '\\'));
					word = scanner.nextLine();
					while (!word.contains("<?xml version=") && !word.equals("<html>")) {
						word = scanner.nextLine();
					}
					formatter.format("%s", word);
					if (!word.contains("</oai_dc:dc>") && !word.contains("</html>")){
						formatter.format("\n");
						boolean stop = false;
						while (!stop) {
							//System.out.printf("%s", word);
							word = scanner.nextLine();
							formatter.format("%s", word);
							if (!scanner.hasNextLine() || word.contains("</oai_dc:dc>") || word.contains("</html>") || word.equals("</html>")) {
								stop = true;
							}else{
								formatter.format("\n");
							}
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
		System.out.println("Empezando...");
		pd.start("Datos/dump");
		System.out.println("FIN");
	}
}
