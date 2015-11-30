package trabajo;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Parser {
	
	private Scanner scanner;
	private ArrayList<Qrel> list;
	
	/**
	 * Class constructor
	 */
	public Parser (String path) {
		try {
			scanner = new Scanner(new File(path));
			list = new ArrayList<Qrel>();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Pre: ---
	 * Post: Returns an ArrayList of Qrel objects.
	 */
	public void start() {
		while(scanner.hasNextLine()){
				list.add(new Qrel(scanner.nextInt(), scanner.nextInt(), scanner.nextInt() == 1));
		}
	}
	
	public ArrayList<Qrel> getList() {
		return list;
	}
	
}
