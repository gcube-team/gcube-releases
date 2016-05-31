package org.gcube.textextractor.entities;

import java.io.Serializable;
import java.util.List;

public class Results implements Serializable{

	private static final long serialVersionUID = 1L;
	public List<Binding> bindings;

	@Override
	public String toString() {
		return "Results [bindings=" + bindings + "]";
	}
	
}