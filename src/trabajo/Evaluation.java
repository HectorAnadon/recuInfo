package trabajo;

import java.util.ArrayList;
import java.util.Arrays;

public class Evaluation {
	
	public double precision = 0;
	public double recall = 0;
	
	public static void main(String[] args) {
		ArrayList<Integer> je = new ArrayList<Integer>();
		je.add(1);
		je.add(2);
		System.out.println(je.contains(1));
		
	}
	
	public double precision(Qrel[] qrel, Result[] results, int idQuery) {
		int relevants = 0;
		for (int i = 0; i < qrel.length; i++) {
			if (qrel[i].isRelevance() && qrel[i].getDoc()==idQuery 
					&& selected(results, qrel[i].getDoc(), idQuery)){
				relevants++;
			}
		}
		return relevants/results.length;
	}

	public double recall(Qrel[] qrel, int[] results) {
		int relevants = 0;
		for (int i = 0; i < qrel.length; i++) {
			if (qrel[i].isRelevance()){
				relevants++;
			}
		}
		return relevants/relevants;
	}
	
	public double f1(double precision, double recall) {
		return (2* precision * recall) / (precision + recall);
	}
	
	//public double precisionK (double )

	//Pre: query exists
	private static boolean selected(Result[] results, int doc, int idQuery) {
		int i = 0;
		while (idQuery != results[i].getQuery()){
			i++;
		}
		return results[i].contains(doc);
	}
}
