package org.gcube.application.perform.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.gcube.application.perform.service.engine.impl.PerformanceManagerImpl;
import org.gcube.application.perform.service.engine.model.importer.AnalysisType;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.vividsolutions.jts.util.Assert;

public class PerformanceTest extends CommonTest{




	@Test
	public void getPerformance() throws IOException {
		File folder= Files.createTempDir();
		
		
		for(AnalysisType analysis:PerformanceManagerImpl.getAnalysisConfiguration().keySet()) {
//		AnalysisType analysis=new AnalysisType("GROW_OUT_INDIVIDUAL","GROW_OUT_INDIVIDUAL");
			WebTarget target=
					target(ServiceConstants.Performance.PATH).
//					queryParam(ServiceConstants.Performance.AREA_PARAMETER, "A1","A2").
////					queryParam(ServiceConstants.Performance.QUARTER_PARAMETER, "Q1","Q2").
//					queryParam(ServiceConstants.Performance.SPECIES_ID_PARAMETER, "Gadilidae","Tonno").
//					queryParam(ServiceConstants.Performance.PERIOD_PARAMETER, "First","Spring").
					queryParam(ServiceConstants.Performance.FARM_ID_PARAMETER, "12682101").
					queryParam(ServiceConstants.Performance.BATCH_TYPE_PARAMETER, analysis.getId());

			System.out.println(target.getUri());		
			Response resp=target.request().get();
			Assert.isTrue(resp.getStatus()==200);
			
			File subFolder= new File(folder,analysis.getId());
			subFolder.mkdirs();
			for(Entry<String,String> entry : resp.readEntity(new GenericType<HashMap<String, String>>() { }).entrySet()) {
				URL csvUrl=new URL(entry.getValue());
				File csv=new File(subFolder,entry.getKey()+".csv");
				csv.createNewFile();
				BufferedWriter writer=Files.newWriter(csv, Charsets.UTF_8);
				IOUtils.copy(csvUrl.openStream(), writer);
				writer.close();
			}
//			System.out.println(analysis.getId()+" "+resp.getStatus() + " : "+ resp.readEntity(String.class));		
			
			
		}
		System.out.println("Wrote to : "+ folder.getAbsolutePath());
	}

	
	
	
	
	
	
	
}
