package visualization;

import java.util.Comparator;

import javax.swing.tree.DefaultMutableTreeNode;

import model.Document;

public class TreeNodeComparator implements Comparator<DefaultMutableTreeNode> {
	private static String otherFolderName = "Other";

	@Override
	public int compare(DefaultMutableTreeNode arg0, DefaultMutableTreeNode arg1) {
		if (arg0.isLeaf() ^ arg1.isLeaf()) {
			if (arg0.isLeaf()) {
				return 1;
			}
			return -1;
		}
		String name1 = getName(arg0);
		String name2 = getName(arg1);
		if (name1.equals(otherFolderName)) {
			return 1;
		}
		if (name2.equals(otherFolderName)) {
			return -1;
		}
		return name1.compareToIgnoreCase(name2);
	}

	private String getName(DefaultMutableTreeNode node) {
		Object object = node.getUserObject();
		String name = null;
		if (object.getClass().equals(Document.class)) {
			Document document = (Document) object;
			name = document.getDescription();
		} else {
			String string = (String) object;
			name = string;
		}
		return name;
	}

}
