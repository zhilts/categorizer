package model;

import java.util.HashSet;

public class Term {
	private String name;
	private int count;
	private HashSet <Document> documents;

	public Term(String name, int count) {
		this.name = name;
		this.count = count;
		documents = new HashSet<Document>();
	}
	
	public void addDocument(Document document) {
		documents.add(document);
	}

	public String getName() {
		return name;
	}
	
	public HashSet<Document> getDocuments() {
		return documents;
	}
	
	public int getCount() {
		return count;
	}
}
