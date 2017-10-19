package org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.server;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

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
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMGroupedAlgorithms;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMListGroupedAlgorithms;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMParameter;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMParameters;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMTables;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMTypeParameter;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMComputation;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMEntry;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMFile;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMResource;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMTable;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.StatisticalServiceType;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.portlets.user.tdw.server.datasource.DataSourceFactoryRegistry;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.Constants;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.StatisticalManagerWidgetService;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.Operator;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.OperatorCategory;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.OperatorsClassification;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.TableItemSimple;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.output.ImagesResource;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.output.MapResource;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.output.Resource;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.parameters.ColumnListParameter;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.parameters.ColumnParameter;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.parameters.EnumParameter;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.parameters.FileParameter;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.parameters.ListParameter;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.parameters.ObjectParameter;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.parameters.Parameter;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.parameters.TabularListParameter;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.parameters.TabularParameter;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.server.util.SessionUtil;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.server.util.StorageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.thoughtworks.xstream.XStream;

/**
 * The server side implementation of the RPC service.
 */
@RemoteServiceRelativePath("statman")
public class StatisticalManagerWidgetServiceImpl extends RemoteServiceServlet
implements StatisticalManagerWidgetService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final long CACHE_REFRESH_TIME = 30 * 60 * 1000; // 30 minutes

	private final static String SEPARATOR = AlgorithmConfiguration
			.getListSeparator();
	private static Logger logger = LoggerFactory.getLogger(StatisticalManagerWidgetServiceImpl.class);
	private Comparator<Operator> operatorsComparator = new Comparator<Operator>() {
		@Override
		public int compare(Operator op1, Operator op2) {
			return op1.getName().compareTo(op2.getName());
		}
	};

	// protected FeedScheduler feeds = new FeedScheduler(CACHE_REFRESH_TIME);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() throws ServletException {
		super.init();
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new ServletException("Error initializing the db", e);
		}
		DataSourceFactoryRegistry.getInstance().add(
				new StatisticaManagerDataSourceFactory());
		//
		//		ExecutionComputationRegistry.getInstance().add(
		//				new ExecutionComputationDefault());

	}

	private StatisticalManagerFactory getFactory() {
		HttpSession httpSession = this.getThreadLocalRequest().getSession();
		return SessionUtil.getFactory(httpSession);
	}

	private StatisticalManagerDataSpace getDataSpaceService() {
		HttpSession httpSession = this.getThreadLocalRequest().getSession();
		return SessionUtil.getDataSpaceService(httpSession);
	}

	private String getUsername() {
		HttpSession httpSession = this.getThreadLocalRequest().getSession();
		return SessionUtil.getUsername(httpSession);
	}

	//
	// private Workspace getWorkspace() throws WorkspaceFolderNotFoundException,
	// InternalErrorException, HomeNotFoundException {
	// HttpSession httpSession = this.getThreadLocalRequest().getSession();
	// return SessionUtil.getWorkspace(httpSession);
	// }

	private String getScope() {
		HttpSession httpSession = this.getThreadLocalRequest().getSession();
		return SessionUtil.getScope(httpSession);
	}

	public List<OperatorsClassification> getOperatorsClassifications(
			ArrayList<String> listOfAlg) throws Exception {
		logger.debug("FABIO : getOperator start");
		try {

			List<OperatorsClassification> classifications = new ArrayList<OperatorsClassification>();
			StatisticalManagerFactory factory = getFactory();

			for (String classificationName : Constants.classificationNames) {
				OperatorsClassification classification = new OperatorsClassification(
						classificationName);
				List<OperatorCategory> categories = new ArrayList<OperatorCategory>();
				List<Operator> operators = new ArrayList<Operator>();
				System.out
				.println("***********###******User :" + getUsername());
				System.out.println("*************######*******Scope :"
						+ getScope());

				// get and print algorithms
				SMListGroupedAlgorithms groups = (classificationName
						.equals(Constants.userClassificationName) ? factory
								.getAlgorithmsUser() : factory.getAlgorithms());

				// get list categories
				for (SMGroupedAlgorithms group : groups.thelist()) {
					OperatorCategory category = new OperatorCategory(
							group.category(), "", "");
					OperatorCategory catSearch = DescriptionRepository
							.getOperatorCategory(category);
					if (catSearch != null)
						category = catSearch.clone();
					// categories.add(category);
					int first_tiem = 0;
					for (SMAlgorithm algorithm : group.thelist()) {
						String operatorId = algorithm.name();
						Operator operator = new Operator(operatorId, "", "",
								category);
						Operator opSearch = DescriptionRepository
								.getOperator(operator);
						if (opSearch != null) {
							operator = opSearch.clone();
							operator.setCategory(category);
						}
						String algDescr = algorithm.description();
						if (algDescr != null) {
							operator.setDescription(algDescr);
							operator.setBriefDescription(algDescr);
						}
						System.out.println("algorithms is:" + operatorId);
						if (listOfAlg.size() != 0) {
							System.out
							.println("size list :" + listOfAlg.size());

							if (listOfAlg.contains(operatorId)) {
								System.out.println("is contained :"
										+ operatorId);

								category.addOperator(operator);
								operators.add(operator);
								if (first_tiem == 0) {
									categories.add(category);
									first_tiem++;

								}
							}
						} else {
							category.addOperator(operator);
							operators.add(operator);
							if (first_tiem == 0) {
								categories.add(category);
								first_tiem++;

							}
						}
					}
					Collections.sort(category.getOperators(),
							operatorsComparator);
				}

				Collections.sort(operators, operatorsComparator);

				classification.setOperatorCategories(categories);
				classification.setOperators(operators);

				classifications.add(classification);
			}

			return classifications;
		} catch (Exception e) {
			logger.error(
					"An error occurred getting the OperatorsClassifications list",
					e);
			System.out
			.println("An error occurred getting the OperatorsClassifications list");
			e.printStackTrace();
			// return null;
			throw e;
		}
	}

	@Override
	public List<Parameter> getParameters(Operator operator) {
		// computation creation
		String algorithmId = operator.getId();
		String category = operator.getCategory().getId();
		// SMComputation computation = new
		// SMComputation(algorithm,ComputationalAgentClass.fromString(category),"Description computation");

		// get parameters info
		logger.info("Parameters of algorithm " + operator.getId());
		StatisticalManagerFactory factory = getFactory();
		SMParameters smParams = factory.getAlgorithmParameters(algorithmId);

		List<Parameter> params = new ArrayList<Parameter>();
		for (SMParameter smParam : smParams.list()) {
			SMTypeParameter smType = smParam.type();
			StatisticalServiceType smTypeName = smType.name();

			String paramName = smParam.name();
			String paramDescription = smParam.description();
			String defaultValue = smParam.defaultValue();

			logger.info("	Param " + paramName);
			logger.info("		description: " + paramDescription);
			logger.info("		default value: " + defaultValue);
			logger.info("		type name: " + smTypeName);
			logger.info("		type values: ");
			for (String value : smType.values())
				logger.info("			type value: " + value);

			if (smTypeName.equals(StatisticalServiceType.TABULAR)) {
				TabularParameter tabularParam = new TabularParameter(paramName,
						paramDescription);
				for (String value : smType.values())
					tabularParam.addTemplate(value);
				params.add(tabularParam);
			} else if (smTypeName.equals(StatisticalServiceType.TABULAR_LIST)) {
				TabularListParameter tabularListParam = new TabularListParameter(
						paramName, paramDescription, SEPARATOR);
				for (String value : smType.values())
					tabularListParam.addTemplate(value);
				params.add(tabularListParam);
			} else if (smTypeName.equals(StatisticalServiceType.PRIMITIVE)) {
				String objectType = smType.values().get(0);
				ObjectParameter objectParam = new ObjectParameter(paramName,
						paramDescription, objectType, defaultValue);
				params.add(objectParam);
			} else if (smTypeName.equals(StatisticalServiceType.ENUM)) {
				List<String> values = smType.values();
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
				ColumnParameter columnParam = new ColumnParameter(paramName,
						paramDescription, referredTabularParameterName,
						defaultValue);
				params.add(columnParam);
			} else if (smTypeName.equals(StatisticalServiceType.COLUMN_LIST)) {
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
			// else if (smTypeName.equals(StatisticalServiceType.BOUNDING_BOX))
			// {
			// BoundingBoxParameter bboxParameter = new
			// BoundingBoxParameter(paramName, paramDescription, defaultValue,
			// "#");
			// params.add(bboxParameter);
			// }
		}

		return params;
	}


	@Override
	public List<TableItemSimple> getTableItems(List<String> templates,Collection<TableItemSimple> callerDefinedTables) {
		logger.error("Get tables , caller Defined are "+callerDefinedTables);
		try{	
			StatisticalManagerDataSpace dataSpace = getDataSpaceService();
		// logger.trace("\n---LIST TABLES");
		SMTables smList = dataSpace.getTables(getUsername());
		// logger.trace("SIZE: "+ smList.getList().length);

		List<TableItemSimple> list = new ArrayList<TableItemSimple>();

		if(callerDefinedTables!=null) list.addAll(callerDefinedTables);

		for (SMTable table : smList.list()) {
			String id = table.resourceId(), name = table.name(), description = table
					.description(), template = table.template();
			logger.info("Name: " + name + ";\tDescr: " + description
					+ ";\tType: " + template); // table type

			if (templates == null)
				list.add(new TableItemSimple(id, name, description, template,
						false));
			else
				for (String t : templates)
					if (t.toUpperCase().contentEquals("GENERIC")
							|| t.toUpperCase().contentEquals(
									template.toUpperCase())) {
						list.add(new TableItemSimple(id, name, description,
								template, false));
						break;
					}
		}
		logger.error("Found Tables "+list);
		return list;
		}catch(Throwable t){
			logger.error("ERROR While getting tables ",t);
			throw t;
		}
	}

	@Override
	public List<TableItemSimple> getFileItems(List<String> templates) {
		StatisticalManagerDataSpace dataSpace = getDataSpaceService();

		// logger.trace("\n---LIST FILES");
		List<SMFile> smList = dataSpace.getFiles(getUsername());
		// logger.trace("SIZE: "+ smList.size());

		List<TableItemSimple> list = new ArrayList<TableItemSimple>();

		for (SMFile file : smList) {
			String id = file.resourceId(), name = file.name(), description = file
					.description();
			logger.info("Name: " + name + ";\tDescr: " + description);

			list.add(new TableItemSimple(id, name, description));
		}

		return list;
	}




	@Override
	public Map<String, String> getParametersMapByJobId(String jobId)
			throws Exception {

		try {
			logger.info("\nJOBID=" + jobId + "\n");
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
			throw (e);
		}

	}

	@Override
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

	@Override
	public Map<String, Resource> getMapFromMapResource(MapResource mapRes)
			throws Exception {
		try {
			String url = mapRes.getUrl();

			InputStream is = StorageUtil.getStorageClientInputStream(url);

			// object serializer
			XStream xstream = new XStream();
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

	@Override
	public void removeComputation(String computationId) throws Exception {
		StatisticalManagerFactory factory = getFactory();
		factory.removeComputation(computationId);

	}

	@Override
	public String saveImages(String computationId, Map<String, String> mapImages)
			throws Exception {
		try {
			StatisticalManagerFactory factory = getFactory();
			SMComputation computation = factory.getComputation(computationId);
			String computationName = computation.title() == null ? computationId
					: computation.title();
			computationName = FileSystemNameUtil.cleanFileName(computationName);

			Workspace workspace = getWorkspace();
			WorkspaceFolder rootFolder = workspace.getRoot();
			computationName = WorkspaceUtil.getUniqueName(computationName,
					rootFolder);

			// create the folder
			WorkspaceFolder folder = rootFolder
					.createFolder(computationName,
							"This folder contains images obtained from Statistical Manager");

			for (String key : mapImages.keySet()) {
				String url = mapImages.get(key);
				String fileName = StorageUtil.getFileName(url);
				fileName = FileSystemNameUtil.cleanFileName(fileName);
				String description = "Image obtained from the StatisticalManager computation \""
						+ computationName + "\"";
				InputStream inputStream = StorageUtil
						.getStorageClientInputStream(url);
				folder.createExternalImageItem(fileName, description, null,
						inputStream);
			}

			return folder.getName();
		} catch (Exception e) {
			e.printStackTrace();
			throw (e);
		}
	}

	@Override
	public void removeResource(String id) throws Exception {
		try {
			StatisticalManagerDataSpace dataSpace = getDataSpaceService();
			dataSpace.removeTable(id);
		} catch (Exception e) {
			e.printStackTrace();
			throw (e);
		}
	}

	@Override
	public void removeImport(String id) throws Exception {
		try {
			StatisticalManagerDataSpace dataSpace = getDataSpaceService();
			dataSpace.removeImport(id);
		} catch (Exception e) {
			e.printStackTrace();
			throw (e);
		}
	}

	// @Override
	// public String exportResource(String folderId, String fileName,
	// ResourceItem resourceItem) throws Exception {
	// try {
	// StatisticalManagerDataSpace dataSpace = getDataSpaceService();
	// String id = resourceItem.getId();
	// InputStream inputStream;
	// if (resourceItem.isTable()) {
	// File file = dataSpace.exportTable(id);
	// inputStream = new FileInputStream(file);
	// }
	// else
	// inputStream =
	// StorageUtil.getStorageClientInputStream(resourceItem.getUrl());
	//
	// Workspace workspace = getWorkspace();
	//
	// // create a file into the <folderId> folder
	// WorkspaceFolder wsFolder = (WorkspaceFolder)workspace.getItem(folderId);
	// String realFileName = WorkspaceUtil.getUniqueName(fileName, wsFolder);
	//
	// wsFolder.createExternalFileItem(realFileName,
	// "Csv table exported by Statistical Manager", "text/csv", inputStream);
	// // file.delete(); // TODO
	//
	// return realFileName;
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// throw(e);
	// }
	// }

	private Workspace getWorkspace() throws WorkspaceFolderNotFoundException,
	InternalErrorException, HomeNotFoundException {
		HttpSession httpSession = this.getThreadLocalRequest().getSession();
		return SessionUtil.getWorkspace(httpSession);
	}
}
