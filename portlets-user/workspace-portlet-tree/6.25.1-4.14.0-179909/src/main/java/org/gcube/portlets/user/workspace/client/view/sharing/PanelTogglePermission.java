package org.gcube.portlets.user.workspace.client.view.sharing;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.workspace.client.util.GetPermissionIconByACL;
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

	protected static final String WORKSPACEACLGROUP = "WORKSPACEACLGROUP";

	private HorizontalPanel hp = new HorizontalPanel();
	
	private WorkspaceACL selectedAcl = null;
	
	private List<ToggleButton> toggles;

	public PanelTogglePermission(List<WorkspaceACL> acls) {

//		setStyleAttribute("margin-top", "10px");
		setStyleAttribute("margin-bottom", "15px");
		setSize(350, 40);
		
		hp.mask("Loading ACLs");
		hp.setVerticalAlign(VerticalAlignment.MIDDLE);
		hp.setStyleAttribute("padding-left", "35px");
		toggles = new ArrayList<ToggleButton>(acls.size());
		
		for (WorkspaceACL acl : acls) {
			if(acl.getUserType().equals(USER_TYPE.OTHER)){
				ToggleButton toggle = createToggle(acl);
				toggle.setToggleGroup(WORKSPACEACLGROUP);
				toggle.setStyleAttribute("margin-right", "10px");
				toggles.add(toggle);
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
		bToggle.toggle(acl.getDefaultValue());
		
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
		
		return bToggle;
	}
	
	
	public WorkspaceACL getSelectedACL() {
		GWT.log("Selected ACL is: "+selectedAcl);
		return selectedAcl;
	}
	
	public void selectACL(WorkspaceACL acl){
		for (ToggleButton toogle : toggles) {
			GWT.log("Comparing toogle.getId(): "+toogle.getId() +" and acl.getId(): "+acl.getId());
			if(toogle.getId().compareTo(acl.getId())==0){
				GWT.log("Toogle: "+acl);
				toogle.toggle(true);
				selectedAcl = acl;
				return;
			}
		}
	}
}
