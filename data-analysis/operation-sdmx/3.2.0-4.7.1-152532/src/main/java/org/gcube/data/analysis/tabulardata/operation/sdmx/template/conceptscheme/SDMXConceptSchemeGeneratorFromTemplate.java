package org.gcube.data.analysis.tabulardata.operation.sdmx.template.conceptscheme;

import java.util.List;

import org.gcube.data.analysis.tabulardata.commons.templates.model.Template;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.TemplateColumn;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.sdmxsource.sdmx.api.model.mutable.conceptscheme.ConceptMutableBean;
import org.sdmxsource.sdmx.api.model.mutable.conceptscheme.ConceptSchemeMutableBean;
import org.sdmxsource.sdmx.sdmxbeans.model.mutable.conceptscheme.ConceptMutableBeanImpl;
import org.sdmxsource.sdmx.sdmxbeans.model.mutable.conceptscheme.ConceptSchemeMutableBeanImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SDMXConceptSchemeGeneratorFromTemplate 
{

	private Logger log;
	private Template template;
	private String targetAgency;
	private String targetId;
	private String targetVersion;
	


	
	
	public SDMXConceptSchemeGeneratorFromTemplate(Template template, String targetId, String targetAgency, String targetVersion) {

		this.log = LoggerFactory.getLogger(this.getClass());
		this.template = template;
		this.targetAgency = targetAgency;
		this.targetVersion = targetVersion;
		this.targetId = targetId;
	}


	public void populateConceptsScheme (ConceptSchemeMutableBean conceptScheme) throws WorkerException 
	{
		
		log.debug("Loading columns");
		List<TemplateColumn<?>> columns = this.template.getActualStructure();
		log.debug("Columns loaded");
		
		for (TemplateColumn<?> column : columns)
		{
			log.debug("Generating concept for column "+column.getLabel());
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
	public ConceptMutableBean createConceptBean (TemplateColumn<?> templateColumn)
	{
		log.debug("Generating concept mutable bean for column "+templateColumn.getLabel());
		char [] labelChars = templateColumn.getLabel().toCharArray();
		StringBuilder conceptIdBuilder = new StringBuilder();
		
		for (char labelChar : labelChars)
		{
			if (labelChar != ' ') conceptIdBuilder.append(labelChar);
		}
		log.debug("Concept id = "+conceptIdBuilder.toString());
		ConceptMutableBean concept = new ConceptMutableBeanImpl();
		concept.setId(conceptIdBuilder.toString()+"_concept");
		concept.setParentAgency(this.targetAgency);
		concept.addName("en", templateColumn.getLabel());
		return concept;
	}
	

	
	/**
	 * 
	 * @return
	 */
	public ConceptSchemeMutableBean createConceptSchemeBean() {
		ConceptSchemeMutableBean conceptScheme = new ConceptSchemeMutableBeanImpl();
		String conceptSchemeID = this.targetId+"_concepts";
		conceptScheme.setId(conceptSchemeID);
		conceptScheme.setAgencyId(this.targetAgency);
		conceptScheme.setVersion(this.targetVersion);
		conceptScheme.addName("en", this.targetId+" Concepts");
		return conceptScheme;
	}



	

}