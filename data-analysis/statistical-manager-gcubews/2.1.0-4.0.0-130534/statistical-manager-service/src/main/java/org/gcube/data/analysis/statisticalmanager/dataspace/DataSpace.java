package org.gcube.data.analysis.statisticalmanager.dataspace;

import static org.gcube.data.streams.dsl.Streams.convert;
import static org.gcube.data.streams.dsl.Streams.pipe;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.field.StringField;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.tika.io.IOUtils;
import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.faults.GCUBEUnrecoverableException;
import org.gcube.common.core.porttypes.GCUBEPortType;
import org.gcube.common.core.types.VOID;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalFile;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.statisticalmanager.Configuration;
import org.gcube.data.analysis.statisticalmanager.SMOperationStatus;
import org.gcube.data.analysis.statisticalmanager.SMOperationType;
import org.gcube.data.analysis.statisticalmanager.SMResourceType;
import org.gcube.data.analysis.statisticalmanager.ServiceContext;
import org.gcube.data.analysis.statisticalmanager.dataspace.exporter.CSVExporter;
import org.gcube.data.analysis.statisticalmanager.dataspace.importer.CSVImporter;
import org.gcube.data.analysis.statisticalmanager.dataspace.importer.OccurrenceStreamConverter;
import org.gcube.data.analysis.statisticalmanager.exception.HibernateManagementException;
import org.gcube.data.analysis.statisticalmanager.exception.StatisticalManagerException;
import org.gcube.data.analysis.statisticalmanager.persistence.DataBaseManager;
import org.gcube.data.analysis.statisticalmanager.persistence.HibernateManager;
import org.gcube.data.analysis.statisticalmanager.persistence.RemoteStorage;
import org.gcube.data.analysis.statisticalmanager.persistence.SMPersistenceManager;
import org.gcube.data.analysis.statisticalmanager.stubs.DataSpacePortType;
import org.gcube.data.analysis.statisticalmanager.stubs.SMCreateTableFromCSVRequest;
import org.gcube.data.analysis.statisticalmanager.stubs.SMCreateTableFromDataStreamRequest;
import org.gcube.data.analysis.statisticalmanager.stubs.SMCreatedTablesRequest;
import org.gcube.data.analysis.statisticalmanager.stubs.SMFiles;
import org.gcube.data.analysis.statisticalmanager.stubs.SMGetFilesRequest;
import org.gcube.data.analysis.statisticalmanager.stubs.SMImporters;
import org.gcube.data.analysis.statisticalmanager.stubs.SMImportersRequest;
import org.gcube.data.analysis.statisticalmanager.stubs.SMResourceNotFoundFault;
import org.gcube.data.analysis.statisticalmanager.stubs.SMResources;
import org.gcube.data.analysis.statisticalmanager.stubs.SMTables;
import org.gcube.data.analysis.statisticalmanager.stubs.SMimportDwcaFileRequest;
import org.gcube.data.analysis.statisticalmanager.stubs.SMimportFileRequest;
import org.gcube.data.analysis.statisticalmanager.util.ServiceUtil;
import org.gcube.data.spd.model.binding.Bindings;
import org.gcube.data.spd.model.products.OccurrencePoint;
import org.gcube.data.streams.Stream;
import org.gcube.data.streams.generators.Generator;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube_system.namespaces.data.analysis.statisticalmanager.types.SMFile;
import org.gcube_system.namespaces.data.analysis.statisticalmanager.types.SMImport;
import org.gcube_system.namespaces.data.analysis.statisticalmanager.types.SMResource;
import org.gcube_system.namespaces.data.analysis.statisticalmanager.types.SMTable;
import org.hibernate.Query;
import org.hibernate.Session;

public class DataSpace extends GCUBEPortType implements DataSpacePortType {


	@Override
	protected ServiceContext getServiceContext() {
		return ServiceContext.getContext();
	}

	@Override
	public String getDBParameters(String string) throws RemoteException,GCUBEFault {
		try{
			DataBaseManager dbMng=DataBaseManager.get();

			String url = dbMng.getUrlDB() + "?user="
					+ dbMng.getUsername() + "&password="
					+ dbMng.getPassword();

			return url;
		}catch(Exception e){
			logger.error("Unable to access DB",e);
			throw new GCUBEFault("Unable to access Database parameters");
		}
	}

