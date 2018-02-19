package org.gcube.data.access.queueManager.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.axis.components.uuid.UUIDGen;
import org.apache.axis.components.uuid.UUIDGenFactory;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;


@XStreamAlias("RequestItem")
public class RequestItem implements QueueItem{

	@XStreamAsAttribute
	private String id;	
	private String toCallScript;
	@XStreamImplicit(itemFieldName="inputFile")
	private List<String> inputFiles;
	private Map<String,Serializable> parameters;
	
	private static UUIDGen uuidFactory= UUIDGenFactory.getUUIDGen();
	
	public RequestItem(String toCallScript, List<String> inputFiles,
			Map<String, Serializable> parameters) {
		super();
		this.id = uuidFactory.nextUUID();
		this.toCallScript = toCallScript;
		this.inputFiles = inputFiles;
		this.parameters = parameters;
	}
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the toCallScript
	 */
	public String getToCallScript() {
		return toCallScript;
	}
	/**
	 * @param toCallScript the toCallScript to set
	 */
	public void setToCallScript(String toCallScript) {
		this.toCallScript = toCallScript;
	}
	/**
	 * @return the inputFiles
	 */
	public List<String> getInputFiles() {
		return inputFiles;
	}
	/**
	 * @param inputFiles the inputFiles to set
	 */
	public void setInputFiles(List<String> inputFiles) {
		this.inputFiles = inputFiles;
	}
	/**
	 * @return the parameters
	 */
	public Map<String, Serializable> getParameters() {
		return parameters;
	}
	/**
	 * @param parameters the parameters to set
	 */
	public void setParameters(Map<String, Serializable> parameters) {
		this.parameters = parameters;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RequestItem [id=");
		builder.append(id);
		builder.append(", toCallScript=");
		builder.append(toCallScript);
		builder.append(", inputFiles=");
		builder.append(inputFiles);
		builder.append(", parameters=");
		builder.append(parameters);
		builder.append("]");
		return builder.toString();
	}
	
		
	
}
