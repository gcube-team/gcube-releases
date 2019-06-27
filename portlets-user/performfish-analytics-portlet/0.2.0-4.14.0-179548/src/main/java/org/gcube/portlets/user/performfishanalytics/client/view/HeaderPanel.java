/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.client.view;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;


/**
 * The Class HeaderPage.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jan 16, 2019
 */
public class HeaderPanel extends FlowPanel {

	private LoaderIcon loading = new LoaderIcon();

	/**
	 * Instantiates a new header page.
	 */
	public HeaderPanel(){
		init();
	}

	/**
	 * Inits the.
	 */
	public void init(){
		this.clear();
		initIconLoading();
	}

	/**
	 * Inits the icon loading.
	 */
	private void initIconLoading() {
		loading.setTitle("Loading");
		loading.setVisible(false);
		add(loading);
	}

	/**
	 * Show loading.
	 *
	 * @param visible the visible
	 * @param text the text
	 */
	public void showLoading(boolean visible, String text){
		loading.setVisible(visible);
		loading.setText(text);
	}

	/**
	 * Show loading.
	 *
	 * @param visible the visible
	 */
	public void showLoading(boolean visible){
		showLoading(visible, "");
	}

	/**
	 * Adds the title.
	 *
	 * @param header the header
	 * @param size the size
	 */
	public void addTitle(String header, double size){
		getElement().setId("HeaderPage");
		//pageTemplate.addTitle(header, "release", 2);
		getElement().getStyle().setMarginLeft(5, Unit.PX);
		getElement().getStyle().setMarginRight(5, Unit.PX);
//		getElement().setAttribute("margin-right", "5px");
	}


}
