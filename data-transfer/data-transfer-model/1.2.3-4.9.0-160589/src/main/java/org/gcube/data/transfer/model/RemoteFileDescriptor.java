package org.gcube.data.transfer.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@EqualsAndHashCode
public class RemoteFileDescriptor {

	private String filename;
	private String path;
	private String absolutePath;
	private String persistenceId;
	private boolean isDirectory;
	private long creationDate;
	private long lastUpdate;	
	private long size;
	
}
