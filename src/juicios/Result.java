package juicios;

import java.util.ArrayList;

public class Result {

	private String query;
	public ArrayList<String> docs;
	
	/**
	 * Constructor
	 */
	public Result (String query, ArrayList<String> docs) {
		this.query = query;
		this.docs = docs;
	}
	
	public String getQuery() {
		return query;
	}
	
	public void setQuery(String query) {
		this.query = query;
	}
	
	public ArrayList<String> getDocs() {
		return docs;
	}

	public void setDocs(ArrayList<String> docs) {
		this.docs = docs;
	}
	
	public boolean contains(String doc) {
		return docs.contains(doc);
	}
	
	public String toString(){
		return "query: " + query + "; docs: " + docs.toString();
	}
	
}
