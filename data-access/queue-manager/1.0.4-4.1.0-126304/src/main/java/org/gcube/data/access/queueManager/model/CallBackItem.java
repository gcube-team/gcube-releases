package org.gcube.data.access.queueManager.model;

import java.util.List;


import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("CallBackItem")
public class CallBackItem implements QueueItem{

	@XStreamAsAttribute
	private String id;
	@XStreamImplicit(itemFieldName="outputFile")
	private List<String> producedFiles;
	private RemoteExecutionStatus exitStatus;
	private String exitMessage;
	
	public CallBackItem(String id, List<String> producedFiles,
			RemoteExecutionStatus exitStatus, String exitMessage) {
		super();
		this.id = id;
		this.producedFiles = producedFiles;
		this.exitStatus = exitStatus;
		this.exitMessage = exitMessage;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the producedFiles
	 */
	public List<String> getProducedFiles() {
		return producedFiles;
	}

	/**
	 * @return the exitStatus
	 */
	public RemoteExecutionStatus getExitStatus() {
		return exitStatus;
	}

	/**
	 * @return the exitMessage
	 */
	public String getExitMessage() {
		return exitMessage;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CallBackItem [id=");
		builder.append(id);
		builder.append(", producedFiles=");
		builder.append(producedFiles);
		builder.append(", exitStatus=");
		builder.append(exitStatus);
		builder.append(", exitMessage=");
		builder.append(exitMessage);
		builder.append("]");
		return builder.toString();
	}
	
	
}
