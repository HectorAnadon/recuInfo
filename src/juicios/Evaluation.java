package juicios;

import java.util.ArrayList;
import java.util.Arrays;

public class Evaluation {
	
	public static void main(String[] args) {
		Parser p = new Parser();
		p.startQrel("practica3/zaguanRels.txt");
		p.startResult("practica3/equipo12.txt");
		ArrayList<Qrel> qrel = p.getQrelList();
		ArrayList<Result> results = p.getResultList();
		double precisionTotal = 0;
		double recallTotal = 0;
		double prec10Total = 0;
		double map = 0;
		ArrayList<Tuple<Double,Double>> interpolateTotal = null;
		String[] idQuerys = {"02-4","05-5","07-2","09-3","13-2"};
		for (int i = 0; i< idQuerys.length; i++){
			System.out.println("Query: " + idQuerys[i]);
			double precision = precision(qrel,results,idQuerys[i]);
			precisionTotal+=precision;
			System.out.println("Precision: " + precision);
			double recall = recall(qrel,results,idQuerys[i]);
			recallTotal+=recall;
			System.out.println("Recall: " + recall);
			System.out.println("F1: " + f1(precision, recall));
			double prec10 = precisionK(10,qrel,results,idQuerys[i]);
			prec10Total+= prec10;
			System.out.println("Precision@10: " + prec10);
			double averagePrecision = averagePrecision(qrel,results,idQuerys[i]);
			map+=averagePrecision;
			System.out.println("Average Precision: " + averagePrecision);
			System.out.println("recall_percision");
			ArrayList<Tuple<Double,Double>> recallPrecision = recallPrecision(qrel, results, idQuerys[i]);
			System.out.println("interpolate recall_percision");
			ArrayList<Tuple<Double,Double>> interpolate = interpolateRecallPrecision(recallPrecision);
			if (interpolateTotal == null) {
				interpolateTotal = interpolate;
			} else {
				for (int j=0; j<interpolateTotal.size(); j++) {
					interpolateTotal.get(j).y+=interpolate.get(j).y;
				}
			}
		}
		System.out.println("Total");
		System.out.println("Precision: " + precisionTotal/idQuerys.length);
		System.out.println("Recall: " + recallTotal/idQuerys.length);
		System.out.println("F1: " + f1(precisionTotal/idQuerys.length, recallTotal/idQuerys.length));
		System.out.println("Precision@10: " + prec10Total/idQuerys.length);
		System.out.println("Map: " + map/idQuerys.length);
		System.out.println("interpolate recall_percision");
		for (int j=0; j<interpolateTotal.size(); j++) {
			System.out.printf("%.3f %.3f\n", interpolateTotal.get(j).x , interpolateTotal.get(j).y/idQuerys.length);
		}
	}
	
	public static double precision(ArrayList<Qrel> qrel, ArrayList<Result> results, String idQuery) {
		double relevantsSelected = numRelevantsSelected(qrel, results, idQuery);
		return relevantsSelected/getResult(results, idQuery).getDocs().size();
	}


	public static double recall(ArrayList<Qrel> qrel, ArrayList<Result> results, String idQuery) {
		double relevants = numRelevants(qrel, idQuery);
		double relevantSelected = numRelevantsSelected(qrel, results, idQuery);
		return relevantSelected/relevants;
	}
	
	public static double f1(double precision, double recall) {
		return (2* precision * recall) / (precision + recall);
	}
	
	public static double precisionK (int k, ArrayList<Qrel> qrel, ArrayList<Result> result, String idQuery) {
		Result results = getResult(result, idQuery);
		double numerator = 0;
		for (int i = 0; i < k; i++) {
			String doc = results.docs.get(i);
			if (isRelevant(doc, qrel, idQuery)) {
				numerator = numerator + 1;
			}
		}
		return numerator/k;
	}
	
	public static double averagePrecision (ArrayList<Qrel> qrel, ArrayList<Result> results, String idQuery) {
		double precK = 0;
		double relevantsSelected = numRelevantsSelected(qrel, results, idQuery);
		Result result = getResult(results, idQuery);
		for (int i = 0; i < result.getDocs().size(); i++) {
			String doc = result.getDocs().get(i);
			if (isRelevant(doc, qrel, idQuery)){
				precK = precK + precisionK(i+1, qrel, results, idQuery);
			}
		}
		return precK/relevantsSelected;
	}
	
	public static ArrayList<Tuple<Double,Double>> recallPrecision(ArrayList<Qrel> qrel, ArrayList<Result> results, String idQuery) {
		Result result = getResult(results, idQuery);
		ArrayList<Result> results2 = new ArrayList<Result>();
		Result result2 = new Result(idQuery,new ArrayList<String>());
		ArrayList<Tuple<Double,Double>> tuples = new ArrayList<Tuple<Double,Double>>();
		for (int i = 0; i < result.getDocs().size(); i++) {
			String doc = result.getDocs().get(i);
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
	
	public static ArrayList<Tuple<Double,Double>> interpolateRecallPrecision(ArrayList<Tuple<Double,Double>> tuples) {
		ArrayList<Tuple<Double,Double>> result = new ArrayList<Tuple<Double,Double>>();
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
			result.add(new Tuple(i,max));
		}
		return result;
	}

	/*
	 * Given a doc, checks if it is relevant for query idQuery
	 */
	private static boolean isRelevant(String doc, ArrayList<Qrel> qrel, String idQuery) {
		for (int i = 0; i < qrel.size(); i++) {
			Qrel q = qrel.get(i);
			if (q.getQuery().equals( idQuery) && q.getDoc() .equals( doc)) {
				if (q.isRelevant()) {
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
	private static Result getResult(ArrayList<Result> results, String idQuery) {
		int i = 0;
		while (!idQuery.equals( results.get(i).getQuery())){
			i++;
		}
		return results.get(i);
	}

	//Pre: query exists
	//@return true if doc is contained in result
	private static boolean selected(ArrayList<Result> results, String doc, String idQuery) {
		int i = 0;
		while (!idQuery.equals(results.get(i).getQuery())){
			i++;
		}
		return results.get(i).contains(doc);
	}
	
	/*
	 * Return number of relevant documents in collection of idQuery
	 */
	private static double numRelevants(ArrayList<Qrel> qrel, String idQuery) {
		double relevants = 0;
		for (int i = 0; i < qrel.size(); i++) {
			if (qrel.get(i).isRelevant()  && qrel.get(i).getQuery().equals(idQuery) ){
				relevants++;
			}
		}
		return relevants;
	}

	/*
	 * Return number of relevant documents given in query idQuery
	 */
	private static double numRelevantsSelected(ArrayList<Qrel> qrel, ArrayList<Result> results, String idQuery) {
		double relevants = 0;
		for (int i = 0; i < qrel.size(); i++) {
			if (qrel.get(i).isRelevant() && qrel.get(i).getQuery().equals(idQuery) 
					&& selected(results, qrel.get(i).getDoc(), idQuery)){
				relevants++;
			}
		}
		return relevants;
	}
	
}
