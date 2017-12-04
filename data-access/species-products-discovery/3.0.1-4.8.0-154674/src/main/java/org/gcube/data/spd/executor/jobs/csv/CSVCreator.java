package org.gcube.data.spd.executor.jobs.csv;

import java.util.List;
import java.util.Map;

import org.gcube.data.spd.model.products.OccurrencePoint;
import org.gcube.data.spd.plugin.fwk.AbstractPlugin;

public class CSVCreator extends CSVJob{

	private static transient OccurrenceCSVConverter  converter;
	
	public CSVCreator(Map<String, AbstractPlugin> plugins) {
		super(plugins);
		CSVCreator.converter = new OccurrenceCSVConverter();
	}
	
	@Override
	public Converter<OccurrencePoint, List<String>> getConverter() {
		if (CSVCreator.converter==null) converter = new OccurrenceCSVConverter();
		return CSVCreator.converter;
	}
	
	@Override
	public List<String> getHeader() {
		return OccurrenceCSVConverter.HEADER;		
	}

}
