package juicios;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Parser {
	
	private Scanner scanner;
	private ArrayList<Qrel> QrelList;
	private ArrayList<Result> ResultList;
	
	/**
	 * Class constructor
	 */
	public Parser() {
			QrelList = new ArrayList<Qrel>();
			ResultList = new ArrayList<Result>();
	}
	
	/**
	 * Pre: ---
	 * Post: Add elements of the file 'path' to the Qrel objects list
	 */
	public void startQrel (String path) {
		try {
			scanner = new Scanner(new File(path));
			while(scanner.hasNextLine()){
				QrelList.add(new Qrel(scanner.nextInt(), scanner.nextInt(), scanner.nextInt() == 1));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Pre: ---
	 * Post: Add elements of the file 'path' to the Result objects list
	 */
	public void startResult (String path) {
		  try {
				scanner = new Scanner(new File(path));
				ArrayList<Integer> lAux = new ArrayList<Integer>();
				int infNeed = -1;
				while(scanner.hasNextLine()){
					int aux = scanner.nextInt();
					if(infNeed != aux){
						if(infNeed != -1){
							ResultList.add(new Result(infNeed, (ArrayList<Integer>)lAux.clone()));
							lAux.clear();;
						}
						infNeed = aux;
						lAux.add(scanner.nextInt());
					}else{
						lAux.add(scanner.nextInt());
					}
				}
				ResultList.add(new Result(infNeed, lAux));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
	}
	
	/**
	 * Pre: ---
	 * Post: Returns an ArrayList of Qrel objects.
	 */
	public ArrayList<Qrel> getQrelList() {
		return QrelList;
	}
	
	public ArrayList<Result> getResultList(){
		return ResultList;
	}
	
	/**
	 * Probe
	 */
	public static void main (String[] a) {
		Parser p = new Parser();
		p.startQrel("datos/qrels.txt");
		ArrayList<Qrel> l = p.getQrelList();
		for(int i=0; i<l.size(); i++){
			System.out.println(l.get(i));
		}
		System.out.println("\n\n");
		p.startResult("datos/results.txt");
		ArrayList<Result> l2 = p.getResultList();
		for(int i=0; i<l2.size(); i++){
			System.out.println(l2.get(i));
		}
	}
	
}
