package org.gcube.data.spd.plugin.fwk.readers;

import org.gcube.data.spd.model.binding.Bindings;
import org.gcube.data.spd.model.products.OccurrencePoint;



public class OccurrencesReader extends RSReader<OccurrencePoint>{

	public OccurrencesReader(String locator) throws Exception {
		super(locator);
	}

	@Override
	public OccurrencePoint transform(String serializedItem) throws Exception {
		return Bindings.fromXml(serializedItem);
	}

}
