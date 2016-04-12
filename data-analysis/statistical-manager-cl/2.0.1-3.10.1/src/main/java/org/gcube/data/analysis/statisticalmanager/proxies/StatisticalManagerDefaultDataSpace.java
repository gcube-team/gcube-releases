package org.gcube.data.analysis.statisticalmanager.proxies;

import static org.gcube.data.streams.dsl.Streams.publish;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.GenericRecordDefinition;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.record.field.FieldDefinition;
import gr.uoa.di.madgik.grs.record.field.StringField;
import gr.uoa.di.madgik.grs.record.field.StringFieldDefinition;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gcube.common.clients.Call;
import org.gcube.common.clients.delegates.AsyncProxyDelegate;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.common.clients.exceptions.FaultDSL;
import org.gcube.common.clients.stubs.jaxws.JAXWSUtils.Empty;
import org.gcube.data.analysis.statisticalmanager.exception.ResourceNotFoundException;
import org.gcube.data.analysis.statisticalmanager.stubs.DataSpaceStub;
import org.gcube.data.analysis.statisticalmanager.stubs.storage.RemoteStorage;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMCreateTableFromCSVRequest;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMCreateTableFromDataStreamRequest;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMCreatedTablesRequest;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMFiles;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMGetFilesRequest;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMImporters;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMImportersRequest;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMResources;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMTables;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMimportDwcaFileRequest;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMimportFileRequest;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMFile;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMImport;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMResource;
import org.gcube.data.spd.model.binding.Bindings;
import org.gcube.data.spd.model.products.OccurrencePoint;
import org.gcube.data.streams.Stream;
import org.gcube.data.streams.publishers.RecordFactory;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;

