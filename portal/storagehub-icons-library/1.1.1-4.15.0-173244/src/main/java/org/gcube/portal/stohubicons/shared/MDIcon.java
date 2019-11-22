package org.gcube.portal.stohubicons.shared;

import java.io.Serializable;

/**
 * 
 * @author M. Assante, CNR-ISTI
 * 
 * easy to incorporate icons into your web page. Here’s a small example: &lt;i class="material-icons"&gt;face&lt;/i&gt;
 * @see <a href="http://google.github.io/material-design-icons/">http://google.github.io/material-design-icons/</a>
 * object representing Material Design Icon &lt;i class="material-icons"&gt; $The Returned Material Design Icon Textual Name &lt;/i&gt;
 */
@SuppressWarnings("serial")
public class MDIcon implements Serializable {

	private StringBuilder html;
	private String textualName;
	private String color;
	
	public MDIcon(String textualName, String color) {
		this.html = new StringBuilder
				("<i style=\"color: ")
				.append(color)
				.append("\" class=\"material-icons\">")
				.append(textualName)
				.append("</i>");
		this.textualName = textualName;
		this.color = color;
	}

	public String getHtml() {
		return html.toString();
	}

	public String getTextualName() {
		return textualName;
	}

	public void setTextualName(String textualName) {
		this.textualName = textualName;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}	
}
