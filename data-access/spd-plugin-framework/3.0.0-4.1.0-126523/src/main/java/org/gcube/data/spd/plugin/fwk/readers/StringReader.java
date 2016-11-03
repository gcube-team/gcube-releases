package org.gcube.data.spd.plugin.fwk.readers;

public class StringReader extends RSReader<String>{

	public StringReader(String locator) throws Exception {
		super(locator);
	}

	@Override
	public String transform(String serializedItem) throws Exception {
		return serializedItem;
	}

	
	
}
