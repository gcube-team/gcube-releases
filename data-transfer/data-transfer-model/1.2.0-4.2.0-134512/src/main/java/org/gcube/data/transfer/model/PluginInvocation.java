package org.gcube.data.transfer.model;

import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class PluginInvocation {

	
	public static final String DESTINATION_FILE_PATH="*******USE_DESTINATION_FILE_PATH*********"; 
	
	@XmlID
	private String pluginId;
	@XmlElement
	private Map<String,String> parameters; 
}
