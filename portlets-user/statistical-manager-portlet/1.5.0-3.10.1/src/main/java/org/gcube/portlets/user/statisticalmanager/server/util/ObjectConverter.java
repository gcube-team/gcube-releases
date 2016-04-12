/**
 * 
 */
package org.gcube.portlets.user.statisticalmanager.server.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.gcube.common.homelibrary.home.Home;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalFile;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMOperationStatus;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMProvenance;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMResourceType;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMFile;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMObject;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMResource;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMTable;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.portlets.user.statisticalmanager.client.Constants;
import org.gcube.portlets.user.statisticalmanager.client.bean.ComputationStatus.Status;
import org.gcube.portlets.user.statisticalmanager.client.bean.ResourceItem;
import org.gcube.portlets.user.statisticalmanager.client.bean.ResourceItem.Provenance;
import org.gcube.portlets.user.statisticalmanager.client.bean.ResourceItem.Type;
import org.gcube.portlets.user.statisticalmanager.client.bean.output.FileResource;
import org.gcube.portlets.user.statisticalmanager.client.bean.output.ImagesResource;
import org.gcube.portlets.user.statisticalmanager.client.bean.output.MapResource;
import org.gcube.portlets.user.statisticalmanager.client.bean.output.ObjectResource;
import org.gcube.portlets.user.statisticalmanager.client.bean.output.Resource;
import org.gcube.portlets.user.statisticalmanager.client.bean.output.TableResource;

/**
 * @author ceras
 * 
 */
public class ObjectConverter {

	/**
	 * @param smResource
	 * @return
	 */
	static Logger logger = Logger.getLogger("");

	public static Resource convertSmResourceToResource(SMResource smResource,
			String urserName) {
		int resourceTypeIndex = smResource.resourceType();
		SMResourceType smResType = SMResourceType.values()[resourceTypeIndex];

		Resource resource = null;

		switch (smResType) {

		case FILE:
			SMFile fileRes = (SMFile) smResource;
			resource = new FileResource(fileRes.url(), fileRes.mimeType());
			break;
		case OBJECT:
			SMObject objRes = (SMObject) smResource;

			if (objRes.name().contentEquals(PrimitiveTypes.MAP.toString())) {
				resource = new MapResource(objRes.url());
			} else if (objRes.name().contentEquals(
					PrimitiveTypes.IMAGES.toString())) {
				resource = new ImagesResource(objRes.url());
			} else
				resource = new ObjectResource(objRes.url());

			break;
		case TABULAR:
			SMTable tableRes = (SMTable) smResource;
			resource = new TableResource(tableRes.template());
			break;
		case ERROR:
			break;
		default:
			break;

		}
		if (resource == null)
			resource = new Resource();
		if (smResource.name() != null) {
			System.out.println(smResource.name());
			resource.setName(smResource.name());
		}
		if (smResource.resourceId() != null) {
			System.out.println(smResource.name());

			resource.setResourceId(smResource.resourceId());
		}
		if (smResource.description() != null){
			System.out.println(smResource.description());

			resource.setDescription(smResource.description());
		}
		return resource;
	}

	public static Status convertStatus(SMOperationStatus smStatus) {
		Status status = null;

		switch (smStatus) {
		case COMPLETED:
			status = Status.COMPLETE;
			break;
		case FAILED:
			status = Status.FAILED;
			break;
		case PENDING:
			status = Status.PENDING;
			break;
		case RUNNING:
			status = Status.RUNNING;
			break;
		case STOPPED:
			break;
		default:
			break;
		}

		return status;
	}

	public static Status convertStatus(int intSmStatus) {
		SMOperationStatus smStatus = SMOperationStatus.values()[intSmStatus];
		return convertStatus(smStatus);
	}

	/**
	 * @param smTable
	 * @return
	 */
	public static ResourceItem convertSmTableToTableItem(SMResource smResource) {
		boolean isTableResource = (smResource.resourceType() == SMResourceType.TABULAR
				.ordinal());
		ResourceItem.Type type = (isTableResource ? Type.TABLE : Type.FILE);
		String template = isTableResource ? ((SMTable) smResource).template()
				: Constants.realFileTemplate;

		String url = isTableResource ? null : ((SMFile) smResource).url();

		String operatorId = smResource.algorithm();
		Date creationDate = smResource.creationDate() == null ? null
				: smResource.creationDate().getTime();
		String description = smResource.description();

		String name = smResource.name();

		String id = smResource.resourceId();
		Provenance provenance = convertSmProvenanceToProvenance(smResource
				.provenance());
		return new ResourceItem(type, id, name, description, template,
				provenance, creationDate, operatorId, url);
	}

	/*
	 * Extrapolations of three object from folder that contains DWCA file (zip +
	 * taxa+ vernacular)
	 */

	public static ArrayList<ResourceItem> convertSmDWCATableItem(
			SMResource smResource, String username)
			throws InternalErrorException, HomeNotFoundException,
			UserNotFoundException, WorkspaceFolderNotFoundException,
			ItemNotFoundException {
		// boolean isTableResource = (smResource.getResourceType() ==
		// SMResourceType.OBJECT.ordinal());

		ResourceItem.Type type = Type.FILE;
		String template = Constants.realFileTemplate;
		ArrayList<ResourceItem> resources = new ArrayList<ResourceItem>();
		Home home = HomeLibrary.getHomeManagerFactory().getHomeManager()
				.getHome(username);
		Workspace ws = home.getWorkspace();
		String url = ((SMFile) smResource).url();
		WorkspaceItem folderItem = ws.getItemByPath(url);

		WorkspaceFolder folder = (WorkspaceFolder) folderItem;
		List<WorkspaceItem> childrenList = folder.getChildren();
		for (WorkspaceItem item : childrenList) {

			ExternalFile file = (ExternalFile) item;
			String name = item.getName(); // smResource.name()+"_"+item.getName();

			String description = item.getDescription();
			String operatorId = smResource.algorithm();
			String id = smResource.resourceId();
			Provenance provenance = convertSmProvenanceToProvenance(smResource
					.provenance());
			Date creationDate = smResource.creationDate() == null ? null
					: smResource.creationDate().getTime();
			String absoluteUrlFile = file.getPublicLink();

			resources.add(new ResourceItem(type, id, name, description,
					template, provenance, creationDate, operatorId,
					absoluteUrlFile));
		}

		return resources;
	}

	/**
	 * @param provenance
	 * @return
	 */
	private static Provenance convertSmProvenanceToProvenance(int intProvenance) {
		SMProvenance smProvenance = SMProvenance.values()[intProvenance];
		return convertSmProvenanceToProvenance(smProvenance);
	}

	/**
	 * @param smProvenance
	 * @return
	 */
	private static Provenance convertSmProvenanceToProvenance(
			SMProvenance smProvenance) {
		Provenance p = null;

		switch (smProvenance) {
		case COMPUTED:
			p = Provenance.COMPUTED;
			break;
		case IMPORTED:
			p = Provenance.IMPORTED;
			break;
		case SYSTEM:
			p = Provenance.SYSTEM;
			break;
		}
		return p;
	}

}
