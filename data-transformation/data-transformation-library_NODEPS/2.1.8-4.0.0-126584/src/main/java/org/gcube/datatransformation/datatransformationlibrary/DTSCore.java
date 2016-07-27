package org.gcube.datatransformation.datatransformationlibrary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.gcube.datatransformation.datatransformationlibrary.adaptor.DTSAdaptor;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataBridge;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataHandler;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSink;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSource;
import org.gcube.datatransformation.datatransformationlibrary.imanagers.IManager;
import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;
import org.gcube.datatransformation.datatransformationlibrary.model.ExtTransformationUnit;
import org.gcube.datatransformation.datatransformationlibrary.model.HandlerDesc;
import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;
import org.gcube.datatransformation.datatransformationlibrary.model.Source;
import org.gcube.datatransformation.datatransformationlibrary.model.TransformationProgram;
import org.gcube.datatransformation.datatransformationlibrary.model.TransformationUnit;
import org.gcube.datatransformation.datatransformationlibrary.model.graph.TransformationsGraph;
import org.gcube.datatransformation.datatransformationlibrary.programs.Program;
import org.gcube.datatransformation.datatransformationlibrary.statistics.Metric;
import org.gcube.datatransformation.datatransformationlibrary.statistics.StatisticsManager;
import org.gcube.datatransformation.datatransformationlibrary.statistics.StatisticsManager.MetricType;
import org.gcube.datatransformation.datatransformationlibrary.transformation.model.TransformationDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Dimitris Katris, NKUA
 * 
 * DTSCore is the class invoked by any interface (as the web service interface) which needs to utilize the data transformationUnit functionality.
 */
public class DTSCore {

	/**
	 * Logs operations performed by {@link DTSCore} class.
	 */
	private static Logger log = LoggerFactory.getLogger(DTSCore.class);

	/**
	 * {@link Metric} that counts the time taken to find one or more applicable {@link TransformationUnit}s.
	 */
	private static Metric findTransformationUnitByGraphMetric = StatisticsManager.createMetric("FindTransformationUnitByGraphMetric", "Time to search for transformationUnit Unit by transformations graph", MetricType.DTS);
	
	/**
	 * Object from which DTSCore can retrieve the required information about transformationUnit programs.
	 */
	private IManager iManager;
	
	/** Workflow Adaptor used by transformation process */
	
	private InheritableThreadLocal<DTSAdaptor> adaptor = new InheritableThreadLocal<DTSAdaptor>();
	
	/**
	 * Returns the iManager object.
	 * 
	 * @return The iManager.
	 */
	public IManager getIManager() {
		return iManager;
	}

	/** Timeout when data source collection is empty */
	public static final long TIMEOUT = 60*1000;

	/**
	 * The graph which is used by DTSCore in order to find transformationUnit units.
	 */
	private TransformationsGraph graph;
	
	/**
	 * Constructor of {@link DTSCore}. {@link DTSCore} requires a {@link IManager} instance and a {@link TransformationsGraph} instance in order to be initialized.
	 * 
	 * @param iManager The {@link IManager} from which the {@link DTSCore} instance will retrieve the {@link TransformationProgram}s.
	 * @param graph The {@link TransformationsGraph} which the {@link DTSCore} instance will use in order to search for applicable {@link TransformationUnit}s.
	 * @throws Exception If imanager or graph is not set.
	 */
	public DTSCore(IManager iManager, TransformationsGraph graph) throws Exception {
		if(iManager==null || graph==null){
			log.error("Information Manager or Transformations Graph is not set...");
			throw new Exception("Information Manager or Transformations Graph is not set...");
		}
		this.iManager = iManager;
		this.graph = graph;
	}
	
	/**
	 * Destroys the information kept by the {@link DTSCore} instance.
	 */
	public void destroy(){
		this.graph.destroy();
		this.iManager=null;
	}
	
	private void transformDataWithAdaptor(ArrayList<TransformationUnit>transformationUnits, ArrayList<ContentType> mimeTypes) throws Exception {
		adaptor.get().addPlan(transformationUnits, mimeTypes);
	}
	
	public void initializeAdaptor(final TransformationDescription desc, String scope, boolean isTest) throws Exception {
		try {
			adaptor.set((DTSAdaptor)Class.forName(DTSADAPTOR).getConstructor(Boolean.class).newInstance(Boolean.valueOf(isTest)));
			log.info("initialized for local execution: " + isTest);
		} catch (Exception e) {
			log.error("Could not create data transformation adaptor", e);
			return;
		}

		adaptor.get().setTransPlan(desc);
		adaptor.get().SetScope(scope);
		adaptor.get().setRequirements(REQS);
		
		try {
			adaptor.get().CreatePlan();
		}catch(Exception e) {
			log.error("create plan faild",e);
			throw new Exception(e.getMessage());
		}

//		ExecutionPlan executionPlan = adaptor.GetCreatedPlan();
//
//		System.out.println(executionPlan.Serialize());

		new Thread() {
			public void run() {
				String output = null;
				try {
					output = adaptor.get().ExecutePlan();
				} catch (Exception e) {
					e.printStackTrace();
				}
				desc.setReturnedValue(output);
			}
		}.start();
		
//		System.out.println(adaptor.GetExecutionID());
	}
	
