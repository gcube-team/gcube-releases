/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.templatecreator.view.suggestion;

import java.util.Map;

import org.gcube.portlets.user.tdtemplate.client.resources.TdTemplateAbstractResources;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;


/**
 * The Class HtmlLegend.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Apr 9, 2015
 */
public class HtmlLegend extends LayoutContainer{

	private String rgbBrackgroundColor;
	private String txtHtml;
	protected Image image = TdTemplateAbstractResources.legend().createImage();
	private LayoutContainer lc = new LayoutContainer();
	private boolean setConstraintVisible;
	private Html html;

	/**
	 * @return the rgbBrackgroundColor
	 */
	public String getRgbBrackgroundColor() {
		return rgbBrackgroundColor;
	}

	/**
	 * Instantiates a new html legend.
	 *
	 * @param top the top
	 * @param left the left
	 * @param bottom the bottom
	 * @param right the right
	 */
	public HtmlLegend(double top, double left, double bottom, double right){
		this.getElement().getStyle().setMarginLeft(left, Unit.PX);
		this.getElement().getStyle().setMarginTop(top, Unit.PX);
		this.getElement().getStyle().setMarginRight(right, Unit.PX);
		this.getElement().getStyle().setMarginBottom(bottom, Unit.PX);
	}
	
	/**
	 * Instantiates a new html label.
	 *
	 * @param rgb the rgb
	 * @param text the text
	 */
	public HtmlLegend(Map<String, String> mapTxtRGB) {
		init("Types of Columns", mapTxtRGB);
	}
	

	
	public void init(String caption, Map<String, String> mapTxtRGB) {
		
		this.addStyleName("legend-columns");
		
		String alert ="";
		for (String txt : mapTxtRGB.keySet()) {
			String rgb = mapTxtRGB.get(txt);
//			<div style="width:100px; heigth:10px; background-color: #D1E6E7; text-align:center;">Base Columns</div>
			alert += "<div style=\"width:100px; margin-left:2px; padding:2px; background-color:"+rgb+"; text-align:center; border: 1px solid #7A89B0;\">"+txt+"</div>";
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
