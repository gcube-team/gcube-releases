package org.gcube.data.spd.model.service.types;

import java.util.Arrays;
import java.util.List;


public class MetadataDetails{
	
	private String abstractField;
	private String purpose;
	private String title;
	private String author;
	private String credits;
	private List<String> keywords;
	
	public MetadataDetails(String abstractField, String purpose,
			String title, String author, String credits, String ... keywords) {
		super();
		this.abstractField = abstractField;
		this.purpose = purpose;
		this.title = title;
		this.author = author;
		this.credits = credits;
		this.keywords = Arrays.asList(keywords);
	}

	public String getAbstractField() {
		return abstractField;
	}

	public String getPurpose() {
		return purpose;
	}

	public String getTitle() {
		return title;
	}

	public String getAuthor() {
		return author;
	}

	public String getCredits() {
		return credits;
	}

	public List<String> getKeywords() {
		return keywords;
	}
	

}
