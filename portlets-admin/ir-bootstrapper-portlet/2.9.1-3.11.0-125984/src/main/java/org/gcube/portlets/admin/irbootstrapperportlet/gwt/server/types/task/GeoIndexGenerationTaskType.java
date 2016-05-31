///**
// * 
// */
//package org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.task;
//
//import java.net.SocketException;
//import java.util.ArrayList;
//import java.util.LinkedList;
//import java.util.List;
//
//import org.apache.axis.message.addressing.Address;
//import org.apache.axis.message.addressing.EndpointReferenceType;
//import org.gcube.common.core.faults.GCUBEUnrecoverableException;
//import org.gcube.common.core.utils.handlers.GCUBEServiceClientImpl;
//import org.gcube.common.core.utils.handlers.GCUBEServiceHandler;
//import org.gcube.datatransformation.datatransformationservice.stubs.ContentType;
//import org.gcube.datatransformation.datatransformationservice.stubs.DataTransformationServicePortType;
//import org.gcube.datatransformation.datatransformationservice.stubs.Input;
//import org.gcube.datatransformation.datatransformationservice.stubs.Output;
//import org.gcube.datatransformation.datatransformationservice.stubs.Parameter;
//import org.gcube.datatransformation.datatransformationservice.stubs.TransformDataWithTransformationUnit;
//import org.gcube.datatransformation.datatransformationservice.stubs.service.DataTransformationServiceAddressingLocator;
//import org.gcube.indexmanagement.geoindexlookup.stubs.GeoIndexLookupFactoryPortType;
//import org.gcube.indexmanagement.geoindexlookup.stubs.service.GeoIndexLookupFactoryServiceAddressingLocator;
//import org.gcube.indexmanagement.geoindexmanagement.stubs.GeoIndexManagementFactoryPortType;
//import org.gcube.indexmanagement.geoindexmanagement.stubs.service.GeoIndexManagementFactoryServiceAddressingLocator;
//import org.gcube.indexmanagement.geoindexupdater.stubs.GeoIndexUpdaterFactoryPortType;
//import org.gcube.indexmanagement.geoindexupdater.stubs.GeoIndexUpdaterPortType;
//import org.gcube.indexmanagement.geoindexupdater.stubs.Process;
//import org.gcube.indexmanagement.geoindexupdater.stubs.service.GeoIndexUpdaterFactoryServiceAddressingLocator;
//import org.gcube.indexmanagement.geoindexupdater.stubs.service.GeoIndexUpdaterServiceAddressingLocator;
//import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.*;
//import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.logging.TaskExecutionLogger;
//import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.resourceManagement.GeoIndexManagementWSResource;
//import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.resourceManagement.ResourceExpression;
//import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.resourceManagement.ResourceManager;
//import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.resourceManagement.RunningInstanceResource;
//import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.data.GeoIndexDataType;
//import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.data.MetadataCollectionDataType;
//import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.util.EntityParsingUtil;
//import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.util.PortTypeUtil;
//import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.util.TaskExecutionData;
//import org.w3c.dom.Document;
//
///**
// * @author Spyros Boutsis, NKUA
// *
// */
//public class GeoIndexGenerationTaskType extends CustomTaskType {
//
//	/**
//	 * A service handler to interact with the Metadata Broker in terms of transforming a given
//	 * metadata collection to geo rowsets.
//	 * @author Spyros Boutsis, NKUA
//	 */
//	private class DTSServiceHandler extends GCUBEServiceHandler<GCUBEServiceClientImpl> {
//
//		private TaskExecutionData execData;
//		private String sourceToEsXSLTID;
//		private String esToGeoXSLTID;
//		private String indexTypeID;
//		private String inputColID;
//		private String outRSEPR;
//
//		/**
//		 * Class constructor 
//		 */
//		public DTSServiceHandler(TaskExecutionData execData, String sourceToEsXSLTID, String esToGeoXSLTID, String indexTypeID, String inputColID) {
//			this.execData = execData;
//			this.sourceToEsXSLTID = sourceToEsXSLTID;
//			this.esToGeoXSLTID = esToGeoXSLTID;
//			this.indexTypeID = indexTypeID;
//			this.inputColID = inputColID;
//			this.outRSEPR = null;
//		}
//
//		/*
//		 * (non-Javadoc)
//		 * @see org.gcube.common.core.utils.handlers.GCUBEServiceHandler#getTargetPortTypeName()
//		 */
//		protected String getTargetPortTypeName() {
//			return "datatransformation/DataTransformationService";
//		}
//
//		/* (non-Javadoc)
//		 * @see org.gcube.common.core.utils.handlers.GCUBEServiceHandler#findInstances()
//		 */
//		@Override
//		protected List<EndpointReferenceType> findInstances() throws Exception {
//			List<EndpointReferenceType> eprs = new ArrayList<EndpointReferenceType>();
//
//			RunningInstanceResource riRes = new RunningInstanceResource(getScope());
//			riRes.setAttributeValue(RunningInstanceResource.ATTR_SERVICENAME, "DataTransformationService");
//			riRes.setAttributeValue(RunningInstanceResource.ATTR_STATUS, "ready");
//			for (RunningInstanceResource RI : ResourceManager.retrieveResourcesFromIS(riRes, false)) {
//				eprs.add(new EndpointReferenceType(new Address(RI.getAttributeValue(RunningInstanceResource.ATTR_ENDPOINT).get(0))));
//			}
//			return eprs;
//		}
//
//		/* (non-Javadoc)
//		 * @see org.gcube.common.core.utils.handlers.GCUBEServiceHandler#interact(org.apache.axis.message.addressing.EndpointReferenceType)
//		 */
//		@Override
//		protected void interact(EndpointReferenceType arg0) throws Exception {
//			TaskExecutionLogger logger = execData.getExecutionLogger();
//
//			/* We need to transform the contents of the input metadata collection to 
//			geo rowsets. The transformation program that will be used depends on whether
//			this is a direct ES->geo transformation, or a composite source->ES->geo 
//			transformation.
//			First of all construct the transformation descriptors (input, output and
//			additional parameters) */
//			TransformDataWithTransformationUnit request = new TransformDataWithTransformationUnit();
//
//			/* INPUT */
//			Input input = new Input();
//			input.setInputType("MCollection");
//			input.setInputValue(inputColID);
//			Input [] inputs = {input};
//			request.setInputs(inputs);
//
//			/* OUTPUT */
//			Output output = new Output();
//			output.setOutputType("RS2");
//			request.setOutput(output);
//
//			/* TARGET CONTENT TYPE */
//			ContentType targetContentType = new ContentType();
//			targetContentType.setMimeType("text/xml");
//			Parameter contentTypeParameter = new Parameter();
//			contentTypeParameter.setName("schemaURI");
//			contentTypeParameter.setValue("http://georowset.xsd");
//			Parameter [] contentTypeParameters = {contentTypeParameter};
//			targetContentType.setParameters(contentTypeParameters);
//			request.setTargetContentType(targetContentType);
//
//			/* TRANSFORMATION PROGRAM ID + PROGRAM PARAMETERS */
//			request.setTransformationUnitID("0");
//			request.setCreateReport(true);
//			if (sourceToEsXSLTID == null) {
//				request.setTPID("$GeoRowset_Transformer");
//				Parameter geoxsltParameter = new Parameter();
//				geoxsltParameter.setName("geoxslt");
//				geoxsltParameter.setValue(esToGeoXSLTID);
//				Parameter indexTypeParameter = new Parameter();
//				indexTypeParameter.setName("indexType");
//				indexTypeParameter.setValue(indexTypeID);
//				request.setTProgramUnboundParameters(new Parameter[] {geoxsltParameter, indexTypeParameter});
//			}
//			else {
//				request.setTPID("$XSLT_GeoRowset_Composite_Transformer");			
//				Parameter xsltParameter = new Parameter();
//				xsltParameter.setName("xslt");
//				xsltParameter.setValue(sourceToEsXSLTID);
//				Parameter geoxsltParameter = new Parameter();
//				geoxsltParameter.setName("geoxslt");
//				geoxsltParameter.setValue(esToGeoXSLTID);
//				Parameter indexTypeParameter = new Parameter();
//				indexTypeParameter.setName("indexType");
//				indexTypeParameter.setValue(indexTypeID);
//				request.setTProgramUnboundParameters(new Parameter[] {xsltParameter, geoxsltParameter, indexTypeParameter});
//			}
//
//			/* Create endpoint reference to the gDTS service */
//			DataTransformationServicePortType dts = null;
//			try {
//				dts = PortTypeUtil.getStubProxy(execData.getSession(), new DataTransformationServiceAddressingLocator().getDataTransformationServicePortTypePort(arg0));
//			} catch (Exception e) {
//				logger.error("Failed to get the gDTS service porttype", e);
//				throw new Exception();
//			}
//
//			/* Now invoke the gDTS in order to perform the actual transformation */
//			try {
//				outRSEPR = dts.transformDataWithTransformationUnit(request).getOutput();
//			} catch (Exception e) {
//				if (e.getCause() instanceof SocketException)
//					throw new Exception();
//				throw new GCUBEUnrecoverableException(e);
//			}
//
//		}
//
//		/**
//		 * Returns the ResultSet EPR of the transformation result
//		 * @return
//		 */
//		String getOutputRSEPR() {
//			return this.outRSEPR;
//		}
//	};
//
//	/**
//	 * A service handler to interact with the GeoIndexManagement service in order to create an index manager.
//	 * @author Spyros Boutsis, NKUA
//	 */
//	private class GeoIndexManagementServiceHandler extends GCUBEServiceHandler<GCUBEServiceClientImpl> {
//
//		private static final int MANAGER_POLLING_INTERVAL = 30000;	// 30 seconds
//
//		private TaskExecutionData execData;
//		private String indexTypeID;
//	//	private String collectionID;
//		private String indexID;
//		private String geoSystem;
//		private String unit;
//		private int numOfDecimals;
//		private String indexManagerID;
//
//		/**
//		 * Class constructor 
//		 */
//		public GeoIndexManagementServiceHandler(TaskExecutionData execData, String indexTypeID, String geoSystem, String unit, int numOfDecimals, String indexManagerID) {
//			this.execData = execData;
//			this.indexTypeID = indexTypeID;
//			this.indexID = null;
//			this.geoSystem = geoSystem;
//			this.unit = unit;
//			this.numOfDecimals = numOfDecimals;
//			this.indexManagerID = indexManagerID;
//		}
//
//		/*
//		 * (non-Javadoc)
//		 * @see org.gcube.common.core.utils.handlers.GCUBEServiceHandler#getTargetPortTypeName()
//		 */
//		protected String getTargetPortTypeName() {
//			return "GeoIndexManagement";
//		}
//
//		/* (non-Javadoc)
//		 * @see org.gcube.common.core.utils.handlers.GCUBEServiceHandler#findInstances()
//		 */
//		@Override
//		protected List<EndpointReferenceType> findInstances() throws Exception {
//			List<EndpointReferenceType> eprs = new ArrayList<EndpointReferenceType>();			
//			RunningInstanceResource riRes = new RunningInstanceResource(getScope());
//			riRes.setAttributeValue(RunningInstanceResource.ATTR_SERVICENAME, "GeoIndexManagement");
//			riRes.setAttributeValue(RunningInstanceResource.ATTR_STATUS, "ready");
//			for (RunningInstanceResource RI : ResourceManager.retrieveResourcesFromIS(riRes, false)) {
//				for (String endpoint : RI.getAttributeValue(RunningInstanceResource.ATTR_ENDPOINT)) {
//					if (endpoint.contains("GeoIndexManagementFactory"))
//						eprs.add(new EndpointReferenceType(new Address(endpoint)));
//				}
//			}
//			return eprs;
//		}
//
//		/* (non-Javadoc)
//		 * @see org.gcube.common.core.utils.handlers.GCUBEServiceHandler#interact(org.apache.axis.message.addressing.EndpointReferenceType)
//		 */
//		@Override
//		protected void interact(EndpointReferenceType arg0) throws Exception {
//			TaskExecutionLogger logger = execData.getExecutionLogger();
//
//			/* Get the FullTextIndexManagementFactory porttype */
//			GeoIndexManagementFactoryPortType geofpt = null;
//			try {
//				GeoIndexManagementFactoryServiceAddressingLocator loc = new GeoIndexManagementFactoryServiceAddressingLocator();
//				geofpt = PortTypeUtil.getStubProxy(execData.getSession(), loc.getGeoIndexManagementFactoryPortTypePort(arg0));
//			} catch (Exception e) {
//				logger.warn("Failed to get the GeoIndexManagementFactory service's porttype", e);
//				throw new Exception("Failed to get the GeoIndexManagementFactory service's porttype", e);
//			}
//
//			if (indexManagerID == null) {
//				/* Create the index management resource */
//				org.gcube.indexmanagement.geoindexmanagement.stubs.CreateResource createRequest = new org.gcube.indexmanagement.geoindexmanagement.stubs.CreateResource();
//				//createRequest.setCollectionID(new String[] { collectionID });
//				createRequest.setIndexTypeID(indexTypeID);
//				createRequest.setGeographicalSystem(geoSystem);
//				createRequest.setIndexID(null);
//				createRequest.setUnitOfMeasurement(unit);
//				createRequest.setNumberOfDecimals(numOfDecimals);
//				org.gcube.indexmanagement.geoindexmanagement.stubs.CreateResourceResponse createResponse = null;
//				try {
//					createResponse = geofpt.createResource(createRequest);
//				} catch (Exception e) {
//					if (e.getCause() instanceof SocketException)
//						throw new Exception();
//					throw new GCUBEUnrecoverableException(e);
//				}
//				indexID = createResponse.getIndexID();
//			}
//			// An index manager id is provided by the user. Use this one and do not create a new WS-Resource
//			else
//				indexID = this.indexManagerID;
//
//			/* Wait until the index management resource has been published to the IS */
//			GeoIndexManagementWSResource ftResTemplate = new GeoIndexManagementWSResource(getScope());
//			ftResTemplate.setAttributeValue(GeoIndexManagementWSResource.ATTR_INDEXID, indexID);
//			ResourceExpression<GeoIndexManagementWSResource> ftExpr = null;
//			try {
//				ftExpr = ResourceManager.generateExpressionForResourceTempate(ftResTemplate, false);
//			} catch (Exception e) {
//				throw new GCUBEUnrecoverableException(e);
//			}
//
//			while (true) {
//				try {
//					Thread.sleep(MANAGER_POLLING_INTERVAL);
//				} catch (InterruptedException e) { /* In case of interruption, just return, because the task has been cancelled by the user */
//					return;
//				}
//
//				/* Retrieve the generated geo manager resource from the IS */
//				List<GeoIndexManagementWSResource> r = null;
//				try {
//					r = ResourceManager.retrieveResourcesFromIS(ftExpr);
//				} catch (Exception e) {
//					throw new GCUBEUnrecoverableException(e);
//				}
//				if (r!=null && r.size()>0)
//					break;
//			}
//		}
//
//		/**
//		 * Returns the indexID of the newly created index manager.
//		 * @return the indexID
//		 */
//		String getIndexID() {
//			return this.indexID;
//		}
//	}
//
//	/**
//	 * A service handler to interact with the GeoIndexManagement service in order to create an index manager.
//	 * @author Spyros Boutsis, NKUA
//	 */
//	private class GeoIndexUpdaterServiceHandler extends GCUBEServiceHandler<GCUBEServiceClientImpl> {
//
//		private String indexID;
//		private String rowsetRSEPR;
//		private TaskExecutionData execData;
//
//		public GeoIndexUpdaterServiceHandler(TaskExecutionData execData, String indexID, String rowsetRSEPR) {
//			this.execData = execData;
//			this.indexID = indexID;
//			this.rowsetRSEPR = rowsetRSEPR;
//		}
//
//		/*
//		 * (non-Javadoc)
//		 * @see org.gcube.common.core.utils.handlers.GCUBEServiceHandler#getTargetPortTypeName()
//		 */
//		protected String getTargetPortTypeName() {
//			return "GeoIndexUpdater";
//		}
//
//		/* (non-Javadoc)
//		 * @see org.gcube.common.core.utils.handlers.GCUBEServiceHandler#findInstances()
//		 */
//		@Override
//		protected List<EndpointReferenceType> findInstances() throws Exception {
//			List<EndpointReferenceType> eprs = new ArrayList<EndpointReferenceType>();			
//			RunningInstanceResource riRes = new RunningInstanceResource(getScope());
//			riRes.setAttributeValue(RunningInstanceResource.ATTR_SERVICENAME, "GeoIndexUpdater");
//			riRes.setAttributeValue(RunningInstanceResource.ATTR_STATUS, "ready");
//			for (RunningInstanceResource RI : ResourceManager.retrieveResourcesFromIS(riRes, false)) {
//				for (String endpoint : RI.getAttributeValue(RunningInstanceResource.ATTR_ENDPOINT)) {
//					if (endpoint.contains("GeoIndexUpdaterFactory"))
//						eprs.add(new EndpointReferenceType(new Address(endpoint)));
//				}
//			}
//			return eprs;
//		}
//
//		/* (non-Javadoc)
//		 * @see org.gcube.common.core.utils.handlers.GCUBEServiceHandler#interact(org.apache.axis.message.addressing.EndpointReferenceType)
//		 */
//		@Override
//		protected void interact(EndpointReferenceType arg0) throws Exception {
//			TaskExecutionLogger logger = execData.getExecutionLogger();
//
//			/* Get the GeoIndexUpdaterFactory port type */
//			GeoIndexUpdaterFactoryPortType geoupfpt = null;
//			try {
//				GeoIndexUpdaterFactoryServiceAddressingLocator loc = new GeoIndexUpdaterFactoryServiceAddressingLocator();
//				geoupfpt = PortTypeUtil.getStubProxy(execData.getSession(), loc.getGeoIndexUpdaterFactoryPortTypePort(arg0));
//			} catch (Exception e) {
//				logger.warn("Failed to get the GeoIndexUpdaterFactory service's porttype", e);
//				throw new Exception("Failed to get the GeoIndexUpdaterFactory service's porttype", e);
//			}
//
//			/* Create the updater resource and get its port type */
//			GeoIndexUpdaterPortType updaterpt = null;
//			try {
//				org.gcube.indexmanagement.geoindexupdater.stubs.CreateResource createRequest = new org.gcube.indexmanagement.geoindexupdater.stubs.CreateResource();       
//				createRequest.setMainIndexID(indexID);
//				org.gcube.indexmanagement.geoindexupdater.stubs.CreateResourceResponse createResponse = geoupfpt.createResource(createRequest);
//				EndpointReferenceType updaterInstanceEPR = createResponse.getEndpointReference();
//				GeoIndexUpdaterServiceAddressingLocator uloc = new GeoIndexUpdaterServiceAddressingLocator();
//				updaterpt = PortTypeUtil.getStubProxy(execData.getSession(), uloc.getGeoIndexUpdaterPortTypePort(updaterInstanceEPR));
//			} catch (Exception e) {
//				if (e.getCause() instanceof SocketException)
//					throw new Exception();
//				logger.error("Error while creating GeoIndexUpdater resource.", e);
//				throw new GCUBEUnrecoverableException(e);
//			}
//
//			Process process = new Process();
//			process.setDestroyAfterUpdate(true);
//			process.setResultSetLocation(rowsetRSEPR);
//			try {
//				updaterpt.process(process);
//			} catch (Exception e) {
//				if (e.getCause() instanceof SocketException)
//					throw new Exception();
//				throw new GCUBEUnrecoverableException(e);
//			}
//		}
//	};
//
//
//
//
//
//	private static final int DEFAULT_NUM_LOOKUPS = 2;
//	private static final String DEFAULT_GEOSYSTEM = "WGS_1984";
//	private static final String DEFAULT_UNIT = "DD";
//	private static final int DEFAULT_NUMDECIMALS = 4;
//
//	/**
//	 * Class constructor
//	 */
//	public GeoIndexGenerationTaskType() { 
//		super(MetadataCollectionDataType.class, 
//				GeoIndexDataType.class,
//				GeoIndexGenerationTaskType.class);
//	}
//
//	/* (non-Javadoc)
//	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.task.CustomTaskType#getUIDescription()
//	 */
//	@Override
//	public String getUIDescription() {
//		try {
//			String sourceName = this.getInput().getAttributeValue(MetadataCollectionDataType.ATTR_COLNAME);
//			String sourceFormat = this.getInput().getAttributeValue(MetadataCollectionDataType.ATTR_SCHEMANAME) + 
//			"/" + this.getInput().getAttributeValue(MetadataCollectionDataType.ATTR_LANGUAGE);
//			return "Create geospatial text index for the metadata collection '" + sourceName + "' with format " + sourceFormat + ".";
//		} catch (Exception e) {
//			return null;
//		}
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.task.CustomTaskType#executeTask(org.gcube.portlets.admin.irbootstrapperportlet.servlet.util.TaskExecutionData)
//	 */
//	public void executeTask(TaskExecutionData execData) {
//		TaskExecutionLogger logger = execData.getExecutionLogger();
//
//		String appendManagerID = null;
//		try {
//			appendManagerID = getAttributeValue(GeoIndexGenerationTaskType.this.getName() + ".GeoIndexGenerationTask.IdOfIndexManagerToAppend");
//			if (appendManagerID == null || appendManagerID.length()==0) 
//				throw new Exception();
//		} catch (Exception e) {
//			logger.info("An index manager ID was not supplied, creating new index...");
//			appendManagerID = null;
//		}
//
//		/* Get the transformation XSLT ID (source -> ES). This parameter is optional. */
//		String sourceToEsXSLTID = null;
//		try {
//			sourceToEsXSLTID = getAttributeValue(GeoIndexGenerationTaskType.this.getName() + ".GeoIndexGenerationTask.SourceToEsXSLTID");
//		} catch (Exception e) {
//			logger.error("Failed to retrieve the value of the source -> ES transformation XSLT ID.");
//			return;
//		}
//
//		/* Get the transformation XSLT ID (ES -> geo). If it has not been set, halt the execution */
//		String esToGeoXSLTID = null;
//		try {
//			esToGeoXSLTID = getAttributeValue(GeoIndexGenerationTaskType.this.getName() + ".GeoIndexGenerationTask.EsToGeoXSLTID");
//			if (esToGeoXSLTID == null) 
//				throw new Exception();
//		} catch (Exception e) {
//			logger.error("A ES -> geo transformation XSLT ID was not supplied!");
//			return;
//		}
//
//		/* Get the index type ID. If it has not been set, halt the execution */
//		String indexTypeID = null;
//		try {
//			indexTypeID = getAttributeValue(GeoIndexGenerationTaskType.this.getName() + ".GeoIndexGenerationTask.IndexTypeID");
//			if (indexTypeID == null) 
//				throw new Exception();
//		} catch (Exception e) {
//			logger.error("An indexTypeID was not supplied!");
//			return;
//		}
//
//		/* Get the number of lookups to create. If it has not been set, set it to "2" (default value) */
//		int numLookups = DEFAULT_NUM_LOOKUPS;
//		try {
//			String s = getAttributeValue(GeoIndexGenerationTaskType.this.getName() + ".GeoIndexGenerationTask.NumberOfLookups");
//			if (s != null) 
//				numLookups = Integer.valueOf(s);
//		} catch (Exception e) {
//			logger.error("Error while retrieving the 'NumberOfLookups' task property.", e);
//			return;
//		}
//
//		/* Get the GeographicalSystem. If it has not been set, set it to the default value */
//		String geoSystem = DEFAULT_GEOSYSTEM;
//		try {
//			String s = getAttributeValue(GeoIndexGenerationTaskType.this.getName() + ".GeoIndexGenerationTask.GeographicalSystem");
//			if (s != null) 
//				geoSystem = s;
//		} catch (Exception e) {
//			logger.error("Error while retrieving the 'GeographicalSystem' task property.", e);
//			return;
//		}
//
//		/* Get the UnitOfMeasurement. If it has not been set, set it to the default value */
//		String unit = DEFAULT_UNIT;
//		try {
//			String s = getAttributeValue(GeoIndexGenerationTaskType.this.getName() + ".GeoIndexGenerationTask.UnitOfMeasurement");
//			if (s != null) 
//				unit = s;
//		} catch (Exception e) {
//			logger.error("Error while retrieving the 'UnitOfMeasurement' task property.", e);
//			return;
//		}
//
//		/* Get the NumberOfDecimals. If it has not been set, set it to the default value */
//		int numDecimals = DEFAULT_NUMDECIMALS;
//		try {
//			String s = getAttributeValue(GeoIndexGenerationTaskType.this.getName() + ".GeoIndexGenerationTask.NumberOfDecimals");
//			if (s != null) 
//				numDecimals = Integer.valueOf(s);
//		} catch (Exception e) {
//			logger.error("Error while retrieving the 'NumberOfDecimals' task property.", e);
//			return;
//		}
//
//		/* Get the list of lookup RIs to be used. If it has not been set, all available RIs will be used */
//		List<EndpointReferenceType> lookupFactoryEPRs = new LinkedList<EndpointReferenceType>();
//		try {
//			String s = getAttributeValue(GeoIndexGenerationTaskType.this.getName() + ".GeoIndexGenerationTask.LookupRunningInstancesToUse");
//			if (s!=null) {
//				for (String endpoint : EntityParsingUtil.attrValueToArrayOfValues(s))
//					lookupFactoryEPRs.add(new EndpointReferenceType(new Address(endpoint)));
//			}
//			else {
//				RunningInstanceResource riRes = new RunningInstanceResource(getScope());
//				riRes.setAttributeValue(RunningInstanceResource.ATTR_SERVICENAME, "GeoIndexLookup");
//				riRes.setAttributeValue(RunningInstanceResource.ATTR_STATUS, "ready");
//				for (RunningInstanceResource RI : ResourceManager.retrieveResourcesFromIS(riRes, false)) {
//					for (String endpoint : RI.getAttributeValue(RunningInstanceResource.ATTR_ENDPOINT)) {
//						if (endpoint.contains("GeoIndexLookupFactory"))
//							lookupFactoryEPRs.add(new EndpointReferenceType(new Address(endpoint)));
//					}
//				}
//			}
//		} catch (Exception e) {
//			logger.error("Error while retrieving the 'LookupRunningInstancesToUse' task property.", e);
//			return;
//		}
//		if (lookupFactoryEPRs.size() == 0) {
//			logger.error("Could not locate running instances of the GeoIndexLookupFactory service.");
//			return;
//		}
//		else if (lookupFactoryEPRs.size() < numLookups) {
//			logger.warn(numLookups + " lookup resources have been requested, but there are only " + 
//					lookupFactoryEPRs.size() + " available GeoIndexLookupFactory running instances. Only " +
//					lookupFactoryEPRs.size() + " lookup resources will be created.");
//			numLookups = lookupFactoryEPRs.size();
//		}
//
//		/* Get the input collection ID. If it has not been set, halt the execution */
//		MetadataCollectionDataType input = (MetadataCollectionDataType) getInput();
//		String inColID = input.getCollectionID();
//		if (inColID == null) {
//			logger.error("An input collection ID value was not supplied!");
//			return;
//		}
//
//		/* Transform the contents of the input metadata collection to Rowset format */
//		GCUBEServiceClientImpl serviceClient = new GCUBEServiceClientImpl();
//		DTSServiceHandler brokerHandler = new DTSServiceHandler(execData, sourceToEsXSLTID, esToGeoXSLTID, indexTypeID, inColID);
//		brokerHandler.setHandled(serviceClient);
//		try {
//			brokerHandler.run();
//		} catch (Exception e) { 
//			logger.error("Error while transforming the metadata collection to rowsets.", e);
//			return;
//		}
//		String rowsetRSEPR = brokerHandler.getOutputRSEPR();
//
//		/* Create the geo index management resource */
//		GeoIndexManagementServiceHandler geoManagerHandler = new GeoIndexManagementServiceHandler(execData, indexTypeID
//				, geoSystem, unit, numDecimals, appendManagerID);
//		geoManagerHandler.setHandled(serviceClient);
//		try {
//			geoManagerHandler.run();
//		} catch (Exception e) {
//			logger.error("Error while creating the index management resource.", e);
//			return;
//		}
//		String indexID = geoManagerHandler.getIndexID();
//
//		/* Set the task output's attributes */
//		GeoIndexDataType output = (GeoIndexDataType) this.getOutput();
//		try {
//			output.setCollectionID(input.getCollectionID());
//			output.setCollectionName(input.getCollectionName());
//			output.setIndexID(indexID);
//		} catch (Exception e) {
//			logger.error("Error while trying to set the generated index attributes on the output object", e);
//			return;
//		}
//
//		/* Feed the index management resource using the RS EPR returned by the gDTS */ 
//		GeoIndexUpdaterServiceHandler geoUpdaterHandler = new GeoIndexUpdaterServiceHandler(execData, indexID, rowsetRSEPR);
//		geoUpdaterHandler.setHandled(serviceClient);
//		try {
//			geoUpdaterHandler.run();
//		} catch (Exception e) {
//			logger.error("Error while feeding the created index management resource.", e);
//			return;
//		}
//
//		if (appendManagerID == null) {
//			/* Create the requested number of lookup resources */
//			int currLookupEPR = 0;
//			for (int i=0; i<numLookups; i++) {
//				if (currLookupEPR == lookupFactoryEPRs.size()) {
//					logger.warn(numLookups + " lookup resources have been requested, but only " + 
//							lookupFactoryEPRs.size() + " created succesfully. There are no more " +
//					"GeoIndexLookup running instances available to try.");
//					break;
//				}
//
//				/* Get the next GeoIndexLookupFactory RI EPR to try, retrieve its porttype
//				 * and create the lookup resource */
//				EndpointReferenceType lookupFactoryEPR = lookupFactoryEPRs.get(currLookupEPR);
//				try {
//					GeoIndexLookupFactoryPortType ptLookupFactory = null;
//					GeoIndexLookupFactoryServiceAddressingLocator lookupFactoryLocator = new GeoIndexLookupFactoryServiceAddressingLocator();
//					ptLookupFactory = PortTypeUtil.getStubProxy(execData.getSession(),
//							lookupFactoryLocator.getGeoIndexLookupFactoryPortTypePort(lookupFactoryEPR));
//					org.gcube.indexmanagement.geoindexlookup.stubs.CreateResource lookupCreateRequest =
//						new org.gcube.indexmanagement.geoindexlookup.stubs.CreateResource();
//					lookupCreateRequest.setMainIndexID(indexID);
//					ptLookupFactory.createResource(lookupCreateRequest);
//				} catch (Exception e) {
//					logger.warn("Error creating a geo index lookup at: " + lookupFactoryEPR.getAddress().getHost(), e);
//				}
//
//				currLookupEPR++;
//			}
//		}
//		else {
//			logger.info("The existing lookups will be used");
//		}
//	}
//
//	/* (non-Javadoc)
//	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.task.CustomTaskType#getXMLTaskDefinitionDocument()
//	 */
//	@Override
//	public Document getXMLTaskDefinitionDocument() throws Exception {
//		return Util.parseXMLString(
//				"<GeoIndexGenerationTask>" +
//				"<IndexTypeID/>" +
//				"<GeographicalSystem/>" +
//				"<UnitOfMeasurement/>" +
//				"<NumberOfDecimals/>" +
//				"<SourceToEsXSLTID/>" +
//				"<EsToGeoXSLTID/>" +
//				"<NumberOfLookups/>" +
//				"<LookupRunningInstancesToUse/>"  +
//				"<IdOfIndexManagerToAppend/>" +
//				"</GeoIndexGenerationTask>"
//		);
//	}
//}
