package org.gcube.data.harmonization.occurrence.impl;

import static org.gcube.data.streams.dsl.Streams.convert;
import static org.gcube.data.streams.dsl.Streams.pipe;
import gr.uoa.di.madgik.grs.record.GenericRecord;

import java.io.File;
import java.io.FileWriter;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.gcube.data.analysis.statisticalmanager.proxies.StatisticalManagerDSL;
import org.gcube.data.analysis.statisticalmanager.proxies.StatisticalManagerDataSpace;
import org.gcube.data.analysis.statisticalmanager.proxies.StatisticalManagerFactory;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMAlgorithm;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMComputationConfig;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMComputationRequest;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMComputations;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMGroupedAlgorithms;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMListGroupedAlgorithms;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMParameter;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMParameters;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMTypeParameter;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMComputation;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMEntries;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMEntry;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMImport;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMInputEntry;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMOperation;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.StatisticalServiceType;
import org.gcube.data.harmonization.occurrence.OccurrenceStreamer;
import org.gcube.data.harmonization.occurrence.Reconciliation;
import org.gcube.data.harmonization.occurrence.impl.model.Computation;
import org.gcube.data.harmonization.occurrence.impl.model.Operation;
import org.gcube.data.harmonization.occurrence.impl.model.PagedRequestSettings;
import org.gcube.data.harmonization.occurrence.impl.model.Resource;
import org.gcube.data.harmonization.occurrence.impl.model.db.TableConnectionDescriptor;
import org.gcube.data.harmonization.occurrence.impl.model.statistical.AlgorithmParameter;
import org.gcube.data.harmonization.occurrence.impl.model.statistical.StatisticalComputation;
import org.gcube.data.harmonization.occurrence.impl.model.statistical.StatisticalFeature;
import org.gcube.data.harmonization.occurrence.impl.model.types.DataType;
import org.gcube.data.harmonization.occurrence.impl.model.types.DataType.Type;
import org.gcube.data.harmonization.occurrence.impl.model.types.OperationType;
import org.gcube.data.harmonization.occurrence.impl.model.types.ResourceType;
import org.gcube.data.harmonization.occurrence.impl.model.types.Status;
import org.gcube.data.harmonization.occurrence.impl.readers.CSVParserConfiguration;
import org.gcube.data.harmonization.occurrence.impl.readers.OccurrenceReader;
import org.gcube.data.harmonization.occurrence.impl.readers.ParserConfiguration;
import org.gcube.data.harmonization.occurrence.impl.readers.XMLParserConfiguration;
import org.gcube.data.harmonization.occurrence.impl.readers.formats.CSVReader;
import org.gcube.data.harmonization.occurrence.impl.readers.formats.XMLReader;
import org.gcube.data.spd.client.ResultGenerator;
import org.gcube.data.spd.model.products.OccurrencePoint;
import org.gcube.data.spd.plugin.fwk.writers.rswrapper.ResultWrapper;
import org.gcube.data.streams.Stream;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ReconciliationImpl implements Reconciliation{

	private static final Logger logger = LoggerFactory.getLogger(ReconciliationImpl.class);



	private static SMTypeParameter DEFAULT_SMTYPE=new SMTypeParameter(StatisticalServiceType.TABULAR,
			Collections.singletonList(
			TableTemplates.OCCURRENCE_SPECIES.toString()));

	
	private String user;
	//	private StatisticalManagerService service;
	private StatisticalManagerFactory factory;
	private StatisticalManagerDataSpace dataSpace;

	private TableConnectionDescriptor tableConn;


	public ReconciliationImpl(String user) {	
		this.user=user;
		factory = StatisticalManagerDSL.createStateful().build();
		dataSpace=StatisticalManagerDSL.dataSpace().build();
	}

	public ReconciliationImpl(String user,URI host) {
		this.user=user;
		factory = StatisticalManagerDSL.createStateful().at(host).build();
		dataSpace=StatisticalManagerDSL.dataSpace().at(host).build();
	}




	@Override
	public List<Resource> getDataSets() {
		ArrayList<Resource> toReturn=new ArrayList<Resource>();
		//Imported

		List<SMImport> importersList=dataSpace.getImports(user, TableTemplates.OCCURRENCE_SPECIES.toString());		
			for(SMImport importRef:importersList){
				if(Status.values()[importRef.operationStatus()].equals(Status.COMPLETED))
					try{
						Resource toAdd=operationToResource(importRef);
						toAdd.getOperation().setOperationType(OperationType.IMPORT);
						toReturn.add(toAdd);
					}catch(Exception e){
						logger.warn("Unable to get resource information for importRef "+importRef.operationId());
						logger.debug("Exception was ",e);
					}
			}

		// obtained by computations

		SMComputations computationList=factory.getComputations(user, DEFAULT_SMTYPE);
		if(computationList!=null && computationList.list()!=null)
			for(SMComputation comp:computationList.list()){
				if(Status.values()[comp.operationStatus()].equals(Status.COMPLETED))
					try{
						Resource toAdd=operationToResource(comp);
						toAdd.getOperation().setOperationType(OperationType.COMPUTATION);
						toReturn.add(toAdd);
					}catch(Exception e){
						logger.warn("Unable to get resource information for comp "+comp.operationId());
						logger.debug("Exception was ",e);
					}
			}

		return toReturn;
	}

	@Override
	public String getJSONImported(PagedRequestSettings settings)throws Exception {
		if(tableConn==null) throw new Exception("Table connection not opened");
		return tableConn.getJSON(settings);
	}

	@Override
	public List<StatisticalFeature> getCapabilities() {
		ArrayList<StatisticalFeature> toReturn=new ArrayList<StatisticalFeature>();
		SMListGroupedAlgorithms list=factory.getAlgorithms(DEFAULT_SMTYPE);
		if(list!=null&&list.thelist()!=null)
			for(SMGroupedAlgorithms groupedAlg:list.thelist()){

				if(groupedAlg!=null&&groupedAlg.thelist()!=null) {
					String category=groupedAlg.category();

					for(SMAlgorithm algorithm : groupedAlg.thelist()){
						StatisticalFeature feature=new StatisticalFeature();
						SMParameters foundParams=factory.getAlgorithmParameters(algorithm.name());
						ArrayList<AlgorithmParameter> params=new ArrayList<AlgorithmParameter>();

						if(foundParams!=null&&foundParams.list()!=null)
							for(SMParameter param:foundParams.list()){
								params.add(new AlgorithmParameter(
										new DataType(
												param.type().values(),
												Type.valueOf(param.type().name()+"")), 
												param.name(), param.defaultValue(), param.description()));
							}						

						feature.setComputation(new StatisticalComputation(algorithm.name(), algorithm.description(), category));
						feature.setParameters(params);
						toReturn.add(feature);
					}
				}
			}

		//		
		//		
		//		
		//			for(Feature f:features.getList()){
		//				// every feature has multiple algorithms
		//				for(Algorithm algorithm : f.getAlgorithms()){
		//					// request parameters for every computation
		//					SMComputation computation=new SMComputation(algorithm.getName(), f.getCategory(), algorithm.getDescription());
		//					ArrayList<AlgorithmParameter> params=new ArrayList<AlgorithmParameter>();
		//					for(SMParameter param:factory.getAlgorithmParameters(computation).getList()){
		//						params.add(new AlgorithmParameter(
		//								new DataType(
		//										Arrays.asList(param.getType().getValue().getValues()),
		//										Type.valueOf(param.getType().getName()+"")), 
		//										param.getName(), param.getDefaultValue(), param.getDescription()));
		//					}
		//					toReturn.add(new StatisticalFeature(params, new StatisticalComputation(algorithm.getName(),algorithm.getDescription(),f.getCategory()+""))); 
		//				}
		//			}

		return toReturn;
	}



	@Override
	public File getResourceAsFile(String operationId,OperationType type) throws Exception {
		SMOperation operation=null;
		switch(type){
			case IMPORT : operation=dataSpace.getImporter(operationId);
								break;
			case COMPUTATION : operation=factory.getComputation(operationId);
								break;
			default : throw new Exception ("Invalid Operation Type "+ type); 
			
		}
		Status status=Status.values()[operation.operationStatus()];
		if(!status.equals(Status.COMPLETED)) throw new Exception("Operation not completed, status is "+status);
		Resource resource=operationToResource(operation);
		if(!resource.getType().equals(ResourceType.TABULAR)) throw new Exception("Unexpected Resource Type "+resource.getType());
		return saveTable(resource);
	}

	@Override
	public String submitOperation(StatisticalComputation comp,
			Map<String, String> parameters, String title, String description) throws Exception {
		SMComputationConfig config=new SMComputationConfig();
		config.algorithm(comp.getAlgorithm());
		List<SMInputEntry> entries=new ArrayList<SMInputEntry>();
		for(Entry<String,String> param:parameters.entrySet()){
			entries.add(new SMInputEntry(param.getKey(), param.getValue()));
		}
		config.parameters(new SMEntries(entries.toArray(new SMInputEntry[entries.size()])));
		SMComputationRequest request=new SMComputationRequest(); 
		request.config(config);
		request.description(description);
		request.title(title);
		request.user(user);

		return factory.executeComputation(request);


		//
		//		ComputationConfig config=new ComputationConfig();
		//		config.setComputation(
		//				new SMComputation(comp.getAlgorithm(), ComputationalAgentClass.fromString(comp.getCategory()), comp.getDescription()));
		//		List<SMEntry> entries=new ArrayList<SMEntry>();
		//		for(Entry<String,String> entry:parameters.entrySet())
		//			entries.add(new SMEntry(entry.getKey(), entry.getValue()));
		//		config.setParameters(new SMEntries(entries.toArray(new SMEntry[entries.size()])));
		//		return service.executeComputation(config);
		//		throw new Exception("To implement");
	}

	@Override
	public List<Computation> getSubmittedOperationList() {
		ArrayList<Computation> toReturn=new ArrayList<Computation>();
		List<SMTypeParameter> types=new ArrayList<SMTypeParameter>();
		types.add(DEFAULT_SMTYPE);
		SMComputations computationList=factory.getComputations(user, DEFAULT_SMTYPE);
		if(computationList!=null && computationList.list()!=null)
			for(SMComputation comp:computationList.list()){
				toReturn.add(translate(comp));
			}
		return toReturn;
	}


	@Override
	public synchronized List<String> openTableInspection(String tableId) throws Exception {
		if(tableConn!=null) tableConn.close();
		tableConn=new TableConnectionDescriptor(dataSpace.getDBParameters(tableId), tableId);
		return tableConn.getColumns();
	}


	@Override
	public synchronized void closeTableConnection() throws Exception {
		if(tableConn!=null) tableConn.close();
		tableConn=null;
	}


	@Override
	public OccurrenceStreamer getStreamer(File toStream,ParserConfiguration configuration,String tableName, String description) throws Exception {
		OccurrenceReader streamer=null;
		if(configuration instanceof XMLParserConfiguration) streamer= new XMLReader(toStream,(XMLParserConfiguration) configuration);
		else if(configuration instanceof CSVParserConfiguration)streamer=new CSVReader(toStream,(CSVParserConfiguration) configuration);
		if(streamer==null)throw new Exception("Invalid passed configuration");
		ResultWrapper<OccurrencePoint> wrapper=new ResultWrapper<OccurrencePoint>();
		streamer.setWrapper(wrapper);		
		Stream<OccurrencePoint> stream=pipe(convert(new URI(wrapper.getLocator())).of(GenericRecord.class).withDefaults()).through(new ResultGenerator<OccurrencePoint>());
		String dataID=dataSpace.createTableFromDataStream(stream, tableName, description, user);
		logger.info("Streaming to resource ID "+dataID);
		return streamer;
	}

	@Override
	public String getTableUrl(String tableId) throws Exception {
		return dataSpace.getDBParameters(tableId);
	}

	
	@Override
	public void removeComputationById(String id) throws Exception {
		factory.removeComputation(id);
	}
	
	//**************************************** OBJECT TRANSLATION


	private static final Resource operationToResource(SMOperation op)throws Exception{
		Resource toReturn=new Resource();
		toReturn.setId(op.abstractResource().resource().resourceId());
		toReturn.setName(op.abstractResource().resource().name());
		toReturn.setResourceDescription(op.abstractResource().resource().description());
		toReturn.setType(ResourceType.values()[op.abstractResource().resource().resourceType()]);
		toReturn.setOperation(translate(op));
		return toReturn;
	}

	private static final Computation translate(SMComputation comp){
		Computation toReturn=new Computation();
		toReturn.setCompletionDate(comp.completedDate());
		toReturn.setOperationDescription(comp.description());
		toReturn.setOperationId(comp.operationId());
		toReturn.setStatus(Status.values()[comp.operationStatus()]);
		toReturn.setSubmissionDate(comp.submissionDate());

		toReturn.setAlgorithm(comp.algorithm());
		toReturn.setCategory(comp.category());
		toReturn.setTitle(comp.title());
		HashMap<String,String> params=new HashMap<String, String>();
		if(comp.parameters()!=null)
			for(SMEntry entry:comp.parameters()){
				params.put(entry.key(), entry.value());
			}
		toReturn.setParameters(params);
		toReturn.setOperationType(OperationType.COMPUTATION);
		return toReturn;
	}

	private static final Operation translate(SMOperation op){
		Operation toReturn=new Operation();
		toReturn.setCompletionDate(op.completedDate());
		toReturn.setOperationDescription(op.description());
		toReturn.setOperationId(op.operationId());
		toReturn.setStatus(Status.values()[op.operationStatus()]);
		toReturn.setSubmissionDate(op.submissionDate());
		return toReturn;
	}



	//**************************************** SAVE RESULT


	private File saveTable(Resource toSave)throws Exception{
		Connection conn=null;
		FileWriter writer=null;
		try{
			logger.debug("Saveing resource "+toSave);
			String connectionUrl=dataSpace.getDBParameters(toSave.getId());
			logger.debug("Connecting to "+connectionUrl);
			conn=DriverManager.getConnection(connectionUrl);
			CopyManager manager=new CopyManager((BaseConnection) conn);
			File toReturn=File.createTempFile("SMResource", ".csv");
			writer=new FileWriter(toReturn);
			long count=manager.copyOut("COPY "+toSave.getId()+" TO STDOUT WITH DELIMITER ',' CSV HEADER ", writer);
			logger.debug("Wrote "+count+" to "+toReturn.getAbsolutePath());
			return toReturn;
		}catch(Exception e){
			throw e;
		}finally{
			if(conn!=null)conn.close();
			if(writer!=null) IOUtils.closeQuietly(writer);
		}
	}




}
