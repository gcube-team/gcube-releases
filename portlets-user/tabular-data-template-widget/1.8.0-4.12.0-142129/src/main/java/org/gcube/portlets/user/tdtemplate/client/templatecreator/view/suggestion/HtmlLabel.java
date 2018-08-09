/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.templatecreator.view.suggestion;

import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * The Class HtmlLabel.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Feb 5, 2014
 */
public class HtmlLabel extends LayoutContainer{

	private String html;
	private Html theHTML = new Html();

	private AbstractImagePrototype image;

	/**
	 * Instantiates a new html label.
	 * the margins in px
	 * @param top the top
	 * @param left the left
	 * @param bottom the bottom
	 * @param right the right
	 */
	public HtmlLabel(double top, double left, double bottom, double right){
		this.getElement().getStyle().setMarginLeft(left, Unit.PX);
		this.getElement().getStyle().setMarginTop(top, Unit.PX);
		this.getElement().getStyle().setMarginRight(right, Unit.PX);
		this.getElement().getStyle().setMarginBottom(bottom, Unit.PX);
	}

	/**
	 * Instantiates a new html label.
	 *
	 * @param caption the caption
	 * @param text the text
	 * @param subText the sub text
	 * @param img the img
	 */
	public HtmlLabel(String caption, String text, String subText, AbstractImagePrototype img) {
		setDefaultImage(img);
		createHtml(caption, text, subText);
	}
	

	/**
	 * Instantiates a new html label.
	 *
	 * @param caption the caption
	 * @param text the text
	 * @param subText the sub text
	 */
	public HtmlLabel(String caption, String text, String subText) {
		createHtml(caption, text, subText);
	}
	
	/**
	 * Creates the html.
	 *
	 * @param caption the label
	 * @param text the text
	 * @param subText the sub text
	 */
	public void createHtml(String caption, String text, String subText){
		
		html = "<div class=\"htmlLabel\">";
				if(image!=null){
					html += "<p style=\"display: inline-block; vertical-align: middle; \" >"
					+ image.getHTML()
					+ "</p>";
				}
				html += "<p style=\"padding-left: 5px; display: inline-block;\" >"
				+ caption + "</p>"
				+ "<span style=\"padding-left: 5px;\">"
				+ text + "</span>";
		
		if(subText!=null && !subText.isEmpty()){
			
			html+="<br><span style=\"padding-left: 5px; vertical-align: middle; font-size: 9px;\">"
					+ subText + "</span>";
		}
		
		html+="</div>";
		
		theHTML.setHtml(html);
		add(theHTML);
	}

	/**
	 * Sets the default image.
	 *
	 * @param img the new default image
	 */
	public void setDefaultImage(AbstractImagePrototype img){
		image = img;
	}
	
}
