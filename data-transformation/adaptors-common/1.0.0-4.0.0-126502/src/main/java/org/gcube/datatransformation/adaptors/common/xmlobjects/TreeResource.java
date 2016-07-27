package org.gcube.datatransformation.adaptors.common.xmlobjects;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.datatransformation.adaptors.common.constants.ConstantNames;
import org.gcube.rest.commons.resourceawareservice.resources.StatefulResource;
import org.gcube.rest.commons.resourceawareservice.resources.exceptions.StatefulResourceException;

@XmlRootElement 
@XmlAccessorType(XmlAccessType.FIELD)
public class TreeResource extends StatefulResource{

	private static final long serialVersionUID = 1L;
	
	
//	@XmlElement(name="host")
//	String host;
//	public String getHost(){
//		return host;
//	}
//	public void setHost(String host){
//		this.host = host;
//	}
	
	@XmlElement(name="sourcetype")
	String sourcetype;
	
	@XmlElement(name="treeID")
	List<String> treeIDs;
	
	public TreeResource(){
		sourcetype = ConstantNames.TREESOURCETYPE;
		treeIDs = new ArrayList<String>();
	}
	
	public String getSourceType(){
		return sourcetype;
	}
	
	public List<String> getTreeIDs() {
		return treeIDs;
	}

	public void addTreeID(String treeID) {
		this.treeIDs.add(treeID);
	}
	
	public void removeTreeID(String treeID) {
		this.treeIDs.remove(treeID);
	}
	

	@Override
	public void onLoad() throws StatefulResourceException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClose() throws StatefulResourceException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDestroy() throws StatefulResourceException {
		// TODO Auto-generated method stub
		
	}

}
