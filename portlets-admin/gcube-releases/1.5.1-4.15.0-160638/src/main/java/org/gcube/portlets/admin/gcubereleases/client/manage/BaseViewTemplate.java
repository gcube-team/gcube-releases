/**
 * 
 */
package org.gcube.portlets.admin.gcubereleases.client.manage;

import com.github.gwtbootstrap.client.ui.FluidRow;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * The Class BaseViewTemplate.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public class BaseViewTemplate extends Composite{

	@UiField
	FluidRow top_container;
	@UiField
	FluidRow base_container;
	@UiField
	FluidRow bottom_container;

	
	private static BaseViewTemplateUiBinder uiBinder = GWT.create(BaseViewTemplateUiBinder.class);

	/**
	 * The Interface BaseViewTemplateUiBinder.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
	 * Feb 19, 2015
	 */
	interface BaseViewTemplateUiBinder extends UiBinder<Widget, BaseViewTemplate> {
	}
	
	/**
	 * Instantiates a new base view template.
	 */
	public BaseViewTemplate() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	/**
	 * Adds the to top.
	 *
	 * @param widget the widget
	 */
	public void addToTop(Widget widget){
		top_container.add(widget);
	}
	
	/**
	 * Adds the to middle.
	 *
	 * @param widget the widget
	 */
	public void addToMiddle(Widget widget){
		base_container.add(widget);
	}
	
	/**
	 * Adds the to bottom.
	 *
	 * @param widget the widget
	 */
	public void addToBottom(Widget widget){
		bottom_container.add(widget);
	}
}
