package org.gcube.dataanalysis.wps.statisticalmanager.synchserver.bindings;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.fusesource.hawtbuf.ByteArrayInputStream;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.datahandler.generator.GenericFileGenerator;

public class GisLinkGenerator extends GenericFileGenerator {
	
	public GisLinkGenerator (){
		super();
		supportedIDataTypes.add(GisLinkDataBinding.class);
	}
	
	public InputStream generateStream(IData data, String mimeType, String schema) throws IOException {
//		InputStream theStream = new ByteArrayInputStream(((GisLinkDataBinding)data).getPayload().getBytes());
//		InputStream theStream = new URL(((GisLinkDataBinding)data).getPayload()).openStream();
		InputStream theStream = ((GisLinkDataBinding)data).getPayload().getDataStream();
		return theStream;
	}
	
}
