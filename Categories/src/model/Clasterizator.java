package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class Clasterizator {
	private ProximityMatrix startMartix = null;

	public double quality;

	public Unit getTree(HashSet<Document> documents, HashSet<Term> terms,
			double[] limits) {
		HashSet<Term> reducedTerms = Reductor.reduceDF(terms, documents.size(),
				5.0, 95.0);
		ArrayList<DocumentUnit> documentUnits = getStartTree(documents);
		HashSet<Unit> tree = new HashSet<Unit>(documentUnits);
		HashMap<Unit, FolderUnit> fullTree = new HashMap<Unit, FolderUnit>();

		ProximityMatrix matrix = new ProximityMatrix(documentUnits,
				reducedTerms);
		if (startMartix == null) {
			startMartix = matrix;
		}

		for (int i = 0; i < limits.length; i++) {
			addClasters(documentUnits, tree, fullTree, matrix, limits[i]);
		}

		if (tree.size() > 1) {
			addClasters(documentUnits, tree, fullTree, matrix, 0.0);
		}

		Iterator<Unit> it = tree.iterator();
		Unit topTree = null;
		if (it.hasNext()) {
			topTree = it.next();
			addOtherFolder(topTree);
			topTree = topTree.getSimple();
		}

		return topTree;
	}

	private ArrayList<DocumentUnit> getStartTree(HashSet<Document> documents) {
		ArrayList<DocumentUnit> result = new ArrayList<DocumentUnit>();
		for (Document document : documents) {
			DocumentUnit unit = new DocumentUnit(document);
			result.add(unit);
		}
		return result;
	}

	private void addClasters(ArrayList<DocumentUnit> documentUnits,
			HashSet<Unit> tree, HashMap<Unit, FolderUnit> fullTree,
			ProximityMatrix matrix, double limit) {
		HashSet<FolderUnit> newFolders = new HashSet<FolderUnit>();
		for (int i = 0; i < documentUnits.size() - 1; i++) {
			for (int j = i + 1; j < documentUnits.size(); j++) {
				DocumentUnit unit1 = documentUnits.get(i);
				DocumentUnit unit2 = documentUnits.get(j);
				if (matrix.getProximity(unit1, unit2) >= limit) {
					merge(unit1, unit2, tree, fullTree, newFolders);
				}
			}
		}
		tree.addAll(newFolders);
	}

	private void merge(Unit unit1, Unit unit2, HashSet<Unit> tree,
			HashMap<Unit, FolderUnit> fullTree, HashSet<FolderUnit> newFolders) {
		Unit topUnit1 = getTop(unit1, fullTree);
		Unit topUnit2 = getTop(unit2, fullTree);

		if (topUnit1 == topUnit2) {
			return;
		}

		if (newFolders.contains(topUnit1)) {
			if (newFolders.contains(topUnit2)) {
				for (Unit unit : topUnit2.children) {
					topUnit1.children.add(unit);
					fullTree.put(unit, (FolderUnit) topUnit1);
				}
				tree.remove(topUnit2);
				newFolders.remove(topUnit2);
			} else {
				topUnit1.children.add(topUnit2);
				fullTree.put(topUnit2, (FolderUnit) topUnit1);
				tree.remove(topUnit2);
			}
		} else {
			if (newFolders.contains(topUnit2)) {
				topUnit2.children.add(topUnit1);
				fullTree.put(topUnit1, (FolderUnit) topUnit2);
				tree.remove(topUnit1);
			} else {
				FolderUnit newFolder = new FolderUnit(null);
				newFolder.children.add(topUnit1);
				newFolder.children.add(topUnit2);
				fullTree.put(topUnit1, newFolder);
				fullTree.put(topUnit2, newFolder);
				tree.remove(topUnit1);
				tree.remove(topUnit2);
				tree.add(newFolder);
				newFolders.add(newFolder);
			}
		}
	}

	private Unit getTop(Unit document, HashMap<Unit, FolderUnit> fullTree) {
		if (!fullTree.keySet().contains(document)) {
			return document;
		}
		FolderUnit parent = fullTree.get(document);
		return getTop(parent, fullTree);
	}

	private void addOtherFolder(Unit unit) {
		if (unit.getClass().equals(DocumentUnit.class)) {
			return;
		}
		HashSet<Unit> others = new HashSet<Unit>();
		boolean hasSubfolders = false;
		for (Unit subunit : unit.children) {
			if (subunit.getClass().equals(FolderUnit.class)) {
				addOtherFolder(subunit);
				hasSubfolders = true;
			} else {
				others.add(subunit);
			}
		}
		if (hasSubfolders && others.size() > 1) {
			FolderUnit otherFolder = new FolderUnit("Other");
			otherFolder.children.addAll(others);
			unit.children.removeAll(others);
			unit.children.add(otherFolder);
		}
	}
}
