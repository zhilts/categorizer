package model;

import java.util.ArrayList;
import java.util.HashSet;

public class ProximityMatrix {
	private double[][] matrix;
	private ArrayList<DocumentUnit> documents;

	public ProximityMatrix(ArrayList<DocumentUnit> documents,
			HashSet<Term> terms) {
		this.documents = documents;
		createMatrix(terms);
	}

	private void createMatrix(HashSet<Term> terms) {
		int documentsCount = documents.size();
		matrix = new double[documentsCount][];
		for (int i = 1; i < documentsCount; i++) {
			matrix[i] = new double[i];
			for (int j = 0; j < i; j++) {
				matrix[i][j] = distance(documents.get(i), documents.get(j),
						terms);
			}
		}
	}

	private static double distance(DocumentUnit unit1, DocumentUnit unit2,
			HashSet<Term> dimention) {

		double division = 0;
		double len1 = 0;
		double len2 = 0;

		for (Term term : dimention) {
			double d1 = unit1.getValueByDimention(term);
			double d2 = unit2.getValueByDimention(term);
			division += d1 * d2;
			len1 += d1 * d1;
			len2 += d2 * d2;
		}
		if (len1 == 0 || len2 == 0) {
			return 0;
		}

		double distance = division / (Math.sqrt(len1) * Math.sqrt(len2));

		return distance;
	}

	public double getProximity(DocumentUnit doc1, DocumentUnit doc2) {
		int index1 = documents.indexOf(doc1);
		int index2 = documents.indexOf(doc2);
		int i = Math.max(index1, index2);
		int j = Math.min(index1, index2);
		return matrix[i][j];
	}

	public double getMetric(ProximityMatrix startMatrix, double[] limits) {
		double M = matrix.length * (matrix.length - 1) / 2;
		double ms = 0, msc = 0;

		for (int i = 1; i < matrix.length; i++) {
			for (int j = 0; j < i; j++) {
				ms += startMatrix.matrix[i][j];
				msc += getsc(matrix[i][j], limits);
			}
		}
		ms /= M;
		msc /= M;

		double S1 = 0, S2 = 0, S3 = 0;
		for (int i = 1; i < matrix.length - 1; i++) {
			for (int j = 0; j < i; j++) {
				S1 += startMatrix.matrix[i][j] * getsc(matrix[i][j], limits);
				S2 += startMatrix.matrix[i][j] * startMatrix.matrix[i][j];
				S3 += getsc(matrix[i][j], limits) * getsc(matrix[i][j], limits);
			}
		}
		double res = (S1 / M - ms * msc)
				/ (Math.sqrt((S2 / M - ms * ms) * (S3 / M - msc * msc)));

		return res;
	}

	private double getsc(double value, double[] limits) {
		if (limits.length == 0 || value < limits[limits.length - 1]) {
			return 0;
		}
		int i = 0;
		while (i < limits.length && value < limits[i]) {
			i++;
		}
		return limits[i];
	}
}
