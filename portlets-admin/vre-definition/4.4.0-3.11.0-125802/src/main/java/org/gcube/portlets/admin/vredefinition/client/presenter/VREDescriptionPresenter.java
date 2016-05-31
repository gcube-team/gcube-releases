package org.gcube.portlets.admin.vredefinition.client.presenter;

import java.util.Date;
import java.util.Map;

import org.gcube.portlets.admin.vredefinition.client.VREDefinitionServiceAsync;
import org.gcube.portlets.admin.vredefinition.client.model.VREDefinitionModel;
import org.gcube.portlets.admin.vredefinition.shared.VREDescriptionBean;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;



public class VREDescriptionPresenter implements Presenter {

	public interface Display {
		TextField<String> getVREName();
		TextField<String> getVREDesigner();
		DateField getFromDate();
		DateField getToDate();
		ComboBox<BaseModel> getVREManager();
		TextArea getVREDescription();
		void setData(Map<String,Object> result,VREDescriptionBean bean);
		Widget asWidget();
	}

	private final VREDefinitionModel vreModel;
	
	private final Display display;
	private final VREDefinitionServiceAsync rpcService;
	private LayoutContainer container;


	public VREDescriptionPresenter(VREDefinitionServiceAsync rpcService,VREDefinitionModel model, Display display) {
		this.vreModel = model;
		this.rpcService = rpcService;	
		this.display = display;
	}

	public boolean doSave() {
		
		String description = display.getVREDescription().getValue();
		String designer = display.getVREDesigner().getValue();
		String name =  display.getVREName().getValue();
		String manager = null;
		if(display.getVREManager().getValue()!= null)
			manager = (String)display.getVREManager().getValue().get("name");
		
		Date fromDate = display.getFromDate().getValue();
		Date toDate = display.getToDate().getValue();
		
		VREDescriptionBean bean = new VREDescriptionBean(name, description,
				designer, manager, fromDate, toDate);
		vreModel.setVREDescription(bean);
		
		return validateForm();
	}
	
	public void go(LayoutContainer container) {
		// TODO Auto-generated method stub
		this.container = container;
		
		container.removeAll();
		getVREInfo();
		container.add(display.asWidget());
		
		container.mask("Loading data...", "loading-indicator");
		container.layout();
		
		
	}

	private void getVREInfo() {

		rpcService.getVRE(new AsyncCallback<Map<String,Object>>() {

		
			public void onFailure(Throwable arg0) {
				// TODO Auto-generated method stub
				MessageBox.alert("VRE Definition","We cannot find any VRE-Manager user for this environment." +
						" There must be at least one.", null);
				//container.unmask();
			}

	
			public void onSuccess(Map<String, Object> result) {
				// TODO Auto-generated method stub
				VREDescriptionBean bean = vreModel.getVREDescriptionBean();
				if(bean == null && result.get("vreName") != null) {
					bean = new VREDescriptionBean((String)result.get("vreName"),(String)result.get("vreDescription"),
							(String)result.get("vreDesigner"),(String)result.get("vreManager"),
							(Date)result.get("vreStartTime"),(Date)result.get("vreEndTime"));
				}
				display.setData(result,bean);
				container.unmask();
		
			}

		});
		
		
		
	}

	private boolean validateForm(){
		
		VREDescriptionBean bean = vreModel.getVREDescriptionBean();
		boolean check = true;
		if(bean.getName() == null || bean.getName().trim().isEmpty() || bean.getName().contains(" "))
			check = false;
		if(bean.getDescription() == null || bean.getDescription().trim().isEmpty())
			check = false;
		if(bean.getDesigner() == null || bean.getDesigner().trim().isEmpty())
			check = false;
		if(bean.getManager() == null || bean.getManager().trim().isEmpty())
			check = false;
		if(bean.getStartTime() == null)
			check = false;
		if(bean.getEndTime() == null)
			check = false;
		
		return check;
	}


	public Widget display() {
		// TODO Auto-generated method stub
		return display.asWidget();
	}

}
