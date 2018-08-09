package org.gcube.usecases.ws.thredds.engine;

import org.gcube.data.transfer.library.TransferResult;
import org.gcube.spatial.data.sdi.model.metadata.MetadataReport;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PublishReport {

	boolean isError=false;
	
	
	private String sourceId;
	private String sourceName;
	
	private TransferResult transferResult;
	private MetadataReport metadataReport;
	
}
