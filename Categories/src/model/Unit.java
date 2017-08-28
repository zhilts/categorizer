package model;

import java.util.HashSet;

public abstract class Unit implements UnitInterface {
	protected HashSet<Unit> children = null;
	
	public HashSet<Unit> getSubunits() {
		return children;
	}
}
