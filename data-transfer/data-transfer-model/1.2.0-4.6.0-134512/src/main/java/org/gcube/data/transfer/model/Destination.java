package org.gcube.data.transfer.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class Destination {

	public static final String DEFAULT_PERSISTENCE_ID="DEFAULT";
	
	
	@XmlElement
	private String persistenceId=DEFAULT_PERSISTENCE_ID;
	@XmlElement
	private String subFolder=null;
	@NonNull
	@XmlElement
	private String destinationFileName=null;
	@XmlElement
	private Boolean createSubfolders=false;
	@XmlElement
	private DestinationClashPolicy onExistingFileName=DestinationClashPolicy.ADD_SUFFIX;
	@XmlElement
	private DestinationClashPolicy onExistingSubFolder=DestinationClashPolicy.APPEND;
	
	public Destination(String destinationFileName) {
		super();
		this.destinationFileName = destinationFileName;
	}

	public Destination(String subFolder, String destinationFileName) {
		super();
		this.subFolder = subFolder;
		this.destinationFileName = destinationFileName;
		this.createSubfolders=true;
	}

	
}