	/**
	 * <p>Executes the transformationUnit defined in a {@link TransformationUnit} instance.</p>
	 * <p>The {@link DataHandler}s which the {@link TransformationUnit} utilizes have to be previously initialized.</p>
	 * 
	 * @param transformationUnit The {@link TransformationUnit} to 
	 * @param targetContentType The target {@link ContentType} of the transformationUnit.
	 * @throws Exception If an error occurred in performing the transformationUnit.
	 */
	private void transformDataWithTransformationUnit(TransformationUnit transformationUnit, ContentType targetContentType) throws Exception {
		ArrayList<TransformationUnit> tPath = new ArrayList<TransformationUnit>();
		ArrayList<ContentType> targetContentTypes = new ArrayList<ContentType>();
		
		if (transformationUnit != null) {
			if(transformationUnit.isComposite()){
				log.info("Composite transformationUnit to be performed...");
				int extTransformationsSize = transformationUnit.getExtTransformationList().size();
				for(int i=0;i<extTransformationsSize;i++){
					ExtTransformationUnit exttransformation = transformationUnit.getExtTransformationList().get(i);
	
					//Getting the external transformationUnit program...
					TransformationProgram extTP = iManager.getTransformationProgram(exttransformation.getReferencedTransformationProgramID());
					for(TransformationUnit extTPTransformation: extTP.getTransformationUnits()){
						//Getting the external transformationUnit...
						if(extTPTransformation.getId().equals(exttransformation.getReferencedTransformationUnitID())){
	//						for(TargetHandlerDesc thdesc: exttransformation.getTargetIOs()){
	//							extTPTransformation.bindHandler(thdesc.getTargetID(), transformationUnit.getDataHandler(thdesc.getThisID()));
	//						}
							if(transformationUnit.getProgramParameters()!=null && transformationUnit.getProgramParameters().size()>0){
								setProgramParametersOfTransformationUnit(extTPTransformation, transformationUnit.getProgramParameters().toArray(new Parameter[transformationUnit.getProgramParameters().size()]));
							}
							//TODO: Should pay attention in the target content type putting here... It should not have any '*' or '-' I guess... (mergeContentTypeParametersToApply does this)
							ContentType finalContentType = mergeContentTypeParametersToApply(targetContentType, extTPTransformation.getTarget().getContentType());
							log.debug("Invoking (external) "+extTPTransformation.getTransformationProgram().getId()+"/"+extTPTransformation.getId()+" with target content type "+finalContentType.toString());
	//						try {
	//							//TODO this will fail if extTPTransformation is a composite TP
	//							transformDataWithTransformationUnit(extTPTransformation, finalContentType);
	//							
	//						} catch (Exception e) {
	//							closeSourcesAndSinkOfTransformationUnit(extTPTransformation);
	//							throw e;
	//						}
							tPath.add(extTPTransformation);
							targetContentTypes.add(finalContentType);
							break;
						}
					}
				}
			}else{
				//TODO: Should pay attention in the target format putting here... It should not have any '*' or '-' I guess...
	//			ArrayList<DataSource> datasources = new ArrayList<DataSource>();
	//			ArrayList<Source> sources = transformationUnit.getSources();
	//			for(Source source: sources){
	//				datasources.add(source.getInput());
	//			}//TODO: Here we shall be sure to arrange the objects in ascending order depending on their TPInpoutID...
	//			DataSink sink = transformationUnit.getTarget().getOutput();
	//			List<Parameter> finalProgramParameters = mergeProgramParameters(transformationUnit.getTransformationProgram().getTransformer().getGlobalProgramParams(), transformationUnit.getProgramParameters());
	//			transformDataWithProgram(datasources, transformationUnit.getTransformationProgram().getTransformer(), finalProgramParameters, targetContentType, sink);
				
				tPath.add(transformationUnit);
				targetContentTypes.add(targetContentType);
			}
		} else {
			targetContentTypes.add(targetContentType);
		}
		
		transformDataWithAdaptor(tPath, targetContentTypes);
	}
	
