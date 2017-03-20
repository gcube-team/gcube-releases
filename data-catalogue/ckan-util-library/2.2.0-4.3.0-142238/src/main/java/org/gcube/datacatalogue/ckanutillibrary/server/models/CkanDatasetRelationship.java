package org.gcube.datacatalogue.ckanutillibrary.server.models;

import static com.google.common.base.Preconditions.checkNotNull;

import org.json.simple.JSONObject;

/**
 * A ckan dataset relationship. It is represented by the following fields
 * <ul>
 * <li> subject dataset id
 * <li> object dataset id
 * <li> type of the relationship
 * <li> comment an optional comment
 * </ul>
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class CkanDatasetRelationship {

	private String subject;
	private String object;
	private String comment;
	private String type;


	public CkanDatasetRelationship(){
		super();
	}

	/**
	 * @param subject
	 * @param object
	 * @param comment
	 * @param type
	 */
	public CkanDatasetRelationship(String subject, String object,
			String comment, String type) {
		super();
		this.subject = subject;
		this.object = object;
		this.comment = comment;
		this.type = type;
	}

	/**
	 * From a json object that must have the properties listed in the class header (comment is optional)
	 * @param object
	 */
	public CkanDatasetRelationship(JSONObject object) {
		this.comment = (String) object.get("comment");
		this.object = (String) object.get("object");
		this.subject = (String) object.get("subject");
		this.type = (String) object.get("type"); // TODO convert to one of enums DatasetRelationships
		checkNotNull(object);
		checkNotNull(subject);
		checkNotNull(type);
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "CkanDatasetRelationship [subject=" + subject + ", object="
				+ object + ", comment=" + comment + ", type=" + type
				+ "]";
	}
}
