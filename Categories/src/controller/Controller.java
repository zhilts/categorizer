package controller;

import java.util.HashSet;

import model.Clasterizator;
import model.Document;
import model.Model;
import model.Term;
import model.Unit;
import visualization.CategoriesWindow;

public class Controller {
	private Model model;
	private CategoriesWindow window;

	public Controller() {
		model = new Model();
		window = new CategoriesWindow(this);
		window.setVisible(true);
	}

	public void getTreeWithParameters(String queryString, double[] limits) {
		HashSet<Document> documents = model.findByString(queryString);

		HashSet<Term> terms = model.getTerms();
		Unit tree = null;
		Clasterizator clasterizator = new Clasterizator();
		model.solveDocsTfidf();
		tree = clasterizator.getTree(documents, terms, limits);

		window.setTree(tree);
	}

}
