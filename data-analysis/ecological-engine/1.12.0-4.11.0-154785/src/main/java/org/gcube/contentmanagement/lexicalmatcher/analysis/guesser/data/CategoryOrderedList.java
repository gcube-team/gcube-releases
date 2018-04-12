package org.gcube.contentmanagement.lexicalmatcher.analysis.guesser.data;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

import org.gcube.contentmanagement.lexicalmatcher.analysis.core.LexicalEngineConfiguration;
import org.gcube.contentmanagement.lexicalmatcher.analysis.guesser.interfaces.Reference;

public class CategoryOrderedList {

	// lista ordinata in ordine decrescente
	ArrayList<Reference> orderedList;
	HashMap<String,Reference> orderedListTable;
	private HashMap<String, CategoryScores> scoresTable;


	public void setOrderedList(ArrayList<Reference> OrderedList){
		orderedList = OrderedList;
	}
	public HashMap<String, CategoryScores> getScoresTable() {
		return scoresTable;
	}
	
	public void setCategoryTable( HashMap<String,Reference> OrderedListTable ) {
		orderedListTable = OrderedListTable ;
	}
	
	public  Reference getCategory ( String categoryName ) {
		return orderedListTable.get(categoryName);
	}
	
	public ArrayList<Reference> getOrderedList() {
		return orderedList;
	}

	LexicalEngineConfiguration config;
	
	public CategoryOrderedList(LexicalEngineConfiguration Config) {
		orderedList = new ArrayList<Reference>();
		scoresTable = new HashMap<String, CategoryScores>();
		config = Config;
		orderedListTable = new HashMap<String, Reference>();
	}

	public void addCategory(Category c) {

		BigInteger nElements = c.getNumberOfElements();
		int index = 0;

		for (Reference cc : orderedList) {
			BigInteger localnum = cc.getNumberOfElements();
			if (localnum.compareTo(nElements) < 0) {
				break;
			}
			index++;
		}
		orderedList.add(index, c);
		scoresTable.put(c.getName(), new CategoryScores(c.getNumberOfElements(),config));
		orderedListTable.put(c.getName(), c);
//		scoresTable.put(c.getName(), new CategoryScores());
	}

	public CategoryOrderedList generateNovelList(){
		CategoryOrderedList newCatList = new CategoryOrderedList(config);
		newCatList.setOrderedList(orderedList);
		newCatList.setCategoryTable(orderedListTable);
		
		for (String key:scoresTable.keySet()){
			CategoryScores ct = scoresTable.get(key);
			CategoryScores ctnew = new CategoryScores(ct.getCategoryElements(), config);
			newCatList.getScoresTable().put(key,ctnew);
		}
		
		return newCatList;
	}

}