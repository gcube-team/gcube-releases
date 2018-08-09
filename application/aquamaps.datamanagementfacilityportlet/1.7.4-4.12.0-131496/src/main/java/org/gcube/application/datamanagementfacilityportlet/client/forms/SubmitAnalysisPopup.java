package org.gcube.application.datamanagementfacilityportlet.client.forms;

import java.util.ArrayList;
import java.util.List;

import org.gcube.application.datamanagementfacilityportlet.client.DataManagementFacility;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.Tags;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.data.ClientResource;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.data.ClientTinyResource;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.data.Response;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.types.ClientAnalysisType;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.dnd.DND.Feedback;
import com.extjs.gxt.ui.client.dnd.GridDragSource;
import com.extjs.gxt.ui.client.dnd.GridDropTarget;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.util.Params;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ComponentPlugin;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.HtmlEditor;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SubmitAnalysisPopup extends Window{
	static ComponentPlugin plugin = new ComponentPlugin() {  
		public void init(Component component) {  
			component.addListener(Events.Render, new Listener<ComponentEvent>() {  
				public void handleEvent(ComponentEvent be) {  
					El elem = be.getComponent().el().findParent(".x-form-element", 3);  
					// should style in external CSS  rather than directly  
					elem.appendChild(XDOM.create("<div style='color: #615f5f;padding: 1 0 2 0px;'>" + be.getComponent().getData("text") + "</div>"));  
				}  
			});  
		}  
	};

	private SubmitAnalysisPopup instance=this;

	public final TextField<String> titleField = new TextField<String>(); 
	public final HtmlEditor htmlDescription = new HtmlEditor(); 
	public Grid<ClientTinyResource> grid;

	public SubmitAnalysisPopup(final List<ClientAnalysisType> types) {
		TabPanel tabs=new TabPanel();
		TabItem generalDetailsTab=new TabItem("General details");  
		FormPanel generalDetailsForm = new FormPanel();		
		generalDetailsForm.setBorders(false);  
		generalDetailsForm.setBodyBorder(false);  
		generalDetailsForm.setLabelWidth(100);  
		generalDetailsForm.setPadding(5);  
		generalDetailsForm.setHeaderVisible(false); 
		generalDetailsForm.setFrame(true);

		titleField.setFieldLabel("Request Title");  
		titleField.setAllowBlank(false);
		titleField.setData("text", "Enter a title for the analysis.");
		titleField.addPlugin(plugin);
		generalDetailsForm.add(titleField, new FormData("100%"));


		htmlDescription.setHideLabel(true);
		htmlDescription.setFieldLabel("Description");
		htmlDescription.setHeight(200);
		htmlDescription.setData("text", "Enter a description for the analysis.");
		htmlDescription.addPlugin(plugin);
		generalDetailsForm.add(htmlDescription, new FormData("100%"));  

		generalDetailsTab.add(generalDetailsForm);
		tabs.add(generalDetailsTab);

		//***************** TAB 2

		TabItem temporalTab=new TabItem("Temporal Order");
		FormPanel temporalForm = new FormPanel();		
		temporalForm.setBorders(false);  
		temporalForm.setBodyBorder(false);  
		temporalForm.setPadding(5);  
		temporalForm.setHeaderVisible(false); 
		temporalForm.setFrame(true);
		temporalForm.add(new LabelField("Specify the temporal order of selected sources by dragging them within the grid."),new FormData("100%"));

		 

		grid = new TinyResourceGrid();  

		
		new GridDragSource(grid);  

		GridDropTarget target = new GridDropTarget(grid);  
		target.setAllowSelfAsSource(true);  
		target.setFeedback(Feedback.INSERT);

		temporalForm.add(grid);
		temporalTab.add(temporalForm);
		tabs.add(temporalTab);

		this.setPlain(true);  
		this.setSize(600, 400);  
		this.setHeading("Analysis Submission");  
		this.setLayout(new FitLayout());
		Button send=new Button ("Send", new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				instance.mask("Sending request..");
				String title=titleField.getValue();
				if(title==null) {
					MessageBox.alert("Invalid Operation", "Please specify a title for the analysis", null);
					instance.unmask();
				}
				else{
					String description=htmlDescription.getValue();
					List<ClientTinyResource> models=grid.getStore().getModels();
					Integer[] ids=new Integer[models.size()];
					for(int i=0;i<models.size();i++){
						ids[i]=models.get(i).getId();
					}
					DataManagementFacility.localService.analyzeResources(types,title, description, ids, new AsyncCallback<Response>() {

						public void onSuccess(Response arg0) {
							instance.unmask();
							if(arg0.getStatus()){
								String message=(String) arg0.getAdditionalObjects().get(Tags.responseAnalysisSubmission);
								String v = Format.ellipse(message, 80);
								Info.display("Analysis Submission","Successfully submitted request, ID : {0}", new Params(v));
								instance.unmask();
								instance.close();							
							}else{
								String message=(String) arg0.getAdditionalObjects().get(Tags.errorMessage);
								String v = Format.ellipse(message, 80);
								Info.display("Analysis Generation Submission","Unable to submit request : {0}", new Params(v));
								instance.unmask();
							}
						}

						public void onFailure(Throwable arg0) {
							Log.error("Unexpected error while submitting request", arg0);
							String message="Please contact support";
							String v = Format.ellipse(message, 80);
							Info.display("Analysis Submission","Unable to submit request : {0}", new Params(v));
							instance.unmask();
						}
					});
				}
			}
		});
		this.addButton(send);
		this.add(tabs);  


	}

}
