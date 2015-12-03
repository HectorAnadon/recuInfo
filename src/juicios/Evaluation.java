package juicios;

import java.util.ArrayList;
import java.util.Arrays;

public class Evaluation {
	
	public double precision = 0;
	public double recall = 0;
	
	public static void main(String[] args) {
		Parser p = new Parser();
		p.startQrel("practica3/qrels.txt");
		p.startResult("practica3/results.txt");
		ArrayList<Qrel> qrel = p.getQrelList();
		ArrayList<Result> results = p.getResultList();
		int idQuery = 2;
		System.out.println(precision(qrel,results,idQuery));
		System.out.println(recall(qrel,results,idQuery));
		
	}
	
	public static double precision(ArrayList<Qrel> qrel, ArrayList<Result> results, int idQuery) {
		double relevants = 0;
		for (int i = 0; i < qrel.size(); i++) {
			if (qrel.get(i).isRelevance() && qrel.get(i).getQuery()==idQuery 
					&& selected(results, qrel.get(i).getDoc(), idQuery)){
				relevants++;
			}
		}
		return relevants/getResult(results, idQuery).getDocs().size();
	}

	private static Result getResult(ArrayList<Result> results, int idQuery) {
		int i = 0;
		while (idQuery != results.get(i).getQuery()){
			i++;
		}
		return results.get(i);
	}

	public static double recall(ArrayList<Qrel> qrel, ArrayList<Result> results, int idQuery) {
		double relevants = 0;
		for (int i = 0; i < qrel.size(); i++) {
			if (qrel.get(i).isRelevance()  && qrel.get(i).getQuery()==idQuery ){
				relevants++;
			}
		}
		int relevantSelected = 0;
		for (int i = 0; i < qrel.size(); i++) {
			if (qrel.get(i).isRelevance() && qrel.get(i).getQuery()==idQuery 
					&& selected(results, qrel.get(i).getDoc(), idQuery)){
				relevantSelected++;
			}
		}
		System.out.println(relevantSelected);
		System.out.println(relevants);
		return relevantSelected/relevants;
	}
	
	public double f1(double precision, double recall) {
		return (2* precision * recall) / (precision + recall);
	}
	
	//public double precisionK (double )

	//Pre: query exists
	private static boolean selected(ArrayList<Result> results, int doc, int idQuery) {
		int i = 0;
		while (idQuery != results.get(i).getQuery()){
			i++;
		}
		return results.get(i).contains(doc);
	}
}
