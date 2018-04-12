package org.gcube.portlets.widgets.workspacesharingwidget.client.view.sharing;

import java.util.List;

import org.gcube.portlets.widgets.workspacesharingwidget.client.resources.GetPermissionIconByACL;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.ACL_TYPE;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.WorkspaceACL;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.WorkspaceACL.USER_TYPE;

import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.Style.IconAlign;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class PanelTogglePermission extends LayoutContainer {

	protected static final String WORKSPACEACLGROUP = "WORKSPACEACLGROUPSHARING";
	private HorizontalPanel hp = new HorizontalPanel();
	private WorkspaceACL selectedAcl = null;
	private ACL_TYPE defaultACLType;

	public PanelTogglePermission(List<WorkspaceACL> acls, ACL_TYPE defaultACL) {
		this.defaultACLType = defaultACL;

//		setStyleAttribute("margin-top", "10px");
		setStyleAttribute("margin-bottom", "15px");
		setSize(350, 40);
		
		hp.mask("Loading ACLs");
		hp.setVerticalAlign(VerticalAlignment.MIDDLE);
		hp.setStyleAttribute("padding-left", "35px");
		
		for (WorkspaceACL acl : acls) {
			if(acl.getUserType().equals(USER_TYPE.OTHER)){
				ToggleButton toggle = createToggle(acl);
				toggle.setToggleGroup(WORKSPACEACLGROUP);
				toggle.setStyleAttribute("margin-right", "10px");
//				buttonGroup.add(toggle);
//				listToogle.add(toggle);
//				add(toggle);
				hp.add(toggle);
			}
		}

		hp.unmask();
		add(hp);
		layout();
	}

	private ToggleButton createToggle(final WorkspaceACL acl) {

		final ToggleButton bToggle = new ToggleButton(acl.getLabel());
		bToggle.setScale(ButtonScale.MEDIUM);
		bToggle.setId(acl.getId());
		bToggle.setAllowDepress(false);
		
		if(defaultACLType!=null){
			GWT.log("comparing.. "+defaultACLType + " and "+acl.getAclType());
			GWT.log(defaultACLType.equals(acl.getAclType())+"");
//			defaultACLType.equals(acl.getAclType());
			bToggle.toggle(defaultACLType.equals(acl.getAclType()));
		}else{
			bToggle.toggle(acl.getDefaultValue());
		}
		
		if(acl.getDefaultValue())
			selectedAcl = acl;
			
		bToggle.setIconAlign(IconAlign.TOP);
		bToggle.addSelectionListener(new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {
				if(bToggle.isPressed()){
					selectedAcl = acl;
				}
				
			}
		});
		bToggle.setToolTip(new ToolTipConfig(acl.getDescription()));
		AbstractImagePrototype img = GetPermissionIconByACL.getImage(acl);
		
		if(img!=null)
			bToggle.setIcon(img);
		
//		setIcon(bToggle, acl.getId());
		return bToggle;

	}
	
//	private void setIcon(ToggleButton bToggle, String id){
//		
//		if(id.compareTo("ADMINISTRATOR")==0){
//		
//		}else 	if(id.compareTo("READ_ONLY")==0){
//			bToggle.setIcon(Resources.getIconReadOnly());
//		}else 	if(id.compareTo("WRITE_OWNER")==0){
//			bToggle.setIcon(Resources.getIconWriteOwn());
//		}else 	if(id.compareTo("WRITE_ALL")==0){
//			bToggle.setIcon(Resources.getIconWriteAll());
//		}
//		
//	}
	
	public WorkspaceACL getSelectedACL() {
		GWT.log("Selected ACL is: "+selectedAcl);
		return selectedAcl;
	}
	

	/*
	public List<WorkspaceACL> getCheckedGroupList() {

		List<WorkspaceACL> listDS = new ArrayList<WorkspaceACL>();

		List<Radio> values = new ArrayList<Radio>();

		if (radioGroup.getValue()!=null){
			Radio radio = radioGroup.getValue();
			values.add((Radio) radio);
		}
		else {
			List<Field<?>> listChecks = radioGroup.getAll();
			for (Field<?> field : listChecks) {
				values.add((Radio) field);
			}
		}

		for (Radio radio : values) {
			if (radio.isEnabled()){
				WorkspaceACL acl = (WorkspaceACL) radio.getData(WORKSPACEACL);
				listDS.add(acl);
			}
		}

		if (listDS.size() == 0)
			return null;

		return listDS;
	}
	*/
}
