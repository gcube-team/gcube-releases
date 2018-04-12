/**
 * 
 */
package org.gcube.portlets.admin.gcubereleases.client.manage.release;

import java.util.Date;

import org.gcube.portlets.admin.gcubereleases.client.GcubeReleasesAppController;
import org.gcube.portlets.admin.gcubereleases.client.event.ReloadReleasesEvent;
import org.gcube.portlets.admin.gcubereleases.client.rpc.GcubeReleasesServiceAsync;
import org.gcube.portlets.admin.gcubereleases.shared.Release;

import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The Class FormNewRelease.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public class FormNewRelease extends AbstractFormRelease {

	/**
	 * Instantiates a new form new release.
	 */
	public FormNewRelease() {
		super();
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
					
					Date date = input_release_date.getValue();
					Release release = new Release(input_id.getValue(), input_name.getValue(), input_URI.getValue(), date.getTime());
//					GWT.log(release.toString());
					String description = input_description.getValue()!=null?input_description.getValue():"";
					
					release.setDescription(description);
					release.setOnLine(Boolean.parseBoolean(select_online.getValue()));
					showLoading(true, "Trying to save release "+input_id.getValue());
					submit_button.setEnabled(false);
					
					GcubeReleasesServiceAsync.Util.getInstance().insertNewRelease(release, new AsyncCallback<Boolean>() {
						
						@Override
						public void onSuccess(Boolean result) {
							showLoading(false, "");
							if(result){
								showAlertSubmitResult(true, input_name.getValue() +" inserted correctly!");
								submit_button.setEnabled(false);
								GcubeReleasesAppController.eventBus.fireEvent(new ReloadReleasesEvent(false));
							}
							else{
								showAlertSubmitResult(true, "An error occurred when trying to insert "+input_id.getValue() +"! Check the URL or try again later");
								submit_button.setEnabled(true);
							}
							
						}
						
						@Override
						public void onFailure(Throwable caught) {
							submit_button.setEnabled(true);
							showLoading(false, "");
							showAlertSubmitResult(true, "An error occurred when trying to insert "+input_id.getValue() +", try again later!");
						}
					});
				}
			}
		});
		
	}
	
	
}
