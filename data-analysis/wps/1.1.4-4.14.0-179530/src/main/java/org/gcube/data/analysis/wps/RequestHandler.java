package org.gcube.data.analysis.wps;
/**
 * ﻿Copyright (C) 2007 - 2014 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *       • Apache License, version 2.0
 *       • Apache Software License, version 1.0
 *       • GNU Lesser General Public License, version 3
 *       • Mozilla Public License, versions 1.0, 1.1 and 2.0
 *       • Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * License version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
org.gcube.dataanalysis.wps.statisticalmanager.synchserver.weberver.handler;
*/

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RejectedExecutionException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.commons.io.IOUtils;
import org.gcube.common.authorization.library.AuthorizedTasks;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mapping.EnvironmentVariableManager;
import org.gcube.smartgears.utils.InnerMethodName;
import org.n52.wps.server.ExceptionReport;
import org.n52.wps.server.WebProcessingService;
import org.n52.wps.server.handler.RequestExecutor;
import org.n52.wps.server.request.CapabilitiesRequest;
import org.n52.wps.server.request.DescribeProcessRequest;
import org.n52.wps.server.request.Request;
import org.n52.wps.server.request.RetrieveResultRequest;
import org.n52.wps.server.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;


public class RequestHandler {
	
    public static final String VERSION_ATTRIBUTE_NAME = "version";

	/** Computation timeout in seconds */
	protected static RequestExecutor pool = new RequestExecutor();

	protected OutputStream os;

	private static Logger LOGGER = LoggerFactory.getLogger(RequestHandler.class);
	
	protected String responseMimeType;
	
	protected Request req;
	
	private EnvironmentVariableManager env;
	
	// Empty constructor due to classes which extend the RequestHandler
	protected RequestHandler() {
		
	}
	private Map<String, String[]> params;
	/**
	 * Handles requests of type HTTP_GET (currently capabilities and
	 * describeProcess). A Map is used to represent the client input.
	 * 
	 * @param params
	 *            The client input
	 * @param os
	 *            The OutputStream to write the response to.
	 * @throws ExceptionReport
	 *             If the requested operation is not supported
	 */
	public RequestHandler(Map<String, String[]> params, OutputStream os, EnvironmentVariableManager env)
			throws ExceptionReport {
		this.os = os;
		this.params=params;
		this.env = env;
		//sleepingTime is 0, by default.
		/*if(WPSConfiguration.getInstance().exists(PROPERTY_NAME_COMPUTATION_TIMEOUT)) {
			this.sleepingTime = Integer.parseInt(WPSConfiguration.getInstance().getProperty(PROPERTY_NAME_COMPUTATION_TIMEOUT));
		}
		String sleepTime = WPSConfig.getInstance().getWPSConfig().getServer().getComputationTimeoutMilliSeconds();
		*/
		
		
		Request req;
		CaseInsensitiveMap ciMap = new CaseInsensitiveMap(params);
		
		/*
		 * check if service parameter is present and equals "WPS"
		 * otherwise an ExceptionReport will be thrown
		 */
		String serviceType = Request.getMapValue("service", ciMap, true);
		
		if(!serviceType.equalsIgnoreCase("WPS")){
			throw new ExceptionReport("Parameter <service> is not correct, expected: WPS, got: " + serviceType, 
					ExceptionReport.INVALID_PARAMETER_VALUE, "service");
		}

		/*
		 * check language. if not supported, return ExceptionReport
		 * Fix for https://bugzilla.52north.org/show_bug.cgi?id=905
		 */
		String language = Request.getMapValue("language", ciMap, false);
		
		if(language != null){
			Request.checkLanguageSupported(language);
		}

		// get the request type
		String requestType = Request.getMapValue("request", ciMap, true);
		
		if (requestType.equalsIgnoreCase("GetCapabilities")) {
			req = new CapabilitiesRequest(ciMap);
			InnerMethodName.instance.set("GetCapabilities");
		} 
		else if (requestType.equalsIgnoreCase("DescribeProcess")) {
			req = new DescribeProcessRequest(ciMap);
			InnerMethodName.instance.set("DescribeProcess");
		}
		else if (requestType.equalsIgnoreCase("Execute")) {
			req = new ExecuteRequest(ciMap, this.env);
			setResponseMimeType((ExecuteRequest)req);
			InnerMethodName.instance.set("Execute");
		} 
		else if (requestType.equalsIgnoreCase("RetrieveResult")) {
			req = new RetrieveResultRequest(ciMap);
			InnerMethodName.instance.set("RetrieveResult");
		} 
		else {
			throw new ExceptionReport(
					"The requested Operation is not supported or not applicable to the specification: "
							+ requestType,
					ExceptionReport.OPERATION_NOT_SUPPORTED, requestType);
		}

		this.req = req;
	}

