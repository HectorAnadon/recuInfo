package juicios;

public class Qrel {

	private String query;
	private String doc;
	private boolean relevance;
	
	public Qrel (int query, int doc, boolean relevance) {
		this.query = String.valueOf(query);
		this.doc = String.valueOf(doc);
		this.relevance = relevance;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getDoc() {
		return doc;
	}
	
	public void setDoc(String doc) {
		this.doc = doc;
	}

	public boolean isRelevant() {
		return relevance;
	}

	public void setRelevance(boolean relevance) {
		this.relevance = relevance;
	}
	
	public String toString() {
		return "query: " + query + ", doc: " + doc + ", relevance: " + relevance;
	}
	
}
