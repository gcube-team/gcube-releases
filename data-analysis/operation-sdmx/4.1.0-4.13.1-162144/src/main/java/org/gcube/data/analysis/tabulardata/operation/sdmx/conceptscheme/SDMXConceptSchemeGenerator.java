package org.gcube.data.analysis.tabulardata.operation.sdmx.conceptscheme;

import java.util.List;

import org.gcube.data.analysis.sdmx.DataInformationProvider;
import org.gcube.data.analysis.tabulardata.metadata.NoSuchMetadataException;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.type.AttributeColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.DimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.MeasureColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.TimeDimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.NamesMetadata;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.sdmxsource.sdmx.api.model.mutable.base.TextTypeWrapperMutableBean;
import org.sdmxsource.sdmx.api.model.mutable.conceptscheme.ConceptMutableBean;
import org.sdmxsource.sdmx.api.model.mutable.conceptscheme.ConceptSchemeMutableBean;
import org.sdmxsource.sdmx.sdmxbeans.model.mutable.base.TextTypeWrapperMutableBeanImpl;
import org.sdmxsource.sdmx.sdmxbeans.model.mutable.conceptscheme.ConceptMutableBeanImpl;
import org.sdmxsource.sdmx.sdmxbeans.model.mutable.conceptscheme.ConceptSchemeMutableBeanImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class SDMXConceptSchemeGenerator 
{

	private Logger log;
	private Table table;
	private String targetAgency;
	private String targetId;
	private String targetVersion;
	
	private List<LocalizedText> tableNamesMetadata;
	

	
	
	public SDMXConceptSchemeGenerator(Table table, String targetId, String targetAgency, String targetVersion) {

		this.log = LoggerFactory.getLogger(this.getClass());
		this.table = table;
		this.targetAgency = targetAgency;
		this.targetVersion = targetVersion;
		this.targetId = targetId;
	}


	@SuppressWarnings("unchecked")
	public void populateConceptsScheme (ConceptSchemeMutableBean conceptScheme) throws WorkerException 
	{
		
		log.debug("Loading columns");
		List<Column> columns = this.table.getColumnsByType(MeasureColumnType.class);
		columns.addAll(this.table.getColumnsByType(DimensionColumnType.class));
		columns.addAll(this.table.getColumnsByType(AttributeColumnType.class));
		columns.addAll(this.table.getColumnsByType(TimeDimensionColumnType.class));
		log.debug("Columns loaded");
		
		for (Column column : columns)
		{
			log.debug("Generating concept for column "+column.getName());
			ConceptMutableBean columnConcept = createConceptBean(column);
			conceptScheme.addItem(columnConcept);
			log.debug("Concept generated");
		}
		}
	
	

	/**
	 * 
	 * @param column
	 * @return
	 */
	public ConceptMutableBean createConceptBean (Column column)
	{
		log.debug("Generating concept mutable bean for column "+column.getName());
		ConceptMutableBean concept = new ConceptMutableBeanImpl();
		concept.setId(DataInformationProvider.getInstance().getColumnConverter().local2Registry(column.getLocalId().getValue()));
		concept.setParentAgency(this.targetAgency);
		
		try {
			List<LocalizedText> conceptNames = column.getMetadata(NamesMetadata.class).getTexts();
			log.debug("Concept names size "+conceptNames.size());
			concept.setNames(getNamesMetadata(conceptNames, null, null));
		} catch (NoSuchMetadataException e) {
			// TODO-LF: This exception should not occur
		}
		
		return concept;
	}

	public ConceptMutableBean createConceptBean (String id, String conceptName, String locale)
	{
		log.debug("Generating concept mutable bean for id "+id);
		ConceptMutableBean concept = new ConceptMutableBeanImpl();
		concept.setId(id);
		concept.setParentAgency(this.targetAgency);
		concept.addName(locale, conceptName);
		return concept;
	}

	
	/**
	 * 
	 * @return
	 */
	public ConceptSchemeMutableBean createConceptSchemeBean() {
		loadMetadata();
		ConceptSchemeMutableBean conceptScheme = new ConceptSchemeMutableBeanImpl();
		String conceptID = this.targetId+"_concepts";
		conceptScheme.setId(conceptID);
		conceptScheme.setAgencyId(this.targetAgency);
		conceptScheme.setVersion(this.targetVersion);
		conceptScheme.setNames(getNamesMetadata(this.tableNamesMetadata,conceptID+ " Concepts", "en"));
		return conceptScheme;
	}






	/**
	 * 
	 * @param metadataValues
	 * @param defaultValue
	 * @param defaultLocale
	 * @return
	 */
	private List<TextTypeWrapperMutableBean> getNamesMetadata (List<LocalizedText> metadataValues,String defaultValue, String defaultLocale)
	{
		List<TextTypeWrapperMutableBean> response = Lists.newArrayList();
		
		if (metadataValues.size() == 0 && defaultValue != null) 
		{
			log.warn("Names Metadata: using default value "+defaultValue);
			response.add(new TextTypeWrapperMutableBeanImpl(defaultLocale, defaultValue));
		}
		else
		{
			for (LocalizedText text : metadataValues) 
			{
				log.debug("Adding metadata value "+text.getValue()+" "+text.getLocale());
				response.add(new TextTypeWrapperMutableBeanImpl(text.getLocale(), text.getValue()));
			}
		}
		
		return response;

	}
	
	/**
	 * 
	 */
	private void loadMetadata ()
	{
		try
		{
			this.tableNamesMetadata = this.table.getMetadata(NamesMetadata.class).getTexts();
		} catch (NoSuchMetadataException e)
		{
			this.tableNamesMetadata = Lists.newArrayList();
		}

		
	}

	
	

}