package visualization;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultTreeModel;

import model.Unit;
import controller.Controller;

public class CategoriesWindow {

	private JFrame frame;
	private JTextField searchField, limitsField;
	private Controller controller;
	private CustomTreeSelectionListener treeSelectionListener;
	private JTextArea textArea;
	private JScrollPane scrollPaneTree, scrollPaneView;

	private ActivityThread activityThread = null;
	private int threadCount;

	/**
	 * Create the application.
	 */
	public CategoriesWindow(Controller controller) {
		this.controller = controller;
		threadCount = 0;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame("Cathegories");
		frame.setBounds(100, 100, 1200, 700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel topPanel = getTopPanel();
		frame.getContentPane().add(topPanel, BorderLayout.NORTH);

		JSplitPane splitPane = new JSplitPane();
		splitPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		frame.getContentPane().add(splitPane, BorderLayout.CENTER);

		DefaultTreeModel treeModel = new DefaultTreeModel(null);

		scrollPaneTree = new JScrollPane();
		JTree tree = new JTree(treeModel);
		scrollPaneTree.setViewportView(tree);
		splitPane.setLeftComponent(scrollPaneTree);

		treeSelectionListener = new CustomTreeSelectionListener(this, tree,
				treeModel);

		textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setEditable(false);
		textArea.setDoubleBuffered(true);

		scrollPaneView = new JScrollPane(textArea);
		splitPane.setRightComponent(scrollPaneView);

		tree.addTreeSelectionListener(treeSelectionListener);
		splitPane.setDividerLocation(250);
	}

	private JPanel getTopPanel() {
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		topPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new GridLayout(2, 1, 10, 5));

		JLabel searchLabel = new JLabel("Search:");
		searchLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		labelPanel.add(searchLabel);

		JLabel limitsLabel = new JLabel("Limits:");
		limitsLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		labelPanel.add(limitsLabel);

		topPanel.add(labelPanel, BorderLayout.WEST);

		JPanel textFields = new JPanel();
		textFields.setLayout(new GridLayout(2, 1, 10, 5));

		searchField = new JTextField();
		searchLabel.setLabelFor(searchField);
		textFields.add(searchField);

		// String limits = "";
		// for (int i = 99; i > 0; i--)
		// limits += "0." + ((i > 9) ? "" : "0") + i + " ";

		limitsField = new JTextField("0.9 0.8 0.7 0.6 0.5 0.4 0.3 0.2 0.1");
		limitsLabel.setLabelFor(limitsField);
		textFields.add(limitsField);

		topPanel.add(textFields, BorderLayout.CENTER);

		JButton searchButton = new JButton("Search");
		searchButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (activityThread != null) {
					return;
				}
				double[] limits = getLimits(limitsField.getText());
				activityThread = new ActivityThread(textArea, 100);
				activityThread.start();

				threadCount++;
				Thread solveThread = new ClasterizatorThread(controller,
						searchField.getText(), limits);
				solveThread.setPriority(3);
				solveThread.start();
			}
		});
		topPanel.add(searchButton, BorderLayout.EAST);
		return topPanel;
	}

	private double[] getLimits(String str) {
		String[] strLimits = str.split(" ");
		double[] limits = new double[strLimits.length];
		int j = 0;
		for (int i = 0; i < strLimits.length; i++) {
			double newLimit = Double.parseDouble(strLimits[i]);
			if (newLimit > 0.0) {
				limits[j] = newLimit;
				j++;
			}
		}
		return limits;
	}

	public void setVisible(boolean visible) {
		frame.setVisible(visible);
	}

	public void setTree(Unit unit) {
		threadCount--;
		if (threadCount == 0) {
			treeSelectionListener.setTree(unit);
			activityThread.pauseActivity();
			activityThread = null;
			textArea.setText("\n Search finished");
		}
	}

	public void displayDocument(String filePath) {
		try {
			File file = new File(filePath);

			FileInputStream inputStream = new FileInputStream(file);
			Reader reader = new InputStreamReader(inputStream, "UTF8");
			reader = new BufferedReader(reader);
			int len;
			char[] cbuf = new char[1024];
			textArea.setText("");
			long size = 0;
			while ((len = reader.read(cbuf)) > 0) {
				String str = new String(cbuf, 0, len);
				size += len;
				textArea.append(str);
			}
			reader.close();
			System.out.println(size + " == " + textArea.getText().length());
			textArea.setCaretPosition(0);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
