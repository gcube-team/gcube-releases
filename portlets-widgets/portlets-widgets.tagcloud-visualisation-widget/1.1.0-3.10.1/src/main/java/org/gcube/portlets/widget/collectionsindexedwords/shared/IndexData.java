package org.gcube.portlets.widget.collectionsindexedwords.shared;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

public class IndexData implements IsSerializable{
	
	ArrayList <String> words;
	ArrayList <Integer> values;

	public IndexData(){
		words = new ArrayList<String>();
		values = new ArrayList<Integer>();
	}

	public ArrayList<String> getWords() {
		return words;
	}

	public void setWords(ArrayList<String> words) {
		this.words = words;
	}

	public ArrayList<Integer> getValues() {
		return values;
	}

	public void setValues(ArrayList<Integer> values) {
		this.values = values;
	}
	
}
