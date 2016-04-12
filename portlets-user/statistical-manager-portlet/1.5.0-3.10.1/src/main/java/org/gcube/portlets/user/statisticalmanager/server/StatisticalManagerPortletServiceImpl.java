package org.gcube.portlets.user.statisticalmanager.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.gcube.application.framework.accesslogger.library.impl.AccessLogger;
import org.gcube.application.framework.core.session.ASLSession;
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
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMResourceType;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMTables;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMTypeParameter;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMAbstractResource;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMComputation;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMEntries;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMEntry;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMFile;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMImport;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMInputEntry;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMObject;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMOperationInfo;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMResource;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMTable;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.StatisticalServiceType;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube.portal.custom.communitymanager.OrganizationsUtil;
import org.gcube.portlets.user.csvimportwizard.server.csv.CSVParserConfiguration;
import org.gcube.portlets.user.csvimportwizard.server.csv.CSVTargetRegistry;
import org.gcube.portlets.user.statisticalmanager.client.Constants;
import org.gcube.portlets.user.statisticalmanager.client.StatisticalManagerPortletService;
import org.gcube.portlets.user.statisticalmanager.client.bean.ComputationStatus;
import org.gcube.portlets.user.statisticalmanager.client.bean.ComputationStatus.Status;
import org.gcube.portlets.user.statisticalmanager.client.bean.CsvMetadata;
import org.gcube.portlets.user.statisticalmanager.client.bean.FileMetadata;
import org.gcube.portlets.user.statisticalmanager.client.bean.ImportStatus;
import org.gcube.portlets.user.statisticalmanager.client.bean.JobItem;
import org.gcube.portlets.user.statisticalmanager.client.bean.Operator;
import org.gcube.portlets.user.statisticalmanager.client.bean.OperatorCategory;
import org.gcube.portlets.user.statisticalmanager.client.bean.OperatorsClassification;
import org.gcube.portlets.user.statisticalmanager.client.bean.ResourceItem;
import org.gcube.portlets.user.statisticalmanager.client.bean.TableItemSimple;
import org.gcube.portlets.user.statisticalmanager.client.bean.output.ImagesResource;
import org.gcube.portlets.user.statisticalmanager.client.bean.output.MapResource;
import org.gcube.portlets.user.statisticalmanager.client.bean.output.Resource;
import org.gcube.portlets.user.statisticalmanager.client.bean.parameters.ColumnListParameter;
import org.gcube.portlets.user.statisticalmanager.client.bean.parameters.ColumnParameter;
import org.gcube.portlets.user.statisticalmanager.client.bean.parameters.EnumParameter;
import org.gcube.portlets.user.statisticalmanager.client.bean.parameters.FileParameter;
import org.gcube.portlets.user.statisticalmanager.client.bean.parameters.ListParameter;
import org.gcube.portlets.user.statisticalmanager.client.bean.parameters.ObjectParameter;
import org.gcube.portlets.user.statisticalmanager.client.bean.parameters.Parameter;
import org.gcube.portlets.user.statisticalmanager.client.bean.parameters.TabularListParameter;
import org.gcube.portlets.user.statisticalmanager.client.bean.parameters.TabularParameter;
import org.gcube.portlets.user.statisticalmanager.server.accounting.AlgorithmTimeLogEntry;
import org.gcube.portlets.user.statisticalmanager.server.accounting.FailureOutcomeAlgorithmLogEntry;
import org.gcube.portlets.user.statisticalmanager.server.accounting.ImportLogEntry;
import org.gcube.portlets.user.statisticalmanager.server.accounting.SuccessOutcomeAlgorithmLogEntry;
import org.gcube.portlets.user.statisticalmanager.server.accounting.UsedSmAlgorithmLogEntry;
import org.gcube.portlets.user.statisticalmanager.server.util.ObjectConverter;
import org.gcube.portlets.user.statisticalmanager.server.util.SessionUtil;
import org.gcube.portlets.user.statisticalmanager.server.util.StorageUtil;
import org.gcube.portlets.user.tdw.server.datasource.DataSourceFactoryRegistry;
import org.gcube.portlets.widgets.file_dw_import_wizard.server.file.TargetRegistry;

