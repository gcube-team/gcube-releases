package org.gcube.datapublishing.sdmx.datasource.data;

public abstract class ResultSetExtractorAbstractImpl implements ResultSetExtractor{


	protected SDMXMetadataProvider metadataProvider;
	

	@Override
	public void setMetadataProvider(SDMXMetadataProvider metadataProvider) {
		this.metadataProvider = metadataProvider;
	}
	

	
}
