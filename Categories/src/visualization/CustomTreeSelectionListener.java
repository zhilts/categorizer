package visualization;

import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import model.Document;
import model.DocumentUnit;
import model.Unit;

public class CustomTreeSelectionListener implements TreeSelectionListener {

	private JTree tree;
	private DefaultTreeModel treeModel;
	private CategoriesWindow window;

	public CustomTreeSelectionListener(CategoriesWindow window, JTree tree,
			DefaultTreeModel treeModel) {
		this.window = window;
		this.tree = tree;
		this.treeModel = treeModel;
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
				.getLastSelectedPathComponent();

		if (node == null) {
			return;
		}

		Object nodeInfo = node.getUserObject();
		if (nodeInfo.getClass().equals(Document.class)) {
			Document document = (Document) nodeInfo;
			window.displayDocument(document.getFilePath());
		}
	}

	public void setTree(Unit unit) {
		if (unit == null) {
			DefaultMutableTreeNode root = new DefaultMutableTreeNode(
					"Search result is empty");
			treeModel.setRoot(root);
		} else {
			DefaultMutableTreeNode root = new DefaultMutableTreeNode();
			addChildren(root, unit);
			fillEmpty(root);
			treeModel.setRoot(root);
		}
	}

	private void addChildren(DefaultMutableTreeNode node, Unit unit) {
		if (unit.getClass().equals(DocumentUnit.class)) {
			DocumentUnit docUnit = (DocumentUnit) unit;
			Document document = docUnit.getDocument();
			node.setUserObject(document);
		} else {
			ArrayList<DefaultMutableTreeNode> children = new ArrayList<DefaultMutableTreeNode>();
			for (Unit subunit : unit.getSubunits()) {
				DefaultMutableTreeNode child = new DefaultMutableTreeNode();
				children.add(child);
				addChildren(child, subunit);
			}

			String name = "{";
			boolean first = true;
			Collections.sort(children, new TreeNodeComparator());
			for (int i = 0; i < children.size(); i++) {
				DefaultMutableTreeNode child = children.get(i);
				node.add(children.get(i));
				if (first) {
					first = false;
				} else {
					name += ", ";
				}
				name += child.getUserObject().toString();
			}
			name += "}";

			if (unit.getName() != null) {
				name = unit.getName();
			}
			node.setUserObject(name);
		}
	}

	private void fillEmpty(DefaultMutableTreeNode node) {
		if (node.isLeaf()) {
			return;
		}
		String name = "{";
		boolean first = true;
		for (int i = 0; i < node.getChildCount(); i++) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) node
					.getChildAt(i);
			fillEmpty(child);
			if (first) {
				first = false;
			} else {
				name += ", ";
			}
			name += child.getUserObject().toString();
		}
		if (node.getUserObject() == null) {
			node.setUserObject(name);
		}
	}
}
