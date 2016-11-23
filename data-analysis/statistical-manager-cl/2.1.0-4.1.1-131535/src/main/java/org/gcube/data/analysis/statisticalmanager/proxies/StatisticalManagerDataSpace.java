package org.gcube.data.analysis.statisticalmanager.proxies;

import java.io.File;
import java.util.List;

import org.gcube.data.analysis.statisticalmanager.exception.ResourceNotFoundException;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMTables;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMFile;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMImport;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMResource;
import org.gcube.data.spd.model.products.OccurrencePoint;
import org.gcube.data.streams.Stream;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;

public interface StatisticalManagerDataSpace {

	List<SMFile> getFiles(String user);

	SMTables getTables(String user);

	SMTables getTables(String user, String template);

	String getDBParameters(String tableId);

	String createTableFromCSV(File file, boolean hasHeader, String delimiter,
			String comment, String tableName, TableTemplates tableTemplate,
			String description, String user);

	String createTableFromDataStream(Stream<OccurrencePoint> points,
			String tableName, String description, String user);

	List<SMImport> getImports(String user, String template);

	SMImport getImporter(String importerId);

	void removeImport(String importerId);

	List<SMResource> getResources(String user, String... template);

	void removeTable(String tableId);

	File exportTable(String tableId) throws ResourceNotFoundException;

	public String importFile(String baseFileName , File file, File taxa,  File vernacular,  
			 String description,  String user, String type);

	String exportTableToStorage(String tableId)	throws ResourceNotFoundException;

}
