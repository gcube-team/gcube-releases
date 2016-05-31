package org.gcube.portlets.admin.wftemplates.client.view.dialog;

import java.util.ArrayList;

import org.gcube.portlets.admin.wfdocslibrary.shared.WfRoleDetails;
import org.gcube.portlets.admin.wftemplates.client.WfTemplatesServiceAsync;
import org.gcube.portlets.admin.wftemplates.client.event.ConnectionRemovedEvent;
import org.gcube.portlets.admin.wftemplates.client.event.RemoveConnectionEvent;
import org.gcube.portlets.admin.wftemplates.client.event.RolesAddedEvent;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import com.orange.links.client.connection.Connection;
/**
 * <code> AddRolesDialog </code>  is the dialog side implementation of the RPC service.
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version May 2011 (0.1) 
 */
public class AddRolesDialog extends Dialog {

	ArrayList<CheckBox> values;

	FlexTable grid;
	HTML name = new HTML("Attribute Name: ");
	TextBox nameValue = new TextBox();	
	Connection selectedEdge;
	HandlerManager eventBus;


	public AddRolesDialog(final HandlerManager eventBus, final Connection selectedEdge, ArrayList<WfRoleDetails> roles) {
		super();
		this.eventBus = eventBus;
		this.selectedEdge = selectedEdge;
		this.setHeading("Add Role(s)");  
		this.setButtons(Dialog.OKCANCEL);		
		this.setScrollMode(Scroll.AUTO);  
		this.setHideOnButtonClick(true);
		this.setSize(250, 250);

		grid = new FlexTable();
		grid.setWidth("100%");
		grid.setStyleName("gridAttribute");
		this.add(grid);


		ButtonBar buttons = this.getButtonBar();

		Button okbutton = (Button) buttons.getItem(0);
		setHideOnButtonClick(false);

		okbutton.addSelectionListener( new SelectionListener<ButtonEvent>() {  
			public void componentSelected(ButtonEvent ce) {  
				ArrayList<WfRoleDetails> selectedRoles = new ArrayList<WfRoleDetails>();
				for (CheckBox check : values) {
					if  (check.getValue())
						selectedRoles.add(new WfRoleDetails(check.getFormValue(), check.getText()));

				}
				if (selectedRoles.size() > 0) {
					eventBus.fireEvent(new RolesAddedEvent(selectedEdge, selectedRoles));
					hide();
				}
				else 
					Info.display("Warning", "You MUST select at least one role");
				
			}  
		});  		
		Button cancelbutton = (Button) buttons.getItem(1);
		cancelbutton.addSelectionListener( new SelectionListener<ButtonEvent>() {  
			public void componentSelected(ButtonEvent ce) {  
				eventBus.fireEvent(new RemoveConnectionEvent(selectedEdge));
				hide();
			}  
		});  

		setContent(roles);
	}


	

	private void setContent(ArrayList<WfRoleDetails> roles) {
		values = new ArrayList<CheckBox>();
		int i = 0;
		for (WfRoleDetails role : roles) {
			//grid.setWidget(i, 0, new HTML("&nbsp;"+role.getDisplayName(), true));
			CheckBox toAdd = new CheckBox(role.getDisplayName(), true);
			toAdd.setFormValue(role.getId());
			values.add(toAdd);
			grid.setWidget(i, 0,toAdd);
			i++;
		}		
	}


}
