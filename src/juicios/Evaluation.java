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
		ArrayList<Tuple<Double,Double>> recallPrecision = recallPrecision(qrel, results, idQuery);
		System.out.println("interpolate recall_percision");
		interpolateRecallPrecision(recallPrecision);
		
	}
	
	public static double precision(ArrayList<Qrel> qrel, ArrayList<Result> results, int idQuery) {
		double relevantsSelected = numRelevantsSelected(qrel, results, idQuery);
		return relevantsSelected/getResult(results, idQuery).getDocs().size();
	}


	public static double recall(ArrayList<Qrel> qrel, ArrayList<Result> results, int idQuery) {
		double relevants = numRelevants(qrel, idQuery);
		double relevantSelected = numRelevantsSelected(qrel, results, idQuery);
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
		double relevantsSelected = numRelevantsSelected(qrel, results, idQuery);
		Result result = getResult(results, idQuery);
		for (int i = 0; i < result.getDocs().size(); i++) {
			int doc = result.getDocs().get(i);
			if (isRelevant(doc, qrel, idQuery)){
				precK = precK + precisionK(i+1, qrel, results, idQuery);
			}
		}
		return precK/relevantsSelected;
	}
	
	public static ArrayList<Tuple<Double,Double>> recallPrecision(ArrayList<Qrel> qrel, ArrayList<Result> results, int idQuery) {
		Result result = getResult(results, idQuery);
		ArrayList<Result> results2 = new ArrayList<Result>();
		Result result2 = new Result(idQuery,new ArrayList<Integer>());
		ArrayList<Tuple<Double,Double>> tuples = new ArrayList<Tuple<Double,Double>>();
		for (int i = 0; i < result.getDocs().size(); i++) {
			int doc = result.getDocs().get(i);
			result2.docs.add(doc);
			if (isRelevant(doc, qrel, idQuery)){
				results2.add(result2);
				double recall = recall(qrel,results2,idQuery);
				double precision = precision(qrel,results2,idQuery);
				tuples.add(new Tuple(recall,precision));
				System.out.println(recall
						+ " " + precision);
				results2.clear();
			}
		}
		return tuples;
	}
	
	public static void interpolateRecallPrecision(ArrayList<Tuple<Double,Double>> tuples) {
		for (double i = 0; i<=1; i = i + 0.1) {
			double max = 0;
			for (int j = 0; j < tuples.size(); j++) {
				if (i <= tuples.get(j).x) {
					if (max < tuples.get(j).y) {
						max = tuples.get(j).y;
					}
				}
			}
			System.out.printf("%.3f %.3f\n", i , max);
		}
	}

	/*
	 * Given a doc, checks if it is relevant for query idQuery
	 */
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
	
	/*
	 *  @return Result given query
	 */
	private static Result getResult(ArrayList<Result> results, int idQuery) {
		int i = 0;
		while (idQuery != results.get(i).getQuery()){
			i++;
		}
		return results.get(i);
	}

	//Pre: query exists
	//@return true if doc is contained in result
	private static boolean selected(ArrayList<Result> results, int doc, int idQuery) {
		int i = 0;
		while (idQuery != results.get(i).getQuery()){
			i++;
		}
		return results.get(i).contains(doc);
	}
	
	/*
	 * Return number of relevant documents in collection of idQuery
	 */
	private static double numRelevants(ArrayList<Qrel> qrel, int idQuery) {
		double relevants = 0;
		for (int i = 0; i < qrel.size(); i++) {
			if (qrel.get(i).isRelevance()  && qrel.get(i).getQuery()==idQuery ){
				relevants++;
			}
		}
		return relevants;
	}

	/*
	 * Return number of relevant documents given in query idQuery
	 */
	private static double numRelevantsSelected(ArrayList<Qrel> qrel, ArrayList<Result> results, int idQuery) {
		double relevants = 0;
		for (int i = 0; i < qrel.size(); i++) {
			if (qrel.get(i).isRelevance() && qrel.get(i).getQuery()==idQuery 
					&& selected(results, qrel.get(i).getDoc(), idQuery)){
				relevants++;
			}
		}
		return relevants;
	}
	
}