	/**
	 * <p>Merges the parameters of the target {@link ContentType} which will be set in the {@link Program}</p>
	 * 
	 * @param targetContentType The requested target {@link ContentType}.
	 * @param transformationsContentType The target {@link ContentType} of the {@link TransformationUnit}.
	 * @return The merged {@link ContentType} parameters.
	 * @throws Exception If an error occurred in merging the {@link ContentType} parameters.
	 */
	private static ContentType mergeContentTypeParametersToApply(ContentType targetContentType, ContentType transformationsContentType) throws Exception {
		if(transformationsContentType==null){
			log.error("Transformations Content Type does not exist");
			throw new Exception("Transformations Content Type does not exist");
		}
		if(targetContentType==null){
			log.warn("Target Content Type does not exist");
			return transformationsContentType.clone();
		}
		if(transformationsContentType.getContentTypeParameters()==null || 
				transformationsContentType.getContentTypeParameters().size()==0 ||
				targetContentType==null || targetContentType.getContentTypeParameters().size()==0){
			//No format parameters exist so not doing anything...
			ContentType finalFormat = transformationsContentType.clone();
			Iterator<Parameter> it = finalFormat.getContentTypeParameters().iterator();
			while(it.hasNext()) {
				Parameter par = it.next();
				if(par.getValue().equals("*") || par.getValue().equals("-"))
					it.remove();
			}
			return finalFormat;
		}
		ContentType finalFormat = new ContentType();
		finalFormat.setMimeType(transformationsContentType.getMimeType());
		ArrayList<Parameter> finalParameters = new ArrayList<Parameter>();
		for(Parameter transformationParam: transformationsContentType.getContentTypeParameters()){
			Parameter finalParam = new Parameter();
			finalParam.setName(transformationParam.getName());
			String value = transformationParam.getValue();
			//Checking if the content format of the transformationUnit has any parameters which are not set...
			if(transformationParam.getValue().equals("*") || transformationParam.getValue().equals("-")){
				for(Parameter targetParam: targetContentType.getContentTypeParameters()){
					if(targetParam.getName().toLowerCase().equals(transformationParam.getName().toLowerCase())){
						value = targetParam.getValue();
						log.trace("Putting in as value of target content type parameter "+transformationParam.getName()+" the "+value);
					}
				}
			}
			finalParam.setValue(value);
			finalParameters.add(finalParam);
		}
		finalFormat.setContentTypeParameters(finalParameters);
		return finalFormat;
	}
	
	private void setProgramParametersOfTransformationUnit(TransformationUnit transformationUnit, Parameter[] programUnboundParameters){
		ArrayList<Parameter> tuProgParams = transformationUnit.getProgramParameters();
		if(tuProgParams!=null && tuProgParams.size()>0){
			for(Parameter param: tuProgParams){
				for(Parameter unboundparam: programUnboundParameters){
					if(param.getName()!=null && unboundparam.getName()!=null){
						if(param.getName().equalsIgnoreCase(unboundparam.getName())){
							log.trace("Setting value "+unboundparam.getName()+"=\""+unboundparam.getValue()+"\" in tu "+transformationUnit.getId());
							param.setValue(unboundparam.getValue());
						}
					}
				}
			}
		}
	}
	
