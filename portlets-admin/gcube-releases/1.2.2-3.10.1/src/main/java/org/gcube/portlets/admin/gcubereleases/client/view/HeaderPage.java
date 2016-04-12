/**
 * 
 */
package org.gcube.portlets.admin.gcubereleases.client.view;

import java.util.List;

import org.gcube.portlets.admin.gcubereleases.shared.Release;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * The Class HeaderPage.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public class HeaderPage extends FlowPanel {

	private PageTemplate pageTemplate;
	private LoaderIcon loading = new LoaderIcon();

	/**
	 * Instantiates a new header page.
	 */
	public HeaderPage(){
		init();
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
		pageTemplate.addTitle(header, "release", 2);
		getElement().getStyle().setMarginLeft(5, Unit.PX);
		getElement().getStyle().setMarginRight(5, Unit.PX);
//		getElement().setAttribute("margin-right", "5px");
	}
	
	/**
	 * Sets the other releases.
	 *
	 * @param otherReleases the new other releases
	 */
	public void setOtherReleases(List<Release> otherReleases){
		pageTemplate.setOtherReleases(otherReleases);
	}
	
	/**
	 * Sets the release info.
	 *
	 * @param reportURI the report uri
	 * @param svnURI the svn uri
	 * @param othersInfo the others info
	 */
	public void setReleaseInfo(String reportURI, String svnURI, List<String> othersInfo){
		pageTemplate.setReleaseInfo(reportURI, svnURI, othersInfo);
	}
	
	/**
	 * Sets the release notes.
	 *
	 * @param notes the new release notes
	 */
	public void setReleaseNotes(String notes){
		pageTemplate.setReleaseNotes(notes);
	}
	
	/**
	 * Adds the filter subsystems.
	 *
	 * @param title the title
	 * @param anchors the anchors
	 */
	public void addFilterSubsystems(String title, List<String> anchors){
		pageTemplate.addSubsystems(title, "300px", anchors);
	}
	
	/**
	 * Inits the.
	 */
	public void init(){
		this.clear();
		initIconLoading();
		pageTemplate = new PageTemplate();
		add(pageTemplate);
	}

	/**
	 * Enable filter by subsystem.
	 *
	 * @param b the b
	 */
	public void enableFilterBySubsystem(boolean b) {
		pageTemplate.enableFilterBySubsystem(b);
	}
	
	/**
	 * Show management.
	 *
	 * @param bool the bool
	 */
	public void showManagement(boolean bool){
		pageTemplate.showManagement(bool);
	}
}
