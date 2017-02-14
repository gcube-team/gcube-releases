package org.gcube.common.homelibrary.jcr.workspace.folder.items;

import java.util.Map;

import org.apache.commons.lang.Validate;
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.type.FolderItemType;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.folder.items.WorkflowReport;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;

import com.thoughtworks.xstream.XStream;

public class JCRWorkflowReport extends JCRWorkspaceFolderItem implements
		WorkflowReport {
	
	public JCRWorkflowReport(JCRWorkspace workspace, ItemDelegate itemDelegate) throws RepositoryException, InternalErrorException {
		super(workspace, itemDelegate);
	}

	public JCRWorkflowReport(JCRWorkspace workspace, ItemDelegate itemDelegate, String name,
			String description, String workflowId, String workflowStatus,
			String workflowData) throws RepositoryException  {
		super(workspace,itemDelegate,name,description);
		
		Validate.notNull(workflowId, "workflowId must be not null");
		Validate.notNull(workflowStatus, "workflowStatus must be not null");
		Validate.notNull(workflowData, "workflowData type must be not null");
		
		Map<NodeProperty, String> content = itemDelegate.getContent();
		content.put(NodeProperty.FOLDER_ITEM_TYPE, FolderItemType.WORKFLOW_REPORT.toString());
		content.put(NodeProperty.WORKFLOW_DATA, new XStream().toXML(workflowData));
		content.put(NodeProperty.WORKFLOW_ID, workflowId);
		content.put(NodeProperty.WORKFLOW_STATUS, workflowStatus);
		
	}

	@Override
	public long getLength() throws InternalErrorException {
		return 0;
	}

	@Override
	public FolderItemType getFolderItemType() {
		return FolderItemType.WORKFLOW_REPORT;
	}

	@Override
	public String getMimeType() throws InternalErrorException {
		return null;
	}

}