	/**
	 * Handles requests of type HTTP_POST (currently executeProcess). A Document
	 * is used to represent the client input. This Document must first be parsed
	 * from an InputStream.
	 * 
	 * @param is
	 *            The client input
	 * @param os
	 *            The OutputStream to write the response to.
	 * @throws ExceptionReport
	 */
	public RequestHandler(InputStream is, OutputStream os, EnvironmentVariableManager env)
			throws ExceptionReport {
		String nodeName, localName, nodeURI, version = null;
		Document doc;
		this.os = os;
		this.env= env;
		
		boolean isCapabilitiesNode = false;
		
		try {
			LOGGER.trace("Parsing Document...");
			//System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
		
			DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
			fac.setNamespaceAware(true);

			// parse the InputStream to create a Document
			doc = fac.newDocumentBuilder().parse(is);
			
			LOGGER.trace("Document Parsing OK");
			// Get the first non-comment child.
			Node child = doc.getFirstChild();
			while(child.getNodeName().compareTo("#comment")==0) {
				child = child.getNextSibling();
			}
			LOGGER.trace("Skipped comments OK");
			nodeName = child.getNodeName();
			localName = child.getLocalName();
			nodeURI = child.getNamespaceURI();
			Node versionNode = child.getAttributes().getNamedItem("version");
			LOGGER.trace("Version OK");
			/*
			 * check for service parameter. this has to be present for all requests
			 */
			Node serviceNode = child.getAttributes().getNamedItem("service");
			
			if(serviceNode == null){
				throw new ExceptionReport("Parameter <service> not specified.", ExceptionReport.MISSING_PARAMETER_VALUE, "service");
			}else{
				if(!serviceNode.getNodeValue().equalsIgnoreCase("WPS")){
					throw new ExceptionReport("Parameter <service> not specified.", ExceptionReport.INVALID_PARAMETER_VALUE, "service");
				}
			}
			LOGGER.trace("Service Node OK");
			
            isCapabilitiesNode = nodeName.toLowerCase().contains("capabilities");
			if(versionNode == null && !isCapabilitiesNode) {
				throw new ExceptionReport("Parameter <version> not specified.", ExceptionReport.MISSING_PARAMETER_VALUE, "version");
			}
			if(!isCapabilitiesNode){
//				version = child.getFirstChild().getTextContent();//.getNextSibling().getFirstChild().getNextSibling().getFirstChild().getNodeValue();
				version = child.getAttributes().getNamedItem("version").getNodeValue();
			}
			
			LOGGER.trace("Capabilities Node OK");
			/*
			 * check language, if not supported, return ExceptionReport
			 * Fix for https://bugzilla.52north.org/show_bug.cgi?id=905
			 */
			Node languageNode = child.getAttributes().getNamedItem("language");
			if(languageNode != null){
				String language = languageNode.getNodeValue();
				Request.checkLanguageSupported(language);
			}
			
			LOGGER.trace("Language Node OK "+languageNode);
			
		} catch (SAXException e) {
			throw new ExceptionReport(
					"There went something wrong with parsing the POST data: "
							+ e.getMessage(),
					ExceptionReport.NO_APPLICABLE_CODE, e);
		} catch (IOException e) {
			throw new ExceptionReport(
					"There went something wrong with the network connection.",
					ExceptionReport.NO_APPLICABLE_CODE, e);
		} catch (ParserConfigurationException e) {
			throw new ExceptionReport(
					"There is a internal parser configuration error",
					ExceptionReport.NO_APPLICABLE_CODE, e);
		}
		//Fix for Bug 904 https://bugzilla.52north.org/show_bug.cgi?id=904
		if(!isCapabilitiesNode && version == null) {
			LOGGER.error("EXCEPTION: Parameter <version> not specified." + ExceptionReport.MISSING_PARAMETER_VALUE + " version");
			throw new ExceptionReport("Parameter <version> not specified." , ExceptionReport.MISSING_PARAMETER_VALUE, "version");
		}
		if(!isCapabilitiesNode && !version.equals(Request.SUPPORTED_VERSION)) {
			LOGGER.error("EXCEPTION: Version not supported." + ExceptionReport.INVALID_PARAMETER_VALUE + "version");
			throw new ExceptionReport("Version not supported." , ExceptionReport.INVALID_PARAMETER_VALUE, "version");
		}
		// get the request type
		if (nodeURI.equals(WebProcessingService.WPS_NAMESPACE) && localName.equals("Execute")) {
			LOGGER.debug("Detected Request to Execute!");
			req = new ExecuteRequest(doc, this.env);
			setResponseMimeType((ExecuteRequest)req);
			InnerMethodName.instance.set("Execute");
			LOGGER.debug("Request to Execute Configured!");
		}else if (nodeURI.equals(WebProcessingService.WPS_NAMESPACE) && localName.equals("GetCapabilities")){
			LOGGER.debug("Detected GetCapabilities!");
			req = new CapabilitiesRequest(doc);
			InnerMethodName.instance.set("GetCapabilities");
			this.responseMimeType = "text/xml";
		} else if (nodeURI.equals(WebProcessingService.WPS_NAMESPACE) && localName.equals("DescribeProcess")) {
			LOGGER.debug("Detected DescribeProcess!");
			req = new DescribeProcessRequest(doc);
			InnerMethodName.instance.set("DescribeProcess");
			this.responseMimeType = "text/xml";
			
		}  else if(!localName.equals("Execute")){
			LOGGER.error("EXCEPTION Detected NON-supported Request "+"The requested Operation not supported or not applicable to the specification: "+ nodeName + ExceptionReport.OPERATION_NOT_SUPPORTED + localName);
			throw new ExceptionReport("The requested Operation not supported or not applicable to the specification: "
					+ nodeName, ExceptionReport.OPERATION_NOT_SUPPORTED, localName);
		}
		else if(nodeURI.equals(WebProcessingService.WPS_NAMESPACE)) {
			LOGGER.error("specified namespace is not supported: "+ nodeURI + ExceptionReport.INVALID_PARAMETER_VALUE);
			throw new ExceptionReport("specified namespace is not supported: "
					+ nodeURI, ExceptionReport.INVALID_PARAMETER_VALUE);
		}
	}