	@Override
	public SMImporters getImporters(SMImportersRequest request)
			throws RemoteException, GCUBEFault {
		try{
			HibernateManager hMng=HibernateManager.get();
			Session session = hMng.getSessionFactory().openSession();

			try {
				Query query = session
						.createQuery("select importer from SMImport  importer "
								+ "where importer.portalLogin like :name ");
				// + "and importer.objectType like :template");

				query.setParameter("name",
						(request.getUser() != null) ? request.getUser() : "%");
				// query.setParameter("template",
				// (request.getObjectType() != null) ? request.getObjectType() :
				// "%");
				@SuppressWarnings("unchecked")
				List<Object> objects = query.list();

				SMImport[] importers = objects
						.toArray(new SMImport[objects.size()]);
				return new SMImporters(importers);

			} finally {
				hMng.closeSession(session);
			}
		}catch(Exception e){
			logger.error("Unable to access Persistence",e);
			throw new GCUBEFault("Unable to access persistence");
		}
	}

	@Override
	public long createTableFromDataStream(
			final SMCreateTableFromDataStreamRequest request)
					throws RemoteException, GCUBEFault {

		try {

			final String callerScope=ScopeProvider.instance.get();

			final long importerId = SMPersistenceManager.addImporter(request);

			Stream<GenericRecord> genericRecords = convert(
					URI.create(request.getRsLocator())).of(GenericRecord.class)
					.withDefaults();
			Generator<GenericRecord, OccurrencePoint> generator = new Generator<GenericRecord, OccurrencePoint>() {
				@Override
				public OccurrencePoint yield(GenericRecord element) {
					try {
						return Bindings.fromXml(((StringField) element
								.getField("result")).getPayload());
					} catch (Exception e) {
						return null;
					}
				}
			};
			final Stream<OccurrencePoint> stream = pipe(genericRecords)
					.through(generator);

			Thread th = new Thread() {
				@Override
				public void run() {
					String resourceId = null;
					try {						
						logger.debug("Init import from stream under scope "+callerScope);
						ScopeProvider.instance.set(callerScope);
						OccurrenceStreamConverter converter = new OccurrenceStreamConverter(
								DataBaseManager.get(), stream);
						converter.run();

						resourceId = converter.getTableName();

						SMTable table = new SMTable(request.getTableType());
						table.setResourceType(SMResourceType.TABULAR.ordinal());
						table.setResourceId(resourceId);
						table.setName(request.getTableName());
						table.setDescription(request.getDescription());

						table.setCreationDate(Calendar.getInstance());
						table.setPortalLogin(request.getUser());
						table.setProvenance(SMOperationType.IMPORTED.ordinal());
						table.setOperationId(importerId);

						SMPersistenceManager.addCreatedResource(importerId,
								table);

					} catch (Exception e) {						
						try {
							SMPersistenceManager.setOperationStatus(importerId,"Import failed ",ServiceUtil.formatDetailedErrorMessage(e),SMOperationStatus.FAILED);
						} catch (Exception e1) {
							logger.fatal("Unable to accessPersistence ",e1);
						} 
					}
				}
			};
			th.start();

			return importerId;
		} catch (Exception e) {
			logger.error("Unexpected Error ",e);
			throw new GCUBEUnrecoverableException(e).toFault();
		}
	}

