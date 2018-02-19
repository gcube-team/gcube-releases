package org.gcube.data.analysis.tabulardata.model.time;

import static org.gcube.data.analysis.tabulardata.model.time.TimeConstants.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.model.ValueFormat;

public enum PeriodType {
	
		
	//columns: id, nomralized day, string representation of date
	DAY("Day", "DD Mon YYYY", "1 day"), 
	MONTH("Month", "Mon YYYY","1 month"), 
	QUARTER("Quarter_of_year", "Qth \"Q\"uarter of YYYY" ,"3 month"), 
	YEAR("Year", "YYYY","1 year"),
	DECADE("Decade", "YYYYs", "10 year" ),
	CENTURY("Century", "CCth centur\"y\"","100 year");

	private static final int START_YEAR = 1700;
	
	private static final int END_YEAR = 2300;
	
	private static Map<PeriodType, List<PeriodType>> hierarchicalRelation = new HashMap<>();
	
	private static Map<PeriodType, List<ValueFormat>> acceptedTimeFormat = new HashMap<>();
	
	static {
		hierarchicalRelation.put(DAY, Arrays.asList(MONTH, QUARTER, YEAR, DECADE, CENTURY));
		hierarchicalRelation.put(MONTH, Arrays.asList(QUARTER, YEAR, DECADE, CENTURY));
		hierarchicalRelation.put(QUARTER, Arrays.asList(YEAR, DECADE, CENTURY));
		hierarchicalRelation.put(YEAR, Arrays.asList(DECADE, CENTURY));
		hierarchicalRelation.put(DECADE, Arrays.asList(CENTURY));
		
		acceptedTimeFormat.put(PeriodType.DAY, Arrays.asList(ISO_DATE_ANY_SEP, EUROPEAN_DATE, US_DATE));
		acceptedTimeFormat.put(PeriodType.MONTH, Arrays.asList(ISO_MONTH));
		acceptedTimeFormat.put(PeriodType.QUARTER, Arrays.asList(ISO_QUARTER));
		acceptedTimeFormat.put(PeriodType.YEAR, Arrays.asList(ISO_YEAR));
		acceptedTimeFormat.put(PeriodType.DECADE, Arrays.asList(DECADE_LITERAL_FORMAT, DECADE_FORMAT));
		acceptedTimeFormat.put(PeriodType.CENTURY, Arrays.asList(CENTURY_FORMAT));
	}
	
	private String name;
	//private Class<? extends DataType>  valueType;
	private String seriesSelectQuery;
		
	private PeriodType(String name, String charRepresentation, String interval) {
		this.name = name;
		this.seriesSelectQuery= String.format("select get_%1$s_id(i), normalize_%1$s(i), to_char(i, '%2$s') " +
				"from generate_series('</START>-01-01','</END>-12-31', '%3$s'::interval) i;", 
				this.name.toLowerCase(), charRepresentation, interval);
	}

	public String getName() {
		return name;
	}
	
	public static PeriodType fromName(String name) {
		for (PeriodType periodType : PeriodType.values()) {
			if (name.equals(periodType.getName()))
				return periodType;
		}
		return null;
	}
	
	public List<ValueFormat> getAcceptedFormats() {
		return acceptedTimeFormat.get(this);
	}
	
	public ValueFormat getTimeFormatById(String id) {
		for (ValueFormat tf : getAcceptedFormats())
			if (tf.getId().equals(id)) 
				return tf;
		return null;
	}

	public String getSeriesSelectQuery() {
		return seriesSelectQuery.replaceAll("</START>", START_YEAR+"").replaceAll("</END>", END_YEAR+"");
	}

	public static Map<PeriodType, List<PeriodType>> getHierarchicalRelation(){
		return hierarchicalRelation;
	}
}