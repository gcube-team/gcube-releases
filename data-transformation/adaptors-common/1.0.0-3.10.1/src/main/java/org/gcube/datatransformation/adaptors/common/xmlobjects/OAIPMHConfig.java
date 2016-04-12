package org.gcube.datatransformation.adaptors.common.xmlobjects;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.datatransformation.adaptors.common.constants.ConstantNames;
import org.gcube.rest.commons.resourceawareservice.resources.StatefulResource;
import org.gcube.rest.commons.resourceawareservice.resources.exceptions.StatefulResourceException;


/**
 * Configuration XML file for the oai-pmh harvester.
 * @author nikolas
 *
 */

@XmlRootElement(name = "OAIPMHConfig")
public class OAIPMHConfig extends StatefulResource{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2906088201899205462L;
	
	
//	@XmlElement(name="host")
//	String host;
	
	@XmlElement(name="harvestBasePathURL")
	List<String> harvestBasePathURL;
	
	@XmlElement(name="sourcetype")
	String sourcetype;
	
//	public String getHost(){
//		return host;
//	}
//	
//	public void setHost(String host){
//		this.host = host;
//	}
	
	public OAIPMHConfig(){
		sourcetype = ConstantNames.OAIPMHSOURCETYPE;
		harvestBasePathURL = new ArrayList<String>();
	}
	
	
	public List<String> getHarvestBasePathURL() {
		return harvestBasePathURL;
	}
	
	public void addBasePathURL(String basePathURL) {
		this.harvestBasePathURL.add(basePathURL);
	}
	
	public void removeBasePathURL(String basePathURL) {
		this.harvestBasePathURL.remove(basePathURL);
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







