package org.gcube.dataanalysis.wps.statisticalmanager.synchserver.bindings;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.fusesource.hawtbuf.ByteArrayInputStream;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.datahandler.generator.GenericFileGenerator;

public class GifFileGenerator extends GenericFileGenerator {
	
	public GifFileGenerator (){
		super();
		supportedIDataTypes.add(GifFileDataBinding.class);
	}
	
	public InputStream generateStream(IData data, String mimeType, String schema) throws IOException {
		
//		InputStream theStream = new ByteArrayInputStream(((GifFileDataBinding)data).getPayload().getBytes());
//		InputStream theStream = new URL(((GifFileDataBinding)data).getPayload()).openStream();
		InputStream theStream = ((GifFileDataBinding)data).getPayload().getDataStream();
		
		return theStream;
	}
	
}
