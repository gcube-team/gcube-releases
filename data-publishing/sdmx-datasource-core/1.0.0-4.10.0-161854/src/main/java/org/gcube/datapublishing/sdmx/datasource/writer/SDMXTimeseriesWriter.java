package org.gcube.datapublishing.sdmx.datasource.writer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.gcube.datapublishing.sdmx.datasource.SampleDataWriter;
import org.sdmxsource.sdmx.api.constants.DATA_TYPE;
import org.sdmxsource.sdmx.api.engine.DataWriterEngine;
import org.sdmxsource.sdmx.api.factory.ReadableDataLocationFactory;
import org.sdmxsource.sdmx.api.manager.parse.StructureParsingManager;
import org.sdmxsource.sdmx.api.manager.retrieval.SdmxBeanRetrievalManager;
import org.sdmxsource.sdmx.api.model.beans.base.MaintainableBean;
import org.sdmxsource.sdmx.api.model.beans.datastructure.DataStructureBean;
import org.sdmxsource.sdmx.api.model.beans.datastructure.DataflowBean;
import org.sdmxsource.sdmx.api.model.beans.reference.MaintainableRefBean;
import org.sdmxsource.sdmx.api.model.data.DataFormat;
import org.sdmxsource.sdmx.api.util.ReadableDataLocation;
import org.sdmxsource.sdmx.dataparser.manager.DataWriterManager;
import org.sdmxsource.sdmx.sdmxbeans.model.data.SdmxDataFormat;
import org.sdmxsource.sdmx.structureretrieval.manager.InMemoryRetrievalManager;
import org.sdmxsource.sdmx.util.beans.reference.MaintainableRefBeanImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class SDMXTimeseriesWriter {

	@Autowired
	private StructureParsingManager structureParsingManager;

	@Autowired
	private ReadableDataLocationFactory rdlFactory;
	
	@Autowired
	private DataWriterManager dataWriterManager;

	@Autowired
	private SampleDataWriter sampleDataWriter;
	
	private void writeData(File structureFile) throws IOException {
		//Step 1 - Create Data Writer Engine
		DataFormat dataFormat = new SdmxDataFormat(DATA_TYPE.COMPACT_2_1);
		DataWriterEngine dataWriterEngine = dataWriterManager.getDataWriterEngine(dataFormat, getFileOutputStream());
		
		//Step 2 - Get Data Structure & Data Flow
		ReadableDataLocation rdl = rdlFactory.getReadableDataLocation(structureFile);
		SdmxBeanRetrievalManager retrievalManager = new InMemoryRetrievalManager(rdl);
		
		String agencyId = "SDMXSOURCE";
		String version = MaintainableBean.DEFAULT_VERSION;  //1.0
		
		MaintainableRefBean dsdRef = new MaintainableRefBeanImpl(agencyId, "WDI", version);
		MaintainableRefBean flowRef = new MaintainableRefBeanImpl(agencyId, "DF_WDI", version);
			
		DataStructureBean dsd = retrievalManager.getMaintainableBean(DataStructureBean.class, dsdRef);
		DataflowBean dataflow = retrievalManager.getMaintainableBean(DataflowBean.class, flowRef);
		
		//sampleDataWriter.writeSampleData(dsd, dataflow, dataWriterEngine);
		//sampleDataWriter.writeSampleCrossSectionalData(dsd, dataflow, dataWriterEngine);
		sampleDataWriter.writeSampleFlatData(dsd, dataflow, dataWriterEngine);
	}

	public static void main(String[] args) throws IOException {
		//Step 1 - Get the Application Context
		ClassPathXmlApplicationContext applicationContext = 
				new ClassPathXmlApplicationContext("spring/spring-beans-chapter1.xml");

		//Step 2 - Get the main class from the Spring beans container
		SDMXTimeseriesWriter main = 
				applicationContext.getBean(SDMXTimeseriesWriter.class);

		//Step 3 - Create a Readable Data Location from the File
		File structureFile = new File("src/main/resources/structures/chapter2/structures_full.xml");

		main.writeData(structureFile);
		
		applicationContext.close();
	}
	
	private OutputStream getFileOutputStream() throws IOException {
		File structureFile = new File("src/main/resources/data/chapter3/sample_data.xml");
		System.out.println("File Deleted : "+ structureFile.delete());
		System.out.println("File Created : "+structureFile.createNewFile());
		return new FileOutputStream(structureFile);
	}
	
}
