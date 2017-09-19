package org.gcube.data.analysis.tabulardata.metadata;

public class NoSuchMetadataException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5202519531225693460L;

	public NoSuchMetadataException(Class<? extends Metadata> metadataType) {
		super("Cannot find metadata with type: " + metadataType.toString());
	}

}
