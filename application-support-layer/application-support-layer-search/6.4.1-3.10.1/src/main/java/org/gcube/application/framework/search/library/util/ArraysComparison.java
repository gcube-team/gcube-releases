package org.gcube.application.framework.search.library.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.gcube.application.framework.search.library.model.CollectionInfo;


/**
 * 
 * @author Nikolas - NKUA
 * 
 */


public class ArraysComparison {

	/**
	 * List of common Strings within list1 and list2
	 * @param list1
	 * @param list2
	 * @return
	 */
	public static ArrayList<String> getCommonFields (ArrayList<String> list1 , ArrayList<String> list2 ){
		ArrayList<String> commonFields = new ArrayList<String>(list1);
		commonFields.retainAll(list2);
		return commonFields;
	}
	
	/**
	 * List of common Strings within the ArrayLists in collectionPresentableFields
	 * @param collectionPresentableFields
	 * @return
	 */
	public static ArrayList<String> getCommonFields (HashMap<String,ArrayList <String>> collectionPresentableFields){
		String [] keySet = (String[]) collectionPresentableFields.keySet().toArray(new String[0]);
		int hmSize = collectionPresentableFields.size();
		if(hmSize == 0) //if so, return an empty list
			return new ArrayList<String>();
		if(hmSize == 1) //return first arraylist as common
			return collectionPresentableFields.get(keySet[0]);
		if(hmSize == 2)  //return the common string of the two arrays
			return getCommonFields(collectionPresentableFields.get(keySet[0]),collectionPresentableFields.get(keySet[1]));
		//for cases with more than 2
		ArrayList<String> common = getCommonFields(collectionPresentableFields.get(keySet[0]),collectionPresentableFields.get(keySet[1]));
		for(int i=2;i<hmSize;i++)
			common = getCommonFields(common, collectionPresentableFields.get(keySet[i]));
		return common;
	}
	
	
	/**
	 * List of common Strings it the subset of the ArrayLists in collectionPresentableFields. Only the HashMap keys contained in the selectedCollections list are compared.  
	 * @param collectionPresentableFields
	 * @param selectedCollections
	 * @return
	 */
	public static ArrayList<String> getCommonFields (HashMap<String,ArrayList <String>> collectionPresentableFields, List<String> selectedCollections){
		String [] keySet = (String[]) collectionPresentableFields.keySet().toArray(new String[0]);
		ArrayList<String> keySetList = new ArrayList<String>();  
		for(int i=0;i<keySet.length;i++)
			keySetList.add(keySet[i]);
		//reloading the keySetList with the common fields 
		keySetList = getCommonFields(keySetList,(ArrayList<String>) selectedCollections);
		HashMap<String,ArrayList <String>> subCollectionPresentableFields = new HashMap<String,ArrayList <String>>();
		for(int i=0;i<keySetList.size();i++)
			subCollectionPresentableFields.put(keySetList.get(i), collectionPresentableFields.get(keySetList.get(i)));
		//now use the other overloaded function getCommonFields(HashMap) to get the common within this Hashmap.
		return getCommonFields(subCollectionPresentableFields);
	}
	
	
	public static HashMap<CollectionInfo, ArrayList<CollectionInfo>> getSortedHashMap (HashMap<CollectionInfo, ArrayList<CollectionInfo>> unsorted){
		HashMap<CollectionInfo, ArrayList<CollectionInfo>> sorted = new HashMap<CollectionInfo, ArrayList<CollectionInfo>>();
		ArraysSorter sorter = new ArraysSorter();
		sorter.setSortingBy(ArraysSorter.Order.Name);
		Set<CollectionInfo> keys = unsorted.keySet();
		for(CollectionInfo key : keys){
			ArrayList<CollectionInfo> tempColInfo = unsorted.get(key);
			Collections.sort(tempColInfo, sorter);
			sorted.put(key, tempColInfo);
		}
		return sorted;
	}
	
}
