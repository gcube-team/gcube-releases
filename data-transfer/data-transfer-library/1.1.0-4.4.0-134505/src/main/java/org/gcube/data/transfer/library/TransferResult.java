package org.gcube.data.transfer.library;

import lombok.Data;
import lombok.NonNull;

import org.gcube.data.transfer.library.model.Source;

@Data
public class TransferResult {

	@NonNull
	private Source source;
	@NonNull
	private String destinationHostName;
	@NonNull
	private Long elapsedTime;
	@NonNull
	private Long transferedBytes;
	@NonNull
	private String remotePath;
	
}