public class StatisticalManagerDefaultDataSpace implements
		StatisticalManagerDataSpace {
	Logger log = Logger.getLogger("");
	private final AsyncProxyDelegate<DataSpaceStub> delegate;

	public StatisticalManagerDefaultDataSpace(
			ProxyDelegate<DataSpaceStub> delegate) {
		super();
		this.delegate = new AsyncProxyDelegate<DataSpaceStub>(delegate);
	}

	@Override
	public String createTableFromCSV(final File file, final boolean hasHeader,
			final String delimiter, final String comment,
			final String tableName, final TableTemplates tableTemplate,
			final String description, final String user) {

		try {
			RemoteStorage storage=new RemoteStorage();
			final String fileId=storage.storeFile(file, true);
			

			Call<DataSpaceStub, String> call = new Call<DataSpaceStub, String>() {
				@Override
				public String call(DataSpaceStub endpoint) throws Exception {

					SMCreateTableFromCSVRequest request = new SMCreateTableFromCSVRequest();
					request.user(user);
					request.fileName(file.getName());
					request.description(description);
					request.hasHeader(hasHeader);
					request.delimiter(delimiter);
					request.commentChar(comment);
					request.rsLocator(fileId);
					request.tableName(tableName);
					request.tableType(tableTemplate.toString());

					return String.valueOf(endpoint.createTableFromCSV(request));
				}
			};

			return delegate.make(call);
		} catch (Exception e) {
			throw FaultDSL.again(e).asServiceException();
		}

	}

	@Override
	public String createTableFromDataStream(Stream<OccurrencePoint> points,
			final String tableName, final String description, final String user) {

		final URI rs = publish(points).using(
				new RecordFactory<OccurrencePoint>() {

					@Override
					public Record newRecord(OccurrencePoint element)
							throws RuntimeException {

						GenericRecord gr = new GenericRecord();
						try {
							gr.setFields(new StringField[] { new StringField(
									Bindings.toXml(element)) });
						} catch (Exception e) {
							throw new RuntimeException(e);
						}

						return gr;
					}

					@Override
					public RecordDefinition[] definitions() {
						StringFieldDefinition fieldDefinition = new StringFieldDefinition(
								"result");
						RecordDefinition[] defs = new RecordDefinition[] { // A
																			// gRS
																			// can
																			// contain
																			// a
																			// number
																			// of
																			// different
																			// record
																			// definitions
						new GenericRecordDefinition((new FieldDefinition[] { // A
																				// record
																				// can
																				// contain
																				// a
																				// number
																				// of
																				// different
																				// field
																				// definitions
								fieldDefinition // The definition of the field
								})) };
						return defs;
					}

				}).withDefaults();

		Call<DataSpaceStub, String> call = new Call<DataSpaceStub, String>() {
			@Override
			public String call(DataSpaceStub endpoint) throws Exception {

				SMCreateTableFromDataStreamRequest request = new SMCreateTableFromDataStreamRequest();
				request.user(user);
				request.description(description);
				request.rsLocator(rs.toString());
				request.tableName(tableName);
				request.tableType(TableTemplates.OCCURRENCE_SPECIES.toString());

				return String.valueOf(endpoint
						.createTableFromDataStream(request));
			}
		};

		try {
			return delegate.make(call);
		} catch (Exception e) {
			throw FaultDSL.again(e).asServiceException();
		}
	}

	@Override
	public String getDBParameters(final String tableId) {

		Call<DataSpaceStub, String> call = new Call<DataSpaceStub, String>() {
			@Override
			public String call(DataSpaceStub endpoint) throws Exception {
				return endpoint.getDBParameters(tableId);
			}
		};

		try {
			return delegate.make(call);
		} catch (Exception e) {
			throw FaultDSL.again(e).asServiceException();
		}
	}

	private SMTables getTables(final SMCreatedTablesRequest request) {

		Call<DataSpaceStub, SMTables> call = new Call<DataSpaceStub, SMTables>() {

			@Override
			public SMTables call(DataSpaceStub endpoint) throws Exception {
				return endpoint.getTables(request);
			};
		};

		try {
			return delegate.make(call);
		} catch (Exception e) {
			throw FaultDSL.again(e).asServiceException();
		}
	}

	@Override
	public SMTables getTables(final String user) {

		SMCreatedTablesRequest request = new SMCreatedTablesRequest();
		request.user(user);
		return getTables(request);

	}

	@Override
	public SMTables getTables(final String user, final String template) {

		SMCreatedTablesRequest request = new SMCreatedTablesRequest();
		request.user(user);
		request.template(template);
		return getTables(request);
	}

	@Override
	public List<SMImport> getImports(final String user, final String template) {

		Call<DataSpaceStub, SMImporters> call = new Call<DataSpaceStub, SMImporters>() {

			@Override
			public SMImporters call(DataSpaceStub endpoint) throws Exception {
				return endpoint.getImporters(new SMImportersRequest(template,
						user));
			};
		};

		try {
			SMImporters imports = delegate.make(call);
			if (imports.theList() != null) {
				return imports.theList();
			} else
				return new ArrayList<SMImport>();

		} catch (Exception e) {
			throw FaultDSL.again(e).asServiceException();
		}
	}

	@Override
	public SMImport getImporter(final String importId) {

		Call<DataSpaceStub, SMImport> call = new Call<DataSpaceStub, SMImport>() {

			@Override
			public SMImport call(DataSpaceStub endpoint) throws Exception {
				return endpoint.getImporter(importId);
			};
		};

		try {
			return delegate.make(call);
		} catch (Exception e) {
			throw FaultDSL.again(e).asServiceException();
		}

	}

	@Override
	public void removeImport(final String importerId) {
		Call<DataSpaceStub, Empty> call = new Call<DataSpaceStub, Empty>() {

			@Override
			public Empty call(DataSpaceStub endpoint) throws Exception {
				// return endpoint.removeImporter(importerId);
				endpoint.removeImporter(importerId);
				return new Empty();

			};
		};

		try {
			delegate.make(call);
		} catch (Exception e) {
			throw FaultDSL.again(e).asServiceException();
		}

	}

	@Override
	public List<SMResource> getResources(final String user,
			final String... template) {

		Call<DataSpaceStub, SMResources> call = new Call<DataSpaceStub, SMResources>() {

			@Override
			public SMResources call(DataSpaceStub endpoint) throws Exception {
				SMCreatedTablesRequest request = new SMCreatedTablesRequest();
				request.page(15);

				request.user(user);

				if (template != null)
					request.template(template[0]);
				return endpoint.getResources(request);
			};
		};

		try {
			SMResources resources = delegate.make(call);
			if (resources.list() != null) {
				return resources.list();
			} else {
				return new ArrayList<SMResource>();
			}
		} catch (Exception e) {
			throw FaultDSL.again(e).asServiceException();
		}
	}

	@Override
	public void removeTable(final String tableId) {

		Call<DataSpaceStub, Empty> call = new Call<DataSpaceStub, Empty>() {

			@Override
			public Empty call(DataSpaceStub endpoint) throws Exception {
				endpoint.removeTable(tableId);
				return new Empty();

			};
		};

		try {
			delegate.make(call);
		} catch (Exception e) {
			throw FaultDSL.again(e).asServiceException();
		}
	}

	@Override
	public File exportTable(final String tableId)
			throws ResourceNotFoundException {

		Call<DataSpaceStub, String> call = new Call<DataSpaceStub, String>() {

			@Override
			public String call(DataSpaceStub endpoint) throws Exception {
				return endpoint.exportTable(tableId);
			};
		};

		try {
			String locator = delegate.make(call);
			File f=File.createTempFile("smp", ".csv");
			new RemoteStorage().downloadFile(locator, f.getAbsolutePath());
			return f;
		} catch (Exception e) {
			throw FaultDSL.again(e).as(ResourceNotFoundException.class);
		}
	}

	@Override
	public String exportTableToStorage(final String tableId)
			throws ResourceNotFoundException {

		Call<DataSpaceStub, String> call = new Call<DataSpaceStub, String>() {

			@Override
			public String call(DataSpaceStub endpoint) throws Exception {
				return endpoint.exportTable(tableId);
			};
		};

		try {
			return delegate.make(call);
		} catch (Exception e) {
			throw FaultDSL.again(e).as(ResourceNotFoundException.class);
		}
	}
	
	
	
	
	@Override
	public List<SMFile> getFiles(String user) {

		final SMGetFilesRequest request = new SMGetFilesRequest();
		request.user(user);
		

		Call<DataSpaceStub, SMFiles> call = new Call<DataSpaceStub, SMFiles>() {
			@Override
			public SMFiles call(DataSpaceStub endpoint) throws Exception {
				return endpoint.getFiles(request);
			};
		};

		try {
			SMFiles files = delegate.make(call);
			if (files.list() != null) {
				return files.list();
			} else
				return new ArrayList<SMFile>();
		} catch (Exception e) {
			throw FaultDSL.again(e).asServiceException();
		}
	}

	@Override
	public String importFile(final String baseFileName, final File file,
			final File taxa, final File vernacular, final String description,
			final String user, String type) {
		log.log(Level.SEVERE, "inside importFile");

		log.log(java.util.logging.Level.SEVERE, "inside importFile*");
		try {
			RemoteStorage stg=new RemoteStorage();
			Call<DataSpaceStub, String> call;
		
			final String locator = stg.storeFile(file, true);
			log.log(Level.SEVERE, "Type is :" + type);

			if (type.equals("DARWINCORE")) {
				log.log(Level.SEVERE, "Type is Darwincore");				
				final String locator_taxa = stg.storeFile(taxa, true);
								
				final String locator_vernacular = stg.storeFile(vernacular, true);
				
				call = new Call<DataSpaceStub, String>() {
					@Override
					public String call(DataSpaceStub endpoint) throws Exception {
						log.log(Level.SEVERE, "Call creation");
						SMimportDwcaFileRequest request = new SMimportDwcaFileRequest();

						request.user(user);
						request.fileName(baseFileName);
						// set table name in order to assign
						// importer name
						request.tableName(baseFileName);
						request.description(description);
						request.taxaLocator(locator_taxa);
						request.vernacularLocator(locator_vernacular);
						request.rsLocator(locator);
						log.log(Level.SEVERE, "before call");

						String result = String.valueOf(endpoint
								.importFromDwcaFile(request));
						log.log(Level.SEVERE, "result of call " + result);

						return result;

					}
				};

			} else {
				log.log(Level.SEVERE, "type is general");
				call = new Call<DataSpaceStub, String>() {
					@Override
					public String call(DataSpaceStub endpoint) throws Exception {
						log.log(Level.SEVERE, "Call creation");

						SMimportFileRequest request = new SMimportFileRequest();
						request.user(user);
						// set table name in order to assign
						// importer name
						request.tableName(baseFileName);
						request.fileName(baseFileName);
						request.description(description);

						request.rsLocator(locator);
						log.log(Level.SEVERE, "before call");

						String result = String.valueOf(endpoint
								.importFromFile(request));
						log.log(Level.SEVERE, "result of call " + result);

						return result;

					}
				};
			}
			return delegate.make(call);
		} catch (Exception e) {
			throw FaultDSL.again(e).asServiceException();
		}

	}

}
