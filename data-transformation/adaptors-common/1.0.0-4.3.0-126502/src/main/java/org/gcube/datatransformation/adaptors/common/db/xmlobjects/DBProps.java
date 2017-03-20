package org.gcube.datatransformation.adaptors.common.db.xmlobjects;

import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.rest.commons.resourceawareservice.resources.StatefulResource;
import org.gcube.rest.commons.resourceawareservice.resources.exceptions.StatefulResourceException;


/**
 * Configuration XML file for the Harvester. It should contain  
 * @author nikolas
 *
 */
@XmlRootElement(name = "DBProps")
public class DBProps extends StatefulResource{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6381493133696594843L;

	

	@XmlElement(name="sourcetype")
	String sourcetype;
	@XmlElement(name="sourcename")
	String sourcename;
	@XmlElement(name="propsname")
	String propsname;
	@XmlElement(name="table")
	ArrayList<Table> table;
	@XmlElement(name="edge")
	ArrayList<Edge> edge;
	
	

	public String getSourceType(){
		return sourcetype;
	}
	
	public String getSourceName(){
		return sourcename;
	}
	
	public String getPropsName(){
		return propsname;
	}
	
	public ArrayList<Table> getTables(){
		return table;
	}
	
	public ArrayList<Edge> getEdges(){
		return edge;
	}
	
	

	public void setSourcename(String sourcename) {
		this.sourcename = sourcename;
	}

	public void setPropsname(String propsname) {
		this.propsname = propsname;
	}

	public void setTable(ArrayList<Table> table) {
		this.table = table;
	}

	public void setEdge(ArrayList<Edge> edge) {
		this.edge = edge;
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

