package search.library.util.cql.query.tree;

import java.util.ArrayList;

public class GCQLRelation extends GCQLNode {
	
	String base;
	
	ArrayList<Modifier> modifiers = new ArrayList<Modifier>();

	public String getBase() {
		return base;
	}

	public void setBase(String base) {
		this.base = base;
	}

	public ArrayList<Modifier> getModifiers() {
		return modifiers;
	}

	public void setModifiers(ArrayList<Modifier> modifiers) {
		this.modifiers = modifiers;
	}
	
	
	@Override
	public String toCQL() {
		String relToCQL = base;
		if (!modifiers.isEmpty()) {
			// we have modifiers also
			for (int i = 0; i < modifiers.size(); i++) {
				relToCQL = relToCQL + modifiers.get(i).toString();
			}
		}
		return relToCQL;
	}

}
