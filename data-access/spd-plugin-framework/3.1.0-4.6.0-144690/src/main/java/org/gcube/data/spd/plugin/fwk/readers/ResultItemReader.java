package org.gcube.data.spd.plugin.fwk.readers;

import org.gcube.data.spd.model.binding.Bindings;
import org.gcube.data.spd.model.products.ResultItem;



public class ResultItemReader extends RSReader<ResultItem>{

	public ResultItemReader(String locator) throws Exception {
		super(locator);
	}

	@Override
	public ResultItem transform(String serializedItem) throws Exception {
		return Bindings.fromXml(serializedItem);
	}

}