	/**
	 * Handle a request after its type is determined. The request is scheduled
	 * for execution. If the server has enough free resources, the client will
	 * be served immediately. If time runs out, the client will be asked to come
	 * back later with a reference to the result.
	 * 
	 * @param req The request of the client.
	 * @throws ExceptionReport
	 */
	public void handle() throws ExceptionReport {
		Response resp = null;
		if(req ==null){
			throw new ExceptionReport("Internal Error","");
		}
		if (req instanceof ExecuteRequest) {
			LOGGER.debug("Request for execution");
			// cast the request to an executerequest
			ExecuteRequest execReq = (ExecuteRequest) req;
			LOGGER.debug("Accepted request for execution");
			execReq.updateStatusAccepted();
			//modification by GP 26-05-2015 to account for multi user and scopes 
			Callable<Response> execCallable = AuthorizedTasks.bind(execReq);
			ExceptionReport exceptionReport = null;
			try {
				if (execReq.isStoreResponse()) {
					LOGGER.debug("Execution with output storing");
					resp = new ExecuteResponse(execReq);
					InputStream is = resp.getAsStream();
					IOUtils.copy(is, os);
					is.close();
//                    pool.submit(execReq);
				    pool.submit(execCallable);
					return;
				}
				try {
					LOGGER.debug("Execution without storing output");
					// retrieve status with timeout enabled
					try {
						resp = pool.submit(execCallable).get();
					}
					catch (ExecutionException ee) {
						LOGGER.warn("exception while handling ExecuteRequest.",ee);
						// the computation threw an error
						// probably the client input is not valid
						if (ee.getCause() instanceof ExceptionReport) {
							exceptionReport = (ExceptionReport) ee
									.getCause();
						} else {
							exceptionReport = new ExceptionReport(
									"An error occurred in the computation: "
											+ ee.getMessage(),
									ExceptionReport.NO_APPLICABLE_CODE);
						}
					} catch (InterruptedException ie) {
						LOGGER.warn("interrupted while handling ExecuteRequest.",ie);
						
						// interrupted while waiting in the queue
						exceptionReport = new ExceptionReport(
								"The computation in the process was interrupted.",
								ExceptionReport.NO_APPLICABLE_CODE);
					}
				} finally {
					if (exceptionReport != null) {
						LOGGER.warn("ExceptionReport not null",exceptionReport);
						// NOT SURE, if this exceptionReport is also written to the DB, if required... test please!
						throw exceptionReport;
					}
					// send the result to the outputstream of the client.
				/*	if(((ExecuteRequest) req).isQuickStatus()) {
						resp = new ExecuteResponse(execReq);
					}*/
					else if(resp == null) {
						LOGGER.warn("null response handling ExecuteRequest.");
						throw new ExceptionReport("Problem with handling threads in RequestHandler", ExceptionReport.NO_APPLICABLE_CODE);
					}
					if(!execReq.isStoreResponse()) {
						InputStream is = resp.getAsStream();
						IOUtils.copy(is, os);
						is.close();
						LOGGER.info("Served ExecuteRequest.");
					}
				}
			} catch (RejectedExecutionException ree) {
                LOGGER.warn("exception handling ExecuteRequest.", ree);
  			// server too busy?
				throw new ExceptionReport(
						"The requested process was rejected. Maybe the server is flooded with requests.",
						ExceptionReport.SERVER_BUSY);
			} catch (Exception e) {
                LOGGER.error("exception handling ExecuteRequest.", e);
                if (e instanceof ExceptionReport) {
                    throw (ExceptionReport)e;
                }
                throw new ExceptionReport("Could not read from response stream.", ExceptionReport.NO_APPLICABLE_CODE);
			}
		} else {
			// for GetCapabilities and DescribeProcess:
			 
			resp = req.call();
			
			try {
				InputStream is = null;
				if (req instanceof CapabilitiesRequest){
					GetCapabilitiesBuilder builder = new GetCapabilitiesBuilder();
					String getCapabilitiesStringFromInfra = "";
					try {
						getCapabilitiesStringFromInfra = builder.buildGetCapabilities(params, env);
					} catch (Exception e) {
						throw new ExceptionReport("Error in building GetCapabilities","getcapabilities",e);
					}
					is = IOUtils.toInputStream(getCapabilitiesStringFromInfra, "UTF-8");
				}
				else
					is = resp.getAsStream();

				IOUtils.copy(is, os);
				is.close();
			} catch (IOException e) {
				throw new ExceptionReport("Could not read from response stream.", ExceptionReport.NO_APPLICABLE_CODE);
			}
			
		}
	}
	
	protected void setResponseMimeType(ExecuteRequest req) {
		if(req.isRawData()){
			responseMimeType = req.getExecuteResponseBuilder().getMimeType();
		}else{
			responseMimeType = "text/xml";
		}
		
		
	}
	
	

	public String getResponseMimeType(){
		if(responseMimeType == null){
			return "text/xml";
		}
		return responseMimeType.toLowerCase();
	}
	
	
}