	@Override
	public long createTableFromCSV(final SMCreateTableFromCSVRequest request)
			throws RemoteException, GCUBEFault {

		try {
			final long importerId = SMPersistenceManager.addImporter(request);
			final String callerScope=ScopeProvider.instance.get();
			logger.debug("retrieve file ");
			logger.debug("Locator :" + request.getRsLocator());



			//			final File file = RSWrapper.getStreamFromLocator(new URI(request
			//					.getRsLocator()));

			final File file=new RemoteStorage().importById(request.getRsLocator());


			logger.debug("File created " + file);

			Thread th = new Thread() {
				@Override
				public void run() {
					try {
						logger.debug("Init import under scope "+callerScope);
						ScopeProvider.instance.set(callerScope);
						CSVImporter converter = new CSVImporter(file,
								request.isHasHeader(), request.getTableName(),
								request.getTableType(), request.getDelimiter(),
								request.getCommentChar());
						String resourceId = converter.toTabularData();
						logger.debug("Import completed with resource id "
								+ resourceId);

						SMTable table = new SMTable(request.getTableType());
						table.setResourceType(SMResourceType.TABULAR.ordinal());
						table.setResourceId(resourceId);
						table.setName(request.getTableName());
						table.setDescription(request.getDescription());
						table.setCreationDate(Calendar.getInstance());
						table.setPortalLogin(request.getUser());
						table.setProvenance(SMOperationType.IMPORTED.ordinal());
						table.setOperationId(importerId);

						SMPersistenceManager.addCreatedResource(table);
						SMPersistenceManager.addCreatedResource(importerId,
								table);

					}catch(HibernateManagementException e){
						
						
					} catch (Exception e) {
						logger.error("Import failed ", e);
						logger.error("Messagge: "+e.getMessage(), e);
						try {
							SMPersistenceManager.setOperationStatus(importerId,"Import failed.",e.getMessage(),
									SMOperationStatus.FAILED);
						} catch (Exception e1) {
							logger.fatal("Unable to accessPersistence ",e1);
						} 
					}
				}
			};
			th.start();

			return importerId;
		} catch (Exception e) {
			logger.error("Unexpected Error ",e);
			throw new GCUBEUnrecoverableException(e).toFault();
		}

	}

	@Override
	public SMTables getTables(SMCreatedTablesRequest request)
			throws RemoteException, GCUBEFault {
		try{
			HibernateManager hMng=HibernateManager.get();
			Session session = hMng.getSessionFactory().openSession();
			try {

				Query query = session
						.createQuery("select table from SMTable table "
								+ "where (table.portalLogin like :name or table.portalLogin = null) "
								+ "and table.template like :template");

				query.setParameter("name",
						(request.getUser() != null) ? request.getUser() : "%");
				String template = request.getTemplate();
				query.setParameter("template", ((template != null) && !template
						.equals(TableTemplates.GENERIC)) ? template : "%");
				@SuppressWarnings("unchecked")
				List<Object> objects = query.list();

				SMTable[] tables = objects.toArray(new SMTable[objects.size()]);
				return new SMTables(tables);

			} finally {
				hMng.closeSession(session);
			}
		}catch(Exception e){
			logger.error("Unable to access persistence");
			throw new GCUBEFault("Unable to access persistence");
		}
	}

	@Override
	public SMImport getImporter(String importerId) throws RemoteException,GCUBEFault {
		try{
			HibernateManager hMng=HibernateManager.get();
			Session session = hMng.getSessionFactory().openSession();
			try {
				Query query = session
						.createQuery("select importer from SMImport  importer "
								+ "where importer.operationId = :operationId");

				query.setParameter("operationId", Long.valueOf(importerId));

				@SuppressWarnings("unchecked")
				List<Object> objects = query.list();

				return (SMImport) objects.get(0);

			} finally {
				hMng.closeSession(session);
			}
		}catch(Exception e){
			logger.error("Unable to access persistence",e);
			throw new GCUBEFault("Unable to access persistence");
		}
	}

	@Override
	public VOID removeImporter(String operationId) throws RemoteException,GCUBEFault {
		try{
			SMPersistenceManager.removeImporter(Long.valueOf(operationId));
			return new VOID();
		}catch(Exception e){
			logger.error("Unable to access persistence",e);
			throw new GCUBEFault("Unable to access persistence");
		}
	}

	@Override
	public SMResources getResources(SMCreatedTablesRequest request)	throws RemoteException, GCUBEFault {
		try{
			List<SMResource> resources = SMPersistenceManager.getResources(
					request.getUser(), request.getTemplate());
			return new SMResources(resources.toArray(new SMResource[resources.size()]));
		}catch(Exception e){
			logger.error("Unable to access persistence",e);
			throw new GCUBEFault("Unable to access persistence");
		}
	}

