/**
 * 
 */
package org.gcube.portlets.admin.gcubereleases.client.manage;

import java.util.List;

import org.gcube.portlets.admin.gcubereleases.client.GcubeReleasesAppController;
import org.gcube.portlets.admin.gcubereleases.client.dialog.DialogConfirm;
import org.gcube.portlets.admin.gcubereleases.client.dialog.DialogResult;
import org.gcube.portlets.admin.gcubereleases.client.event.DisplaySelectedReleaseEvent;
import org.gcube.portlets.admin.gcubereleases.client.event.ReloadReleasesEvent;
import org.gcube.portlets.admin.gcubereleases.client.manage.release.FormUpdateRelease;
import org.gcube.portlets.admin.gcubereleases.client.manage.release.ReleasesTable;
import org.gcube.portlets.admin.gcubereleases.client.rpc.GcubeReleasesServiceAsync;
import org.gcube.portlets.admin.gcubereleases.client.view.LoaderIcon;
import org.gcube.portlets.admin.gcubereleases.shared.Release;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Heading;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * The Class ReleasesManager.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public class ReleasesManager implements HandlerReleaseOperation, FormCompleted{

	private BaseViewTemplate template;
	
	private DialogResult dialog = new DialogResult(null, "Manage Releases");

	private FlowPanel centerPanel = new FlowPanel();

	private LoaderIcon loader = new LoaderIcon();
	
	private FlowPanel headerPanel = new FlowPanel();
	
	private FlowPanel bottomPanel = new FlowPanel();
	
	private FlowPanel updatePanel = new FlowPanel();
	
	private Alert alertResult = new Alert();

	private ScrollPanel scrollPanel;

	private ReleasesTable releasesTable = new ReleasesTable((HandlerReleaseOperation) this);

	private Release releaseUpdateSelected;

	private Release releaseDisplayed;
	
	
	/**
	 * Instantiates a new releases manager.
	 *
	 * @param releaseDisplayed the release displayed
	 */
	public ReleasesManager(Release releaseDisplayed) {
		
		this.template = new BaseViewTemplate();
		this.releaseDisplayed = releaseDisplayed;
		
		Heading ph = new Heading(4);
		ph.setText("Releases");
		initAlertResult();
		showResult(false, "");
		
		scrollPanel = new ScrollPanel();
		scrollPanel.setWidth("100%");
		scrollPanel.setHeight("350px");
	
		initCentralPanel();
		initBottomPanel();

		headerPanel.add(ph);
		headerPanel.add(alertResult);


		template.addToTop(headerPanel);
		template.addToMiddle(scrollPanel);
		template.addToBottom(bottomPanel);
		
		dialog.addToCenterPanel(template);
		dialog.setWidth("900px");
		
		retrieveReleases();
		
		centerPanel.add(releasesTable.getDataGrid());
		dialog.center();
	}
	
	/**
	 * Inits the bottom panel.
	 */
	private void initBottomPanel(){
		bottomPanel.clear();
		updatePanel.clear();
		bottomPanel.add(updatePanel);
	}
	
	/**
	 * Ser visible update panel.
	 *
	 * @param bool the bool
	 */
	private void serVisibleUpdatePanel(boolean bool){
		bottomPanel.setVisible(bool);
	}
	
	/**
	 * Retrieve releases.
	 */
	private void retrieveReleases(){
		showLoader(true);
		
		GcubeReleasesServiceAsync.Util.getInstance().getReleases(false, new AsyncCallback<List<Release>>() {

			@Override
			public void onFailure(Throwable caught) {
				showResult(true, "Sorry, an error occurred on recovering results, try again later!");
				showLoader(false);
				
			}

			@Override
			public void onSuccess(List<Release> result) {
				releasesTable.addReleases(result);
				showLoader(false);
			}
		});
		
	}
	
	/**
	 * Inits the central panel.
	 */
	private void initCentralPanel(){
		centerPanel.clear();
		scrollPanel.clear();

		loader.setText("Loading Releases...");
		showLoader(false);
		
		centerPanel.add(loader);
		scrollPanel.add(centerPanel);
	}
	
	/**
	 * Show result.
	 *
	 * @param show the show
	 * @param html the html
	 */
	private void showResult(boolean show, String html){
		alertResult.setVisible(show);
		alertResult.setHTML(html);
	}
	
	/**
	 * Inits the alert result.
	 */
	private void initAlertResult(){
		alertResult.setClose(false);
		alertResult.setType(AlertType.INFO);
	}
	
	/**
	 * Show dialog.
	 */
	public void showDialog() {
		dialog.show();
	}

	/**
	 * Show loader.
	 *
	 * @param bool the bool
	 */
	private void showLoader(boolean bool) {
		loader.setVisible(bool);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.buildreportmng.client.manage.HandlerReleaseOperation#delete(org.gcube.portlets.admin.buildreportmng.shared.Release)
	 */
	@Override
	public void delete(final Release rls) {
		serVisibleUpdatePanel(false);
		
		final DialogConfirm dialog = new DialogConfirm(null, "Confirm delete?");
		
		String msg = "Do yo confirm "+rls.getName()+" delete?"+
		"<br/><br/><i><b> All data will be removed from DB</b></i>";
		
		dialog.addToCenterPanel(new HTML(msg));
		dialog.center();
		
		dialog.getYesButton().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				showResult(true, "Deleting "+rls.getName() +"...");
				GWT.log("Deleting "+rls.getName() +"...");
				GcubeReleasesServiceAsync.Util.getInstance().deleteRelease(rls, new AsyncCallback<Boolean>() {

					@Override
					public void onFailure(Throwable caught) {
						showResult(true, "An error occurred when trying to delete "+rls.getId()+"! Try again later");
						dialog.hide();
						GcubeReleasesAppController.eventBus.fireEvent(new ReloadReleasesEvent(false));
					}

					@Override
					public void onSuccess(Boolean result) {
						if(result){
							showResult(true, rls.getName() + " deleted correctly!");
							doFormCompleted();
						}
						else{
							showResult(true, "An error occurred when trying to delete "+rls.getId()+"! Try again later");
//							BuildReportRootPanel.eventBus.fireEvent(new ReloadReleasesEvent(true));
//							submit_button.setEnabled(true);
						}
						
//						BuildReportRootPanel.eventBus.fireEvent(new ReloadReleasesEvent(false));
						
					}
				});
				
				dialog.hide();
				
			}
		});
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.buildreportmng.client.manage.HandlerReleaseOperation#update(org.gcube.portlets.admin.buildreportmng.shared.Release)
	 */
	@Override
	public void update(Release rls) {
		this.releaseUpdateSelected = rls;
		initBottomPanel();
		
		Heading ph = new Heading(5);
		ph.setText("Update Release: ");
		ph.setSubtext(rls.getId());
		
		VerticalPanel vp = new VerticalPanel();
		vp.add(ph);
		
		FormUpdateRelease formRelease = new FormUpdateRelease((FormCompleted) this, (HandlerReleaseOperation) this);
		
		formRelease.setInputIDValue(rls.getId(), true);
		formRelease.setInputDescriptionValue(rls.getDescription(), false);
		formRelease.setInputNameValue(rls.getName(), false);
		formRelease.setInputURIValue(rls.getUrl(), true);
		formRelease.setSelectOnlineValue(rls.isOnLine(), false);
		formRelease.setInputReleaseDate(rls.getReleaseDate());

		vp.add(formRelease);
		
		updatePanel.add(vp);
		
		serVisibleUpdatePanel(true);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.buildreportmng.client.manage.FormCompleted#doFormCompleted()
	 */
	@Override
	public void doFormCompleted() {
		initCentralPanel();
		
		releasesTable = new ReleasesTable(this);
		centerPanel.add(releasesTable.getDataGrid());
		retrieveReleases();
		
		if(releaseUpdateSelected!=null){ //IS UPDATE
			if(releaseDisplayed.getInternalId()==releaseUpdateSelected.getInternalId()){
				GcubeReleasesAppController.eventBus.fireEvent(new DisplaySelectedReleaseEvent(releaseDisplayed));
			}
		}
		
		GcubeReleasesAppController.eventBus.fireEvent(new ReloadReleasesEvent(false));
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.buildreportmng.client.manage.HandlerReleaseOperation#getReleaseSelected()
	 */
	@Override
	public Release getReleaseSelected() {
		return this.releaseUpdateSelected;
	}

}
