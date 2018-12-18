package org.gcube.dataanalysis.wps.statisticalmanager.synchserver.bindings;

import java.io.InputStream;

import org.n52.wps.io.data.GenericFileData;
import org.n52.wps.io.datahandler.parser.AbstractParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class D4ScienceFileParser extends AbstractParser{
	
	private static Logger LOGGER = LoggerFactory.getLogger(D4ScienceDataInputBinding.class);

	public D4ScienceFileParser() {
		super();
		supportedIDataTypes.add(D4ScienceDataInputBinding.class);
	}
	
	@Override
	public D4ScienceDataInputBinding parse(InputStream input, String mimeType, String schema) {
		
		GenericFileData theData = new GenericFileData(input, mimeType);
		LOGGER.info("Found Gis File Input " + mimeType);
		
		return new D4ScienceDataInputBinding(theData);
	}

}