	@Override
	public VOID removeTable(String tableId) throws RemoteException, GCUBEFault {
		try{
			SMPersistenceManager.removeResource(tableId,
					Boolean.parseBoolean(Configuration.getProperty(Configuration.FORCE_COMPUTATION_REMOVAL)));
			return new VOID();
		}catch(Exception e){
			logger.error("Unable to access persistence",e);
			throw new GCUBEFault("Unable to access persistence");
		}
	}

	@Override
	public String exportTable(String tableId) throws RemoteException,
	SMResourceNotFoundFault, GCUBEFault {

		File file = null;
		try {
			file = File.createTempFile("export", "csv");
			CSVExporter exporter = new CSVExporter(tableId, file);
			exporter.exporterToFile();
			return new RemoteStorage().putFile(file,true);
		} catch (SQLException e) {
			logger.error("Resource not retrieved ", e);
			throw new SMResourceNotFoundFault();
		} catch (StatisticalManagerException e) {
			logger.debug("Data base connection internal error ", e);
			throw new GCUBEUnrecoverableException(e).toFault();
		} catch (IOException e) {
			logger.error("Temp file to export did not create", e);
			throw new GCUBEUnrecoverableException(e).toFault();
		}catch (Exception e) {
			logger.error("Unexpected Error ",e);
			throw new GCUBEUnrecoverableException(e).toFault();
		}finally{
			try{
				if(file!=null)FileUtils.forceDelete(file);
			}catch(Throwable t){
				logger.warn("Unable to delete file "+file.getAbsolutePath(),t);
			}
		}

	}

	@Override
	public SMFiles getFiles(SMGetFilesRequest request) throws RemoteException,GCUBEFault {
		try{
			HibernateManager hMng=HibernateManager.get();
			Session session = hMng.getSessionFactory().openSession();
			try {
				Query query = session
						.createQuery("select file from SMFile file "
								+ "where (file.portalLogin like :name or file.portalLogin = null)");

				query.setParameter("name",
						(request.getUser() != null) ? request.getUser() : "%");
				@SuppressWarnings("unchecked")
				List<Object> objects = query.list();

				SMFile[] files = objects.toArray(new SMFile[objects.size()]);
				return new SMFiles(files);

			} finally {
				hMng.closeSession(session);
			}
		}catch(Exception e){
			logger.error("Unable to access persistence",e);
			throw new GCUBEFault("Unable to access persistence");
		}
	}



	@Override
	public long importFromFile(final SMimportFileRequest request)
			throws RemoteException, GCUBEFault {
		try {
			final String callerScope=ScopeProvider.instance.get();
			final long importerId = SMPersistenceManager.addImporter(request);
			logger.debug("Received request import file  "+request.getRsLocator()+" under scope "+callerScope);			

			Thread th = new Thread() {
				@Override
				public void run() {
					InputStream is=null;
					try {						
						ScopeProvider.instance.set(callerScope);
						logger.debug("Importing file, scope is  "+ScopeProvider.instance.get());
						RemoteStorage stg=new RemoteStorage();
						is= stg.getStreamByUrl((stg.getUrlById(request.getRsLocator())));						
						
						logger.debug("Importing file, got input stream, final scope "+ScopeProvider.instance.get());
						WorkspaceFolder appFolder = ServiceUtil
								.getWorkspaceSMFolder(ServiceUtil.getWorkspaceHome(request.getUser()));
						logger.debug("Importing file, Got workspace home, final scope "+ScopeProvider.instance.get());
						ExternalFile f = appFolder.createExternalFileItem(
								request.getFileName(),
								request.getDescription(), null, is);
						logger.debug("Importing file, stored file in home, final scope "+ScopeProvider.instance.get());
						String url = f.getPublicLink();

						logger.debug("Importing file, got public link, final scope "+ScopeProvider.instance.get());
						SMFile file = new SMFile("mimeType",
								request.getFileName(), url);
						file.setPortalLogin(request.getUser());
						file.setResourceType(SMResourceType.FILE.ordinal());
						file.setResourceId(UUID.randomUUID().toString());
						file.setDescription(request.getDescription());
						file.setName(request.getFileName());
						file.setProvenance(SMOperationType.IMPORTED.ordinal());
						file.setCreationDate(Calendar.getInstance());
						file.setOperationId(importerId);

						logger.debug("Inserting reference for imported file under scope "+ScopeProvider.instance.get());
						SMPersistenceManager.addCreatedResource(file);
						
						logger.debug("Inserting reference for importer of file under scope "+ScopeProvider.instance.get());
						SMPersistenceManager.addCreatedResource(importerId,
								file);
						logger.debug("Import file request served, scope "+ScopeProvider.instance.get());
					} catch (Exception e) {						
						try {
							SMPersistenceManager.setOperationStatus(importerId,"Import failed ",ServiceUtil.formatDetailedErrorMessage(e),SMOperationStatus.FAILED);
						} catch (Exception e1) {
							logger.fatal("Unable to accessPersistence ",e1);
						} 
					}finally{
						IOUtils.closeQuietly(is);
					}
				}
			};
			th.start();

			return importerId;
		} catch (Exception ex) {
			logger.error("Unexpected Error ",ex);
			throw new GCUBEUnrecoverableException(ex).toFault();
		}
	}

