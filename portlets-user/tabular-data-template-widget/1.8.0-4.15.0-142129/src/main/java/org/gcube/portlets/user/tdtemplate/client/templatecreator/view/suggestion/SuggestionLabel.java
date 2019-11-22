/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.templatecreator.view.suggestion;

import org.gcube.portlets.user.tdtemplate.client.resources.TdTemplateAbstractResources;

import com.extjs.gxt.ui.client.widget.Html;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Feb 5, 2014
 * 
 */
public class SuggestionLabel {

	private String html;
	private AbstractImagePrototype image = TdTemplateAbstractResources.tip();

	/**
	 * 
	 * @param caption
	 * @param text
	 * @param subText
	 * @param img
	 */
	public SuggestionLabel(String caption, String text, String subText, AbstractImagePrototype img) {
		createHtml(caption, text, subText, img);

	}
	
	/**
	 * Add image "Tip" to default!
	 * @param caption
	 * @param text
	 * @param subText
	 */
	public SuggestionLabel(String caption, String text, String subText) {
		createHtml(caption, text, subText, image);
	}
	
	private void createHtml(String label, String text, String subText, AbstractImagePrototype img){
		
		html = "<div class=\"suggestionLabel\">";
				if(img!=null){
					html += "<p style=\"display: inline-block; vertical-align: middle; \" >"
					+ img.getHTML()
					+ "</p>";
				}
				html += "<p style=\"padding-left: 5px; display: inline-block;\" >"
				+ label + "</p>"
				+ "<span style=\"padding-left: 5px;\">"
				+ text + "</span>";
		
		if(subText!=null && !subText.isEmpty()){
			
			html+="<br><span style=\"padding-left: 5px; vertical-align: middle; font-size: 9px;\">"
					+ subText + "</span>";
		}
		
		html+="</div>";
	}

	public Html getHtml() {
		return new Html(html);

	}

	public String getHtmlToString() {
		return html;

	}
}
