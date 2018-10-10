package org.gcube.dataanalysis.wps.statisticalmanager.synchserver.bindings;

import java.io.InputStream;

import org.n52.wps.io.data.GenericFileData;
import org.n52.wps.io.datahandler.parser.AbstractParser;
import org.n52.wps.io.datahandler.parser.GenericFileParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GisLinkParser extends AbstractParser{
	
	private static Logger LOGGER = LoggerFactory.getLogger(GenericFileParser.class);
	//TODO manage gis link bindings
	public GisLinkParser() {
		super();
		supportedIDataTypes.add(GisLinkDataBinding.class);
	}
	
	@Override
	public GisLinkDataInputBinding parse(InputStream input, String mimeType, String schema) {
		
		GenericFileData theData = new GenericFileData(input, mimeType);
		LOGGER.info("Found Gis File Input " + mimeType);
		
		return new GisLinkDataInputBinding(theData);
	}

}
