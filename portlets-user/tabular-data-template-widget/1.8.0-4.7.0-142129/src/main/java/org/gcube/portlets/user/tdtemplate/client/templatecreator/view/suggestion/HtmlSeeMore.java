/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.templatecreator.view.suggestion;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * The Class HtmlSeeMore.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Apr 30, 2015
 */
public class HtmlSeeMore extends HTMLPanel{

	public static String SEE_MORE = "See More";
	public static String SEE_LESS = "See Less";
	private String html;
	private boolean toggle = false;
	private String msg;
	private String caption;
	private int maxChars;


	/**
	 * Instantiates a new html see more.
	 *
	 * @param msg the msg
	 * @param maxChars the max chars
	 */
	public HtmlSeeMore(String msg, int maxChars) {
		super("");
		this.msg = msg;
		this.maxChars = maxChars;
		init(maxChars);
	}

	/**
	 * Inits the.
	 *
	 * @param maxChars the max chars
	 */
	private void init(int maxChars) {
		caption = msg;
		if(msg.length()>maxChars){
			toggle = true;
			String text = stripTags(msg);
			caption = stripTags(text.substring(0, maxChars)+"... ");
		}
		
		html = "<span id=\"textAreaCaption\">"+caption+"</span>";		
		this.getElement().setInnerHTML(html);
		
		if(toggle){
			final Anchor anchor = new Anchor(SEE_MORE);
			anchor.getElement().setId("toogleButton");
			anchor.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					
					if(toggle){
						anchor.getElement().setInnerText(SEE_LESS);
						DOM.getElementById("textAreaCaption").setInnerHTML(msg);
						setToggle(false);
					}else{
						anchor.getElement().setInnerText(SEE_MORE);
						DOM.getElementById("textAreaCaption").setInnerHTML(caption);
						setToggle(true);
					}
				}
			});
			add(anchor);
		}
	}
	
	public String stripTags(String str) {
		return str.replaceAll("\\<.*?>"," ");
	}
	
	public void updateMsg(String msg){
		this.msg = msg;
		this.clear();
		init(maxChars);
	}

	/**
	 * Gets the msg.
	 *
	 * @return the msg
	 */
	public String getMsg() {
		return msg;
	}

	/**
	 * Gets the caption.
	 *
	 * @return the caption
	 */
	public String getCaption() {
		return caption;
	}

	/**
	 * Sets the toggle.
	 *
	 * @param toggle the toggle to set
	 */
	private void setToggle(boolean toggle) {
		this.toggle = toggle;
	}




	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("HtmlSeeMore [html=");
		builder.append(html);
		builder.append(", toggle=");
		builder.append(toggle);
		builder.append(", msg=");
		builder.append(msg);
		builder.append(", caption=");
		builder.append(caption);
		builder.append("]");
		return builder.toString();
	}

}
