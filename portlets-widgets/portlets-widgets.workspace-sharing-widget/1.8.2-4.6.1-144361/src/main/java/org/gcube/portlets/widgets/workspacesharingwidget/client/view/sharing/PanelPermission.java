package org.gcube.portlets.widgets.workspacesharingwidget.client.view.sharing;

import java.util.List;

import org.gcube.portlets.widgets.workspacesharingwidget.shared.WorkspaceACL;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.WorkspaceACL.USER_TYPE;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;

public class PanelPermission extends LayoutContainer {

	protected static final String WORKSPACEACL = "WORKSPACEACL";
	private RadioGroup radioGroup = new RadioGroup();

	public PanelPermission(List<WorkspaceACL> acls) {

		setStyleAttribute("margin-top", "10px");
		setStyleAttribute("margin-bottom", "10px");
		setSize(350, 30);
		
		radioGroup.mask("Loading ACLs");
		
		for (WorkspaceACL acl : acls) {
			
			if(acl.getUserType().equals(USER_TYPE.OTHER)){
				Radio radio = createRadio(acl);
				radioGroup.add(radio);
			}
		}

		radioGroup.unmask();
		add(radioGroup);
	}

	private Radio createRadio(WorkspaceACL acl) {

		Radio radio = new Radio();
		// check.setId(dsm.getId());
		// check.setBoxLabel(dsm.getName() + " ("+property+")");
		radio.setBoxLabel(acl.getLabel());
		radio.setValueAttribute(acl.getLabel());
		radio.setData(WORKSPACEACL, acl);
		radio.setToolTip(new ToolTipConfig(acl.getDescription()));
		radio.setValue(acl.getDefaultValue());
		return radio;

	}
	
	public WorkspaceACL getSelectedACL() {
		
		if (radioGroup.getValue()!=null)
			return (WorkspaceACL) radioGroup.getValue().getData(WORKSPACEACL);
		
		return null;
	}
}
