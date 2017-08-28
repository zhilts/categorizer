package model;

import java.util.HashMap;
import java.util.HashSet;

public class Document {
	private String description;
	private String path;

	private HashMap<Term, Integer> termsToFrequency;
	private HashMap<Term, Double> termsToTfidf;

	public Document(String description, String path) {
		this.description = description;
		this.path = path;
		termsToFrequency = new HashMap<Term, Integer>();
	}

	public void addTerm(Term term, int count) {
		termsToFrequency.put(term, new Integer(count));
	}

	public String getFilePath() {
		return path;
	}

	public String getDescription() {
		return description;
	}

	public HashSet<Term> getTerms() {
		return new HashSet<Term>(termsToFrequency.keySet());
	}

	public int getFrequency(Term term) {
		Integer frequency = termsToFrequency.get(term);
		if (frequency == null) {
			frequency = new Integer(0);
		}
		return frequency.intValue();
	}

	public void solveTfidf(int docsCount) {
		termsToTfidf = new HashMap<Term, Double>();
		HashSet<Term> terms = new HashSet<Term>(termsToFrequency.keySet());
		for (Term term : terms) {
			int currentFrequency = termsToFrequency.get(term).intValue();
			int docFrequency = term.getDocuments().size();
			double reversFrequency = Math.log10(docsCount * 1.0 / docFrequency);
			double tdidf = currentFrequency * reversFrequency;
			termsToTfidf.put(term, new Double(tdidf));
		}
		// normilizeTerms();
	}

	@SuppressWarnings("unused")
	private void normilizeTerms() {
		double len = solveLen();
		HashSet<Term> terms = (HashSet<Term>) termsToTfidf.keySet();
		for (Term term : terms) {
			double dim = termsToTfidf.get(term);
			double normDim = dim / len;
			termsToTfidf.put(term, new Double(normDim));
		}
	}

	private double solveLen() {
		double len = 0;
		for (Double dimention : termsToTfidf.values()) {
			double dim = dimention.doubleValue();
			len += dim * dim;
		}
		return Math.sqrt(len);
	}

	public double getTfidfByTerm(Term term) {
		Double tfidf = termsToTfidf.get(term);
		if (tfidf == null) {
			return 0.0;
		}
		return tfidf.doubleValue();
	}

	@Override
	public String toString() {
		return this.getDescription();
	}
}