	/**
	 * Private method which basically implements the functionality of the public method {@link DTSCore#transformDataWithTransformationProgram(DataSource, String, Parameter[], ContentType, DataSink)}
	 *  
	 * @param source The {@link DataSource} from which the {@link DTSCore#transformDataWithTransformationProgram(DataSource, TransformationProgram, ContentType, DataSink)} method fetches the {@link DataElement}s.
	 * @param transformationProgram The {@link TransformationProgram} that will be used to perform the transformationUnit.
	 * @param targetContentType The {@link ContentType} to which the {@link DataElement}s are transformed.
	 * @throws Exception If an error occurred in the transformationUnit process.
	 */
	private void transformDataWithTransformationProgram(DataSource source, TransformationProgram transformationProgram, ContentType targetContentType) throws Exception {
		//This hash map maps the hashCode of a content format with the transformationUnit to be used...
		//Maybe change it to something less dangerous...
		HashMap <Integer, TransformationUnitAndBridge> contentTypeToTransformationUnit = new HashMap<Integer, TransformationUnitAndBridge>();
//		DataSourceMerger merger = new DataSourceMerger();
//		merger.setSink(sink);//The sink is closed by the merger...
//		merger.start();
		try {
			getNext: while(source.hasNext()){
				try {
//					DataElement object = source.next();
					ContentType contentType = source.nextContentType();
					if(contentType==null){
						continue getNext;
					}
					//Keep a hashmap ContentType
					TransformationUnitAndBridge trandbridges = contentTypeToTransformationUnit.get(contentType.hashCode());
					ContentType finalTargetContentType = targetContentType; 
					//First check for exact support
					if(trandbridges==null){
						//TODO: Make it static method of Transformation.exactlySupports, Transformation.supports...
						nextTR: for(TransformationUnit tmptr: transformationProgram.getTransformationUnits()){
							if(tmptr.getSources().size()!=1){
								log.error("Invocation transformDataWithKnownTP in TPs with more that one Sources is not permitted...");
								continue nextTR;
							}
							if(tmptr.getSources().get(0).getContentType().getMimeType().equalsIgnoreCase(contentType.getMimeType())
									&& tmptr.getTarget().getContentType().getMimeType().equalsIgnoreCase(targetContentType.getMimeType())
									&& Parameter.equals(tmptr.getSources().get(0).getContentType().getContentTypeParameters(), contentType.getContentTypeParameters())
									&& Parameter.equals(tmptr.getTarget().getContentType().getContentTypeParameters(), targetContentType.getContentTypeParameters())){
								trandbridges = new TransformationUnitAndBridge();
								trandbridges.transformationUnit=tmptr;
//								trandbridges.srcbridge = getDataBridge();
//								trandbridges.trgbridge = getDataBridge();
								break;
							}
						}
					
						//Then for support
						if(trandbridges==null){
							nextTR: for(TransformationUnit tmptr: transformationProgram.getTransformationUnits()){
								if(tmptr.getSources().size()!=1){
									log.error("Invocation transformDataWithKnownTP in TPs with more that one Sources is not permitted...");
									continue nextTR;
								}
								if(ContentType.support(tmptr.getSources().get(0).getContentType(), contentType)
										&& ContentType.support(tmptr.getTarget().getContentType(), targetContentType)){
									trandbridges = new TransformationUnitAndBridge();
									trandbridges.transformationUnit=tmptr;
//									trandbridges.srcbridge = getDataBridge();
//									trandbridges.trgbridge = getDataBridge();
									break;
								}
							}
						}
						
						//At last for generic support
						if(trandbridges==null){
							log.info("Trying generic support...");
							nextTR: for(TransformationUnit tmptr: transformationProgram.getTransformationUnits()){
								if(tmptr.getSources().size()!=1){
									log.error("Invocation transformDataWithKnownTP in TPs with more that one Sources is not permitted...");
									continue nextTR;
								}
								if(ContentType.gensupport(tmptr.getSources().get(0).getContentType(), contentType)
										&& ContentType.gensupport(tmptr.getTarget().getContentType(), targetContentType)){
									finalTargetContentType=tmptr.getTarget().getContentType();
									trandbridges = new TransformationUnitAndBridge();
									trandbridges.transformationUnit=tmptr;
//									trandbridges.srcbridge = getDataBridge();
//									trandbridges.trgbridge = getDataBridge();
									break;
								}
							}
						}
						
						if(trandbridges==null){
							log.warn("Could not find transformationUnit in the TP: "+transformationProgram.getId()+" for object with content type "+contentType.toString());
//							ReportManager.manageRecord(object.getId(), "Object with id "+object.getId()+" and content type "+contentType.toString()+" was filtered", Status.FAILED, Type.FILTER);
							continue getNext;
						}else{
							contentTypeToTransformationUnit.put(contentType.hashCode(), trandbridges);
//							merger.add(trandbridges.trgbridge);//The transformed content of each transformationUnit will be merged into the output sink...
							
							//IOsss...
//							ArrayList<HandlerDesc> ios = trandbridges.transformationUnit.getIOs();
//							for(HandlerDesc hdesc: ios) {
//								log.debug("HandlerDesc: "+hdesc.getID()+", "+hdesc.getType());
//								if(hdesc.getType().equals(HandlerDesc.HandlerType.Input)) {
//									trandbridges.transformationUnit.bindHandler(hdesc.getID(), trandbridges.srcbridge);
//								} else if(hdesc.getType().equals(HandlerDesc.HandlerType.Output)) {
//									trandbridges.transformationUnit.bindHandler(hdesc.getID(), trandbridges.trgbridge);
//								}else if(hdesc.getType().equals(HandlerDesc.HandlerType.Bridge)) {
//									trandbridges.transformationUnit.bindHandler(hdesc.getID(), getDataBridge());
//								} else{
//									log.error("Unknown HandlerDesc type: "+hdesc.getType());
//								} 
//							}							
							//TODO: Should pay attention in the target format putting here...
							//It should not have any '*' or '-' I guess...
							try {
								transformDataWithTransformationUnit(trandbridges.transformationUnit, finalTargetContentType);
							} catch (Exception e) {
//								closeSourcesAndSinkOfTransformationUnit(trandbridges.transformationUnit);
								throw e;
							}
						}
					}
//					if(trandbridges.srcbridge.isClosed()){
//						log.warn("Bridge of "+trandbridges.transformationUnit.getTransformationProgram().getId()+"/"+trandbridges.transformationUnit.getId()+" is closed, cannot append data element");
////						ReportManager.manageRecord(object.getId(), "Object with id "+object.getId()+" and content type "+object.getContentType().toString()+" won't be transformed by "+trandbridges.transformationUnit.getTransformationProgram().getId()+"/"+trandbridges.transformationUnit.getId()+", data bridge is closed", Status.FAILED, Type.FILTER);
//						continue getNext;
//					}
					
//					ReportManager.manageRecord(object.getId(), "Object with id "+object.getId()+" and content type "+contentType.toString()+" was not filtered, input to transformationUnit Unit with id "+trandbridges.transformationUnit.getId(), Status.SUCCESSFUL, Type.FILTER);
//					trandbridges.srcbridge.append(object);
				} catch (Exception e) {
					log.error("Undefined error inside the iteration of the source objects.", e);
				}
			}
			try {source.close();} catch (Exception e) {log.error("Did not manage to close initial data source", e);}
//			merger.finishedAddingSources();//Source objects have finished so we won't add any more sources to the merger...
			adaptor.get().finishedAddingPLans();

			//In addition we are closing any bridge with is the source of the D
			for(TransformationUnitAndBridge trandbridge:  contentTypeToTransformationUnit.values()){
				if(trandbridge.srcbridge!=null)
					trandbridge.srcbridge.close();
			}
		} catch(Exception e) {
			log.error("Undefined error in DTSCore ",e);
			throw new Exception("Undefined error in DTSCore", e);
		}
	}
	
