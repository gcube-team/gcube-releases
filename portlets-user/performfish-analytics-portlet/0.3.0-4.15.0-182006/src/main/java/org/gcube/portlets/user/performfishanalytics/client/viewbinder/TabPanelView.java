/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.client.viewbinder;

import java.util.ArrayList;
import java.util.List;

import com.github.gwtbootstrap.client.ui.Tab;
import com.github.gwtbootstrap.client.ui.TabPanel;
import com.github.gwtbootstrap.client.ui.TabPanel.ShowEvent;
import com.github.gwtbootstrap.client.ui.TabPanel.ShowEvent.Handler;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;


/**
 * The Class TabPanelView.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 29, 2019
 */
public class TabPanelView extends Composite {

	private static TabPanelViewUiBinder uiBinder =
		GWT.create(TabPanelViewUiBinder.class);

	/**
	 * The Interface TabPanelViewUiBinder.
	 *
	 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
	 * Jan 29, 2019
	 */
	interface TabPanelViewUiBinder extends UiBinder<Widget, TabPanelView> {
	}

	@UiField
	Tab field_create_analytics_request;

	@UiField
	TabPanel field_base_tabpanel;


	private List<Tab> results = new ArrayList<Tab>();


	/**
	 * Because this class has a default constructor, it can
	 * be used as a binder template. In other words, it can be used in other
	 * *.ui.xml files as follows:
	 * <ui:UiBinder xmlns:ui="urn:ui:com.google.gwt.uibinder"
	 *   xmlns:g="urn:import:**user's package**">
	 *  <g:**UserClassName**>Hello!</g:**UserClassName>
	 * </ui:UiBinder>
	 * Note that depending on the widget that is used, it may be necessary to
	 * implement HasHTML instead of HasText.
	 */
	public TabPanelView() {

		initWidget(uiBinder.createAndBindUi(this));

		field_base_tabpanel.addShowHandler(new Handler() {

			@Override
			public void onShow(ShowEvent showEvent) {

				GWT.log("Showing: "+showEvent.toString());
			}
		});


		field_create_analytics_request.asWidget().addAttachHandler(new AttachEvent.Handler() {

			  @Override
			  public void onAttachOrDetach(AttachEvent event) {
				  //field_create_analytics_request.asWidget().getElement().getFirstChildElement().getStyle().setBackgroundColor("#F0F8FF");
			  }
		});
	}

	/**
	 * Gets the tab create request panel.
	 *
	 * @return the tab create request panel
	 */
	public Tab getTabCreateRequestPanel(){
		return field_create_analytics_request;
	}

	/**
	 * Adds the create request panel.
	 *
	 * @param w the w
	 */
	public void addCreateRequestPanel(Widget w){
		field_create_analytics_request.add(w);
	}


	/**
	 * Adds the as tab.
	 *
	 * @param tabTitle the tab title
	 * @param tabDescr the tab descr
	 * @param spinner the spinner
	 * @param w the w
	 * @return the tab
	 */
	public Tab addAsTab(String tabTitle, String tabDescr, boolean spinner, Widget w){

//		field_create_analytics_request.setActive(false);

		Tab tab = new Tab();
		if(!spinner)
			tab.setIcon(IconType.BAR_CHART);
		else{
			tab.setCustomIconStyle("icon-rotate-right icon-spin");
		}
		field_base_tabpanel.add(tab);

		tab.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

			}
		});
		//tab.setActive(true);
		tab.setHeading(tabTitle);
		results.add(tab);
		if(tabDescr!=null) {
			//tabDescr = "<h5>"+tabDescr+"</h5>";
			HTML html = new HTML(tabDescr);
			html.addStyleName("to-algorithms-descr");
			tab.add(html);
		}
		tab.add(w);
		activeTabPanels(false);
		field_base_tabpanel.selectTab(results.size()+1); //+1 because the first tab is "Manual"
		return tab;

//		tab.setActive(true);
	}

	/**
	 * Active tab panels.
	 *
	 * @param bool the bool
	 */
	private void activeTabPanels(boolean bool){
		for (Tab tabLink : results) {
			tabLink.setActive(false);
		}
	}

	/**
	 * Count tab.
	 *
	 * @return the int
	 */
	public int countTab(){

		return results.size();
	}


	/**
	 * Sets the no spinner.
	 *
	 * @param tab the new no spinner
	 */
	public void setNoSpinner(Tab tab) {
		try{
		tab.asTabLink().getAnchor().removeStyleName("icon-spin");
		tab.asTabLink().getAnchor().removeStyleName("icon-rotate-right");
		Element anchorElem = tab.asTabLink().getAnchor().asWidget().getElement();
		anchorElem.getFirstChildElement().removeClassName("icon-spin");
		anchorElem.getFirstChildElement().removeClassName("icon-rotate-right");
		}catch(Exception e){
			//silent
		}
		tab.asTabLink().getAnchor().setIcon(IconType.BAR_CHART);
		//tab.asTabLink().getAnchor().setVisible(false);
		//if(tab.asTabLink().getAnchor().setVisible(false);)

	}

}
