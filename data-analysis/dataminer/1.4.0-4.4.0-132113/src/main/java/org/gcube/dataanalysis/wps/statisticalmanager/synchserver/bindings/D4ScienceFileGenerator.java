package org.gcube.dataanalysis.wps.statisticalmanager.synchserver.bindings;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.fusesource.hawtbuf.ByteArrayInputStream;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.datahandler.generator.GenericFileGenerator;

public class D4ScienceFileGenerator extends GenericFileGenerator {
	
	public D4ScienceFileGenerator (){
		super();
		supportedIDataTypes.add(D4ScienceFileDataBinding.class);
	}
	
	public InputStream generateStream(IData data, String mimeType, String schema) throws IOException {
		
//		InputStream theStream = new ByteArrayInputStream(((D4ScienceFileDataBinding)data).getPayload().getBytes());
//		InputStream theStream = new URL(((D4ScienceFileDataBinding)data).getPayload()).openStream();
		InputStream theStream = ((D4ScienceFileDataBinding)data).getPayload().getDataStream();
		
		return theStream;
	}
	
}
