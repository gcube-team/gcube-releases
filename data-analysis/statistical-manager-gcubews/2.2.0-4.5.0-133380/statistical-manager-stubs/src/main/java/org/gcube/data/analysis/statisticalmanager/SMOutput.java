package org.gcube.data.analysis.statisticalmanager;

import org.gcube.data.analysis.statisticalmanager.stubs.SMTypeParameter;

public class SMOutput {

	private SMTypeParameter type;
	private String content;
	
	public SMOutput(SMTypeParameter type, String content) {
		 this.setType(type);
		 this.setContent(content);
	}

	/**
	 * @return the type
	 */
	public SMTypeParameter getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(SMTypeParameter type) {
		this.type = type;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}
	
}
