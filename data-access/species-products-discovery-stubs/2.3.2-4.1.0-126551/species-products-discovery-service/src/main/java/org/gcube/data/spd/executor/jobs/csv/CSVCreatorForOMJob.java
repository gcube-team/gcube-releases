package org.gcube.data.spd.executor.jobs.csv;

import java.util.List;

import org.gcube.data.spd.model.products.OccurrencePoint;

public class CSVCreatorForOMJob extends CSVJob{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private static transient OccurrenceCSVConverterOpenModeller  converter;
	
	public CSVCreatorForOMJob(String referenceIds) {
		super(referenceIds);
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
