package org.gcube.portlets.user.trendylyzer_portlet.server;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.gcube.application.framework.accesslogger.library.impl.AccessLogger;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.util.FileSystemNameUtil;
import org.gcube.common.homelibrary.util.WorkspaceUtil;
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
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMTables;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMTypeParameter;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMAbstractResource;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMComputation;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMEntries;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMEntry;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMFile;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMInputEntry;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMObject;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMOperationInfo;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMResource;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMTable;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.StatisticalServiceType;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.portlets.user.trendylyzer_portlet.client.TrendyLyzerPortletService;
import org.gcube.portlets.user.trendylyzer_portlet.client.algorithms.Algorithm;
import org.gcube.portlets.user.trendylyzer_portlet.client.algorithms.AlgorithmCategory;
import org.gcube.portlets.user.trendylyzer_portlet.client.algorithms.AlgorithmClassification;
import org.gcube.portlets.user.trendylyzer_portlet.client.algorithms.ComputationStatus;
import org.gcube.portlets.user.trendylyzer_portlet.client.algorithms.ComputationStatus.Status;
import org.gcube.portlets.user.trendylyzer_portlet.client.bean.output.ImagesResource;
import org.gcube.portlets.user.trendylyzer_portlet.client.bean.output.MapResource;
import org.gcube.portlets.user.trendylyzer_portlet.client.bean.output.Resource;
import org.gcube.portlets.user.trendylyzer_portlet.client.form.TableItemSimple;
import org.gcube.portlets.user.trendylyzer_portlet.client.results.JobItem;
import org.gcube.portlets.user.trendylyzer_portlet.server.accounting.StatisticalManagerExecution;
import org.gcube.portlets.user.trendylyzer_portlet.server.utils.ObjectConverter;
import org.gcube.portlets.user.trendylyzer_portlet.server.utils.SessionUtil;
import org.gcube.portlets.user.trendylyzer_portlet.server.utils.StorageUtil;
import org.gcube.portlets.user.trendylyzer_portlet.shared.parameters.ColumnListParameter;
import org.gcube.portlets.user.trendylyzer_portlet.shared.parameters.ColumnParameter;
import org.gcube.portlets.user.trendylyzer_portlet.shared.parameters.EnumParameter;
import org.gcube.portlets.user.trendylyzer_portlet.shared.parameters.FileParameter;
import org.gcube.portlets.user.trendylyzer_portlet.shared.parameters.ListParameter;
import org.gcube.portlets.user.trendylyzer_portlet.shared.parameters.ObjectParameter;
import org.gcube.portlets.user.trendylyzer_portlet.shared.parameters.Parameter;
import org.gcube.portlets.user.trendylyzer_portlet.shared.parameters.TabularListParameter;
import org.gcube.portlets.user.trendylyzer_portlet.shared.parameters.TabularParameter;
import org.slf4j.LoggerFactory;

import com.extjs.gxt.ui.client.data.BaseListLoadResult;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.thoughtworks.xstream.XStream;

