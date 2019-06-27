/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.client.viewbinder;

import org.gcube.portlets.user.performfishanalytics.client.view.LoaderIcon;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;


// TODO: Auto-generated Javadoc
/**
 * The Class SubmitRequestPanel.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Mar 4, 2019
 */
public class SubmitRequestPanel extends Composite {

	/** The ui binder. */
	private static SubmitRequestPanelPanelUiBinder uiBinder =
		GWT.create(SubmitRequestPanelPanelUiBinder.class);

	/**
	 * The Interface SubmitPerformFishRequestPanelUiBinder.
	 *
	 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
	 * Jan 22, 2019
	 */
	interface SubmitRequestPanelPanelUiBinder
		extends UiBinder<Widget, SubmitRequestPanel> {
	}

	/** The title. */
	@UiField
	HTML theTitle;

	/** The container panel. */
	@UiField
	HTMLPanel containerPanel;

	/** The loader. */
	LoaderIcon loader;
	
	/** The completed requests. */
	private int completedRequests = 0;
	
	/** The total requests. */
	private int totalRequests = 1;

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
	 *
	 * @param title the title
	 * @param totalRequests the total requests
	 */
	public SubmitRequestPanel(String title, int totalRequests) {

		initWidget(uiBinder.createAndBindUi(this));
		theTitle.getElement().addClassName("to-big-title");
		theTitle.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		setTheTitle(title);
		this.totalRequests = totalRequests;
	}


	/**
	 * Sets the the title.
	 *
	 * @param title the new the title
	 */
	public void setTheTitle(String title) {

		if(title!=null)
			theTitle.setHTML(title);
		else
			theTitle.setHTML("");
	}

	/**
	 * Gets the panel.
	 *
	 * @return the panel
	 */
	public HTMLPanel getContainerPanel(){
		return containerPanel;
	}

	/**
	 * Adds the.
	 *
	 * @param bool the bool
	 * @param txtHTML the txt html
	 */
	public void showLoader(boolean bool, String txtHTML) {

		if(bool){
			loader = new LoaderIcon(txtHTML);
			containerPanel.add(loader);
		}else{
			try{
				containerPanel.remove(loader);
			}catch(Exception e){

			}
		}
	}

	
	/**
	 * Increment completed requests.
	 */
	public synchronized void incrementCompletedRequests() {
		completedRequests++;
	}
	
	/**
	 * Gets the completed requests.
	 *
	 * @return the completed requests
	 */
	public synchronized int getCompletedRequests() {
		return completedRequests;
	}
	
	/**
	 * Gets the total requests.
	 *
	 * @return the total requests
	 */
	public int getTotalRequests() {
		return totalRequests;
	}
	
	/**
	 * Sets the total requests.
	 *
	 * @param totalRequests the new total requests
	 */
	public void setTotalRequests(int totalRequests) {
		this.totalRequests = totalRequests;
	}

	/**
	 * Adds the widget.
	 *
	 * @param child the child
	 */
	public void addWidget(Widget child){
		containerPanel.add(child);
	}
}