	/**
	 * Closes any {@link DataHandler}s of a {@link TransformationUnit} which may be possible remained opened. This method is primarily useful in cases where errors have occurred in the transformationUnit process.  
	 * 
	 * @param transformationUnit The {@link TransformationUnit} which its {@link DataHandler}s will be closed. 
	 */
	private static void closeSourcesAndSinkOfTransformationUnit(TransformationUnit transformationUnit){
		try {
			ArrayList<Source> sources = transformationUnit.getSources();
			if(sources!=null){
				for(Source source: sources){
					try {source.getInput().close();} catch (Exception e1) {}
				}
			}
//			transformationUnit.getTarget().getOutput().close();
		} catch (Exception e1) {}
	}
	/**
	 * <p>
	 * This method transforms the input data which are fetched by the {@link DataSource}s to the target {@link ContentType} and stores the transformed {@link DataElement}s to the {@link DataSink}. The {@link TransformationProgram} which will be used by the service is indicated by the client.
	 * </p>
	 * <p>
	 * This method should be only used externally as internally the transformationUnit id is known.
	 * Putting a TP without the transformationUnit id is useful when having a TP
	 * that is created in order to perform an operation over some variable 
	 * input content types, performed by different programs and the 
	 * target format has common semantics.
	 * </p>
	 * @param source The {@link DataSource} from which the {@link DTSCore#transformDataWithTransformationProgram(DataSource, String, Parameter[], ContentType, DataSink)} method fetches the {@link DataElement}s.
	 * @param transformationProgramID The id of the {@link TransformationProgram}.
	 * @param programUnboundParameters The unbound program parameters which are set by the client.
	 * @param targetContentType The {@link ContentType} to which the {@link DataElement}s are transformed.
	 * @throws Exception If an error occurred in the transformationUnit process.
	 */
	public void transformDataWithTransformationProgram(DataSource source, String transformationProgramID, Parameter[] programUnboundParameters, ContentType targetContentType) throws Exception{
		
		TransformationProgram transformationProgram;
		try {
			transformationProgram = iManager.getTransformationProgram(transformationProgramID);
		} catch (Exception e) {
			log.error("Could not perform the transformationUnit as dCore could not find program with id: "+transformationProgramID);
//			if(sink!=null){sink.close();}
			throw new Exception("Could not perform the transformationUnit as dCore could not find program with id: "+transformationProgramID);
		}
		
		if(programUnboundParameters!=null && programUnboundParameters.length>0){
			//Setting any unbound program parameter values to the global program parameters of the transformationUnit program...
			ArrayList<Parameter> tpProgParams = transformationProgram.getTransformer().getGlobalProgramParams();
			if(tpProgParams!=null && tpProgParams.size()>0){
				for(Parameter param: tpProgParams){
					for(Parameter unboundparam: programUnboundParameters){
						if(param.getName()!=null && unboundparam.getName()!=null){
							if(param.getName().equalsIgnoreCase(unboundparam.getName())){
								param.setValue(unboundparam.getValue());
							}
						}
					}
				}
			}
		}
		transformDataWithTransformationProgram(source, transformationProgram, targetContentType);
	}
	
