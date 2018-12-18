package org.gcube.data_catalogue.grsf_publish_ws.json.input.others;

import javax.validation.constraints.Size;

import org.gcube.datacatalogue.common.Constants;
import org.json.simple.JSONObject;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Stores the annotation message sent by an administrator
 * @author Costantino Perciante at ISTI-CNR 
 * (costantino.perciante@isti.cnr.it)
 */
public class AnnotationBean {
	
	@JsonProperty(Constants.ANNOTATION_ADMIN_JSON_KEY)
	@Size(min=1, message= Constants.ANNOTATION_ADMIN_JSON_KEY + " cannot be empty")
	private String admin;

	@JsonProperty(Constants.ANNOTATION_MESSAGE_JSON_KEY)
	private String annotationMessage;

	@JsonProperty(Constants.ANNOTATION_TIME_JSON_KEY)
	@Size(min=1, message= Constants.ANNOTATION_TIME_JSON_KEY + " cannot be empty")
	private String time;


	/**
	 * 
	 */
	public AnnotationBean() {
		super();
	}

	/**
	 * @param admin
	 * @param annotationMessage
	 * @param time
	 */
	public AnnotationBean(String admin, String annotationMessage, String time) {
		super();
		this.admin = admin;
		this.annotationMessage = annotationMessage;
		this.time = time;
	}

	public String getAdmin() {
		return admin;
	}

	public void setAdmin(String admin) {
		this.admin = admin;
	}

	public String getAnnotationMessage() {
		return annotationMessage;
	}

	public void setAnnotationMessage(String annotationMessage) {
		this.annotationMessage = annotationMessage;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String toString() {

		JSONObject obj = new JSONObject();

		obj.put(Constants.ANNOTATION_ADMIN_JSON_KEY, admin);
		obj.put(Constants.ANNOTATION_MESSAGE_JSON_KEY, annotationMessage);
		obj.put(Constants.ANNOTATION_TIME_JSON_KEY, time);

		return obj.toJSONString();
	}

}
