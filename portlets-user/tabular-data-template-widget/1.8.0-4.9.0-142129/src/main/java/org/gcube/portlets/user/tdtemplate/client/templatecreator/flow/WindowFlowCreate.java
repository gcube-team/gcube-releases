/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.templatecreator.flow;

import org.gcube.portlets.user.tdtemplate.shared.TdFlowModel;

import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.shared.GWT;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Oct 16, 2014
 *
 */
public class WindowFlowCreate extends Window{
	
	private static WindowFlowCreate instance;
	private FlowCreatePanel flowCreatePanel = new FlowCreatePanel(this);
	
	/**
	 * 
	 */
	
	public static synchronized WindowFlowCreate geInstance(){
		if (instance == null)
			instance = new WindowFlowCreate();
		return instance;
	}
	
	private WindowFlowCreate() {
		int width = 425;
		int height = 380;
		setSize(width, height);
		setHeading("Create a new Flow");
		setLayout(new FitLayout());
		add(flowCreatePanel);
	}
	
	public void resetFlowCreatePanel(){
		try{
			remove(flowCreatePanel);
		}catch(Exception e){
			
		}
		flowCreatePanel = new FlowCreatePanel(this);
		add(flowCreatePanel);
	}
	
	public boolean flowExists(){
		return flowCreatePanel.isFlowCreated();
	}
	
	public boolean flowIsReadOnly(){
		return flowCreatePanel.isFlowReadOnly();
	}
	
	public TdFlowModel getFlow(){
		TdFlowModel flowModel = new TdFlowModel();
		flowModel.setName(flowCreatePanel.getName());
		flowModel.setDescription(validString(flowCreatePanel.getDescription()));
		flowModel.setFromDate(flowCreatePanel.getFromDate());
		flowModel.setToDate(flowCreatePanel.getToDate());
		flowModel.setAgency(validString(flowCreatePanel.getAgency()));
		flowModel.setLicenceId(validString(flowCreatePanel.getLicenceId()));
		flowModel.setRights(validString(flowCreatePanel.getRights()));
		flowModel.setBehaviourId(validString(flowCreatePanel.getBehaviourId()));
		
		GWT.log("Flow returned is "+flowModel);
		return flowModel;
	}
	
	public static String validString(String value){
		if(value==null)
			return "";
		return value;
	}
	
	public void setFlowAsReadOnly(boolean bool){
		flowCreatePanel.setAsReadOnly(bool);
		
		if(bool)
			setHeading("Flow attached (Read Only)");
	}

	/**
	 * @param result
	 */
	public void loadFlowData(TdFlowModel result) {
		flowCreatePanel.setName(result.getName());
		flowCreatePanel.setAgency(result.getAgency());
		flowCreatePanel.setLicence(result.getLicenceId());
		flowCreatePanel.setRights(result.getRights());
		flowCreatePanel.setDescription(result.getDescription());
		flowCreatePanel.setBehaviour(result.getBehaviourId());
		flowCreatePanel.setFromDate(result.getFromDate());
		flowCreatePanel.setToDate(result.getToDate());
	}

}
