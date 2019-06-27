package org.gcube.datatransfer.resolver.catalogue;


/**
 * The Class CatalogueRequest.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Nov 6, 2018
 *
 * Binding Catalogue Request as a JSON
 */
public class CatalogueRequest {

	private String gcube_scope;
	private String entity_context;
	private String entity_name;

	/**
	 * Gets the gcube_scope.
	 *
	 * @return the gcube_scope
	 */
	public String getGcube_scope() {
		return gcube_scope;
	}

	/**
	 * Gets the entity_context.
	 *
	 * @return the entity_context
	 */
	public String getEntity_context() {
		return entity_context;
	}

	/**
	 * Gets the entity_name.
	 *
	 * @return the entity_name
	 */
	public String getEntity_name() {
		return entity_name;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("CatalogueRequest [gcube_scope=");
		builder.append(gcube_scope);
		builder.append(", entity_context=");
		builder.append(entity_context);
		builder.append(", entity_name=");
		builder.append(entity_name);
		builder.append("]");
		return builder.toString();
	}

}
