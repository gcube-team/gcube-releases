package org.gcube.portlets.user.td.gwtservice.server.resource;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.resources.ResourceDescriptor;
import org.gcube.data.analysis.tabulardata.model.resources.InternalURI;
import org.gcube.data.analysis.tabulardata.model.resources.Resource;
import org.gcube.data.analysis.tabulardata.model.resources.ResourceType;
import org.gcube.data.analysis.tabulardata.model.resources.StringResource;
import org.gcube.data.analysis.tabulardata.model.resources.TableResource;
import org.gcube.data.analysis.tabulardata.model.resources.Thumbnail;
import org.gcube.portlets.user.td.gwtservice.server.uriresolver.UriResolverTDClient;
import org.gcube.portlets.user.td.gwtservice.server.util.ServiceCredentials;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.resources.InternalURITD;
import org.gcube.portlets.user.td.gwtservice.shared.tr.resources.ResourceTD;
import org.gcube.portlets.user.td.gwtservice.shared.tr.resources.ResourceTDDescriptor;
import org.gcube.portlets.user.td.gwtservice.shared.tr.resources.ResourceTDType;
import org.gcube.portlets.user.td.gwtservice.shared.tr.resources.StringResourceTD;
import org.gcube.portlets.user.td.gwtservice.shared.tr.resources.TableResourceTD;
import org.gcube.portlets.user.td.gwtservice.shared.uriresolver.UriResolverSession;
import org.gcube.portlets.user.td.widgetcommonevent.shared.thumbnail.ThumbnailTD;
import org.gcube.portlets.user.td.widgetcommonevent.shared.uriresolver.ApplicationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class ResourceTDCreator {
	private static Logger logger = LoggerFactory
			.getLogger(ResourceTDCreator.class);

	protected static SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm");

	private ServiceCredentials serviceCredentials;

	public ResourceTDCreator(ServiceCredentials serviceCredentials) {
		this.serviceCredentials = serviceCredentials;
	}

	/**
	 * 
	 * @param resources
	 * @return
	 * @throws TDGWTServiceException
	 */
	public ArrayList<ResourceTDDescriptor> createResourcesDescriptorTD(
			List<ResourceDescriptor> resources) throws TDGWTServiceException {
		ArrayList<ResourceTDDescriptor> resourcesTD = new ArrayList<ResourceTDDescriptor>();

		for (ResourceDescriptor resourceDescriptor : resources) {
			ResourceTDDescriptor resourceTDDescriptor = null;
			long id = resourceDescriptor.getId();
			String name = resourceDescriptor.getName();
			String description = resourceDescriptor.getDescription();
			long creatorId = resourceDescriptor.getCreatorId();

			Resource resource = resourceDescriptor.getResource();
			if (resource != null) {
				ResourceTD resourceTD = createResourceTD(resource);
				ResourceType resourceType = resourceDescriptor
						.getResourceType();
				ResourceTDType resourceTDType = ResourceTypeMap
						.getResourceTDType(resourceType);
				String creationDate = null;

				try {
					creationDate = sdf.format(resourceDescriptor
							.getCreationDate().getTime());
				} catch (Throwable e) {
					logger.error("ResourceDescription[id=" + id + ", name="
							+ name + ", description=" + description
							+ ", creatorId=" + creatorId
							+ "] has invalid creation date!");

				}
				resourceTDDescriptor = new ResourceTDDescriptor(id, name,
						description, creationDate, creatorId, resourceTDType,
						resourceTD);
				resourcesTD.add(resourceTDDescriptor);

			} else {
				logger.error("ResourceDescription[id=" + id + ", name=" + name
						+ ", description=" + description + ", creatorId="
						+ creatorId + "] has resource null!");
			}

		}

		logger.debug("Resources retrieved: " + resourcesTD);
		return resourcesTD;
	}

	/**
	 * 
	 * @param resource
	 * @return
	 */
	protected ResourceTD createResourceTD(Resource resource)
			throws TDGWTServiceException {
		ResourceTD resourceTD = null;

		Class<? extends Resource> resourceClass = resource.getResourceType();
		if (resourceClass == InternalURI.class) {

			InternalURI internalURI = (InternalURI) resource;
			String id = internalURI.getUri().toString();
			Thumbnail thumbnail = internalURI.getThumbnail();

			ThumbnailTD thumbnailTD = null;
			if (thumbnail != null && thumbnail.getUri() != null) {
				UriResolverTDClient uriResolverTDClient = new UriResolverTDClient();
				UriResolverSession uriResolverSession = new UriResolverSession(
						thumbnail.getUri().toString(), ApplicationType.SMP_ID,
						"resourcethumbnail.jpg", thumbnail.getMimeType());
				String link = uriResolverTDClient.resolve(uriResolverSession,
						serviceCredentials);
				thumbnailTD = new ThumbnailTD(link, thumbnail.getMimeType());
			}

			return new InternalURITD(id, internalURI.getMimeType(), thumbnailTD);
		} else {
			if (resourceClass == StringResource.class) {
				StringResource stringResource = (StringResource) resource;
				return new StringResourceTD(stringResource.getStringValue());
			} else {
				if (resourceClass == TableResource.class) {
					TableResource tableResource = (TableResource) resource;
					return new TableResourceTD(tableResource.getTableId()
							.getValue(), tableResource.getStringValue());
				} else {

				}
			}
		}

		return resourceTD;

	}

}
