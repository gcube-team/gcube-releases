package org.gcube.application.aquamaps.aquamapsportlet.servlet;

import static org.gcube.application.aquamaps.aquamapsservice.client.plugins.AbstractPlugin.maps;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import net.sf.csv4j.CSVLineProcessor;
import net.sf.csv4j.CSVReaderProcessor;

import org.gcube.application.aquamaps.aquamapsportlet.client.constants.Tags;
import org.gcube.application.aquamaps.aquamapsportlet.servlet.db.DBManager;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Filter;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.SpeciesOccursumFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ResourceType;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.common.scope.api.ScopeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpeciesImporterThread extends Thread {
	private static final Logger logger = LoggerFactory.getLogger(SpeciesImporterThread.class);
	private Integer totalCount;
	private ASLSession session;
	
	
	
	
	
	public SpeciesImporterThread(Integer totalCount, ASLSession session,
			String sortColumn, String sortDir) {
		super("Species Selection Importer");
		this.totalCount = totalCount;
		this.session = session;		
	}




	@Override
	public void run() {		
		int hspenId=(Integer) session.getAttribute(ResourceType.HSPEN+"");
		
		
		
		session.setAttribute(Tags.IMPORT_PROGRESS,new Integer(0));
		
			try{
			logger.debug("Importing filtered species..");
			
			
			List<Filter> speciesFilter=(List<Filter>) session.getAttribute(Tags.SPECIES_FILTER);
			
			
			final List<String> ids=new ArrayList<String>();
			
			
			
			ScopeProvider.instance.set(session.getScope().toString());
			File toImport=maps().build().getCSVSpecies(hspenId, new ArrayList<Filter>(), speciesFilter,session.getUsername());
			CSVReaderProcessor processor= new CSVReaderProcessor();
			processor.setDelimiter(',');
			processor.setHasHeader(true);			
			Reader reader= new InputStreamReader(new FileInputStream(toImport), Charset.defaultCharset());
			processor.processStream(reader , new CSVLineProcessor(){
				Integer parsed=0;
				Integer idIndex=0;				
				public boolean continueProcessing() {return true;}
				
				public void processDataLine(int arg0, List<String> arg1) {
					ids.add(arg1.get(idIndex));
					if(ids.size()==100){
						try{
							DBManager.getInstance(session.getScope()).addToUserBasket(ids, session.getUsername());
						}catch(Exception e){
							logger.error("Unable to insert line");
						}
						session.setAttribute(Tags.IMPORT_PROGRESS, parsed+=ids.size());
						ids.clear();
					}
				}

				
				public void processHeaderLine(int arg0, List<String> arg1) {
					idIndex=arg1.indexOf(SpeciesOccursumFields.speciesid+"");
				}});
		
			//finalizing..
			if(ids.size()>0){
				DBManager.getInstance(session.getScope()).addToUserBasket(ids, session.getUsername());
				session.setAttribute(Tags.IMPORT_PROGRESS, totalCount);
			}
			
			
			
			}catch(Exception e){
				logger.warn("Unable to add portion of selection ",e);
				e.printStackTrace();
			}
	}
	
	
}