public class TrendyLyzerServiceImpl extends RemoteServiceServlet implements
		TrendyLyzerPortletService {
	public static AccessLogger accountinglog = AccessLogger.getAccessLogger();

	private org.slf4j.Logger log = LoggerFactory
			.getLogger(TrendyLyzerServiceImpl.class);
	private Logger logger = Logger.getLogger("");

	/**
	 * 
	 */
	private static final long serialVersionUID = -5927344456122275568L;
	// private static Logger logger =
	// LoggerFactory.getLogger(TrendyLyzerServiceImpl.class);

	private final static String SEPARATOR = AlgorithmConfiguration
			.getListSeparator();

	@Override
	public void init() throws ServletException {
		super.init();

	}

	public List<AlgorithmClassification> getAlgorithmsClassifications()
			throws Exception {
		log.debug("Called method 'getAlgorithmsClassifications'...");
		try {
			StatisticalManagerFactory factory = getFactory();
			List<AlgorithmClassification> classifications = new ArrayList<AlgorithmClassification>();
			AlgorithmClassification classification = new AlgorithmClassification(
					"User Perspective");
			List<AlgorithmCategory> categories = new ArrayList<AlgorithmCategory>();
			List<Algorithm> algorithms = new ArrayList<Algorithm>();
			// get and print algorithms
			SMListGroupedAlgorithms groups = factory.getAlgorithmsUser();

			// get list categories
			for (SMGroupedAlgorithms group : groups.thelist()) {
				log.debug(group.category().toString());
				if (group.category().toString()
						.equals("OBIS_OBSERVATIONS_TRENDS")
						|| group.category().toString()
								.equals("OBIS_OBSERVATIONS_SPECIES_DATA")) {
					log.debug("Find trendylyzer algorithm");
					AlgorithmCategory category = new AlgorithmCategory(
							group.category(), "", "");
					// TO DO :ADD DESCRIPTIONO TO CATEGORY
					categories.add(category);
					for (SMAlgorithm algorithm : group.thelist()) {
						String algorithmID = algorithm.name();
						Algorithm algItem = new Algorithm(algorithmID, "", "",
								category);
						String algDescr = algorithm.description();
						if (algDescr != null) {
							algItem.setDescription(algDescr);
							algItem.setBriefDescription(algDescr);
						}

						category.addAlgorithm(algItem);
						algorithms.add(algItem);
					}
				}

				classification.setAlgorithmCategories(categories);
				classification.setAlgorithms(algorithms);
				classifications.add(classification);
			}
			return classifications;

		} catch (Exception e) {
			// logger.error("An error occurred getting the OperatorsClassifications list",
			// e);
			log.error(
					"Error encountered while executing 'getAlgorithmsClassifications'.",
					e);
			System.out
					.println("An error occurred getting the OperatorsClassifications list");
			e.printStackTrace();
			// return null;
			throw e;
		}
	}

	private StatisticalManagerFactory getFactory() {
		try {
			HttpSession httpSession = this.getThreadLocalRequest().getSession();
			return SessionUtil.getFactory(httpSession);
		} catch (Exception e) {
			log.error("An error occurred getting the Factory", e);
			System.out.println("An error occurred getting the Factory");
			e.printStackTrace();
			return null;
		}

	}

	public List<Parameter> getParameters(Algorithm algorithm) throws Exception {
		try {
			log.info("Inside getParameters");
			String algorithmId = algorithm.getId();
			// String category = algorithm.getCategory().getId(); not used
			StatisticalManagerFactory factory = getFactory();
			log.info("take factory");
			SMParameters smParams = factory.getAlgorithmParameters(algorithmId);
			List<Parameter> params = new ArrayList<Parameter>();
			log.info("TAKE PARAMETER");
			for (SMParameter smParam : smParams.list()) {
				SMTypeParameter smType = smParam.type();
				StatisticalServiceType smTypeName = smType.name();
				String paramName = smParam.name();
				String paramDescription = smParam.description();
				String defaultValue = smParam.defaultValue();
				log.info("	Param " + paramName);
				log.info("		description: " + paramDescription);
				log.info("		default value: " + defaultValue);
				log.info("		type name: " + smTypeName);
				for (String value : smType.values())
					log.info("			type value: " + value);

				if (smTypeName.equals(StatisticalServiceType.TABULAR)) {
					TabularParameter tabularParam = new TabularParameter(
							paramName, paramDescription);
					for (String value : smType.values())
						tabularParam.addTemplate(value);
					params.add(tabularParam);

				} else if (smTypeName
						.equals(StatisticalServiceType.TABULAR_LIST)) {
					TabularListParameter tabularListParam = new TabularListParameter(
							paramName, paramDescription, SEPARATOR);
					for (String value : smType.values())
						tabularListParam.addTemplate(value);
					params.add(tabularListParam);
				} else if (smTypeName.equals(StatisticalServiceType.PRIMITIVE)) {
					String objectType = smType.values().get(0);
					ObjectParameter objectParam = new ObjectParameter(
							paramName, paramDescription, objectType,
							defaultValue);
					params.add(objectParam);
				} else if (smTypeName.equals(StatisticalServiceType.ENUM)) {
					List<String> values =smType.values();
					EnumParameter enumParam = new EnumParameter(paramName,
							paramDescription, values, defaultValue);
					params.add(enumParam);
				} else if (smTypeName.equals(StatisticalServiceType.LIST)) {
					String listType = smType.values().get(0);
					ListParameter listParam = new ListParameter(paramName,
							paramDescription, listType, SEPARATOR);
					params.add(listParam);
				} else if (smTypeName.equals(StatisticalServiceType.COLUMN)) {
					String referredTabularParameterName = smType.values().get(0);
					ColumnParameter columnParam = new ColumnParameter(
							paramName, paramDescription,
							referredTabularParameterName, defaultValue);
					params.add(columnParam);
				} else if (smTypeName
						.equals(StatisticalServiceType.COLUMN_LIST)) {
					String referredTabularParameterName = smType.values().get(0);
					ColumnListParameter columnListParameter = new ColumnListParameter(
							paramName, paramDescription,
							referredTabularParameterName, SEPARATOR);
					params.add(columnListParameter);
				} else if (smTypeName.equals(StatisticalServiceType.FILE)) {
					FileParameter fileParameter = new FileParameter(paramName,
							paramDescription); // TODO
					params.add(fileParameter);
				}
			}
			log.info("return param");
			for (Parameter p : params) {
				log.debug(p.getDescription());
				log.debug(p.getName());
				log.debug(p.getValue());
				log.debug(p.getTypology().toString());
			}
			return params;
		} catch (Exception e) {
			log.error("An error ", e);
			System.out.println(e);
			e.printStackTrace();
			return null;
		}

	}

	public List<TableItemSimple> getTableItems(List<String> templates) {
		StatisticalManagerDataSpace dataSpace = getDataSpaceService();

		// logger.trace("\n---LIST TABLES");
		SMTables smList = dataSpace.getTables(getUsername());
		// logger.trace("SIZE: "+ smList.getList().length);

		List<TableItemSimple> list = new ArrayList<TableItemSimple>();

		for (SMTable table : smList.list()) {
			String id = table.resourceId(), name = table.name(), description = table
					.description(), template = table.template();
			// logger.info("Name: "+name+";\tDescr: "+description+";\tType: "+template);
			// // table type

			if (templates == null)
				list.add(new TableItemSimple(id, name, description, template));
			else
				for (String t : templates)
					if (t.toUpperCase().contentEquals("GENERIC")
							|| t.toUpperCase().contentEquals(
									template.toUpperCase())) {
						list.add(new TableItemSimple(id, name, description,
								template));
						break;
					}
		}

		return list;
	}

	private StatisticalManagerDataSpace getDataSpaceService() {
		HttpSession httpSession = this.getThreadLocalRequest().getSession();
		return SessionUtil.getDataSpaceService(httpSession);
	}

	private String getUsername() {
		HttpSession httpSession = this.getThreadLocalRequest().getSession();
		return SessionUtil.getUsername(httpSession);
	}

	

	public ComputationStatus getComputationStatus(String computationId) throws Exception  {
		try {
			StatisticalManagerFactory factory = getFactory();
			SMOperationInfo infos = factory.getComputationInfo(computationId, getUsername());

			//				Status status=convertStatus(infos.getStatus());
			Status status = ObjectConverter.convertStatus(infos.status());

			double perc = Double.parseDouble(infos.percentage());

			logger.info("STATUS: "+infos.status());
			//float status = Float.parseFloat(infos.getPercentage());
			logger.info("PERCENTAGE: "+perc);

			ComputationStatus computationStatus = new ComputationStatus(status, perc);

			if (computationStatus.isTerminated()) {
				try {
					SMComputation computation = factory.getComputation(computationId);
					// try to set the end date
					computationStatus.setEndDate(computation.completedDate().getTime());

					if (computationStatus.isFailed()) {
						StatisticalManagerExecution succesAlgLogEntry = new StatisticalManagerExecution(
								computation.algorithm(),0,"FAILED");
						accountinglog.logEntry(getUsername(), getScope(),
								succesAlgLogEntry);
						SMResource res = computation.abstractResource().resource();
						computationStatus.setMessage(res.description());
					}
					else
					{
						Date endDate = computation.completedDate().getTime();
						Date startDate = computation.submissionDate().getTime();
						int secondsBetween = (int) ((endDate.getTime() - startDate
								.getTime()) / 1000);
					
						StatisticalManagerExecution succesAlgLogEntry = new StatisticalManagerExecution(
								computation.algorithm(),secondsBetween,"DONE");
						accountinglog.logEntry(getUsername(), getScope(),
								succesAlgLogEntry);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return computationStatus;

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}		
			
	}
	
	private JobItem convertSMComputationToJobItem(SMComputation computation, boolean loadResourceData) {
		//logger.log(Level.SEVERE, "Inside convertSMComputationToJobItem");

		String computationId = ""+computation.operationId();
		String operatorId = computation.algorithm();
		String computationTitle = computation.title(); // TODO change name
		String computationDescription = computation.description();
		String categoryId = computation.category();
		Date startDate = computation.submissionDate() == null ? null : computation.submissionDate().getTime();
		Date endDate = computation.completedDate() == null ? null : computation.completedDate().getTime();
		String infrastructure = computation.infrastructure();
		float percentage = 0;
		Status status = ObjectConverter.convertStatus(computation.operationStatus());
		Resource resource = null;

		if (status==Status.RUNNING) {
			StatisticalManagerFactory factory = getFactory();
		
//			SMOperationInfo infos = factory.getComputationInfo(computationId, getUsername());
//			percentage = Float.parseFloat(infos.getPercentage());
			

			
		} else if (status==Status.COMPLETE) {
			SMAbstractResource abstractResource = computation.abstractResource();
			// generic resource 
			SMResource smResource = abstractResource.resource();
			resource = ObjectConverter.convertSmResourceToResource(smResource, getUsername());
		}

		ComputationStatus computationStatus = new ComputationStatus(status, percentage);		

		return new JobItem(computationId, computationTitle, computationDescription, categoryId, operatorId, infrastructure, startDate, endDate, computationStatus, resource);
	}

	
	
	
	
	
	
	public Resource getResourceByJobId(String jobId) {
		StatisticalManagerFactory factory = getFactory();

		SMComputation computation = factory.getComputation(jobId);
		JobItem jobItem = convertSMComputationToJobItem(computation, true);

		// try to add map data (if resource is an object)
		try {
			Resource res = jobItem.getResource();
			if (res.isMap()) {
				MapResource mapResource = (MapResource) res;
				Map<String, Resource> map = getMapFromMapResource(mapResource);
				mapResource.setMap(map);
				// jobItem.setMapData(map);
			} else if (res.isImages()) {
				ImagesResource imgsRes = (ImagesResource) res;
				Map<String, String> map = getImagesInfoFromImagesResource(imgsRes);
				imgsRes.setMapImages(map);
			}
			return res;
		} catch (Exception e) {
			// in this case the resource doesn't exist, nothing to do
			return null;
		}
	}

	public Map<String, String> getImagesInfoFromImagesResource(
			ImagesResource imgsRes) throws Exception {
		try {
			String url = imgsRes.getFolderUrl();
			// TODO get images
			String serviceClass = "org.gcube.data.analysis.statisticalmanager";
			String serviceName = "StatisticalManager";
			return StorageUtil.getFilesUrlFromFolderUrl(serviceClass,
					serviceName, url, getUsername(), getScope());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	private String getScope() {
		HttpSession httpSession = this.getThreadLocalRequest().getSession();
		return SessionUtil.getScope(httpSession);
	}

	
	public Map<String, Resource> getMapFromMapResource(MapResource mapRes)
			throws Exception {
		try {
			String url = mapRes.getUrl();

			InputStream is = StorageUtil.getStorageClientInputStream(url);

			// object serializer
			XStream xstream = new XStream();
			xstream.alias(
					"org.gcube_system.namespaces.data.analysis.statisticalmanager.types.SMObject",
					SMObject.class);
			xstream.alias(
					"org.gcube_system.namespaces.data.analysis.statisticalmanager.types.SMFile",
					SMFile.class);
			xstream.alias(
					"org.gcube_system.namespaces.data.analysis.statisticalmanager.types.SMResource",
					SMResource.class);
			xstream.alias(
					"org.gcube_system.namespaces.data.analysis.statisticalmanager.types.SMTable",
					SMTable.class);
			@SuppressWarnings("unchecked")
			Map<String, SMResource> smMap = (Map<String, SMResource>) (xstream
					.fromXML(is));

			Map<String, Resource> map = new LinkedHashMap<String, Resource>();
			for (String key : smMap.keySet()) {
				SMResource smResource = smMap.get(key);
				Resource resource = ObjectConverter
						.convertSmResourceToResource(smResource, getUsername());

				// if is an images resource, add urls images
				if (resource.isImages()) {
					ImagesResource imgsRes = (ImagesResource) resource;
					Map<String, String> mapImages = getImagesInfoFromImagesResource(imgsRes);
					imgsRes.setMapImages(mapImages);
				}

				map.put(key, resource);
			}

			return map;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public String resubmit(JobItem jobItem) throws Exception {
		try {
			StatisticalManagerFactory factory = getFactory();

			String computationId = factory.resubmitComputation(jobItem.getId());
			return computationId;

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public String startComputation(Algorithm algorithm,
			String computationTitle, String computationDescription)
			throws Exception {
		String catID = algorithm.getCategory().getId();
		SMComputationConfig config = new SMComputationConfig();
		List<Parameter> parameters = algorithm.getAlgorithmParameters();
		SMInputEntry[] list = new SMInputEntry[parameters.size()];
		int i = 0;
		for (Parameter p : algorithm.getAlgorithmParameters())
			list[i++] = new SMInputEntry(p.getName(), p.getValue());
		config.parameters(new SMEntries(list));
		config.algorithm(algorithm.getId());
		// create a computation request
		SMComputationRequest request = new SMComputationRequest();
		request.user(getUsername());
		request.title(computationTitle);
		request.description(computationDescription);
		request.config(config);

		try {
			StatisticalManagerFactory factory = getFactory();

			String computationId = factory.executeComputation(request);
			return computationId;

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	@Override
	public String saveImages(String computationId, Map<String, String> mapImages) {
		try {
			logger.log(Level.SEVERE, "INSIDE SAVE IMAGES ***");
			StatisticalManagerFactory factory = getFactory();
			SMComputation computation = factory.getComputation(computationId);
			String computationName = computation.title()==null ? computationId : computation.algorithm();
			computationName=computationName+"_param(";
			List<SMEntry>  parameters=computation.parameters();
			int i=0;
			for(SMEntry parameter: parameters)
			{ if(i!=0)
				computationName= computationName+"_"+parameter.value().toString();
			else
				computationName= computationName+parameter.value().toString();
			i++;

			}
			//logger.log(Level.SEVERE, "****************titleeeee "+ computationName+" ****** ***");
			computationName=computationName+")";
			computationName = FileSystemNameUtil.cleanFileName(computationName);

			Workspace workspace = getWorkspace();
			WorkspaceFolder rootFolder = workspace.getRoot();
			computationName = WorkspaceUtil.getUniqueName(computationName, rootFolder);
			
			// create the folder
			WorkspaceFolder folder = rootFolder.createFolder(computationName, "This folder contains images obtained from Statistical Manager");

			for (String key: mapImages.keySet()) {
				String url = mapImages.get(key);
				String fileName = StorageUtil.getFileName(url);
				fileName = FileSystemNameUtil.cleanFileName(fileName);				
				String description = "Image obtained from the StatisticalManager computation \""+computationName+"\""; 
				InputStream inputStream = StorageUtil.getStorageClientInputStream(url);
				folder.createExternalImageItem(fileName, description, null, inputStream);
			}

			return folder.getName();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
					
			//throw(e);
		}
	}
//	@Override
//	public String saveImages(String computationId, Map<String, String> mapImages)
//			throws Exception {
//		try {
//			StatisticalManagerFactory factory = getFactory();
//			SMComputation computation = factory.getComputation(computationId);
//			String computationName = computation.getTitle() == null ? computationId
//					: computation.getTitle();
//			computationName = FileSystemNameUtil.cleanFileName(computationName);
//
//			Workspace workspace = getWorkspace();
//			WorkspaceFolder rootFolder = workspace.getRoot();
//			computationName = WorkspaceUtil.getUniqueName(computationName,
//					rootFolder);
//
//			// create the folder
//			WorkspaceFolder folder = rootFolder.createFolder(computationName,
//					"This folder contains images obtained from TrendyLyzer");
//
//			for (String key : mapImages.keySet()) {
//				String url = mapImages.get(key);
//				String fileName = StorageUtil.getFileName(url);
//				fileName = FileSystemNameUtil.cleanFileName(fileName);
//				String description = "Image obtained from the TrendyLyzer \""
//						+ computationName + "\"";
//				InputStream inputStream = StorageUtil
//						.getStorageClientInputStream(url);
//				
//				//WorkspaceUtil.createExternalFile(folder, fileName+".jpg", description, "image/jpeg", inputStream);
//				
//				
//				folder.createExternalImageItem(fileName, description, null,
//						inputStream);
//			}
//
//			return folder.getName();
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw (e);
//		}
//
//	}

	private Workspace getWorkspace() throws WorkspaceFolderNotFoundException,
			InternalErrorException, HomeNotFoundException {
		HttpSession httpSession = this.getThreadLocalRequest().getSession();
		return SessionUtil.getWorkspace(httpSession);
	}

	@Override
	public void removeComputation(String computationId) throws Exception {
		logger.log(Level.SEVERE, "inside implementations service");
		StatisticalManagerFactory factory = getFactory();
		logger.log(Level.SEVERE, "take factory");
		logger.log(Level.SEVERE, "computationID: "+ computationId);
		factory.removeComputation(computationId);
		logger.log(Level.SEVERE, "remove");
	}

	@Override
	public ListLoadResult<JobItem> getListJobs() throws Exception {
		try {
			logger.log(Level.SEVERE, "Inside getListJob");
			StatisticalManagerFactory factory = getFactory();
			
			HashMap<String,Integer>algorithmNames= new HashMap<String,Integer>();
			List<JobItem> jobs = new ArrayList<JobItem>();
			int i=0;
			SMListGroupedAlgorithms groups= factory.getAlgorithmsUser();
			for (SMGroupedAlgorithms group : groups.thelist()) {
				//logger.log(Level.SEVERE, "trovati algorithmi");

				if (group.category().toString()
						.equals("OBIS_OBSERVATIONS_TRENDS")
						|| group.category().toString()
								.equals("OBIS_OBSERVATIONS_SPECIES_DATA")) {
					//logger.log(Level.SEVERE, "dentro l if");
					log.debug("Find trendylyzer algorithm");
					for (SMAlgorithm algorithm : group.thelist()) {
						//logger.log(Level.SEVERE, "*****in hash  "+ algorithm.getName());
					
						algorithmNames.put(algorithm.name(), i++);
					}
					}
				
			}
			SMComputations computations = factory
					.getComputations(getUsername());
			// ItemHistory[] historyList = service.getUserHistory().getList();
			if (computations.list() == null) {
				return null;
			}
			for (SMComputation computation : computations.list())
				{
				Iterator iterator = algorithmNames.keySet().iterator();  				   				
					String name= computation.algorithm();
				
					if(algorithmNames.containsKey(name))
					{
						jobs.add(convertSMComputationToJobItem(computation, false));
						Collections.sort(jobs, new Comparator<JobItem>() {
							@Override
							public int compare(JobItem jobItem1, JobItem jobItem2) {
								Date date1 = jobItem1.getCreationDate();
								Date date2 = jobItem2.getCreationDate();
								return (-1 * date1.compareTo(date2));
							}
						});
					}
				}
			

			return new BaseListLoadResult<JobItem>(jobs);
		} catch (Exception e) {
			// logger.error("An error occurred getting the job list", e);
			System.out.println("An error occurred getting the job list");
			e.printStackTrace();
			 return null;
//			throw e;
		}
	}

	@Override
	public Map<String, String> getParametersMapByJobId(String jobId) {
		try {
			StatisticalManagerFactory factory = getFactory();
			SMComputation computation = factory.getComputation(jobId);

			Map<String, String> parametersMap = new LinkedHashMap<String, String>();

			for (SMEntry entry : computation.parameters()) {
				String value = entry.value();
				if (value != null)
					value = value.replaceAll("#", ", ");
				parametersMap.put(entry.key(), value);
			}

			return parametersMap;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

}
