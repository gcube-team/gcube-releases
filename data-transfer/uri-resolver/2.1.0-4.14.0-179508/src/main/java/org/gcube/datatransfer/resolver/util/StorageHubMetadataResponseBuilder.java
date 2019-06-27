/**
 *
 */
package org.gcube.datatransfer.resolver.util;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.gcube.common.storagehub.client.StreamDescriptor;
import org.gcube.common.storagehub.client.plugins.AbstractPlugin;
import org.gcube.common.storagehub.client.proxies.ItemManagerClient;
import org.gcube.datatransfer.resolver.ConstantsResolver;


/**
 * The Class StorageHubMetadataResponseBuilder.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Dec 27, 2018
 */
public class StorageHubMetadataResponseBuilder {

	private ItemManagerClient client = AbstractPlugin.item().build();
	private HttpServletRequest request;
	private ResponseBuilder responseBuilder;


	/**
	 * Instantiates a new storage hub metadata response builder.
	 *
	 * @param req the req
	 * @param responseBuilder the response builder
	 */
	public StorageHubMetadataResponseBuilder(HttpServletRequest req, ResponseBuilder responseBuilder){
		this.request = req;
		this.responseBuilder = responseBuilder;
	}


	/**
	 * Fill metadata.
	 * @param streamDescriptor the stream descriptor
	 * @param entityId the entity id
	 * @return the response builder
	 */
	public ResponseBuilder fillMetadata(StreamDescriptor streamDescriptor, String entityId){

		//Adding "Content-Disposition"
		responseBuilder.header(ConstantsResolver.CONTENT_DISPOSITION,"attachment; filename=\""+streamDescriptor.getFileName()+"\"");

		//Adding "Content-Location"
		String contentLocation = String.format("%s/%s/%s",  Util.getServerURL(request), "shub", entityId);
		responseBuilder.header("Content-Location", contentLocation);

		//Managing "Content-Type"
		if (streamDescriptor.getContentType()!= null && !streamDescriptor.getContentType().isEmpty())
			responseBuilder.header("Content-Type", streamDescriptor.getContentType()+"; charset=utf-8");

		//Managing "ETag"
		//Here is not feasible because the entityId is cripted
//		List<Version> versions = client.getFileVersions(entityId);
//		if(versions!=null && !versions.isEmpty()){
//			responseBuilder.header("ETag", versions.get(versions.size()));
//		}

		return responseBuilder;

	}
}
