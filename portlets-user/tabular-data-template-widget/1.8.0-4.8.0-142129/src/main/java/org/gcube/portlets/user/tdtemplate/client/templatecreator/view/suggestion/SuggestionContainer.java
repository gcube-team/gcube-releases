/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.templatecreator.view.suggestion;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Apr 14, 2014
 *
 */
public class SuggestionContainer extends ContentPanel{
	
	private LayoutContainer suggestionDescription = new LayoutContainer();
	/**
	 * 
	 */
	public SuggestionContainer() {
//		setLayout(new FitLayout());
		setScrollMode(Scroll.AUTOY);
		setHeaderVisible(false);
		setBorders(false);
		setBodyBorder(false);
		add(suggestionDescription);
	}
	
	public void setSuggestion(String title, String text){
		SuggestionLabel sugg = new SuggestionLabel(title, text, "");
		setSuggestionLabel(sugg);
	}
	
	public void setSuggestion(String title, String text, String subText, AbstractImagePrototype image){
		SuggestionLabel sugg = new SuggestionLabel(title, text, subText,image);
		setSuggestionLabel(sugg);
	}
	
	public void setSuggestionLabel(SuggestionLabel sugg){
		suggestionDescription.removeAll();
		suggestionDescription.add(sugg.getHtml());
	}

	public LayoutContainer getSuggestionDescription() {
		return suggestionDescription;
	}
}
