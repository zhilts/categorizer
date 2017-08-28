import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;
import javax.swing.UIManager;

public class DBFillerWindow {

	private JFrame frame;
	private JTabbedPane tabbedPane;
	private SpringLayout sl_panel;
	private JPanel panel;
	private JTextArea textArea;
	private JScrollPane scrollPane;

	private JButton chooseButton;
	private JButton loadButton;
	private JButton cleanDBButton;
	private JButton cleanFileStorageButton;

	private static DataBaseConnector dbc;
	static JFileChooser fileChooser;
	static int numberOfRun = 0;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DBFillerWindow window = new DBFillerWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public DBFillerWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame("DB Filler");
		frame.setBounds(100, 100, 700, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		dbc = new DataBaseConnector();
		createFileChooser();

		addTabbedPane();
		sl_panel = new SpringLayout();
		addAddFilePanel();
	}

	private void addTabbedPane() {
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
	}

	private void addAddFilePanel() {
		panel = new JPanel();
		tabbedPane.addTab("Add files", null, panel, null);
		panel.setLayout(sl_panel);

		addTextArea();
		addChooseButton();
		addLoadButton();
		addCleaneDBButton();
		addCleanFileStorageButton();
	}

	private void addTextArea() {
		textArea = new JTextArea();
		textArea.setDragEnabled(true);
		textArea.setBorder(UIManager.getBorder("DesktopIcon.border"));

		scrollPane = new JScrollPane();
		scrollPane.setViewportView(textArea);

		sl_panel.putConstraint(SpringLayout.NORTH, scrollPane, 10,
				SpringLayout.NORTH, panel);
		sl_panel.putConstraint(SpringLayout.WEST, scrollPane, 10,
				SpringLayout.WEST, panel);
		sl_panel.putConstraint(SpringLayout.SOUTH, scrollPane, -10,
				SpringLayout.SOUTH, panel);
		sl_panel.putConstraint(SpringLayout.EAST, scrollPane, -160,
				SpringLayout.EAST, panel);
		panel.add(scrollPane);
	}

	private void createFileChooser() {
		fileChooser = new JFileChooser("/mnt/data/documents/studies/Диплом");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fileChooser.setMultiSelectionEnabled(true);
	}

	private void addChooseButton() {
		chooseButton = new JButton("Choose");
		chooseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int chooseResult = fileChooser.showOpenDialog(panel);
				if (chooseResult == JFileChooser.APPROVE_OPTION) {
					File[] files = fileChooser.getSelectedFiles();

					if (files.length == 0) {
						files[0] = fileChooser.getSelectedFile();
					}
					fillFiles(files, textArea);
				}
			}
		});
		sl_panel.putConstraint(SpringLayout.NORTH, chooseButton, 10,
				SpringLayout.NORTH, panel);
		sl_panel.putConstraint(SpringLayout.EAST, chooseButton, -10,
				SpringLayout.EAST, panel);
		sl_panel.putConstraint(SpringLayout.WEST, chooseButton, 10,
				SpringLayout.EAST, scrollPane);
		panel.add(chooseButton);
	}

	private void addLoadButton() {
		loadButton = new JButton("Load");
		loadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				saveFiles(textArea);
			}
		});
		sl_panel.putConstraint(SpringLayout.NORTH, loadButton, 10,
				SpringLayout.SOUTH, chooseButton);
		sl_panel.putConstraint(SpringLayout.EAST, loadButton, -10,
				SpringLayout.EAST, panel);
		sl_panel.putConstraint(SpringLayout.WEST, loadButton, 10,
				SpringLayout.EAST, scrollPane);
		panel.add(loadButton);
	}

	private void addCleaneDBButton() {
		cleanDBButton = new JButton("Clean DB");
		cleanDBButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dbc.clerDB();
			}
		});
		sl_panel.putConstraint(SpringLayout.NORTH, cleanDBButton, 40,
				SpringLayout.SOUTH, loadButton);
		sl_panel.putConstraint(SpringLayout.EAST, cleanDBButton, -10,
				SpringLayout.EAST, panel);
		sl_panel.putConstraint(SpringLayout.WEST, cleanDBButton, 10,
				SpringLayout.EAST, scrollPane);
		panel.add(cleanDBButton);
	}

	private void addCleanFileStorageButton() {
		cleanFileStorageButton = new JButton("Clean Files");
		cleanFileStorageButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				File file = new File(dbc.getFilestoragePath());
				for (File subfile : file.listFiles()) {
					removeFile(subfile);
				}
			}
		});
		sl_panel.putConstraint(SpringLayout.NORTH, cleanFileStorageButton, 10,
				SpringLayout.SOUTH, cleanDBButton);
		sl_panel.putConstraint(SpringLayout.EAST, cleanFileStorageButton, -10,
				SpringLayout.EAST, panel);
		sl_panel.putConstraint(SpringLayout.WEST, cleanFileStorageButton, 10,
				SpringLayout.EAST, scrollPane);
		panel.add(cleanFileStorageButton);
	}

	private static void fillFiles(File[] files, JTextArea textArea) {
		for (File file : files) {
			if (file.isFile()) {
				String text = textArea.getText();
				if (text.length() > 0 && !text.endsWith("\n")) {
					text += "\n";
				}
				text += file.getPath();
				textArea.setText(text);
			} else {
				fillFiles(file.listFiles(), textArea);
			}
		}
	}

	private void saveFiles(JTextArea textArea) {
		numberOfRun++;

		String[] pathes = textArea.getText().split("\n");
		long time = System.currentTimeMillis();
		for (int i = 0; i < pathes.length; i++) {
			File file = new File(pathes[i]);
			if (file.exists()) {
				System.out.println("processing: " + file.getName());
				dbc.addDocument(file);
			}
		}
		time = System.currentTimeMillis() - time;
		System.out.println("total time = " + time / 60000 + ":" + (time / 1000)
				% 60);
		numberOfRun--;
	}

	private void removeFile(File file) {
		if (file.isDirectory()) {
			File[] subfiles = file.listFiles();
			for (File subfile : subfiles) {
				removeFile(subfile);
			}
		}
		file.delete();
	}
}
