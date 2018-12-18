package org.gcube.data.spd.executor.jobs.csv;

import java.util.List;
import java.util.Map;

import org.gcube.data.spd.model.products.OccurrencePoint;
import org.gcube.data.spd.plugin.fwk.AbstractPlugin;

public class CSVCreatorForOMJob extends CSVJob{
	
	private static transient OccurrenceCSVConverterOpenModeller  converter;
	
	public CSVCreatorForOMJob(Map<String, AbstractPlugin> plugins) {
		super(plugins);
		converter = new OccurrenceCSVConverterOpenModeller();
	}
	
	@Override
	public Converter<OccurrencePoint, List<String>> getConverter() {
		if (CSVCreatorForOMJob.converter==null) converter = new OccurrenceCSVConverterOpenModeller();
		return CSVCreatorForOMJob.converter;
	}

	@Override
	public List<String> getHeader() {
		return OccurrenceCSVConverterOpenModeller.HEADER;		
	}

}
