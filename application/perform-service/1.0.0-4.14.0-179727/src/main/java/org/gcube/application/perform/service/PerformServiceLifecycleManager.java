package org.gcube.application.perform.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.application.perform.service.engine.impl.PerformanceManagerImpl;
import org.gcube.application.perform.service.engine.impl.SchemaDefinition;
import org.gcube.application.perform.service.engine.model.InternalException;
import org.gcube.application.perform.service.engine.model.importer.AnalysisType;
import org.gcube.application.perform.service.engine.model.importer.ImportedTable;
import org.gcube.smartgears.context.application.ApplicationContext;
import org.gcube.smartgears.handlers.application.ApplicationLifecycleEvent.Start;
import org.gcube.smartgears.handlers.application.ApplicationLifecycleEvent.Stop;
import org.gcube.smartgears.handlers.application.ApplicationLifecycleHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement(name = "perform-lifecycle")
public class PerformServiceLifecycleManager extends ApplicationLifecycleHandler {

	private static final Logger log= LoggerFactory.getLogger(PerformServiceLifecycleManager.class);


	@Override
	public void onStart(Start e) {	
		super.onStart(e);
		try{
			ApplicationContext context=e.context();
			log.debug("Starting Service. ApplicationContext is {} ",context);
			log.debug("Application is {} ",context.application());
			URL resourceUrl = context.application().getResource("/WEB-INF/config.properties");
			LocalConfiguration.init(resourceUrl);

			ServletContext ctx=context.application();
			String webinfPath=ctx.getRealPath("/WEB-INF");
			if(Boolean.parseBoolean(LocalConfiguration.getProperty(LocalConfiguration.LOAD_SCHEMA))) {
				initSchema(webinfPath);
				log.info("Loaded configuration schema : ");
				for(Entry<AnalysisType,Set<ImportedTable>> entry:PerformanceManagerImpl.getAnalysisConfiguration().entrySet()) {
					log.info("Tables registered for {} ",entry.getKey());
					for(ImportedTable t:entry.getValue())
						log.info("Table {} : {} [Analysis : {}]",t.getSchema().getRelatedDescription(),t.getTableName(),t.getSchema().getAnalysisEnabled());
				}
			}else log.warn("SKIPPING LOADING CONFIGURATION. INVALID BEHAVIOUR IN PRODUCTION ENVIRONMENT.");
		}catch(Exception ex) {
			throw new RuntimeException("Unable to init",ex);
		}

	}

	@Override
	public void onStop(Stop e) {		
		super.onStop(e);
	}


	/**
	 * schema/analysis_id/description.csv
	 * @throws IOException 
	 * @throws InternalException 
	 * @throws SQLException 
	 * 
	 * 
	 */
	static final void initSchema(String webinfPath) throws IOException, SQLException, InternalException {
		String configurationPath=webinfPath+"/schema";
		log.info("Reading Analysis Configuration from {} ",configurationPath);
		File folder=new File(configurationPath);
		for(File analysisFolder:folder.listFiles()) {
			String analysisName=analysisFolder.getName();
			AnalysisType type=new AnalysisType(analysisName, analysisName);
			log.info("Reading from "+analysisFolder.getPath());			
			for(File schemaFile:analysisFolder.listFiles()) {
				FileInputStream fis=null;
				try {
					Properties props=new Properties();
					fis=new FileInputStream(schemaFile);
					props.load(fis);
					SchemaDefinition schema=new SchemaDefinition(type,props);					
					PerformanceManagerImpl.importSchema(schema,webinfPath);
				}catch(Throwable t) {
					log.warn("SKPPING CONFIGURATION FILE "+schemaFile.getPath(),t);
				}finally {
					if(fis!=null) fis.close();
				}
			}
		}				
	}

}
