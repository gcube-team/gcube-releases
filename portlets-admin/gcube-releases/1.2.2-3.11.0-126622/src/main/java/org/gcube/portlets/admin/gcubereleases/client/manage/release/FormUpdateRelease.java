/**
 * 
 */
package org.gcube.portlets.admin.gcubereleases.client.manage.release;

import java.util.Date;

import org.gcube.portlets.admin.gcubereleases.client.GcubeReleasesAppController;
import org.gcube.portlets.admin.gcubereleases.client.event.ReloadReleasesEvent;
import org.gcube.portlets.admin.gcubereleases.client.manage.FormCompleted;
import org.gcube.portlets.admin.gcubereleases.client.manage.HandlerReleaseOperation;
import org.gcube.portlets.admin.gcubereleases.client.rpc.GcubeReleasesServiceAsync;
import org.gcube.portlets.admin.gcubereleases.shared.Release;

import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The Class FormUpdateRelease.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public class FormUpdateRelease extends AbstractFormRelease {

	private FormCompleted onFormCompleted;
	private HandlerReleaseOperation handlerReleaseOperation;

	/**
	 * Instantiates a new form update release.
	 *
	 * @param formCompleted the form completed
	 * @param handlerReleaseOperation the handler release operation
	 */
	public FormUpdateRelease(FormCompleted formCompleted, HandlerReleaseOperation handlerReleaseOperation) {
		super();
		this.onFormCompleted = formCompleted;	
		this.handlerReleaseOperation = handlerReleaseOperation;
		submit_button.setText("Update Release");
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.gcubereleases.client.manage.release.AbstractFormRelease#validateForm()
	 */
	public boolean validateForm(){
		
		boolean valid = true;
		input_id_group.setType(ControlGroupType.NONE);
		input_name_group.setType(ControlGroupType.NONE);
		input_URI_group.setType(ControlGroupType.NONE);
		
		if(input_id.getValue()==null || input_id.getValue().isEmpty()){
			input_id_group.setType(ControlGroupType.ERROR);
			valid = false;
		}
		
		if(input_name.getValue()==null || input_name.getValue().isEmpty()){
			input_name_group.setType(ControlGroupType.ERROR);
			valid = false;
		}
		
		if(input_URI.getValue()==null || input_URI.getValue().isEmpty()){
			input_URI_group.setType(ControlGroupType.ERROR);
			valid = false;
		}
		
		if(input_release_date.getValue()==null){
			input_release_date_group.setType(ControlGroupType.ERROR);
			valid = false;
		}

//		alertError.setVisible(!valid);
		setAlertErrorVisible(!valid);
		return valid;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.buildreportmng.client.manage.newrelease.AbstractFormRelease#subtmitHandler()
	 */
	@Override
	public void subtmitHandler() {
		
		submit_button.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
//				Window.alert("aa");
				if(validateForm()){
					showLoading(true, "Trying to update data for "+input_id.getValue());
					
					Release release = handlerReleaseOperation.getReleaseSelected();
					String description = input_description.getValue()!=null?input_description.getValue():"";
					Date date = input_release_date.getValue();
					
					release.setName(input_name.getValue());
					release.setDescription(description);
					release.setOnLine(Boolean.parseBoolean(select_online.getValue()));
					release.setReleaseDate(date.getTime());
					
					GcubeReleasesServiceAsync.Util.getInstance().updateReleaseInfo(release, new AsyncCallback<Release>() {
						
						@Override
						public void onSuccess(Release result) {
							showLoading(false, "");
							if(result!=null){
								showAlertSubmitResult(true, input_name.getValue() +" updated correctly!");
//								submit_button.setEnabled(false);
								onFormCompleted.doFormCompleted();
							}
							else{
								showAlertSubmitResult(true, "An error occurred when trying to update "+input_id.getValue() +"! Try again later");
//								submit_button.setEnabled(true);
//								BuildReportRootPanel.eventBus.fireEvent(new ReloadReleasesEvent(true));
							}
						}
						
						@Override
						public void onFailure(Throwable caught) {
							submit_button.setEnabled(true);
							showLoading(false, "");
							showAlertSubmitResult(true, "An error occurred when trying to update "+input_id.getValue() +", try again later!");
							GcubeReleasesAppController.eventBus.fireEvent(new ReloadReleasesEvent(false));
						}
					});
				}
			}
		});
		
	}
	
}
