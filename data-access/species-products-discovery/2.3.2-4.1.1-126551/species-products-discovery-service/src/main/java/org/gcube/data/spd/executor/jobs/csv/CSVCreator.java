package org.gcube.data.spd.executor.jobs.csv;

import java.util.List;

import org.gcube.data.spd.model.products.OccurrencePoint;

public class CSVCreator extends CSVJob{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private static transient OccurrenceCSVConverter  converter;
	
	public CSVCreator(String referenceIds) {
		super(referenceIds);
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
