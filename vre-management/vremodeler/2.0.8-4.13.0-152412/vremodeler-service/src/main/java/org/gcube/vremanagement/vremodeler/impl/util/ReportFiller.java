package org.gcube.vremanagement.vremodeler.impl.util;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.vremodeler.impl.peristentobjects.FunctionalityPersisted;
import org.gcube.vremanagement.vremodeler.impl.peristentobjects.VreFunctionalityRelation;
import org.gcube.vremanagement.vremodeler.utils.reports.DeployReport;
import org.gcube.vremanagement.vremodeler.utils.reports.FunctionalityDeployingReport;
import org.gcube.vremanagement.vremodeler.utils.reports.FunctionalityReport;
import org.gcube.vremanagement.vremodeler.utils.reports.Resource;
import org.gcube.vremanagement.vremodeler.utils.reports.ResourceDeployingReport;
import org.gcube.vremanagement.vremodeler.utils.reports.ServiceReport;
import org.gcube.vremanagement.vremodeler.utils.reports.Status;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.j256.ormlite.dao.Dao;

public class ReportFiller {

	public static GCUBELog logger= new GCUBELog(ReportFiller.class);
	
	/**
	 * 
	 * @param report
	 * @param vreResourceId
	 * @param functionalityDao 
	 * @param vreFunctionalityDao 
	 * @throws Exception
	 */
	public static void initializeFunctionalityForReport(FunctionalityDeployingReport report, String vreResourceId, Dao<VreFunctionalityRelation, String> vreFunctionalityDao, Dao<FunctionalityPersisted, Integer> functionalityDao) throws Exception{
		report.setStatus(Status.Running);
		List<FunctionalityPersisted> functs = Util.getSelectedFunctionality(vreFunctionalityDao, functionalityDao, vreResourceId);
		for (FunctionalityPersisted functionality: functs){
			logger.trace("adding report for functionality "+functionality.getId());
			FunctionalityReport funcReport= new FunctionalityReport();
			funcReport.setFunctionalityId(functionality.getId());
			funcReport.setFunctionalityName(functionality.getName());
			funcReport.setStatus(Status.Running);
			
			List<ServiceReport> listService = new ArrayList<ServiceReport>();			
			for (ServicePair servicePair: functionality.getServices()){
				ServiceReport servReport= new ServiceReport();
				servReport.setServiceName(servicePair.getServiceName());
				servReport.setServiceClass(servicePair.getServiceClass());
				servReport.setServiceVersion("1.0.0");
				servReport.setStatus(Status.Running);
				listService.add(servReport);
			}
			report.getFunctionalityTable().put(funcReport, listService);
		}
	}
	
	/**
	 * 
	 * @param report
	 * @param vreResourceId
	 * @throws Exception
	 */
	public static void initializeResourcesForReport(ResourceDeployingReport report) throws Exception{
		report.setStatus(Status.Running);
	}
	
	public static void addResourceToReport(String resourceId, String resourceType, ResourceDeployingReport report){
		report.getResources().add(new Resource(resourceId, resourceType));
	}
	
	/**
	 * 
	 * @param report
	 */
	public static void reportElaboration(DeployReport report){
		logger.trace("Elaborationg report");
		String rmReport = report.getFunctionalityDeployingReport().getResourceManagerReport();
		if (rmReport==null) return;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		try{
			db = dbf.newDocumentBuilder();
			Document document = db.parse(new ByteArrayInputStream(rmReport.getBytes()));
			XPath xpath= XPathFactory.newInstance().newXPath();
			//first step: services retrieving
			for (Entry<FunctionalityReport,List<ServiceReport>> entry: report.getFunctionalityDeployingReport().getFunctionalityTable().entrySet()){
				Status generalFunctState=Status.Finished;
				for (ServiceReport serviceReport: entry.getValue()){
					if (serviceReport.getStatus()==Status.Finished || serviceReport.getStatus()==Status.Failed) continue;
					NodeList nResoulution = (NodeList)xpath.evaluate("/ResourceReport/Services/Service/DeploymentActivity/GHN/LastReportReceived/Packages/Package[/ServiceClass/text()='"+serviceReport.getServiceClass()+"' and /ServiceName/text()='"+serviceReport.getServiceName()+"' and /ServiceVersion/text()='"+serviceReport.getServiceVersion()+"']/Status",document,XPathConstants.NODESET);
					boolean isServiceDeployed=true;
					boolean isServiceFailed=false;
					for (int i = 0; i < nResoulution.getLength(); i++) {
						
						logger.trace("retrieved package for service "+serviceReport.getServiceName());
						
						if(nResoulution.item(i).getFirstChild().getNodeValue().compareTo("FAILED")==0){
							isServiceFailed=true;
							break;
						}else if(!(nResoulution.item(i).getFirstChild().getNodeValue().compareTo("RUNNING")==0 || nResoulution.item(i).getFirstChild().getNodeValue().compareTo("ACTIVATED")==0)){
							isServiceDeployed=false;
						}  
					}
					
					if (isServiceFailed){
						serviceReport.setStatus(Status.Failed);
						generalFunctState= Status.Failed;
					}
					if (isServiceDeployed) serviceReport.setStatus(Status.Finished);
					else generalFunctState= Status.Running;
				}
				entry.getKey().setStatus(generalFunctState);
			}
			
			logger.trace("second step : resources retrieving");
			
			//second step: resources retrieving
			//State generalResourceState=State.Finished;
			for (Resource resource:report.getResourceDeployingReport().getResources()){
				if (resource.getStatus()==Status.Finished || resource.getStatus()==Status.Failed) continue;
				logger.trace("checking resource with id "+resource.getResourceId());
				NodeList nResoulution = (NodeList)xpath.evaluate("/ResourceReport/Resources//Resource[./ID/text()='"+resource.getResourceId()+"']/Status",document,XPathConstants.NODESET); 
				logger.trace("found "+nResoulution.getLength());
				if (nResoulution.getLength()>0){
					logger.trace("found "+nResoulution.item(0).getFirstChild().getNodeValue());
					if(nResoulution.item(0).getFirstChild().getNodeValue().compareTo("FAILED")==0)
						resource.setStatus(Status.Failed);
					else resource.setStatus(Status.Finished);
				}
			}
		}catch (Exception e) {
			logger.warn("cannot fill report",e);
		}
		
		
	}
	
}
