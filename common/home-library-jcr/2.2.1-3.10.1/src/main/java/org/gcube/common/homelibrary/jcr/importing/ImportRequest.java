/**
 * 
 */
package org.gcube.common.homelibrary.jcr.importing;
/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public abstract class ImportRequest {
	
	protected ImportRequestType type;

	/**
	 * Create a request.
	 * @param type the request type.
	 */
	public ImportRequest(ImportRequestType type) {
		this.type = type;
	}


	/**
	 * @return the request type.
	 */
	public ImportRequestType getType(){
		return type;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ImportRequest [type=");
		builder.append(type);
		builder.append("]");
		return builder.toString();
	}

}
