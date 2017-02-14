package org.gcube.application.datamanagementfacilityportlet.client.forms;

import org.gcube.application.datamanagementfacilityportlet.client.DataManagementFacility;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.Tags;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.data.GroupGenerationRequest;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.data.Response;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.types.ClientLogicType;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.util.Params;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SubmissionPopup extends Window{


	private SubmissionPopup instance=this;

	SourceGenerationForm form=null;

	//**** Form Fields


	public SubmissionPopup(final ClientLogicType logic) throws Exception{
		this.setPlain(true);  
		this.setSize(600, 400);  
		this.setHeading(logic+" group generation settings");  
		this.setLayout(new FitLayout());
		
		switch(logic){
		case HCAF : form=new HCAFGenerationForm();
		break;
		case HSPEC : form=new HSPECGenerationForm();
		break;
		case HSPEN : form=new HSPENGenerationForm();
		break;
		default : throw new Exception("Invalid Logic");			
		}
		//***************************** SEND Button


		Button send=new Button ("Send", new SelectionListener<ButtonEvent>() {  

			@Override  
			public void componentSelected(ButtonEvent ce) {  
				GroupGenerationRequest request=form.getSettings();
				if(request!=null){
					instance.mask("Sending request..");
					DataManagementFacility.localService.submitGroupGenerationRequest(request, new AsyncCallback<Response>() {

						public void onSuccess(Response arg0) {
							instance.unmask();
							if(arg0.getStatus()){
								String message=(String) arg0.getAdditionalObjects().get(Tags.responseGroupGeneration);
								String v = Format.ellipse(message, 80);
								Info.display("Generation Submission","Successfully submitted request, ID : {0}", new Params(v));
								instance.unmask();
								instance.close();
								DataManagementFacility.get().centerGrid.loader.load();
							}else{
								String message=(String) arg0.getAdditionalObjects().get(Tags.errorMessage);
								String v = Format.ellipse(message, 80);
								Info.display("Generation Submission","Unable to submit request : {0}", new Params(v));
								instance.unmask();
							}

						}

						public void onFailure(Throwable arg0) {
							Log.error("Unexpected error while submitting request", arg0);
							String message="Please contact support";
							String v = Format.ellipse(message, 80);
							Info.display("Generation Submission","Unable to submit request : {0}", new Params(v));
							instance.unmask();
						}
					});
				}  
			}
		});

		this.addButton(send); 
		this.add(form);  
	}



}
