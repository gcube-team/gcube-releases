package org.gcube.informationsystem.exporter.mapper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.gcube.common.authorization.client.Constants;
import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.informationsystem.exporter.mapper.exception.CreateException;
import org.gcube.informationsystem.exporter.mapper.exception.UpdateException;
import org.gcube.informationsystem.impl.utils.ISMapper;
import org.gcube.informationsystem.model.ISConstants;
import org.gcube.informationsystem.model.entity.Facet;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.resource.ResourceAvailableInAnotherContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.resource.ResourceNotFoundException;
import org.gcube.informationsystem.resourceregistry.client.ResourceRegistryClient;
import org.gcube.informationsystem.resourceregistry.client.ResourceRegistryClientFactory;
import org.gcube.informationsystem.resourceregistry.publisher.ResourceRegistryPublisher;
import org.gcube.informationsystem.resourceregistry.publisher.ResourceRegistryPublisherFactory;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.resources.discovery.icclient.ICFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class GCoreResourceMapper<GR extends org.gcube.common.resources.gcore.Resource, R extends Resource> {

	/**
	 * Logger
	 */
	private static Logger logger = LoggerFactory.getLogger(GCoreResourceMapper.class);

	public static final String EXPORTED = "EXPORTED";
	public static final String EXPORTED_FROM_OLD_GCORE_IS = "EXPORTED_FROM_OLD_GCORE_IS";

	public static final String MAPPING_ERROR = "mapping";
	public static final String PUBLISHING_ERROR = "publishing";
	public static final String TYPE = "type";
	public static final String ID = "id";
	public static final String ERROR = "error";
	public static final String DATE = "date";

	public static final String EXCEPTION_TYPE = "exceptionType";
	public static final String CREATE = "create";
	public static final String UPDATE = "update";

	protected final Class<GR> grClass;
	protected final Class<R> rClass;
	protected final boolean filteredReport;

	public static final String UTF8 = "UTF-8";

	protected ResourceRegistryPublisher resourceRegistryPublisher;
	protected ResourceRegistryClient resourceRegistryClient;

	public static String getCurrentContextName() {
		String token = SecurityTokenProvider.instance.get();
		AuthorizationEntry authorizationEntry = null;
		try {
			authorizationEntry = Constants.authorizationService().get(token);
		} catch (Exception e) {
			return ScopeProvider.instance.get();
		}
		return authorizationEntry.getContext();
	}

	public static String getDateString(Calendar calendar) {
		Date date = calendar.getTime();
		SimpleDateFormat format = new SimpleDateFormat(ISConstants.DATETIME_PATTERN);
		return format.format(date);
	}

	public File getFile(Class<?> grClass, String contextFullName, String dateString) {
		return new File(contextFullName.replace("/", "_") + "-" + grClass.getSimpleName() + "-" + dateString
				+ "-exporter.json");
	}

	protected GCoreResourceMapper(Class<GR> grClass, Class<R> rClass, boolean filteredReport) {
		this.grClass = grClass;
		this.rClass = rClass;
		this.resourceRegistryPublisher = ResourceRegistryPublisherFactory.create();
		this.resourceRegistryClient = ResourceRegistryClientFactory.create();
		this.filteredReport = filteredReport;
	}

	protected List<GR> getAll() {
		SimpleQuery query = ICFactory.queryFor(grClass);
		DiscoveryClient<GR> client = ICFactory.clientFor(grClass);
		return client.submit(query);
	}

	protected String getStringAsUTF8(String s) throws UnsupportedEncodingException {
		byte bytes[] = s.getBytes("ISO-8859-1");
		return new String(bytes, UTF8);
	}

	protected R create(R r) throws ResourceRegistryException {
		return resourceRegistryPublisher.createResource(r);
	}

	protected R update(R r) throws ResourceRegistryException {
		return resourceRegistryPublisher.updateResource(r);
	}

	protected R read(UUID uuid) throws ResourceRegistryException {
		return resourceRegistryClient.getInstance(rClass, uuid);
	}

	protected R createOrUpdate(R r) throws ResourceRegistryException {

		UUID uuid = r.getHeader().getUUID();
		boolean update = false;

		try {
			resourceRegistryClient.exists(rClass, uuid);
			update = true;
		} catch (ResourceNotFoundException e) {
			update = false;
		} catch (ResourceAvailableInAnotherContextException e) {
			// This code should never be reached because this should be fixed in
			// map function
			resourceRegistryPublisher.addResourceToContext(uuid);
			try {
				Thread.sleep(100);
			} catch (Exception ee) {
			}
			update = true;
		}

		if (update) {
			logger.debug("Resource with UUID {} exist. It will be updated", uuid);
			try {
				return update(r);
			} catch (ResourceRegistryException e) {
				throw new UpdateException(e);
			}
		} else {
			logger.debug("Resource with UUID {} does not exist. It will be created", uuid);
			try {
				return create(r);
			} catch (ResourceRegistryException e) {
				throw new CreateException(e);
			}
		}

	}

	private ObjectNode addNodeToArray(ArrayNode arrayNode, ObjectMapper objectMapper, GR gr, Exception e) {
		ObjectNode objectNode = objectMapper.createObjectNode();
		objectNode.put(TYPE, grClass.getSimpleName());
		objectNode.put(ID, gr.id());
		objectNode.put(ERROR, e.getMessage());
		arrayNode.add(objectNode);
		return objectNode;
	}

	protected String getUsername() throws Exception {
		String token = SecurityTokenProvider.instance.get();
		return Constants.authorizationService().get(token).getClientInfo().getId();
	}

	/*
	 * 
	 * @SuppressWarnings("deprecation") protected Home getHome(HomeManager
	 * manager) throws Exception{ Home home = null; String scope =
	 * ScopeProvider.instance.get(); if(scope!=null){ home =
	 * manager.getHome(getUsername()); }else{ home = manager.getHome(); } return
	 * home; }
	 * 
	 * protected WorkspaceFolder getExporterFolder(Workspace ws) throws
	 * Exception { WorkspaceFolder root = ws.getRoot();
	 * 
	 * WorkspaceFolder exporterFolder = null;
	 * if(!ws.exists(ISExporterPluginDeclaration.NAME, root.getId())){ String
	 * folderDescription = String.
	 * format("The folder is used by %s plugin to store informations regarding failures exporting old GCore Resource to new Resource Registry"
	 * , ISExporterPluginDeclaration.NAME); exporterFolder =
	 * ws.createFolder(ISExporterPluginDeclaration.NAME, folderDescription,
	 * root.getId()); }else{ exporterFolder = (WorkspaceFolder)
	 * ws.find(ISExporterPluginDeclaration.NAME, root.getId()); } return
	 * exporterFolder; }
	 * 
	 * public static final String APPLICATION_JSON_MIMETYPE =
	 * "application/json";
	 * 
	 * private static final String FOLDER_DESCRIPTION =
	 * "Failures Report Folder for " + ISExporterPluginDeclaration.NAME;
	 * 
	 * protected FolderItem publishFileToWorkspace(File file) throws Exception {
	 * HomeManagerFactory factory = HomeLibrary.getHomeManagerFactory();
	 * HomeManager manager = factory.getHomeManager(); Home home =
	 * getHome(manager); Workspace ws = home.getWorkspace(); WorkspaceFolder
	 * exporterFolder = getExporterFolder(ws); FileInputStream fileInputStream =
	 * new FileInputStream(file);
	 * 
	 * FolderItem folderItem = WorkspaceUtil.createExternalFile( exporterFolder,
	 * file.getName(), FOLDER_DESCRIPTION, APPLICATION_JSON_MIMETYPE,
	 * fileInputStream); return folderItem; }
	 */

	protected abstract R map(GR gr) throws Exception;

	protected void notifyFailures(int allSize, List<GR> failed) {
		String contextName = getCurrentContextName();

		if (failed.size() == 0) {
			logger.debug("No needs to create an empty report besause there were no exporting failures on {}",
					contextName);
			return;
		}

		logger.warn("-------------------------------------------------------");
		logger.warn("{} : {} of {} ({} failures) {}s were exported as {}s", contextName, allSize - failed.size(),
				allSize, failed.size(), grClass.getSimpleName(), rClass.getSimpleName());
		logger.warn("-------------------------------------------------------\n\n");

		ObjectMapper objectMapper = new ObjectMapper();

		ObjectNode objectNode = objectMapper.createObjectNode();

		Calendar calendar = Calendar.getInstance();
		String dateString = getDateString(calendar);
		objectNode.put(DATE, dateString);
		ObjectNode context = objectNode.putObject(contextName);
		ArrayNode mappingArrayNode = context.putArray(MAPPING_ERROR);
		ArrayNode publishingArrayNode = context.putArray(PUBLISHING_ERROR);

		for (GR gr : failed) {
			logger.trace("-------------------------------------------------------");

			if (filteredReport) {
				try {
					UUID.fromString(gr.id());
				} catch (Exception e) {
					logger.debug(
							"{} with has id {} which is not a valid UUID. The reports are filtered and such an error is not reported.",
							gr.getClass().getSimpleName(), gr.id());
					continue;
				}
			}

			R r = null;
			try {
				r = map(gr);
			} catch (Exception e) {
				addNodeToArray(mappingArrayNode, objectMapper, gr, e);
				logger.trace("Error exporting {}. The problem was on mapping {} with UUID {}", gr,
						grClass.getSimpleName(), gr.id());
				logger.trace("-------------------------------------------------------\n");
				continue;
			}

			try {
				createOrUpdate(r);
			} catch (Exception e) {
				if (e.getCause() instanceof NullPointerException) {
					logger.error("This MUST BE A BUG. Please Investigate");
				}

				if (e.getMessage().contains(
						"com.orientechnologies.orient.server.distributed.task.ODistributedOperationException")) {
					logger.error("This is an OrientDB distributed Issue");
				}

				ObjectNode node = addNodeToArray(publishingArrayNode, objectMapper, gr, e);
				if (e instanceof CreateException) {
					node.put(EXCEPTION_TYPE, CREATE);
				}

				if (e instanceof UpdateException) {
					node.put(EXCEPTION_TYPE, UPDATE);
				}

				try {
					logger.trace("Error exporting {}. The problem was on publishing {} as {}", gr,
							rClass.getSimpleName(), ISMapper.marshal(r));
				} catch (JsonProcessingException e1) {
					logger.trace("", e1);
				}
			}
			logger.trace("-------------------------------------------------------\n");
		}

		if(filteredReport){
			if(mappingArrayNode.size()==0 && publishingArrayNode.size()==0){
				logger.debug("No need to produce JSON reports because alla the errors where filtered as requested by the input parameters.");
				return;
			}
		}
		
		try {
			String json = objectMapper.writeValueAsString(objectNode);

			File file = getFile(rClass, contextName, dateString);
			synchronized (file) {
				try (FileWriter fw = new FileWriter(file, true);
						BufferedWriter bw = new BufferedWriter(fw);
						PrintWriter out = new PrintWriter(bw)) {
					out.println(json);
					out.flush();
				} catch (IOException e) {
					throw e;
				}
			}

			/*
			 * try { publishFileToWorkspace(file); file.delete(); }catch
			 * (Exception e) {
			 * logger.error("Error while saving file {} on Workspace",
			 * file.getName(), e); // TODO Use the Social Notification }
			 */

		} catch (Exception e) {
			logger.error("Error exporting JSON error result", e);
		}

		logger.trace("-------------------------------------------------------\n\n\n\n\n");
	}

	protected R mapAndPublish(GR gr) throws Exception {
		R r = map(gr);

		List<? extends Facet> facets = r.getIdentificationFacets();
		for (Facet f : facets) {
			f.setAdditionalProperty(EXPORTED, EXPORTED_FROM_OLD_GCORE_IS);
		}

		return createOrUpdate(r);
	}

	public void export() {

		List<GR> all = getAll();

		logger.debug("-------------------------------------------------------");
		logger.debug("Going to export {} {}s as {}s", all.size(), grClass.getSimpleName(), rClass.getSimpleName());
		logger.debug("-------------------------------------------------------");

		List<GR> failed = new ArrayList<>();

		for (GR gr : all) {
			try {
				Thread.sleep(300);
				mapAndPublish(gr);
			} catch (Exception e) {
				failed.add(gr);
			}
		}

		notifyFailures(all.size(), failed);

	}

}
