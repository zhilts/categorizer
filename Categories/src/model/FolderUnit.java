package model;

import java.util.HashSet;

public class FolderUnit extends Unit {
	private String title = null;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public FolderUnit(String name) {
		title = name;
		children = new HashSet<Unit>();
	}

	@Override
	public String getName() {
		return title;
	}

	@Override
	public UnitType getType() {
		return UnitType.UnitTypeFolder;
	}

	@Override
	public Unit getSimple() {
		HashSet<Unit> simpleChildren = new HashSet<Unit>();
		for (Unit child : children) {
			Unit simpleChild = child.getSimple();
			simpleChildren.add(simpleChild);
			// Unit simpleChild = child.getSimple();
			// if (simpleChild != child) {
			// children.remove(child);
			// children.add(simpleChild);
			// }
		}
		children = simpleChildren;

		if (children.size() == 1) {
			return children.iterator().next();
		}

		return this;
	}
}
