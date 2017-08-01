package org.apache.jackrabbit.j2ee.workspacemanager;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.jackrabbit.j2ee.workspacemanager.items.JCRExternalFile;
import org.apache.jackrabbit.j2ee.workspacemanager.items.JCRExternalImage;
import org.apache.jackrabbit.j2ee.workspacemanager.items.JCRExternalPDFFile;
import org.apache.jackrabbit.j2ee.workspacemanager.items.JCRExternalUrl;
import org.apache.jackrabbit.j2ee.workspacemanager.items.JCRGCubeItem;
import org.apache.jackrabbit.j2ee.workspacemanager.items.JCRQuery;
import org.apache.jackrabbit.j2ee.workspacemanager.items.JCRReport;
import org.apache.jackrabbit.j2ee.workspacemanager.items.JCRReportTemplate;
import org.apache.jackrabbit.j2ee.workspacemanager.items.JCRTimeSeries;
import org.apache.jackrabbit.j2ee.workspacemanager.items.JCRWorkflowReport;
import org.apache.jackrabbit.j2ee.workspacemanager.items.gcube.JCRDocument;
import org.apache.jackrabbit.j2ee.workspacemanager.items.gcube.JCRImageDocument;
import org.apache.jackrabbit.j2ee.workspacemanager.items.gcube.JCRMetadata;
import org.apache.jackrabbit.j2ee.workspacemanager.items.gcube.JCRPDFDocument;
import org.apache.jackrabbit.j2ee.workspacemanager.items.gcube.JCRUrlDocument;
import org.apache.jackrabbit.j2ee.workspacemanager.search.JCRSearchFolder;
import org.apache.jackrabbit.j2ee.workspacemanager.search.JCRSearchFolderItem;
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.SearchItemDelegate;
import org.gcube.common.homelibary.model.items.type.PrimaryNodeType;

import com.thoughtworks.xstream.XStream;

public class NodeManager {

//	private static Logger logger = LoggerFactory.getLogger(NodeManager.class);

	Node node;
	String login;
	XStream xstream;

	public NodeManager(Node node, String login) throws Exception {	

		this.node = node;
		this.login = login;
	}

	public ItemDelegate getItemDelegate() throws RepositoryException {
//			System.out.println("*****node.getPath(): " + node.getPath() + " - node.getPrimaryNodeType().getName(): " + node.getPrimaryNodeType().getName());

		String type = node.getPrimaryNodeType().getName();

		switch (type) {		
		case PrimaryNodeType.NT_WORKSPACE_ROOT:
			return new JCRWorkspaceFolder(node, login).getItemDelegate();
		case PrimaryNodeType.NT_HOME:
			return new JCRWorkspaceItem(node, login).getItemDelegate();
		case PrimaryNodeType.NT_FOLDER:
			return new JCRWorkspaceFolder(node, login).getItemDelegate();
		case PrimaryNodeType.NT_WORKSPACE_REFERENCE:
			return new JCRWorkspaceReference(node, login).getItemDelegate();
		case PrimaryNodeType.NT_WORKSPACE_FOLDER_ITEM:
			return new JCRWorkspaceFolder(node, login).getItemDelegate();
		case PrimaryNodeType.NT_WORKSPACE_FOLDER:
			return new JCRWorkspaceFolder(node, login).getItemDelegate();
		case PrimaryNodeType.NT_WORKSPACE_SMART_FOLDER:
			return new JCRWorkspaceSmartFolder(node, login).getItemDelegate();
		case PrimaryNodeType.NT_WORKSPACE_SHARED_FOLDER:
			return new JCRWorkspaceSharedFolder(node,login).getItemDelegate();
		case PrimaryNodeType.NT_WORKSPACE_VRE_FOLDER:
			return new JCRWorkspaceVREFolder(node, login).getItemDelegate();
		case PrimaryNodeType.NT_ROOT_FOLDER_BULK_CREATOR:
			return new JCRWorkspaceFolder(node, login).getItemDelegate();
		case PrimaryNodeType.NT_WORKSPACE_FILE:
			return new JCRExternalFile(node, login).getItemDelegate();
		case PrimaryNodeType.NT_WORKSPACE_IMAGE:
			return new JCRExternalImage(node, login).getItemDelegate();
		case PrimaryNodeType.NT_WORKSPACE_PDF_FILE:
			return new JCRExternalPDFFile(node, login).getItemDelegate();
		case PrimaryNodeType.NT_WORKSPACE_URL:
			return new JCRExternalUrl(node, login).getItemDelegate();
		case PrimaryNodeType.NT_GCUBE_ITEM:
			return new JCRGCubeItem(node, login).getItemDelegate();
		case PrimaryNodeType.NT_TRASH_ITEM:
			return new JCRWorkspaceTrashItem(node, login).getItemDelegate();
		case PrimaryNodeType.NT_ITEM_SENT:
			return new JCRWorkspaceMessage(node, login).getItemDelegate();
		case PrimaryNodeType.NT_ROOT_MESSAGES:
			return new JCRWorkspaceFolder(node, login).getItemDelegate();
		case PrimaryNodeType.NT_TIMESERIES_ITEM:
			return new JCRTimeSeries(node, login).getItemDelegate();
		case PrimaryNodeType.NT_QUERY:
			return new JCRQuery(node, login).getItemDelegate();
		case PrimaryNodeType.NT_WORKSPACE_REPORT:
			return new JCRReport(node, login).getItemDelegate();
		case PrimaryNodeType.NT_WORKSPACE_REPORT_TEMPLATE:
			return new JCRReportTemplate(node, login).getItemDelegate();

		case PrimaryNodeType.NT_DOCUMENT_ITEM:
			return new JCRDocument(node, login).getItemDelegate();
		case PrimaryNodeType.NT_IMAGE_DOCUMENT_ITEM:
			return new JCRImageDocument(node, login).getItemDelegate();
		case PrimaryNodeType.NT_PDF_DOCUMENT_ITEM:
			return new JCRPDFDocument(node, login).getItemDelegate();
		case PrimaryNodeType.NT_URL_DOCUMENT_ITEM:
			return new JCRUrlDocument(node, login).getItemDelegate();
		case PrimaryNodeType.NT_METADATA_ITEM:
			return new JCRMetadata(node, login).getItemDelegate();
		case PrimaryNodeType.NT_WORKSPACE_WORKFLOW_REPORT:
			return new JCRWorkflowReport(node, login).getItemDelegate();


		default: 
			throw new RepositoryException("JCR node type unknow");
		}
	}


	public SearchItemDelegate getSearchItem(String itemName) throws RepositoryException {

		String type = node.getPrimaryNodeType().getName();

		if (type.equals(PrimaryNodeType.NT_WORKSPACE_FOLDER) ||
				type.equals(PrimaryNodeType.NT_WORKSPACE_SHARED_FOLDER)) {
			return new JCRSearchFolder(node, itemName).getSearchItemDelegate();
		} else {
			return new JCRSearchFolderItem(node, itemName).getSearchItemDelegate();
		}



	}




}
