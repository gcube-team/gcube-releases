package org.gcube.data.transfer.library;

import java.util.Map;

import org.gcube.data.transfer.library.model.Source;
import org.gcube.data.transfer.model.ExecutionReport;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
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
	
	private Map<String,ExecutionReport> executionReports;
}
