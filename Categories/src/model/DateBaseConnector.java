package model;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import com.mysql.jdbc.ConnectionImpl;
import com.mysql.jdbc.StatementImpl;

public class DateBaseConnector {
	private static ResultSet result;
	private static final String DB_NAME = "categories_db";
	private static final String MYSQL_LOGIN = "root";
	private static final String MYSQL_PASSWORD = "mango";
	private static ConnectionImpl connection;
	private static StatementImpl statement;

	public void connect() {
		String dbURL = "jdbc:mysql:" + "//localhost:3306/" + DB_NAME
				+ "?useUnicode=true&characterEncoding=UTF-8";
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			connection = (ConnectionImpl) DriverManager.getConnection(dbURL,
					MYSQL_LOGIN, MYSQL_PASSWORD);
			statement = (StatementImpl) connection.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public HashMap<Integer, Term> loadTerms() throws SQLException {
		String query = "SELECT id, name, count FROM term";
		HashMap<Integer, Term> terms = new HashMap<Integer, Term>();
		result = statement.executeQuery(query);
		while (result.next()) {
			String name = result.getString("name");
			int id = result.getInt("id");
			int count = result.getInt("count");
			Term term = new Term(name, count);
			terms.put(new Integer(id), term);
		}
		return terms;
	}

	public HashMap<Integer, Document> loadFiles() throws SQLException {
		String query = "SELECT id, description, file_path FROM document";
		HashMap<Integer, Document> documents = new HashMap<Integer, Document>();
		result = statement.executeQuery(query);
		while (result.next()) {
			String description = result.getString("description");
			int id = result.getInt("id");
			String path = result.getString("file_path");
			Document document = new Document(description, path);
			documents.put(new Integer(id), document);
		}
		return documents;
	}

	public void fillDocumentTermReferences(
			HashMap<Integer, Document> documents, HashMap<Integer, Term> terms)
			throws SQLException {
		String query = "SELECT document_ref, term_ref, frequency FROM ref_doc_term";
		result = statement.executeQuery(query);
		while (result.next()) {
			int docId = result.getInt("document_ref");
			int termId = result.getInt("term_ref");
			int frequency = result.getInt("frequency");

			Document document = documents.get(new Integer(docId));
			Term term = terms.get(new Integer(termId));

			document.addTerm(term, frequency);
			term.addDocument(document);
		}
	}

	public void closeConnection() throws SQLException {
		connection.close();
	}
}