import com.extjs.gxt.ui.client.data.BaseListLoadResult;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.thoughtworks.xstream.XStream;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class StatisticalManagerPortletServiceImpl extends RemoteServiceServlet
		implements StatisticalManagerPortletService {
	public static AccessLogger accountinglog = AccessLogger.getAccessLogger();

	// private static final long CACHE_REFRESH_TIME = 30 * 60 * 1000; // 30
	// minutes

	private final static String SEPARATOR = AlgorithmConfiguration
			.getListSeparator();
	private static Logger logger = Logger
			.getLogger(StatisticalManagerPortletServiceImpl.class);
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
			// e.printStackTrace();
			throw new ServletException("Error initializing the db", e);
		}

		CSVTargetRegistry.getInstance().add(new StatisticalCSVTarget());
		TargetRegistry.getInstance().add(new StatisticalFileTarget());
		DataSourceFactoryRegistry.getInstance().add(
				new StatisticaManagerDataSourceFactory());

		/*
		 * try { // T();
		 * 
		 * // feeds.schedule(); } catch (Exception e) { e.printStackTrace(); }
		 */

		logger.info("StatisticalManager started!");

	}

	private StatisticalManagerFactory getFactory() throws Exception {
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

	private Workspace getWorkspace() throws WorkspaceFolderNotFoundException,
			InternalErrorException, HomeNotFoundException {
		HttpSession httpSession = this.getThreadLocalRequest().getSession();
		return SessionUtil.getWorkspace(httpSession);
	}

	private String getScope() {
		HttpSession httpSession = this.getThreadLocalRequest().getSession();
		return SessionUtil.getScope(httpSession);
	}

	@Override
	public List<OperatorsClassification> getOperatorsClassifications()
			throws Exception {

		try {

			List<OperatorsClassification> classifications = new ArrayList<OperatorsClassification>();
			StatisticalManagerFactory factory = getFactory();

			for (String classificationName : Constants.classificationNames) {
				OperatorsClassification classification = new OperatorsClassification(
						classificationName);
				List<OperatorCategory> categories = new ArrayList<OperatorCategory>();
				List<Operator> operators = new ArrayList<Operator>();

				// get and print algorithms
				SMListGroupedAlgorithms groups = (classificationName
						.equals(Constants.userClassificationName) ? factory
						.getAlgorithmsUser() : factory.getAlgorithms());

				if (groups == null)
					System.out.print("GROUPS OF ALGORITHMS IS NULL!");
				else
					System.out.print("GROUPS OF ALGORITHMS IS NOT NULL!");

				// get list categories
				for (SMGroupedAlgorithms group : groups.thelist()) {
					OperatorCategory category = new OperatorCategory(
							group.category(), "", "");
					OperatorCategory catSearch = DescriptionRepository
							.getOperatorCategory(category);
					if (catSearch != null)
						category = catSearch.clone();
					categories.add(category);

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
						category.addOperator(operator);
						operators.add(operator);
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
		} catch (Throwable e) {
			logger.error(
					"An error occurred getting the OperatorsClassifications list",
					e);
			System.out
					.println("An error occurred getting the OperatorsClassifications list");
			e.printStackTrace();
			// return null;
			throw new Exception(e.getLocalizedMessage());
		}
	}

	@Override
	public List<Parameter> getParameters(Operator operator) throws Exception {
		try {

			// computation creation
			String algorithmId = operator.getId();
			// String category = operator.getCategory().getId();
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

				// logger.info("	Param " + paramName);
				// logger.info("		description: " + paramDescription);
				// logger.info("		default value: " + defaultValue);
				// logger.info("		type name: " + smTypeName);
				// logger.info("		type values: ");
				// for (String value : smType.values())
				// logger.info("			type value: " + value);

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
					String referredTabularParameterName = smType.values()
							.get(0);
					ColumnParameter columnParam = new ColumnParameter(
							paramName, paramDescription,
							referredTabularParameterName, defaultValue);
					params.add(columnParam);
				} else if (smTypeName
						.equals(StatisticalServiceType.COLUMN_LIST)) {
					String referredTabularParameterName = smType.values()
							.get(0);
					ColumnListParameter columnListParameter = new ColumnListParameter(
							paramName, paramDescription,
							referredTabularParameterName, SEPARATOR);
					params.add(columnListParameter);
				} else if (smTypeName.equals(StatisticalServiceType.FILE)) {
					FileParameter fileParameter = new FileParameter(paramName,
							paramDescription); // TODO
					params.add(fileParameter);
				}
				// else if
				// (smTypeName.equals(StatisticalServiceType.BOUNDING_BOX))
				// {
				// BoundingBoxParameter bboxParameter = new
				// BoundingBoxParameter(paramName, paramDescription,
				// defaultValue,
				// "#");
				// params.add(bboxParameter);
				// }
			}

			return params;

		} catch (Throwable e) {
			logger.error("Error retrieving parameters: "
					+ e.getLocalizedMessage());
			e.printStackTrace();
			throw new Exception(e.getLocalizedMessage());
		}
	}

	@Override
	public String startComputation(Operator operator, String computationTitle,
			String computationDescription) throws Exception {
		try {

			UsedSmAlgorithmLogEntry logEntry = new UsedSmAlgorithmLogEntry(
					getUsername(), operator.getId());
			accountinglog.logEntry(getUsername(), getScope(), logEntry);
			logger.info("NEW COMPUTATION: " + operator.getId());
			logger.info("PARAMETERS:");
			for (Parameter p : operator.getOperatorParameters())
				logger.info("	" + p.getName() + "= " + p.getValue());

			// computation creation
			// String catId = operator.getCategory().getId();
			// SMComputation computation = new SMComputation(operator.getId(),
			// ComputationalAgentClass.fromString(cat), computationTitle); //
			// TODO

			// create computation config
			SMComputationConfig config = new SMComputationConfig();

			// create list SMEntries
			List<Parameter> parameters = operator.getOperatorParameters();
			SMInputEntry[] list = new SMInputEntry[parameters.size()];
			int i = 0;

			for (Parameter p : operator.getOperatorParameters())
				list[i++] = new SMInputEntry(p.getName(), p.getValue());
			config.parameters(new SMEntries(list));
			config.algorithm(operator.getId());

			// create a computation request
			SMComputationRequest request = new SMComputationRequest();
			request.user(getUsername());
			request.title(computationTitle);
			request.description(computationDescription);
			request.config(config);

			StatisticalManagerFactory factory = getFactory();

			String computationId = factory.executeComputation(request);

			return computationId;

		} catch (Throwable e) {
			logger.error("Error in computation: " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new Exception(e.getLocalizedMessage());
		}
	}

	@Override
	public String resubmit(JobItem jobItem) throws Exception {
		try {
			logger.info("RESUBMIT COMPUTATION: " + jobItem.getId());

			StatisticalManagerFactory factory = getFactory();

			String computationId = factory.resubmitComputation(jobItem.getId());
			return computationId;

		} catch (Throwable e) {
			logger.error("Error in resubmit: " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new Exception(e.getLocalizedMessage());
		}
	}

	public ComputationStatus getComputationStatus(String computationId)
			throws Exception {
		try {
			// logger.info("service get Computation Status..");
			StatisticalManagerFactory factory = getFactory();

			SMOperationInfo infos = factory.getComputationInfo(computationId,
					getUsername());

			// Status status=convertStatus(infos.getStatus());
			Status status = ObjectConverter.convertStatus(infos.status());
			// logger.info("before parsing  percetage: " + infos.percentage());
			double perc = Double.parseDouble(infos.percentage());

			// logger.info("STATUS: " + infos.status());
			// float status = Float.parseFloat(infos.getPercentage());
			// logger.info("PERCENTAGE: " + perc);

			ComputationStatus computationStatus = new ComputationStatus(status,
					perc);

			if (computationStatus.isTerminated()) {
				try {
					// logger.info("is terminate");
					// logger.info("call getComputation");

					SMComputation computation = factory
							.getComputation(computationId);
					// try to set the end date
					computationStatus.setEndDate(computation.completedDate()
							.getTime());

					if (computationStatus.isFailed()) {
						FailureOutcomeAlgorithmLogEntry succesAlgLogEntry = new FailureOutcomeAlgorithmLogEntry(
								computation.algorithm());
						accountinglog.logEntry(getUsername(), getScope(),
								succesAlgLogEntry);
						logger.info("computation is failed");
						SMAbstractResource abstractResource = computation
								.abstractResource();
						// generic resource
						SMResource smResource = abstractResource.resource();
						Resource resource = ObjectConverter
								.convertSmResourceToResource(smResource,
										getUsername());

						logger.info("error message is :"
								+ resource.getDescription());
						computationStatus.setErrResource(resource);
					}
					Date endDate = computation.completedDate().getTime();
					Date startDate = computation.submissionDate().getTime();
					int secondsBetween = (int) ((endDate.getTime() - startDate
							.getTime()) / 1000);
					AlgorithmTimeLogEntry timeElapsedentryLog = new AlgorithmTimeLogEntry(
							computation.algorithm(), secondsBetween);
					accountinglog.logEntry(getUsername(), getScope(),
							timeElapsedentryLog);
					SuccessOutcomeAlgorithmLogEntry succesAlgLogEntry = new SuccessOutcomeAlgorithmLogEntry(
							computation.algorithm());
					accountinglog.logEntry(getUsername(), getScope(),
							succesAlgLogEntry);
				} catch (Exception e) {
					logger.info(e.toString());

					// e.printStackTrace();
				}
			}
			return computationStatus;

		} catch (Throwable e) {
			//logger.error("Error in getComputationStatus: "
			//		+ e.getLocalizedMessage());
			//e.printStackTrace();
			throw new Exception(e.getLocalizedMessage());
		}
	}

	@Override
	public List<TableItemSimple> getTableItems(List<String> templates)
			throws Exception {
		try {
			StatisticalManagerDataSpace dataSpace = getDataSpaceService();

			// logger.trace("\n---LIST TABLES");
			SMTables smList = dataSpace.getTables(getUsername());
			// logger.trace("SIZE: " + smList.list().size());

			List<TableItemSimple> list = new ArrayList<TableItemSimple>();

			for (SMTable table : smList.list()) {
				String id = table.resourceId(), name = table.name(), description = table
						.description(), template = table.template();
				// logger.info("Name: " + name + ";\tDescr: " + description
				// + ";\tType: " + template); // table type

				if (templates == null)
					list.add(new TableItemSimple(id, name, description,
							template));
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
		} catch (Throwable e) {
			logger.error("Error in getTableItems:" + e.getLocalizedMessage());
			e.printStackTrace();
			throw new Exception(e.getLocalizedMessage());
		}

	}

	@Override
	public List<TableItemSimple> getFileItems(List<String> templates)
			throws Exception {
		try {
			StatisticalManagerDataSpace dataSpace = getDataSpaceService();

			// logger.trace("\n---LIST FILES");
			List<SMFile> smList = dataSpace.getFiles(getUsername());
			// logger.trace("SIZE: " + smList.size());

			List<TableItemSimple> list = new ArrayList<TableItemSimple>();

			for (SMFile file : smList) {
				String id = file.resourceId();
				String name = file.name();
				String description = file.description();
				// logger.info("Name: " + name + ";\tDescr: " + description);

				list.add(new TableItemSimple(id, name, description));
			}

			return list;
		} catch (Throwable e) {
			logger.error("Error in getFileItems:" + e.getLocalizedMessage());
			e.printStackTrace();
			throw new Exception(e.getLocalizedMessage());
		}
	}

	@Override
	public ListLoadResult<ResourceItem> getResourcesItems() throws Exception {
		try {
			StatisticalManagerDataSpace dataSpace = getDataSpaceService();
			String username = getUsername();

			List<SMResource> smResources = dataSpace.getResources(username,
					null);
			List<ResourceItem> resources = new ArrayList<ResourceItem>();

			for (SMResource smResource : smResources)
				// discrimino se object e creo un nuovo converete
				if (smResource.resourceType() == SMResourceType.FILE.ordinal()
						&& ((SMFile) smResource).mimeType().equals(
								"dwca/directory")) {
					logger.debug("dwca ");
					resources.addAll(ObjectConverter.convertSmDWCATableItem(
							smResource, getUsername()));
				} else {
					resources.add(ObjectConverter
							.convertSmTableToTableItem(smResource));
				}

			return new BaseListLoadResult<ResourceItem>(resources);

		} catch (Throwable e) {
			logger.error("Error in getResourcesItems() :"
					+ e.getLocalizedMessage());
			e.printStackTrace();
			// return null;
			throw new Exception(e.getLocalizedMessage());
		}
	}

	@Override
	public ListLoadResult<JobItem> getListJobs() throws Exception {
		try {
			StatisticalManagerFactory factory = getFactory();

			List<JobItem> jobs = new ArrayList<JobItem>();
			SMComputations computations = factory
					.getComputations(getUsername());
			// ItemHistory[] historyList = service.getUserHistory().getList();

			if (computations.list() == null)
				return null;
			for (SMComputation computation : computations.list())
				jobs.add(convertSMComputationToJobItem(computation, false));

			Collections.sort(jobs, new Comparator<JobItem>() {
				@Override
				public int compare(JobItem jobItem1, JobItem jobItem2) {
					Date date1 = jobItem1.getCreationDate();
					Date date2 = jobItem2.getCreationDate();
					return (-1 * date1.compareTo(date2));
				}
			});

			return new BaseListLoadResult<JobItem>(jobs);
		} catch (Exception e) {
			logger.error("An error occurred getting the job list", e);
			System.out.println("An error occurred getting the job list");
			e.printStackTrace();
			// return null;
			throw e;
		}
	}

	/**
	 * @param computation
	 * @return
	 */
	private JobItem convertSMComputationToJobItem(SMComputation computation,
			boolean loadResourceData) throws Exception {
		String computationId = "" + computation.operationId();
		String operatorId = computation.algorithm();
		String computationTitle = computation.title(); // TODO change name
		String computationDescription = computation.description();
		String categoryId = computation.category();
		Date startDate = computation.submissionDate() == null ? null
				: computation.submissionDate().getTime();
		Date endDate = computation.completedDate() == null ? null : computation
				.completedDate().getTime();
		String infrastructure = computation.infrastructure();
		float percentage = 0;
		Status status = ObjectConverter.convertStatus(computation
				.operationStatus());

		Resource resource = null;

		if (status == Status.RUNNING) {
			StatisticalManagerFactory factory = getFactory();
			SMOperationInfo infos = factory.getComputationInfo(computationId,
					getUsername());
			percentage = Float.parseFloat(infos.percentage());
		} else if (status == Status.COMPLETE) {
			SMAbstractResource abstractResource = computation
					.abstractResource();
			// generic resource
			SMResource smResource = abstractResource.resource();
			resource = ObjectConverter.convertSmResourceToResource(smResource,
					getUsername());
		}

		ComputationStatus computationStatus = new ComputationStatus(status,
				percentage);
		if (status == Status.FAILED) {
			SMAbstractResource abstractResource = computation
					.abstractResource();
			// generic resource
			SMResource smResource = abstractResource.resource();
			resource = ObjectConverter.convertSmResourceToResource(smResource,
					getUsername());
			computationStatus.setMessage(smResource.description());
		}

		return new JobItem(computationId, computationTitle,
				computationDescription, categoryId, operatorId, infrastructure,
				startDate, endDate, computationStatus, resource);
	}

	@Override
	public Resource getResourceByJobId(String jobId) {
		try {
			StatisticalManagerFactory factory = getFactory();

			SMComputation computation = factory.getComputation(jobId);
			JobItem jobItem = convertSMComputationToJobItem(computation, true);

			// try to add map data (if resource is an object)

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
			logger.error("Error in getResourceByJobId: "
					+ e.getLocalizedMessage());// in this case the resource
												// doesn't exist, nothing to do
			return null;
		}

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
			logger.error(e);
			// e.printStackTrace();
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
			logger.error(e);
			// e.printStackTrace();
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
			logger.error(e);
			// e.printStackTrace();
			throw e;
		}
	}

	@Override
	public String importTable(CsvMetadata csvMetadata) throws Exception {
		try {
			File tempFile = new File(csvMetadata.getFileAbsolutePath());

			StatisticalManagerDataSpace dataSpace = getDataSpaceService();
			String user = getUsername();
			String template = csvMetadata.getTemplate();

			TableTemplates tableTemplate = null;
			for (TableTemplates t : TableTemplates.values())
				if (template.contentEquals(t.toString())) {
					tableTemplate = t;
					break;
				}

			// if (template.equals("HSPEN"))
			// tableTemplate = TableTemplates.HSPEN;
			// else if (template.equals("HCAF"))
			// tableTemplate = TableTemplates.HCAF;
			// else if (template.equals("HSPEC"))
			// tableTemplate = TableTemplates.HSPEC;
			// else if (template.equals("OCCURRENCE_AQUAMAPS"))
			// tableTemplate = TableTemplates.OCCURRENCE_AQUAMAPS;
			// else if (template.equals("MINMAXLAT"))
			// tableTemplate = TableTemplates.MINMAXLAT;
			// else if (template.equals("TRAININGSET"))
			// tableTemplate = TableTemplates.TRAININGSET;
			// else if (template.equals("TESTSET"))
			// tableTemplate = TableTemplates.TESTSET;
			// else if (template.equals("GENERIC"))
			// tableTemplate = TableTemplates.GENERIC;
			// else if (template.equals("CLUSTER"))
			// tableTemplate = TableTemplates.CLUSTER;
			// else if (template.equals("OCCURRENCE_SPECIES"))
			// tableTemplate = TableTemplates.OCCURRENCE_SPECIES;
			// else if (template.equals("TIME_SERIES"))
			// tableTemplate = TableTemplates.TIMESERIES;
			ImportLogEntry logEntry = new ImportLogEntry();

			accountinglog.logEntry(getUsername(), getScope(), logEntry);
			String id = dataSpace.createTableFromCSV(tempFile,
					csvMetadata.isHasHeader(), csvMetadata.getDelimiterChar(),
					csvMetadata.getCommentChar(), csvMetadata.getTableName(),
					tableTemplate, csvMetadata.getDescription(), user);
			return id;
		} catch (Exception e) {
			logger.error(e);
			// e.printStackTrace();
			throw (e);
		}
	}

	@Override
	public String importFile(FileMetadata fileMetadata) throws Exception {
		try {
			// logger.info("call servlet importFile");
			// logger.info("file path: " + fileMetadata.getFileAbsolutePath());
			String id;
			File tempFile = new File(fileMetadata.getFileAbsolutePath());

			String user = getUsername();
			// logger.info("user " + user);
			StatisticalManagerDataSpace dataSpace = getDataSpaceService();
			// logger.info("took dataspace");
			// logger.info("type before check  is :" + fileMetadata.getType());
			if (fileMetadata.getType().equals("DARWINCORE")) {
				// logger.info("type is DARWINCORE");

				File taxaTempFile = new File(
						fileMetadata.getTaxaFileAbsolutePath());
				File vernacularTempFile = new File(
						fileMetadata.getVernacularFileAbsolutePath());
				// logger.info("vernacular and taxa file created");
				id = dataSpace.importFile(fileMetadata.getFileName(), tempFile,
						taxaTempFile, vernacularTempFile,
						fileMetadata.getDescription(), user, "DARWINCORE");
				// String id = dataSpace.createTableFromCSV(tempFile,
				// csvMetadata.isHasHeader(), csvMetadata.getDelimiterChar(),
				// csvMetadata.getCommentChar(), csvMetadata.getTableName(),
				// tableTemplate, csvMetadata.getDescription(), user);
				// logger.info("id is " + id);

			} else {
				// logger.info("type is general");
				ImportLogEntry logEntry = new ImportLogEntry();

				accountinglog.logEntry(getUsername(), getScope(), logEntry);

				id = dataSpace.importFile(fileMetadata.getFileName(), tempFile,
						null, null, fileMetadata.getDescription(), user,
						fileMetadata.getType());
				// logger.info("id is " + id);

			}

			return id;
		} catch (Exception e) {
			logger.error(e);

			// e.printStackTrace();
			throw (e);
		}
	}

	@Override
	public CsvMetadata getCsvMetadataFromCsvImporterWizard() throws Exception {
		HttpSession httpSession = this.getThreadLocalRequest().getSession();
		CSVParserConfiguration csvParserConfiguration = (CSVParserConfiguration) httpSession
				.getAttribute("csvParserConfiguration");
		String csvImportFilePath = (String) httpSession
				.getAttribute("csvImportFilePath");
		boolean hasHeader = csvParserConfiguration.isHasHeader();
		String delimiter = "" + csvParserConfiguration.getDelimiter();
		String comment = "" + csvParserConfiguration.getComment();

		return new CsvMetadata(hasHeader, csvImportFilePath, delimiter, comment);
	}

	@Override
	public FileMetadata getFilePathFromImporterWizard() throws Exception {
		FileMetadata result;
		// logger.info("getFilePathFromImporterWizard");
		HttpSession httpSession = this.getThreadLocalRequest().getSession();
		String fileImportFilePath = (String) httpSession
				.getAttribute("fileImportPath");
		// logger.info("filePaht: " + fileImportFilePath);
		String type = (String) httpSession.getAttribute("typeFile");
		// logger.info("type: " + type);

		if (type == "DARWINCORE") {
			String fileTaxaImportFilePath = (String) httpSession
					.getAttribute("generatedTaxaFilePath");
			// logger.info("fileTaxaImportFilePath: " + fileTaxaImportFilePath);
			String fileVernacularImportFilePath = (String) httpSession
					.getAttribute("generatedVernacular");
			// logger.info("fileVernacularImportFilePath: "
			// + fileVernacularImportFilePath);

			result = new FileMetadata(fileImportFilePath,
					fileTaxaImportFilePath, fileVernacularImportFilePath, type);
		} else
			result = new FileMetadata(fileImportFilePath, type);

		// logger.info("result.filePath: " + result.getFileAbsolutePath());
		// logger.info("result.type: " + result.getType());

		return result;
	}

	@Override
	public List<ImportStatus> getImportsStatus(Date todayDate) {
		String user = getUsername();

		StatisticalManagerDataSpace dataSpace = getDataSpaceService();

		List<SMImport> importers = dataSpace.getImports(user, null);
		List<ImportStatus> listStatus = new ArrayList<ImportStatus>();

		for (SMImport importer : importers) {
			Status status = ObjectConverter.convertStatus(importer
					.operationStatus());

			Date date = null;
			if (importer.submissionDate() != null)
				date = importer.submissionDate().getTime();
			ImportStatus importStatus = new ImportStatus();
			importStatus.setId("" + importer.operationId());
			importStatus.setFileName(importer.fileName());
			importStatus.setDate(date);
			importStatus.setStatus(status);

			if (importer.abstractResource() != null) {
				if (importer.abstractResource().resource() != null) {

					Resource res = ObjectConverter.convertSmResourceToResource(
							importer.abstractResource().resource(),
							getUsername());
					importStatus.setResource(res);
					importStatus.setResourceId(importer.abstractResource()
							.abstractResourceId());
				}
			}

			listStatus.add(importStatus);
		}

		Collections.sort(listStatus, new Comparator<ImportStatus>() {
			@Override
			public int compare(ImportStatus o1, ImportStatus o2) {
				if (o1.getDate() == null || o2.getDate() == null)
					return 0;
				else
					return o2.getDate().compareTo(o1.getDate());
			}
		});

		return listStatus;
	}

	@Override
	public ImportStatus getImportStatusById(String importId) {
		StatisticalManagerDataSpace dataSpace = getDataSpaceService();

		SMImport smImport = dataSpace.getImporter(importId);
		Status status = ObjectConverter.convertStatus(smImport
				.operationStatus());
		Date date = (smImport.submissionDate() == null) ? null : smImport
				.submissionDate().getTime();

		ImportStatus importStatus = new ImportStatus();

		importStatus.setId("" + smImport.operationId());
		importStatus.setFileName(smImport.portalLogin());
		if (smImport.abstractResource() != null) {
			if (smImport.abstractResource().resource() != null) {
				Resource res = ObjectConverter.convertSmResourceToResource(
						smImport.abstractResource().resource(), getUsername());
				importStatus.setResource(res);
				importStatus.setResourceId(smImport.abstractResource()
						.abstractResourceId());
			}
		}
		importStatus.setDate(date);
		importStatus.setStatus(status);

		return importStatus;
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
				// String fileName = StorageUtil.getFileName(url);
				String fileName = key;
				fileName = FileSystemNameUtil.cleanFileName(fileName);
				String description = "Image obtained from the StatisticalManager computation \""
						+ computationName + "\"";
				InputStream inputStream = StorageUtil
						.getStorageClientInputStream(url);
				// folder.createExternalImageItem(fileName, description, null,
				// inputStream);

				WorkspaceUtil.createExternalFile(folder, fileName, description,
						null, inputStream);

			}

			return folder.getName();
		} catch (Exception e) {
			logger.error("Error in save images: " + e.getLocalizedMessage());

			// e.printStackTrace();
			throw (e);
		}
	}

	@Override
	public void removeResource(String id) throws Exception {
		try {
			StatisticalManagerDataSpace dataSpace = getDataSpaceService();
			dataSpace.removeTable(id);
		} catch (Exception e) {
			logger.error(e);

			// e.printStackTrace();
			throw (e);
		}
	}

	@Override
	public void removeImport(String id) throws Exception {
		try {
			StatisticalManagerDataSpace dataSpace = getDataSpaceService();
			dataSpace.removeImport(id);
		} catch (Exception e) {
			logger.error(e);

			// e.printStackTrace();
			throw (e);
		}
	}

	@Override
	public String exportResource(String folderId, String fileName,
			ResourceItem resourceItem) throws Exception {
		try {
			StatisticalManagerDataSpace dataSpace = getDataSpaceService();
			String id = resourceItem.getId();
			InputStream inputStream;
			if (resourceItem.isTable()) {
				File file = dataSpace.exportTable(id);
				inputStream = new FileInputStream(file);
			} else
				inputStream = StorageUtil
						.getStorageClientInputStream(resourceItem.getUrl());

			Workspace workspace = getWorkspace();

			// create a file into the <folderId> folder
			WorkspaceFolder wsFolder = (WorkspaceFolder) workspace
					.getItem(folderId);
			String realFileName = WorkspaceUtil.getUniqueName(fileName,
					wsFolder);

			wsFolder.createExternalFileItem(realFileName,
					"Csv table exported by Statistical Manager", "text/csv",
					inputStream);
			// file.delete(); // TODO

			return realFileName;

		} catch (Exception e) {
			logger.error(e);

			// e.printStackTrace();
			throw (e);
		}
	}

	@Override
	public void checkSession() {
		ASLSession session = SessionUtil.getSession(this
				.getThreadLocalRequest().getSession());
		String toWrite = "\n" + new Date()
				+ " Statistical Manager check: scope= *" + session.getScope()
				+ "* Statistical Manager  user=" + session.getUsername();
		try {
			File file = new File(OrganizationsUtil.getTomcatFolder()
					+ "/sm-check-session-log.txt");

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(toWrite);
			bw.close();

			// _log.info("WROTE: " + toWrite);

		} catch (IOException e) {
			logger.error(e);

			// e.printStackTrace();
		}
	}
}
