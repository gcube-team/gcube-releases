package search.library.util.cql.query.tree;

import java.util.ArrayList;

public class ModifierSet {
	
	private ArrayList<Modifier> modifiers = new ArrayList<Modifier>();
	private String base;
	
	
	/**
	 * Creates a new ModifierSet with the specified base.
	 * @param base
	 */
	public ModifierSet(String base) {
		this.base = base;
	}
	
	/**
	 * Adds a modifier of the specified type, but with no comparison and value, to a ModifierSet.
	 * @param type
	 */
	public void addModifier(String type){
		Modifier modif = new Modifier(type);
		modifiers.add(modif);
	}
	
	/**
	 * Adds a modifier of the specified type, comparison and value to a ModifierSet.
	 * @param type
	 * @param comparison
	 * @param value
	 */
	public void addModifier(String type, String comparison, String value) {
		Modifier modif = new Modifier(type, comparison, value);
		modifiers.add(modif);
	}
	
	/**
	 * Returns the base string with which the ModifierSet was created.
	 * @return
	 */
	public String getBase() {
		return base;
	}
	
	/**
	 * Returns an array list of the modifiers in a ModifierSet.
	 * @return
	 */
	public ArrayList<Modifier> getModifiers() {
		return modifiers;
	}
	
	
	/**
	 * Returns the value of the modifier in the specified ModifierSet that corresponds 
	 * to the specified type.
	 * @param type
	 * @return
	 */
	public String modifier (String type) {
		for (int i = 0; i < modifiers.size(); i++) {
			if (modifiers.get(i).getType().equals(type)) {
				return modifiers.get(i).getValue();
			}
		}
		return null;
	}
	
	public void setModifiers(ArrayList<Modifier> modifs) {
		modifiers = modifs;
	}
	
	public String toCQL() {
		String modifSetCQL = base;
		for (int i = 0; i < modifiers.size(); i++) {
			modifSetCQL = modifSetCQL + modifiers.get(i).toString();
		}
		return modifSetCQL;
	}

}
