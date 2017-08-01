/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.templatecreator.view.suggestion;

import java.util.List;

import org.gcube.portlets.user.tdtemplate.client.resources.TdTemplateAbstractResources;
import org.gcube.portlets.user.tdtemplate.shared.validator.ViolationDescription;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Feb 5, 2014
 * 
 */
public class ConstraintSuggestionLabel extends LayoutContainer{

	private String txtHtml;
	
	protected Image image = TdTemplateAbstractResources.lock().createImage();
	
	private LayoutContainer lc = new LayoutContainer();

	private boolean setConstraintVisible;
	
	private Html html;
	
	public ConstraintSuggestionLabel(String caption, List<ViolationDescription> violations, boolean setConstraintDefaulVisible) {

		this.setConstraintVisible = setConstraintDefaulVisible;
		this.addStyleName("suggestionLabel2");
		
		String alert ="";
		for (ViolationDescription violationDescription : violations) {
				alert+="* "+violationDescription.getDescription()+"<br/>";
		}
		
		HorizontalPanel hp = new HorizontalPanel();
		hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hp.add(image);
		
		Html htmlCaption = new Html(caption);
		htmlCaption.setStyleAttribute("margin-left", "5px");
		
		htmlCaption.addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				changeVisibility();
			}
		});
		
		hp.add(htmlCaption);
		
		image.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				changeVisibility();
				
			}
		});
		
		
		createHtml(caption, alert, TdTemplateAbstractResources.info());
		
		lc.setVisible(setConstraintVisible);
		
		html = new Html(txtHtml);
		
		html.addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				changeVisibility();
			}
		});
		
		lc.add(html);
		
		add(hp);
		add(lc);
		
	}
	
	private void changeVisibility(){
		setConstraintVisible = !setConstraintVisible;
		lc.setVisible(setConstraintVisible);	
	}
	
	private void createHtml(String label, String text, AbstractImagePrototype img){
		
		txtHtml = "<div>"+
				"<span style=\"padding-left: 19px; display: block; vertical-align: middle; font-size: 10px;\">"
				+ text + "</span>";
		
		txtHtml+="</div>";
	}

	public Html getHtml() {
		return html;

	}
}
