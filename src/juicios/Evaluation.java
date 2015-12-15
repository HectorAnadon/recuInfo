package juicios;

import java.util.ArrayList;
import java.util.Arrays;

public class Evaluation {
	
	public static void main(String[] args) {
		Parser p = new Parser();
		p.startQrel("practica3/qrels.txt");
		p.startResult("practica3/results.txt");
		ArrayList<Qrel> qrel = p.getQrelList();
		ArrayList<Result> results = p.getResultList();
		int idQuery = 2;
		double precision = precision(qrel,results,idQuery);
		System.out.println(precision);
		double recall = recall(qrel,results,idQuery);
		System.out.println(recall);
		System.out.println(f1(precision, recall));
		System.out.println(precisionK(10,qrel,results,idQuery));
		System.out.println(averagePrecision(qrel,results,idQuery));
		System.out.println("recall_percision");
		recallPrecision(qrel, results, idQuery);
		
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
		return relevantSelected/relevants;
	}
	
	public static double f1(double precision, double recall) {
		return (2* precision * recall) / (precision + recall);
	}
	
	public static double precisionK (int k, ArrayList<Qrel> qrel, ArrayList<Result> result, int idQuery) {
		Result results = getResult(result, idQuery);
		double numerator = 0;
		for (int i = 0; i < k; i++) {
			int doc = results.docs.get(i);
			if (isRelevant(doc, qrel, idQuery)) {
				numerator = numerator + 1;
			}
		}
		return numerator/k;
	}
	
	public static double averagePrecision (ArrayList<Qrel> qrel, ArrayList<Result> results, int idQuery) {
		double precK = 0;
		double relevants = 0;
		for (int i = 0; i < qrel.size(); i++) {
			if (qrel.get(i).isRelevance()  && qrel.get(i).getQuery()==idQuery ){
				relevants++;
			}
		}
		Result result = getResult(results, idQuery);
		for (int i = 0; i < result.getDocs().size(); i++) {
			int doc = result.getDocs().get(i);
			if (isRelevant(doc, qrel, idQuery)){
				precK = precK + precisionK(i+1, qrel, results, idQuery);
			}
		}
		return precK/relevants;
	}

	private static boolean isRelevant(int doc, ArrayList<Qrel> qrel, int idQuery) {
		for (int i = 0; i < qrel.size(); i++) {
			Qrel q = qrel.get(i);
			if (q.getQuery() == idQuery && q.getDoc() == doc) {
				if (q.isRelevance()) {
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}

	//Pre: query exists
	private static boolean selected(ArrayList<Result> results, int doc, int idQuery) {
		int i = 0;
		while (idQuery != results.get(i).getQuery()){
			i++;
		}
		return results.get(i).contains(doc);
	}
	
	public static void recallPrecision(ArrayList<Qrel> qrel, ArrayList<Result> results, int idQuery) {
		Result result = getResult(results, idQuery);
		ArrayList<Result> results2 = new ArrayList<Result>();
		Result result2 = new Result(idQuery,new ArrayList<Integer>());
		for (int i = 0; i < result.getDocs().size(); i++) {
			int doc = result.getDocs().get(i);
			result2.docs.add(doc);
			if (isRelevant(doc, qrel, idQuery)){
				results2.add(result2);
				System.out.println(recall(qrel,results2,idQuery)
						+ " " + precision(qrel,results2,idQuery));
				results2.clear();
			}
		}
	}
}
