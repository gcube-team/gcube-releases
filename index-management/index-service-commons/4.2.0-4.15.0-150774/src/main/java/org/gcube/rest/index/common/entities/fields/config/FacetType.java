package org.gcube.rest.index.common.entities.fields.config;

public enum FacetType {
	NONE("none"), // no facet for this collection available, tokenize them with the default tokenizer
	NORMAL("raw_normal"), // provide facet for this field, with the default tokenizer
	NON_TOKENIZED("raw_non_tokenized"); // this is if we want to create a duplicate field, one for searching, and one for presenting the facets

	private String text;

	FacetType(String text) {
		this.text = text;
	}

	public String getText() {
		return this.text;
	}

	@Override
	public String toString(){
		return text;
	}
	
	
}