	/**
	 * This method transforms the input data which are fetched by the {@link DataSource}s to the target {@link ContentType} and stores the transformed {@link DataElement}s to the {@link DataSink}. The {@link TransformationUnit} which will be used by the service is indicated by the client.
	 * 
	 * @param transformationProgramID The id of the {@link TransformationProgram}.
	 * @param transformationUnitID The id of the {@link TransformationUnit}.
	 * @param programUnboundParameters A list with {@link Program} parameters set by the client.
	 * @param targetContentType The {@link ContentType} to which the {@link DataElement}s are transformed.
	 * @param filterSources If true then the {@link ContentType} of each {@link DataElement} from the {@link DataSource}s is checked if it conforms to the input expected by the {@link TransformationUnit}.
	 * @throws Exception If an error occurred in the transformationUnit process.
	 */
	public void transformDataWithTransformationUnit(String transformationProgramID, String transformationUnitID, Parameter[] programUnboundParameters, ContentType targetContentType, boolean filterSources) throws Exception {

		TransformationUnit transformationUnit;
		try {
			transformationUnit = iManager.getTransformationUnit(transformationProgramID, transformationUnitID);
		} catch (Exception e) {
			log.error("Did not manage to get transformationUnit Unit with id "+transformationProgramID+"/"+transformationUnitID+" from iManager", e);
			try {
//				if(sink!=null){sink.close();}
//				for(DataSource source: sources){
//					try {source.close();} catch (Exception e1) {}
//				}
			} catch (Exception e1) {}
			throw new Exception("Did not manage to get transformationUnit Unit with id "+transformationProgramID+"/"+transformationUnitID+" from iManager", e);
		}
		
		if(programUnboundParameters!=null && programUnboundParameters.length>0){
			//Setting any unbound program parameter values to the program parameters of the transformationUnit unit...
			setProgramParametersOfTransformationUnit(transformationUnit, programUnboundParameters);
			
			//Setting any unbound program parameter values to the global program parameters of the transformationUnit program...
			ArrayList<Parameter> tpProgParams = transformationUnit.getTransformationProgram().getTransformer().getGlobalProgramParams();
			if(tpProgParams!=null && tpProgParams.size()>0){
				for(Parameter param: tpProgParams){
					for(Parameter unboundparam: programUnboundParameters){
						if(param.getName()!=null && unboundparam.getName()!=null){
							if(param.getName().equalsIgnoreCase(unboundparam.getName())){
								param.setValue(unboundparam.getValue());
							}
						}
					}
				}
			}
		}
		
		//Binding data handlers with io ids...
		ArrayList<HandlerDesc> ios = transformationUnit.getIOs();
		for(HandlerDesc hdesc: ios) {
			if(hdesc.getType().equals(HandlerDesc.HandlerType.Input)) {
				if(filterSources){
					log.debug("Filtering is enabled...");
//					FilterDataBridge fbridge = new FilterDataBridge(sources.get(Integer.parseInt(hdesc.getID().replaceAll("TRInput", ""))), hdesc.getRuleElement().getContentType());
//					log.debug("Binding Input Handler: "+hdesc.getID()+", with filter bridge of source("+Integer.parseInt(hdesc.getID().replaceAll("TRInput", ""))+")");
//					transformationUnit.bindHandler(hdesc.getID(), fbridge);
					transformationUnit.getSources().get(Integer.parseInt(hdesc.getID().replaceAll("TRInput", ""))).setContentType(hdesc.getRuleElement().getContentType());
				}else{
//					log.debug("Binding Input Handler: "+hdesc.getID()+", with source("+Integer.parseInt(hdesc.getID().replaceAll("TRInput", ""))+")");
//					transformationUnit.bindHandler(hdesc.getID(), sources.get(Integer.parseInt(hdesc.getID().replaceAll("TRInput", ""))));
				}
			} else if(hdesc.getType().equals(HandlerDesc.HandlerType.Output)) {
//				log.debug("Binding Output Handler: "+hdesc.getID()+", with the sink");
//				transformationUnit.bindHandler(hdesc.getID(), sink);
			} else if(hdesc.getType().equals(HandlerDesc.HandlerType.Bridge)) {
				transformationUnit.bindHandler(hdesc.getID(), getDataBridge());
			} else{
				log.error("Unknown HandlerDesc type: "+hdesc.getType());
//				if(sink!=null){sink.close();}
//				for(DataSource source: sources){
//					try {source.close();} catch (Exception e1) {}
//				}
				throw new Exception("Unknown HandlerDesc type: "+hdesc.getType());
			}
		}
		try {
			transformDataWithTransformationUnit(transformationUnit, targetContentType);
			adaptor.get().finishedAddingPLans();
		} catch (Exception e) {
			closeSourcesAndSinkOfTransformationUnit(transformationUnit);
			throw e;
		}
	}
	
