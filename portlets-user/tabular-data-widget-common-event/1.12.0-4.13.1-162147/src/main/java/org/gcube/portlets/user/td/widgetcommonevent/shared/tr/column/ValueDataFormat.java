package org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column;

import java.io.Serializable;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ValueDataFormat implements Serializable {

	private static final long serialVersionUID = 6790236931169590842L;

	private String id;
	private String example;
	private String regexp;

	public ValueDataFormat() {

	}

	public ValueDataFormat(String id, String example, String regexp) {
		super();
		this.id=id;
		this.example = example;
		this.regexp = regexp;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getExample() {
		return example;
	}

	public void setExample(String example) {
		this.example = example;
	}

	public String getRegexp() {
		return regexp;
	}

	public void setRegexp(String regexp) {
		this.regexp = regexp;
	}

	public String getLabel(){
		return id;
	}
	
	@Override
	public String toString() {
		return "TimeDataFormat [id=" + id + ", example=" + example
				+ ", regexp=" + regexp + "]";
	}

}
