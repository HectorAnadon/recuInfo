package trabajo;

import java.util.ArrayList;

public class Result {

	private int query;
	public ArrayList<Integer> docs;
	
	/**
	 * Constructor
	 */
	public Result (int query, ArrayList<Integer> docs) {
		this.query = query;
		this.docs = docs;
	}
	
	public int getQuery() {
		return query;
	}
	
	public void setQuery(int query) {
		this.query = query;
	}
	
	public ArrayList<Integer> getDocs() {
		return docs;
	}

	public void setDocs(ArrayList<Integer> docs) {
		this.docs = docs;
	}
	
	public boolean contains(int doc) {
		return docs.contains(doc);
	}
	
	public String toString(){
		return "query: " + query + "; docs: " + docs.toString();
	}
	
}
