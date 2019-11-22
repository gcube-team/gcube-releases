package org.gcube.data.publishing.gCatFeeder.service.mockups;

import java.util.concurrent.atomic.AtomicLong;

import org.gcube.data.publishing.gCatFeeder.model.CatalogueFormatData;
import org.gcube.data.publishing.gCatfeeder.collectors.model.CustomData;

public class FakeCustomData implements CustomData, CatalogueFormatData{

	private static final AtomicLong counter=new AtomicLong(0);
	
	
	private long number=counter.incrementAndGet();
	
	
	
	@Override
	public String toCatalogueFormat() {
		return "DATA "+number; 
	}
	
}