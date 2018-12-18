package org.gcube.contentmanagement.timeseries.geotools.utils;

import java.io.Serializable;
import java.util.ArrayList;

public class Tuple <A> implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
		ArrayList<A> elements;
		
		public Tuple(A... elements){
			this.elements = new ArrayList<A>();
				for (A element:elements){
					this.elements.add(element);
				}
		}
		
		public ArrayList<A> getElements(){
				return elements;
		}
		
		public String toString(){
			String s  = "";
			for (A element:elements){
					s+=element+" ";
			}
			return s;
		}
}
