package org.gcube.application.framework.search.library.util;

import java.util.Comparator;

import org.gcube.application.framework.search.library.model.CollectionInfo;

/**
 * 
 * @author Nikolas - NKUA
 * 
 */

public class ArraysSorter implements Comparator<CollectionInfo> {
	public enum Order {Name, ID};
	
	private Order sortingBy = Order.Name;
	

	public void setSortingBy(Order sortBy) {
		this.sortingBy = sortBy;
	}


	@Override
	public int compare(CollectionInfo colInfo1, CollectionInfo colInfo2) {
		switch(sortingBy) {
		case Name: return colInfo1.getName().compareTo(colInfo2.getName());
		case ID: return colInfo1.getId().compareTo(colInfo2.getId());
		}
		return 0;
	}

}
