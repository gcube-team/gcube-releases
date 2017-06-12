/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.locale;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.gcube.portlets.user.tdtemplate.shared.util.SortedList;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Mar 31, 2014
 *
 */
public abstract class LocalesManager {
	
	protected SortedList<String> listLocales;
	protected List<String> listSelectLocales;
	
	/**
	 * 
	 */
	public LocalesManager(List<String> loadedLocales) {
		listLocales = new SortedList<String>(lexicographicallyComparator);
		listLocales.addAll(loadedLocales);
	}

	protected SortedList<String> getListLocales() {
		return listLocales;
	}
	
	
	protected void selectLocale(String value){
		if(value!=null && !value.isEmpty()){
			
			if(listSelectLocales==null)
				listSelectLocales = new ArrayList<String>();
			
			listSelectLocales.add(value);
		}
	}
	
	/**
	 * 
	 * @param value
	 * @return true if the first occurrence of the specified element from this list is removed
	 */
	protected boolean deselectLocale(String value){
		if(value!=null && !value.isEmpty()){
			return listSelectLocales.remove(value);
		}
		
		return false;
	}

	protected List<String> getListSelectLocales() {
		return listSelectLocales;
	}
	
	/**
	 * 
	 * @return List Exclusives Locales (remove list selected locales from list locales)
	 */
	protected List<String> getExclusivesLocales(){
		
		SortedList<String> exclusivesLocales = new SortedList<String>(lexicographicallyComparator);
		exclusivesLocales.addAll(listLocales);
		
		if(listSelectLocales==null)
			return exclusivesLocales;
		
		for (String selected : listSelectLocales) {
			int index = exclusivesLocales.getElementIndex(selected);
			if(index>=0)
				exclusivesLocales.remove(index);
		}
		
		return exclusivesLocales;
	}
	
	
	/**
	 * Compares two strings lexicographically
	 */
	protected Comparator<String> lexicographicallyComparator = new Comparator<String>() {

		@Override
		public int compare(String o1, String o2) {
			
			if(o1==null)
				return 1;
			
			if(o2==null)
				return -1;
			return o1.compareToIgnoreCase(o2);
		}
	};
	
	protected void clearSelectedLocales(){
		listSelectLocales.clear();
	}

}
