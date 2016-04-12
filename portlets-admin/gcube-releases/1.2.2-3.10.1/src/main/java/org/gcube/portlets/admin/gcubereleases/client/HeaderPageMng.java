/**
 * 
 */
package org.gcube.portlets.admin.gcubereleases.client;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.gcube.portlets.admin.gcubereleases.client.view.DateUtilFormatter;
import org.gcube.portlets.admin.gcubereleases.client.view.HeaderPage;
import org.gcube.portlets.admin.gcubereleases.shared.Release;

/**
 * The Class HeaderPageMng.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public class HeaderPageMng {

	/**
	 * 
	 */
	public static final String SVN_RESEARCH_INFRASTRUCTURES_GCUBE_TAGS = "http://svn.research-infrastructures.eu/public/d4science/gcube/tags/";

	/** The header. */
	private HeaderPage header = new HeaderPage();
	
	/** The root panel. */
	private GcubeReleasesAppController rootPanel;
	
	/**
	 * Instantiates a new header page mng.
	 *
	 * @param buildReportRootPanel the build report root panel
	 */
	public HeaderPageMng(GcubeReleasesAppController buildReportRootPanel) {
		this.rootPanel = buildReportRootPanel;
		handleEvents();
	}
	
	/**
	 * Handle events.
	 */
	private void handleEvents(){
	}
	
	/**
	 * Header reset.
	 */
	public void headerReset(){
		header.init();
	}
	
	/**
	 * Show loading.
	 *
	 * @param bool the bool
	 */
	public void showLoading(boolean bool){
		header.showLoading(bool);
	}
	
	/**
	 * Update header title and info.
	 *
	 * @param release the release
	 */
	public void updateHeaderTitleAndInfo(Release release){
		
		header.addTitle(release.getName(), 18);
		
		List<String> othersInfo = new ArrayList<String>();

		String date = release.getReleaseDate()!=null?DateUtilFormatter.getDateToString(release.getReleaseDate()):"";
		othersInfo.add("Release Date: "+date);
		othersInfo.add("Number of packages: "+release.getPackagesNmb());
		String svnURI = SVN_RESEARCH_INFRASTRUCTURES_GCUBE_TAGS+release.getId();
		
		header.setReleaseInfo(release.getUrl(), svnURI, othersInfo);
		header.setReleaseNotes(release.getDescription());
	}
	
	/**
	 * Header update navigation.
	 *
	 * @param title the title
	 * @param result the result
	 */
	public void headerUpdateNavigation(String title, LinkedHashMap<String, Long> result){

		List<String> anchors = new ArrayList<String>();
		for (String package1 : result.keySet()) {
			anchors.add(package1);
		}
		
		header.addFilterSubsystems(title, anchors);
	}

	/**
	 * Gets the header.
	 *
	 * @return the header
	 */
	public HeaderPage getHeader() {
		return header;
	}

	/**
	 * Enable filter by subsystem.
	 *
	 * @param b the b
	 */
	public void enableFilterBySubsystem(boolean b) {
		header.enableFilterBySubsystem(b);
	}

	/**
	 * Sets the other releases.
	 *
	 * @param result the result
	 * @param releaseDisplayed the release displayed
	 */
	public void setOtherReleases(List<Release> result, Release releaseDisplayed) {
		List<Release> rs = new ArrayList<Release>(result.size());
		
		for (Release release : result) {
			if(releaseDisplayed==null || release.getId().compareTo(releaseDisplayed.getId())!=0)
				rs.add(release);
		}
		
		header.setOtherReleases(rs);
	}
	
	/**
	 * Show management options.
	 *
	 * @param bool the bool
	 */
	public void showManagementOptions(boolean bool){
		header.showManagement(bool);
	}
}
