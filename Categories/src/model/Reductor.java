package model;

import java.util.HashSet;

public class Reductor {
	/**
	 * Reducing by <b>D</b>ocument <b>F</b>requency
	 * 
	 * @param terms
	 * @param documentCount
	 * @param lowLimit
	 * @param upLimit
	 * @return
	 */
	public static HashSet<Term> reduceDF(HashSet<Term> terms,
			int documentCount, double lowLimit, double upLimit) {
		HashSet<Term> result = new HashSet<Term>();
		for (Term term : terms) {
			double percent = term.getDocuments().size() * 100.0 / documentCount;
			if (percent > lowLimit && percent < upLimit) {
				result.add(term);
			}
		}
		return result;
	}
}