	/**
	 * This method transforms the input data which are fetched by the {@link DataSource} to the target {@link ContentType} and stores the transformed {@link DataElement}s to the {@link DataSink}. In this method the service by its self discovers the {@link TransformationProgram} to use.
	 * 
	 * @param source The {@link DataSource} from which the {@link DTSCore#transformData(DataSource, ContentType, DataSink)} method fetches the {@link DataElement}s.
	 * @param targetContentType The {@link ContentType} to which the {@link DataElement}s are transformed.
	 */
	public void transformData(final DataSource source, final ContentType targetContentType){
		
		Thread dataElementsBroker = new Thread(){
			public void run(){
				//This hash map maps the hashCode of a content format with the transformationUnit to be used...
				//Maybe change it to something less dangerous...
				HashMap <Integer, TransformationUnitAndBridge> contentTypeToTransformationUnit = new HashMap<Integer, TransformationUnitAndBridge>();
				HashSet<Integer> contentTypesWithNoApplicableTUnit = new HashSet<Integer>();
//				DataSourceMerger merger = new DataSourceMerger();
//				merger.setSink(sink);//The sink is closed by the merger...
//				merger.start();
				try {
					getNext: while(source.hasNext()){
//						DataElement object=null;
						ContentType contentType = null;
						try {
//							object = source.next();
							contentType = source.nextContentType();
//							if(contentType==null){log.warn("Got null object from Data Source");continue getNext;}
//							if(object.getId()==null || object.getId().trim().length()==0){log.error("Data element does not contain a valid identifier");continue getNext;}
							if(contentType==null){
								log.error("The content type of an object is not evaluated by the data source");
//								ReportManager.manageRecord(object.getId(), "The content type of object with id "+object.getId()+" is not evaluated by the data source", Status.FAILED, Type.FILTER);
								continue getNext;
							}
							//Keep a hashmap ContentType
							TransformationUnitAndBridge trandbridges = contentTypeToTransformationUnit.get(contentType.hashCode());
							
							if(trandbridges==null){//Find through the graph a proper tr...
								//Currently the graph doesn't return non generic transformations that generically support...
								if(contentTypesWithNoApplicableTUnit.contains(contentType.hashCode())){
									log.debug("Content type "+contentType+" is already checked and no transformationUnit Unit is available");
									log.warn("Could not find transformationUnit for object with type "+contentType.toString()+" and target content type "+targetContentType.toString());
//									ReportManager.manageRecord(object.getId(), "Could not find program to transform object with id "+object.getId()+" and content type "+contentType.toString(), Status.FAILED, Type.FILTER);
									continue getNext;
								}else{
									long starttime = System.currentTimeMillis();
									ArrayList<TransformationUnit> applicableTransformationUnits = graph.findApplicableTransformationUnits(contentType, targetContentType, true);
									findTransformationUnitByGraphMetric.addMeasure(System.currentTimeMillis()-starttime);
									if(applicableTransformationUnits!=null && applicableTransformationUnits.size()>0){
										log.info("Managed to find a proper transformationUnit from the graph for the object...");
										trandbridges = new TransformationUnitAndBridge();
										trandbridges.transformationUnit=applicableTransformationUnits.get(0);
//										trandbridges.srcbridge = getDataBridge();
//										trandbridges.trgbridge = getDataBridge();
										contentTypeToTransformationUnit.put(contentType.hashCode(), trandbridges);
//										merger.add(trandbridges.trgbridge);//The transformed content of each transformationUnit will be merged into the output sink...
										//IOsss...
//										ArrayList<HandlerDesc> ios = trandbridges.transformationUnit.getIOs();
//										for(HandlerDesc hdesc: ios) {
//											log.debug("HandlerDesc: "+hdesc.getID()+", "+hdesc.getType());
//											if(hdesc.getType().equals(HandlerDesc.HandlerType.Input)) {
//												trandbridges.transformationUnit.bindHandler(hdesc.getID(), trandbridges.srcbridge);
//											} else if(hdesc.getType().equals(HandlerDesc.HandlerType.Output)) {
//												trandbridges.transformationUnit.bindHandler(hdesc.getID(),  trandbridges.trgbridge);
//											} else if(hdesc.getType().equals(HandlerDesc.HandlerType.Bridge)) {
//												trandbridges.transformationUnit.bindHandler(hdesc.getID(), getDataBridge());
//											} else{
//												log.error("Unknown HandlerDesc type");
//											}
//										}
										//Here should see the targetFormat which is going to put in case of finding a generic support...
										try {
											transformDataWithTransformationUnit(trandbridges.transformationUnit, targetContentType);
										} catch (Exception e) {
//											closeSourcesAndSinkOfTransformationUnit(trandbridges.transformationUnit);
											throw e;
										}
									}else{
										if (contentType.equals(targetContentType)) {
											log.info("No need to transform object. It will be forwarded to output.");
											trandbridges = new TransformationUnitAndBridge();
											trandbridges.transformationUnit=null;
											contentTypeToTransformationUnit.put(contentType.hashCode(), trandbridges);
											try {
												transformDataWithTransformationUnit(trandbridges.transformationUnit, targetContentType);
											} catch (Exception e) {
												throw e;
											}
										} else {
											log.warn("Could not find transformationUnit for object with type "+contentType.toString()+" and target content type "+targetContentType.toString());
//											ReportManager.manageRecord(object.getId(), "Could not find program to transform object with content type "+contentType.toString(), Status.FAILED, Type.FILTER);
											contentTypesWithNoApplicableTUnit.add(contentType.hashCode());
											continue getNext;
										}
									}
								}
							}else{
								log.info("Managed to find the proper transformationUnit in the hashmap for the object...");
							}
//							if(trandbridges.srcbridge.isClosed()){
//								log.warn("Bridge of "+trandbridges.transformationUnit.getTransformationProgram().getId()+"/"+trandbridges.transformationUnit.getId()+" is closed, cannot append data element with id "+object.getId());
//								ReportManager.manageRecord(object.getId(), "Object with id "+object.getId()+" and content type "+object.getContentType().toString()+" won't be transformed by "+trandbridges.transformationUnit.getTransformationProgram().getId()+"/"+trandbridges.transformationUnit.getId()+", data bridge is closed", Status.FAILED, Type.FILTER);
//								continue getNext;
//							}
//							if (trandbridges.transformationUnit == null) {
//								ReportManager.manageRecord(object.getId(), "Object with id "+object.getId()+" and content type "+object.getContentType().toString()+" no need to be transformed. It will be forwarded to ouput", Status.SUCCESSFUL, Type.FILTER);
//							}else {
//								ReportManager.manageRecord(object.getId(), "Object with id "+object.getId()+" and content type "+object.getContentType().toString()+" will be transformed by "+trandbridges.transformationUnit.getTransformationProgram().getId()+"/"+trandbridges.transformationUnit.getId(), Status.SUCCESSFUL, Type.FILTER);
//							}
								
//							trandbridges.srcbridge.append(object);
						} catch (Exception e) {
							log.error("Undefined error inside the iteration of the source objects.", e);
							if(contentType!=null){
//								ReportManager.manageRecord(object.getId(), "Fatal Error when trying to find program to transform object with id "+object.getId()+" and content type "+object.getContentType().toString(), Status.FAILED, Type.FILTER);
							}
						}
					}
					try {source.close();} catch (Exception e) {log.error("Did not manage to close initial data source", e);}
					
//					merger.finishedAddingSources();//Source objects have finished so we won't add any more sources to the merger...
					adaptor.get().finishedAddingPLans();
					//In addition we are closing any bridge with is the source of the D
					for(TransformationUnitAndBridge trandbridge:  contentTypeToTransformationUnit.values()){
						if(trandbridge.srcbridge!=null)
							trandbridge.srcbridge.close();
					}
				} catch(Exception e) {
					log.error("Unknown error in DTSCore ", e);
				}
			}
		};
		dataElementsBroker.start();
	}
	
