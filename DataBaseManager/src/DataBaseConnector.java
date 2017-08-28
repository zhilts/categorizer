import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

import org.mozilla.universalchardet.UniversalDetector;

import snowball.SnowballStemmer;
import snowball.ext.russianStemmer;

import com.mysql.jdbc.ConnectionImpl;
import com.mysql.jdbc.StatementImpl;

public class DataBaseConnector {
	private static ResultSet result;
	private static final String DB_NAME = "categories_db";
	private static final String MYSQL_LOGIN = "root";
	private static final String MYSQL_PASSWORD = "mango";
	private static final String fileStorage = "/mnt/data/categories_storage/";
	private static ConnectionImpl connection;
	private static StatementImpl statement;

	private String fileCharset = "UTF-8";

	public static final int maxTermLength = 80;

	public DataBaseConnector() {
		File directory = new File(getFilestoragePath());
		if (!directory.exists() || directory.isFile()) {
			directory.mkdirs();
		}
	}

	public void clerDB() {
		connect();
		try {
			String query = "TRUNCATE TABLE document";
			statement.execute(query);
			query = "TRUNCATE TABLE term";
			statement.execute(query);
			query = "TRUNCATE TABLE ref_doc_term";
			statement.execute(query);
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String addDocument(File inputFile) {
		try {
			connect();

			String newFilePath = copyFile(inputFile);

			if (newFilePath != null) {
				analizeFile(newFilePath);
			}
			connection.close();
			return newFilePath;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private String copyFile(File inputFile) throws Exception {
		BufferedReader reader = null;
		BufferedWriter writer = null;
		try {
			String inputCharset = getCharset(inputFile);
			FileInputStream fileInputStream = new FileInputStream(inputFile);
			InputStreamReader inputStreamReader = new InputStreamReader(
					fileInputStream, inputCharset);
			reader = new BufferedReader(inputStreamReader);

			String newFilePath = getFilestoragePath() + generateName();
			File outputFile = new File(newFilePath);
			FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
					fileOutputStream, fileCharset);
			writer = new BufferedWriter(outputStreamWriter);

			char[] cbuf = new char[4096];
			int len;
			int hash = 0;
			while ((len = reader.read(cbuf)) > 0) {
				String str = new String(cbuf, 0, len);
				hash = hash ^ str.hashCode();
				writer.write(cbuf, 0, len);
			}

			if (checkExistingFiles(hash, newFilePath)) {
				System.err.println("existing file: deleting");
				outputFile.delete();
				throw new Exception("File is in the DB");
			}

			String query = "INSERT INTO document (description, file_path, hash) values ('"
					+ inputFile.getName()
					+ "', '"
					+ newFilePath
					+ "', "
					+ hash
					+ ");";

			statement.execute(query);
			return newFilePath;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (reader != null) {
				reader.close();
			}
			if (writer != null) {
				writer.close();
			}
		}
	}

	private String getCharset(File file) throws IOException {
		byte[] buf = new byte[4096];
		FileInputStream fis = new FileInputStream(file);
		UniversalDetector detector = new UniversalDetector(null);
		int nread;
		while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
			detector.handleData(buf, 0, nread);
		}
		fis.close();
		detector.dataEnd();
		String encoding = detector.getDetectedCharset();
		if (encoding == null) {
			System.out.println("No encoding detected. " + file.getName());
			encoding = "UTF-8";
		}
		detector.reset();
		return encoding;
	}

	private boolean checkExistingFiles(int hash, String path) {
		String getQuery = "select file_path from document where hash = " + hash;
		InputStream newStream, oldStream;
		boolean functionResult = false;
		try {
			result = statement.executeQuery(getQuery);
			while (!functionResult && result.next()) {
				String oldFilePath = result.getString("file_path");
				newStream = new FileInputStream(new File(path));
				oldStream = new FileInputStream(new File(oldFilePath));
				byte[] bufNew = new byte[4096];
				byte[] bufOld = new byte[4096];
				int lenNew, lenOld;
				boolean equals = true;

				do {
					lenOld = oldStream.read(bufOld);
					lenNew = newStream.read(bufNew);
					if (lenOld != lenNew) {
						equals = false;
					} else if (!equalsArrays(bufNew, bufOld)) {
						equals = false;
					}
				} while (equals && lenNew > 0);

				functionResult = equals;

				newStream.close();
				oldStream.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return functionResult;
	}

	private boolean equalsArrays(byte[] arr1, byte[] arr2) {
		if (arr1.length != arr2.length) {
			return false;
		}

		for (int i = 0; i < arr1.length; i++) {
			if (arr1[i] != arr2[i]) {
				return false;
			}
		}

		return true;
	}

	private String generateName() {
		String name = null;
		try {
			do {
				name = "" + UUID.randomUUID();
				result = statement
						.executeQuery("select * from document where file_path ='"
								+ name + "'");

			} while (result.next());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return name;
	}

	private static void connect() {
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

	private void analizeFile(String fileName) throws IOException {
		SnowballStemmer stemmer = new russianStemmer();
		Reader reader;
		reader = new InputStreamReader(new FileInputStream(fileName),
				fileCharset);
		reader = new BufferedReader(reader);

		StringBuffer input = new StringBuffer();

		HashMap<String, Integer> words = new HashMap<String, Integer>();

		int character;

		while ((character = reader.read()) != -1) {
			char ch = (char) character;
			if (!Character.isAlphabetic(ch) && !Character.isDigit(ch)) {
				if (input.length() > 0) {
					stemmer.setCurrent(input.toString());
					stemmer.stem();
					String word = stemmer.getCurrent();
					if (word.length() > maxTermLength) {
						System.err.println("word: " + word);
					} else {
						Integer count = 0;
						if (words.containsKey(word)) {
							count = words.get(word);
						}
						count++;
						words.put(word, count);
					}
					input.delete(0, input.length());
				}
			} else {
				input.append(Character.toLowerCase(ch));
			}
		}
		reader.close();

		try {
			result = statement
					.executeQuery("select id from document where file_path='"
							+ fileName + "'");
			if (result.next()) {
				int docId = result.getInt("id");
				saveMapToDB(docId, words);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void saveMapToDB(int documentId, HashMap<String, Integer> map) {
		for (String word : map.keySet()) {
			int termId = getTermId(word);
			try {

				int frequency = map.get(word);
				String query = "insert into ref_doc_term (`document_ref`, `term_ref`, `frequency`) values ("
						+ documentId + ", " + termId + ", " + frequency + ")";

				statement.execute(query);

				statement.execute("UPDATE `term` SET `count`= (count + "
						+ frequency + ") WHERE id=" + termId);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private int getTermId(String term) {
		try {
			String getQuery = "select id from term where name ='" + term + "'";
			result = statement.executeQuery(getQuery);
			if (result.next()) {
				return result.getInt("id");
			}
			String query = "INSERT INTO term (`name`) VALUES ('" + term + "')";
			statement.execute(query);

			result = statement.executeQuery(getQuery);
			result.next();
			int id = (int) result.getInt("id");
			return id;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public String getFilestoragePath() {
		return fileStorage;
	}
}
