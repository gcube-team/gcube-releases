/**
 * 
 */
package org.gcube.portlets.user.trendylyzer_portlet.server.utils;

import org.gcube.data.analysis.statisticalmanager.stubs.types.SMOperationStatus;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMResourceType;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMFile;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMObject;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMResource;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMTable;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.portlets.user.trendylyzer_portlet.client.algorithms.ComputationStatus.Status;
import org.gcube.portlets.user.trendylyzer_portlet.client.bean.output.FileResource;
import org.gcube.portlets.user.trendylyzer_portlet.client.bean.output.ImagesResource;
import org.gcube.portlets.user.trendylyzer_portlet.client.bean.output.MapResource;
import org.gcube.portlets.user.trendylyzer_portlet.client.bean.output.ObjectResource;
import org.gcube.portlets.user.trendylyzer_portlet.client.bean.output.Resource;
import org.gcube.portlets.user.trendylyzer_portlet.client.bean.output.TableResource;



public class ObjectConverter {

	/**
	 * @param smResource
	 * @return
	 */
	public static Resource convertSmResourceToResource(SMResource smResource, String urserName) {
		int resourceTypeIndex = smResource.resourceType();
		SMResourceType smResType = SMResourceType.values()[resourceTypeIndex];

		Resource resource=null;

		switch(smResType) {

		case FILE:
			SMFile fileRes = (SMFile)smResource;
			resource = new FileResource(fileRes.url(), fileRes.mimeType());
			break;
		case OBJECT:
			SMObject objRes = (SMObject)smResource;

			if (objRes.name().contentEquals(PrimitiveTypes.MAP.toString())) {
				resource = new MapResource(objRes.url());
			}
			else if (objRes.name().contentEquals(PrimitiveTypes.IMAGES.toString())) {
				resource = new ImagesResource(objRes.url());
			}
			else
				resource = new ObjectResource(objRes.url());

			break;
		case TABULAR:
			SMTable tableRes = (SMTable)smResource;
			resource = new TableResource(tableRes.template());
			break;
		}
		
		resource.setName(smResource.name());
		resource.setResourceId(smResource.resourceId());
		resource.setDescription(smResource.description());

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
//	public static ResourceItem convertSmTableToTableItem(SMResource smResource) {
//		boolean isTableResource = (smResource.getResourceType() == SMResourceType.TABULAR.ordinal());
//		ResourceItem.Type type = (isTableResource ? Type.TABLE : Type.FILE);
//		String template = isTableResource ? ((SMTable)smResource).getTemplate() : Constants.realFileTemplate;
//		
//		String url = isTableResource ? null : ((SMFile)smResource).getUrl();
//		
//		String operatorId = smResource.getAlgorithm();
//		Date creationDate = smResource.getCreationDate()==null ? null : smResource.getCreationDate().getTime();
//		String description = smResource.getDescription();
//		String name = smResource.getName();
//		String id = smResource.getResourceId();
//		Provenance provenance = convertSmProvenanceToProvenance(smResource.getProvenance());		
//		return new ResourceItem(type, id, name, description, template, provenance, creationDate, operatorId, url);
//	}

//	/**
//	 * @param provenance
//	 * @return
//	 */
//	private static Provenance convertSmProvenanceToProvenance(int intProvenance) {
//		SMProvenance smProvenance = SMProvenance.values()[intProvenance];
//		return convertSmProvenanceToProvenance(smProvenance);
//	}
//
//	/**
//	 * @param smProvenance
//	 * @return
//	 */
//	private static Provenance convertSmProvenanceToProvenance(
//			SMProvenance smProvenance) {
//		Provenance p=null;
//		
//		switch(smProvenance) {
//		case COMPUTED: p = Provenance.COMPUTED; break;
//		case IMPORTED: p = Provenance.IMPORTED; break;
//		case SYSTEM: p = Provenance.SYSTEM; break;
//		}
//		return p;
//	}

}
