package org.gcube.portlets.user.takecourse.questionnaire;

import java.io.Serializable;

@SuppressWarnings("serial")
public class QuestionnaireDTO implements Serializable{
	private String id;
	private String name;
	private String url;
	private boolean answered;
	
	public QuestionnaireDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	public QuestionnaireDTO(String id, String name, String url, boolean answered) {
		super();
		this.id = id;
		this.name = name;
		this.url = url;
		this.answered = answered;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public boolean isAnswered() {
		return answered;
	}
	public void setAnswered(boolean answered) {
		this.answered = answered;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("QuestionnaireDTO [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", url=");
		builder.append(url);
		builder.append(", answered=");
		builder.append(answered);
		builder.append("]");
		return builder.toString();
	}
	
}
