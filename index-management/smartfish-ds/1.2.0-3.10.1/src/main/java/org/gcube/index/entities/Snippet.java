package org.gcube.index.entities;

import java.io.Serializable;

public class Snippet implements Serializable {
	private static final long serialVersionUID = 1L;
	public String text;

	public Snippet(String text) {
		super();
		this.text = text;
	}

}