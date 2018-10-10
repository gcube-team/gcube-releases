package org.gcube.data.spd.client;

import java.util.concurrent.TimeUnit;

import javax.xml.bind.JAXBException;

import org.gcube.data.spd.model.binding.Bindings;
import org.gcube.data.spd.model.products.ResultElement;

public class ResultElementRecordIterator<T extends ResultElement> extends JerseyRecordIterator<T> {
	
	public ResultElementRecordIterator(String endpointId, String locator,
			long timeout, TimeUnit timeoutUnit) {
		super(endpointId, locator, timeout, timeoutUnit);
	}

	@Override
	public T convertFromString(String element) {
		try {
			return Bindings.fromXml(currentElement);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}

}
