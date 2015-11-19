package trabajo;

public class Qrel {

	private int query;
	private int doc;
	private boolean relevance;
	
	public Qrel (int query, int doc, boolean relevance) {
		this.query = query;
		this.doc = doc;
		this.relevance = relevance;
	}

	public int getQuery() {
		return query;
	}

	public void setQuery(int query) {
		this.query = query;
	}

	public int getDoc() {
		return doc;
	}

	public void setDoc(int doc) {
		this.doc = doc;
	}

	public boolean isRelevance() {
		return relevance;
	}

	public void setRelevance(boolean relevance) {
		this.relevance = relevance;
	}
	
	
}