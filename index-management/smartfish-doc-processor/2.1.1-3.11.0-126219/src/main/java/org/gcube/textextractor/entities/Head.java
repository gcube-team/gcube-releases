package org.gcube.textextractor.entities;

import java.io.Serializable;
import java.util.List;

public class Head implements Serializable{
	private static final long serialVersionUID = 1L;
	
	public List<String> vars;

	@Override
	public String toString() {
		return "Head [vars=" + vars + "]";
	}
	
	
}