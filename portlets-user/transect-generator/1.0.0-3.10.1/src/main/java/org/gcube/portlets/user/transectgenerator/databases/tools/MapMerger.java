package org.gcube.portlets.user.transectgenerator.databases.tools;

import java.util.ArrayList;
import java.util.Map;

//merges two Maps in a single one eliminating common column
public class MapMerger<A,B> {

	ArrayList<Object> am1;
	ArrayList<Object> am2;
	
	public MapMerger(){
		am1 = new ArrayList<Object>(); 
		am2 = new ArrayList<Object>();
	}
	
	//merges two maps by taking common keys
	public void mergeMaps(Map m1,Map m2)
	{
		for (Object key:m1.keySet()){
			Object value2 = m2.get(key);
			if (value2!=null)
			{
				Object value1 = m1.get(key);
				am1.add(value1);
				am2.add(value2);
				
			}
		}
		
	}
	
	
	public ArrayList<A> extractFirstVector()
	{
		ArrayList<A> array = new ArrayList<A>();
		
		for (Object o:am1){
			array.add((A)o);
		}
		return array;
	}
	
	public ArrayList<B> extractSecondVector()
	{
		ArrayList<B> array = new ArrayList<B>();
		
		for (Object o:am2){
			array.add((B)o);
		}
		return array;
	}
}
