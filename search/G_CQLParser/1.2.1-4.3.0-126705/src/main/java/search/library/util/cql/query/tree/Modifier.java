package search.library.util.cql.query.tree;

public class Modifier {
	
	String type;
	String comparison;
	String value;
	
	public Modifier(String type) {
		this.type = type;
	}
	
	public Modifier(String type, String comparison, String value) {
		this.type = type;
		this.comparison = comparison;
		this.value = value;
	}
	
	public String getComparison() {
		return comparison;
	}
	
	public String getType() {
		return type;
	}
	
	public String getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		String modifStr = "/";
		if (comparison != null && !comparison.equals("")) {
			// we have a comparison and a value
			modifStr = modifStr + type + comparison + value;
		} else {
			// we just have a type
			modifStr = modifStr + type;
		}
		return modifStr;
	}

}
