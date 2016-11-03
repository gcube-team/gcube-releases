package org.gcube.dataanalysis.wps.statisticalmanager.synchserver.bindings;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.n52.wps.io.data.IData;
import org.n52.wps.io.datahandler.generator.GenericFileGenerator;

public class CsvFileGenerator extends GenericFileGenerator {
	
	public CsvFileGenerator (){
		super();
		supportedIDataTypes.add(CsvFileDataBinding.class);
	}
	
	public InputStream generateStream(IData data, String mimeType, String schema) throws IOException {
		
//		InputStream theStream = new ByteArrayInputStream(((CsvFileDataBinding)data).getPayload().getBytes());
		InputStream theStream = ((CsvFileDataBinding)data).getPayload().getDataStream();
//		InputStream theStream = new URL(((CsvFileDataBinding)data).getPayload()).openStream();
		
		return theStream;
	}
	
}
