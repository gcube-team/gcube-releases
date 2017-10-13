/**
 * 
 */
package org.gcube.common.homelibrary.jcr.importing;

import java.net.URI;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class ImportContentManagerItemRequest  extends ImportRequest {
	
	public static ImportContentManagerItemRequest annotationRequest(URI uri)
	{
		return new ImportContentManagerItemRequest(ContentManagerItemType.ANNOTATION, uri);
	}
	
	public static ImportContentManagerItemRequest alternativeRequest(URI uri)
	{
		return new ImportContentManagerItemRequest(ContentManagerItemType.ALTERNATIVE, uri);
	}
	
	public static ImportContentManagerItemRequest partRequest(URI uri)
	{
		return new ImportContentManagerItemRequest(ContentManagerItemType.PART, uri);
	}
	
	public static ImportContentManagerItemRequest metadataRequest(URI uri)
	{
		return new ImportContentManagerItemRequest(ContentManagerItemType.METADATA, uri);
	}
	
	public static ImportContentManagerItemRequest documentRequest(URI uri)
	{
		return new ImportContentManagerItemRequest(ContentManagerItemType.DOCUMENT, uri);
	}
	
	protected URI uri;
	protected ContentManagerItemType itemType;

	public ImportContentManagerItemRequest(ContentManagerItemType itemType, URI uri) {
		super(ImportRequestType.CONTENT_MANAGER_ITEM);
		this.uri = uri;
		this.itemType = itemType;
	}

	/**
	 * @return the uri
	 */
	public URI getUri() {
		return uri;
	}

	/**
	 * @return the itemType
	 */
	public ContentManagerItemType getItemType() {
		return itemType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ImportContentManagerItem [itemType=");
		builder.append(itemType);
		builder.append(", uri=");
		builder.append(uri);
		builder.append("]");
		return builder.toString();
	}

}
