package org.gcube.vremanagement.softwaregateway.answer;

import java.util.List;

import org.gcube.common.core.resources.GCUBEService;
import org.gcube.vremanagement.softwaregateway.answer.xml.XMLBuilder;
import org.gcube.vremanagement.softwaregateway.impl.coordinates.GCubeCoordinates;


/**
 * Class that build a String response
 * @author Roberto Cirillo (ISTI - CNR)
 *
 */
public class AnswerBuild {
	
	private XMLBuilder xmlBuilder;

	
	/**
	 * 
	 */
	public AnswerBuild(){
		
	}
	
	/**
	 * Build  answer that contains a list of dependencies: solved and missing
	 * @param resolved
	 * @param missing
	 */
	public void buildDependencies(List<GCubeCoordinates> resolved, List<GCubeCoordinates> missing){
	}
	
	/**
	 * build answer that contains a list of packages 
	 * @param packageList
	 */
	public void buildPackages(List<GCubeCoordinates> packageList){
	}
	
	/**
	 * Build answer that contains a list of package
	 * @param packageList
	 */
	public void buildPlugin(List<GCubeCoordinates> packageList){
	}

	/**
	 * build a xml answer
	 * 
	 * @param dependenciesResolved: list of resolved dependencies
	 * @param dependenciesMissing: list of missing dependencies
	 * @param pluginList: list of packages 
	 * @param packageList list of packages
	 * @return xml string
	 */
	public String constructAnswer(List<GCubeCoordinates> dependenciesResolved, List<GCubeCoordinates> dependenciesMissing, List<GCubeCoordinates> pluginList, List<GCubeCoordinates> packageList ){
		String xmlResult=null;
		XMLBuilder builder=new XMLBuilder();
		builder.addDependencies(dependenciesResolved, dependenciesMissing);
		xmlResult=builder.getXml();
		return xmlResult;
	}
	
	public  String constructReportAnswer(List<ReportObject> reportEntryList){
		XMLBuilder builder= new XMLBuilder();
		builder.addHeader();
		for(ReportObject obj : reportEntryList){
			builder.addReportPackage(obj);
		}
		builder.addFooter();
		return builder.getXml();
	}
	
	
}
