package org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.threads;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.util.List;

import net.sf.csv4j.CSVReaderProcessor;

import org.apache.commons.io.IOUtils;
import org.apache.tools.ant.util.FileUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext.FOLDERS;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.utils.CSVUtils;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ResourceStatus;
import org.gcube.application.aquamaps.aquamapsservice.stubs.utils.Storage;
import org.gcube.common.scope.api.ScopeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SourceImporter extends Thread {
	final static Logger logger= LoggerFactory.getLogger(SourceImporter.class);	

	private String csv;
	private String locator;
	private Resource importing;
	private Integer metaId;
	private char delimiter;
	private boolean[] fieldSelection;
	private boolean hasHeaders;
	private String encoding;
	private String scope;





	public SourceImporter(String locator, Resource i, Integer metaId,
			char delimiter, boolean[] fieldSelection, boolean hasHeaders,String encoding) {
		super();
		this.locator=locator;
		this.importing = i;
		this.metaId = metaId;
		this.delimiter = delimiter;
		this.fieldSelection = fieldSelection;
		this.hasHeaders = hasHeaders;
		this.encoding=encoding;
		this.scope=ScopeProvider.instance.get();
	}






	public void run() {
		DBSession session=null;
		StatefullCSVLineProcessor lineProcessor=null;
		try{		
			//Read File from RS
			csv=ServiceContext.getContext().getFolderPath(FOLDERS.IMPORTS)+File.separator+ServiceUtils.generateId("import", ".csv");			
			logger.debug("Importing file from locator "+locator+" to file "+csv+" under scope "+scope);
			
			ScopeProvider.instance.set(scope);
			
			FileWriter writer=new FileWriter(csv);
			FileInputStream is=new FileInputStream(Storage.getFileById(locator, false,"AquaMaps Service Importer"));			
			IOUtils.copy(is, writer, encoding);
			IOUtils.closeQuietly(writer);
			IOUtils.closeQuietly(is);
			
			
			logger.debug("Started importing operation from "+csv+" TO "+importing.getTableName());
			long toInsertCount=CSVUtils.countCSVRows(csv, delimiter, hasHeaders);
			if(toInsertCount==0) throw new Exception("No rows to insert from csv file "+csv);
			logger.info("Found "+toInsertCount+" rows in csv file");
			Resource defaultResource=SourceManager.getById(SourceManager.getDefaultId(importing.getType()));
			session=DBSession.getInternalDBSession();

			logger.debug("Going to evaluate default table "+defaultResource.getTableName()+" fields");
			ResultSet templateRs=session.executeQuery("SELECT * FROM "+defaultResource.getTableName()+" LIMIT 1 OFFSET 0");
			templateRs.next();
			final List<Field> model=Field.loadRow(templateRs); 
			session.close();


			CSVReaderProcessor processor=new CSVReaderProcessor();
			processor.setDelimiter(delimiter);
			processor.setHasHeader(hasHeaders);
			Reader reader= new InputStreamReader(new FileInputStream(csv), Charset.defaultCharset());

			lineProcessor=new StatefullCSVLineProcessor(model, importing,toInsertCount,fieldSelection);
			logger.debug("Starting file processing");
			processor.processStream(reader , lineProcessor);
			logger.debug("Complete processing");			
			logger.debug("Completed import of source ID "+importing.getSearchId());			
		}catch(Exception e){
			logger.error("Unexpected Exception ", e);
			importing.setStatus(ResourceStatus.Error);
			try{
				SourceManager.update(importing);				
			}catch (Exception e1){
				logger.error("Unable to update resource "+metaId, e1);
			}
		}finally{
			if(session!=null){
				try{
					session.close();
				}catch(Exception e){
					logger.error("Unable to close session ",e);
				}
			}
			if(lineProcessor!=null)lineProcessor.close();
			FileUtils.delete(new File(csv));
		}
	};
}