	public void clean() {
		try {
			adaptor.remove();
		} catch(Exception e) {
			log.warn("exception during cleanup", e);
		}
	}
	
	/**
	 * The default {@link DataBridge} class.
	 */
	private static String DEFAULTFASTBRIDGECLASS = "org.gcube.datatransformation.datatransformationlibrary.datahandlers.impl.REFDataBridge";

	/**
	 * The default Adaptor.
	 */
	private static String DEFAULTADAPTORCLASS = "gr.uoa.di.madgik.workflow.adaptor.datatransformation.WorkflowDTSAdaptor";
	
	private static String DEFAULTREQUIREMENTS = "dts.execution==true";

	/**
	 * The {@link DataBridge} class which will be used by the {@link DTSCore}{@link #getDataBridge()} method.
	 */
	private static String FASTBRIDGECLASS = PropertiesManager.getPropertyValue("dcore.fastbridgeclass", DEFAULTFASTBRIDGECLASS);

	/**
	 * The {@link DTSAdaptor} class which will be used.
	 */
	private static String DTSADAPTOR = PropertiesManager.getPropertyValue("dcore.adaptor", DEFAULTADAPTORCLASS);

	private static String REQS = PropertiesManager.getPropertyValue("dts.requirements", DEFAULTREQUIREMENTS);
	/**
	 * Returns an instance of a {@link DataBridge}. The class name of the {@link DataBridge} is set in the "dtslib.properties" file.
	 * 
	 * @return The instance of the {@link DataBridge}.
	 */
	public static DataBridge getDataBridge(){
		try {
			return (DataBridge)Class.forName(FASTBRIDGECLASS).newInstance();
		} catch (Exception e) {
			log.error("Could not create data bridge", e);
		}
		return null;
	}

	/**
	 * The default {@link DataBridge} class which stores {@link DataElement}s to hard disk.
	 */
	private static String DEFAULTHARDBRIDGECLASS = "org.gcube.datatransformation.datatransformationlibrary.datahandlers.impl.RSBlobDataBridge";
	/**
	 * The {@link DataBridge} class which will be used by the {@link DTSCore#getHardDataBridge()} method.
	 */
	private static String HARDBRIDGECLASS = PropertiesManager.getPropertyValue("dcore.hardbridgeclass", DEFAULTHARDBRIDGECLASS);

	/**
	 * Returns an instance of a {@link DataBridge}. The class name of the {@link DataBridge} is set in the "dtslib.properties" file.
	 * The difference with the getDataBridge() method is that hard {@link DataBridge}s copies the content of each {@link DataElement} in the hard disk.  
	 * 
	 * @return The instance of the {@link DataBridge}.
	 */
	public static DataBridge getHardDataBridge(){
		try {
			return (DataBridge)Class.forName(HARDBRIDGECLASS).newInstance();
		} catch (Exception e) {
			log.error("Could not create data bridge", e);
		}
		return null;
	}
	
	/**
	 * Searches for {@link TransformationUnit}s that are able to perform a transformationUnit from a source to a target {@link ContentType}. 
	 * 
	 * @param sourceContentType The source {@link ContentType}.
	 * @param targetContentType The target {@link ContentType}.
	 * @param createAndPublishCompositeTP If true then the {@link TransformationsGraph} creates and publishes the composite {@link TransformationProgram}s.
	 * @return A list of applicable {@link TransformationUnit}s.
	 */
	public ArrayList <TransformationUnit> findApplicableTransformationUnits(ContentType sourceContentType, ContentType targetContentType, boolean createAndPublishCompositeTP){
		long starttime = System.currentTimeMillis();
		ArrayList<TransformationUnit> availabletransformationUnits = this.graph.findApplicableTransformationUnits(sourceContentType, targetContentType, createAndPublishCompositeTP);
		findTransformationUnitByGraphMetric.addMeasure(System.currentTimeMillis()-starttime);
		return availabletransformationUnits;
	}
	
	/**
	 * Searches for {@link ContentType}s to which an object can be transformed.
	 * 
	 * @param sourceContentType The source {@link ContentType}.
	 * @return One or more available target {@link ContentType}s.
	 */
	public ArrayList<ContentType> getAvailableTargetContentTypes(ContentType sourceContentType){
		return this.graph.findAvailableTargetContentTypes(sourceContentType);
	}
}

/**
 * A simple structure for internal use.
 * 
 * @author Dimitris Katris, NKUA
 */
class TransformationUnitAndBridge{
	/**
	 * The {@link TransformationUnit} instance.
	 */
	public TransformationUnit transformationUnit;
	/**
	 * The {@link DataBridge} which acts as {@link DataSource} to the {@link TransformationUnit}.
	 */
	public DataBridge srcbridge;
	
	/**
	 * The {@link DataBridge} whihc acts as {@link DataSink} to the {@link TransformationUnit}.
	 */
	public DataBridge trgbridge;
}