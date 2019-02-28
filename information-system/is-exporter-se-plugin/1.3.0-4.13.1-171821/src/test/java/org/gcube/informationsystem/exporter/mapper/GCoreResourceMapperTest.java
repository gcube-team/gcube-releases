package org.gcube.informationsystem.exporter.mapper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

import org.gcube.informationsystem.exporter.ScopedTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GCoreResourceMapperTest extends ScopedTest {

	private static Logger logger = LoggerFactory.getLogger(GenericResourceExporterTest.class);
	
	protected File getFile(GCoreResourceMapper<?,?> grm) throws Exception {
		String contextName = GCoreResourceMapper.getCurrentContextName();
		String dateString = GCoreResourceMapper.getDateString(Calendar.getInstance());
		File file = grm.getFile(GenericResourceExporterTest.class, contextName, dateString);
		String json = "{}";
		synchronized (file) {
			try(FileWriter fw = new FileWriter(file, true);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter out = new PrintWriter(bw)){
					out.println(json);
					out.flush();
			} catch( IOException e ){
			   throw e;
			}
		}
		return file;
	}
	
	//@Test
	public void testWorkspace() throws Exception {
		
		String[] tokens = {
				ScopedTest.GCUBE, 
				ScopedTest.GCUBE_DEVSEC,
				ScopedTest.GCUBE_DEVSEC_DEVVRE,
				ScopedTest.GCUBE_DEVNEXT,
				ScopedTest.GCUBE_DEVNEXT_NEXTNEXT
		};
		
		for(String token : tokens){
			logger.info("\n\n\n-------------------------------------------------------------------------");
			ScopedTest.setContext(token);
			GenericResourceExporter gre = new GenericResourceExporter(false);
			File file = getFile(gre);
			logger.info("\n\n\n {}", file);
			/*
			gre.publishFileToWorkspace(file);
			logger.info("\n\n\n");
			file.delete();
			*/
		}
		
	}
	
}
