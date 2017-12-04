package org.gcube.dataanalysis.wps.statisticalmanager.synchserver.bindings;

import java.io.IOException;
import java.io.InputStream;

import org.n52.wps.io.data.IData;
import org.n52.wps.io.datahandler.generator.GenericFileGenerator;

public class PngFileGenerator extends GenericFileGenerator {
	
	public PngFileGenerator (){
		super();
		supportedIDataTypes.add(PngFileDataBinding.class);
	}
	
	public InputStream generateStream(IData data, String mimeType, String schema) throws IOException {
//		InputStream theStream = new ByteArrayInputStream(((PngFileDataBinding)data).getPayload().getBytes());
//		InputStream theStream = new URL(((PngFileDataBinding)data).getPayload()).openStream();
		InputStream theStream = ((PngFileDataBinding)data).getPayload().getDataStream();
		
		return theStream;
	}
	
}
