package org.gcube.textextractor.entities;

import java.io.Serializable;

public class CE4NameResponse implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public Head head;
	public Results results;
	@Override
	public String toString() {
		return "CE4NameResponse [head=" + head + ", results=" + results
				+ "]";
	}
	
}
