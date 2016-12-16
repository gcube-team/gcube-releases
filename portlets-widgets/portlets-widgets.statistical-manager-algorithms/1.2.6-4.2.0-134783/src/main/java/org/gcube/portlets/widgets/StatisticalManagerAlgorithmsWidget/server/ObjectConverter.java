/**
 * 
 */
package org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.gcube.common.homelibrary.home.Home;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalFile;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMProvenance;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMResourceType;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMFile;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMObject;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMResource;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMTable;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.Constants;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.ResourceItem;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.ResourceItem.Provenance;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.ResourceItem.Type;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.output.FileResource;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.output.ImagesResource;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.output.MapResource;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.output.ObjectResource;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.output.Resource;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.output.TableResource;


/**
 * @author ceras
 *
 */
public class ObjectConverter {

	/**
	 * @param smResource
	 * @return
	 */
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
		}

		resource.setName(smResource.name());
		resource.setResourceId(smResource.resourceId());
		resource.setDescription(smResource.description());

		return resource;
	}


	/**
	 * @param smTable
	 * @return
	 */
	public static ResourceItem convertSmTableToTableItem(SMResource smResource) {
		
		
		
	System.out.println("Inside convertSMTableToTableItem");
		boolean isTableResource = (smResource.resourceType() == SMResourceType.TABULAR
				.ordinal());
		ResourceItem.Type type = (isTableResource ? Type.TABLE : Type.FILE);
		
		String template = isTableResource ? ((SMTable) smResource)
				.template() : Constants.realFileTemplate;

		String url = isTableResource ? null : ((SMFile) smResource).url();

		String operatorId = smResource.algorithm();
		Date creationDate = smResource.creationDate() == null ? null
				: smResource.creationDate().getTime();
		String description = smResource.description();
		
		String name = smResource.name();
		System.out.println("name :"+name);

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

	public static ArrayList<ResourceItem> convertSmDWCATableItem(SMResource smResource,String username) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, WorkspaceFolderNotFoundException, ItemNotFoundException, InsufficientPrivilegesException {
		//boolean isTableResource = (smResource.getResourceType() == SMResourceType.OBJECT.ordinal());
		System.out.println( "convertSmObjecTableItem  ");

		ResourceItem.Type type =  Type.FILE;
		String template =  Constants.realFileTemplate;
		ArrayList<ResourceItem> resources= new ArrayList<ResourceItem>();
		Home home = HomeLibrary.getHomeManagerFactory().getHomeManager()
				.getHome(username);
			Workspace ws = home.getWorkspace();
		String url=((SMFile)smResource).url();
		WorkspaceItem folderItem = ws.getItemByPath(url);

		WorkspaceFolder folder = (WorkspaceFolder) folderItem;
		List<WorkspaceItem> childrenList = folder.getChildren();
		for (WorkspaceItem item : childrenList) {
			System.out.println("item  ");

			ExternalFile file = (ExternalFile) item;
			String name =  smResource.name()+"_"+item.getName();
			System.out.println("name :"+name);

			String description = item.getDescription();
			String operatorId = smResource.algorithm();
			String id = smResource.resourceId();
			Provenance provenance = convertSmProvenanceToProvenance(smResource.provenance());		
			Date creationDate = smResource.creationDate()==null ? null : smResource.creationDate().getTime();
			String absoluteUrlFile = file.getPublicLink();
			System.out.println( "url:  "+absoluteUrlFile);

			resources.add(new ResourceItem(type, id, name, description, template, provenance, creationDate, operatorId, absoluteUrlFile));
		}
		System.out.println( "return resources");
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
