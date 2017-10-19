package org.gcube.data.analysis.tabulardata.commons.templates.model;

import java.util.Arrays;
import java.util.List;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.ColumnDescription;
import org.gcube.data.analysis.tabulardata.commons.utils.Cardinality;

import static org.gcube.data.analysis.tabulardata.commons.templates.model.columns.ColumnCategory.*;



public enum TemplateCategory {


	
	CODELIST(new ColumnDescription(CODENAME, Cardinality.AT_LEAST_ONE), new ColumnDescription(CODEDESCRIPTION, Cardinality.ZERO_OR_MORE), 
			new ColumnDescription(ANNOTATION, Cardinality.ZERO_OR_MORE), new ColumnDescription(CODE, Cardinality.ONE)),
	DATASET(new ColumnDescription(ATTRIBUTE, Cardinality.ZERO_OR_MORE),  new ColumnDescription(DIMENSION, Cardinality.AT_LEAST_ONE),
			new ColumnDescription(MEASURE, Cardinality.AT_LEAST_ONE), new ColumnDescription(TIMEDIMENSION, Cardinality.ONE)), 
	GENERIC(new ColumnDescription(MEASURE, Cardinality.ZERO_OR_MORE), new ColumnDescription(ATTRIBUTE, Cardinality.ZERO_OR_MORE),
			new ColumnDescription(TIMEDIMENSION, Cardinality.ZERO_OR_ONE), new ColumnDescription(DIMENSION, Cardinality.ZERO_OR_MORE) );
	
	private List<ColumnDescription> descriptions;
	
	private TemplateCategory(ColumnDescription ... descriptions ) {
		this.descriptions = Arrays.asList(descriptions);
	}

	/**
	 * @return the classes
	 */
	public List<ColumnDescription> getAllowedColumn() {
		return descriptions;
	}
	
	
}