	@Override
	public long importFromDwcaFile(final SMimportDwcaFileRequest request)
			throws RemoteException, GCUBEFault {
		try {
			final long importerId = SMPersistenceManager.addImporter(request);
			final String callerScope=ScopeProvider.instance.get();
			logger.debug("retrieve file ");
			logger.debug("Locator :" + request.getRsLocator());
			logger.debug("Locator taxa :" + request.getTaxaLocator());
			logger.debug("Locator taxa :" + request.getVernacularLocator());



			Thread th = new Thread() {
				@Override
				public void run() {
					InputStream importIs=null;
					InputStream taxaIs=null;
					InputStream vernacularIs=null;
					ScopeProvider.instance.set(callerScope);
					try {
						RemoteStorage stg=new RemoteStorage();
						importIs=stg.getStreamByUrl(stg.getUrlById(request.getRsLocator()));
						taxaIs=stg.getStreamByUrl(stg.getUrlById(request.getTaxaLocator()));
						vernacularIs=stg.getStreamByUrl(stg.getUrlById(request.getVernacularLocator()));

						WorkspaceFolder appFolder = ServiceUtil
								.getWorkspaceSMFolder(ServiceUtil.getWorkspaceHome(request.getUser()));
						WorkspaceFolder subFolder = appFolder.createFolder(
								request.getFileName(), "SM import DWCA");

						//						String dirName = File.separator + request.getFileName();

						subFolder.createExternalFileItem(request.getFileName(),request.getDescription(), null, importIs);
						subFolder.createExternalFileItem(request.getFileName()+ "_Taxa", request.getDescription(),null, taxaIs);
						subFolder.createExternalFileItem(request.getFileName()+ "_Vernacular",request.getDescription(), null,vernacularIs);

						String url = subFolder.getPath();

						SMFile file = new SMFile("dwca/directory",
								request.getFileName(), url);
						file.getMimeType();
						file.setPortalLogin(request.getUser());
						file.setResourceType(SMResourceType.FILE.ordinal());
						file.setResourceId(UUID.randomUUID().toString());
						file.setDescription(request.getDescription());
						file.setName(request.getFileName());
						file.setProvenance(SMOperationType.IMPORTED.ordinal());
						file.setCreationDate(Calendar.getInstance());
						file.setOperationId(importerId);

						SMPersistenceManager.addCreatedResource(file);
						SMPersistenceManager.addCreatedResource(importerId,
								file);

					} catch (Exception e) {						
						try {
							SMPersistenceManager.setOperationStatus(importerId,"Import failed ",ServiceUtil.formatDetailedErrorMessage(e),SMOperationStatus.FAILED);
						} catch (Exception e1) {
							logger.fatal("Unable to accessPersistence ",e1);
						}					
					}finally{
						IOUtils.closeQuietly(importIs);
						IOUtils.closeQuietly(taxaIs);
						IOUtils.closeQuietly(vernacularIs);
					}
				}
			};
			th.start();
			logger.debug("Return  " + importerId);
			return importerId;

		} catch (Exception ex) {
			logger.error("Unexpected Error ",ex);
			throw new GCUBEUnrecoverableException(ex).toFault();
		}
	}
}
