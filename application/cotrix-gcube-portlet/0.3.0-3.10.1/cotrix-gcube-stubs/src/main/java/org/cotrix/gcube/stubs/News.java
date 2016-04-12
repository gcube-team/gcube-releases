/**
 * 
 */
package org.cotrix.gcube.stubs;

import com.google.gson.Gson;

/**
 * @author "Federico De Faveri federico.defaveri@fao.org"
 *
 */
public class News {
	
	private static final Gson converter = new Gson();
	
	private String text;

	/**
	 * @param text
	 */
	public News(String text) {
		this.text = text;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}
	
	public static News valueOf(String json) {
		return converter.fromJson(json, News.class);
	}
	
	public String encoded() {
		return converter.toJson(this);
	}

	/** 
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("News [text=");
		builder.append(text);
		builder.append("]");
		return builder.toString();
	}
}
