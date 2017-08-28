package model;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import snowball.SnowballStemmer;
import snowball.ext.russianStemmer;

public class Model {

	private HashMap<Integer, Term> terms;
	private HashMap<String, Term> termsForSearch;
	private HashMap<Integer, Document> documents;

	public Model() {
		DateBaseConnector dbc = new DateBaseConnector();
		loadFromDB(dbc);
	}

	private void loadFromDB(DateBaseConnector dbc) {
		dbc.connect();
		try {
			terms = dbc.loadTerms();
			termsForSearch = getTermsForSearch(terms);
			documents = dbc.loadFiles();
			dbc.fillDocumentTermReferences(documents, terms);
			dbc.closeConnection();
			// solveDocsTfidf();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private HashMap<String, Term> getTermsForSearch(HashMap<Integer, Term> terms) {
		HashMap<String, Term> result = new HashMap<String, Term>();
		for (Term term : terms.values()) {
			result.put(term.getName(), term);
		}
		return result;
	}

	public HashSet<Document> findByString(String string) {
		SnowballStemmer stemmer = new russianStemmer();
		HashSet<String> terms = new HashSet<String>();
		String lowerCaseString = string.toLowerCase();
		String[] words = lowerCaseString.split(" ");
		for (int i = 0; i < words.length; i++) {
			stemmer.setCurrent(words[i]);
			stemmer.stem();
			String term = stemmer.getCurrent();
			if (!term.equals("")) {
				terms.add(term);
			}
		}
		return findByTerms(terms);
	}

	public HashSet<Document> findByTerms(HashSet<String> terms) {

		HashSet<Document> searchResult = new HashSet<Document>();
		searchResult.addAll(documents.values());

		Iterator<String> it = terms.iterator();
		while (it.hasNext()) {
			String word = it.next();
			if (termsForSearch.keySet().contains(word)) {
				Term term = termsForSearch.get(word);
				HashSet<Document> concreteSet = term.getDocuments();
				searchResult = division(searchResult, concreteSet);
			} else {
				searchResult = new HashSet<Document>();
			}
		}
		return searchResult;
	}

	private HashSet<Document> division(HashSet<Document> s1,
			HashSet<Document> s2) {
		HashSet<Document> divisionResult = new HashSet<Document>();
		Iterator<Document> it = s1.iterator();
		while (it.hasNext()) {
			Document document = it.next();
			if (s2.contains(document)) {
				divisionResult.add(document);
			}
		}
		return divisionResult;
	}

	public void solveDocsTfidf() {
		HashSet<Document> documentsSet = new HashSet<Document>(
				documents.values());
		int docsCount = documentsSet.size();
		for (Document document : documentsSet) {
			document.solveTfidf(docsCount);
		}
	}

	public HashSet<Term> getTerms() {
		return new HashSet<Term>(terms.values());
	}
}
