package org.gcube.data.publishing.gCatFeeder.collectors.dm.model.ckan;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter

public class CkanItem {

	@Getter
	@Setter
	public static class Tag{
		public Tag() {
			// TODO Auto-generated constructor stub
		}
		
		public Tag(String value) {
			name=value;
		}
		
		private String name;
	}
	
	
	public CkanItem() {
		// TODO Auto-generated constructor stub
	}
	
	private String name;
	private String title;	
	private String version;
	@JsonProperty("private")
	private Boolean privateFlag;
	private String license_id;
	private String author;
	private String maintainer;
	private String notes;
	private ArrayList<Tag> tags=new ArrayList<Tag>();

	private ArrayList<CKanExtraField> extras=new ArrayList<>();
}
