package org.gcube.vremanagement.softwaregateway.answer.xml;


import java.util.List;

import org.gcube.vremanagement.softwaregateway.answer.ReportObject;
import org.gcube.vremanagement.softwaregateway.impl.coordinates.GCubeCoordinates;



/**
 *  Model for Xml answer
 * @author Roberto Cirillo (ISTI - CNR)
 *
 */
public abstract class XMLModel {

	StringBuilder xml;
	
	public XMLModel(){
		xml=new StringBuilder();
	}
	
	public abstract void addHeader();
	
	public abstract void addFooter();
	
	public abstract void addDependencies(List<GCubeCoordinates> resolved, List<GCubeCoordinates> missing);
	
	public abstract void addPlugin(List <GCubeCoordinates> plugin);
	
	public abstract void addPackages(List <GCubeCoordinates> plugin);
	
	public String getXml(){
		return xml.toString();
	}

	public void addReportPackage(ReportObject obj) {
		// TODO Auto-generated method stub
		
	}

}
