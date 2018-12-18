package org.gcube.data.analysis.tabulardata.operation.sdmx.template.bean;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.sdmxsource.sdmx.api.model.beans.codelist.CodelistBean;
import org.sdmxsource.sdmx.api.model.beans.conceptscheme.ConceptBean;
import org.sdmxsource.sdmx.api.model.beans.conceptscheme.ConceptSchemeBean;

public class SDMXTemplateBean {
	
	
	
	private class Item
	{
		private ConceptBean concept;
		private CodelistBean codelist;
		
		public Item (ConceptBean concept, CodelistBean codelist)
		{
			this.codelist = codelist;
			this.concept = concept;
		}
	}
	
	private Map<String, ConceptSchemeBean> conceptSchemes;
	private Map<String, List<Item>> measureDimensions;
	private Map<String, List<Item>> normalDimensions;
	private Map<String, List<Item>> observationAttributes;
	private ConceptBean timeDimension;
	
	
	public SDMXTemplateBean() {
		this.conceptSchemes = new HashMap<>();
		this.measureDimensions = new HashMap<>();
		this.normalDimensions = new HashMap<>();
		this.observationAttributes = new HashMap<>();
	}
	
	public Map<String, ConceptSchemeBean>  getConceptSchemes ()
	{
		return this.conceptSchemes;
	}

	
	public void addMeasureDimension (String conceptSchemaID, ConceptBean concept,CodelistBean codelist)
	{
		List<Item> dimensions = this.measureDimensions.get(conceptSchemaID);
		
		if (dimensions == null)
		{
			dimensions = new LinkedList<>();
			this.measureDimensions.put(conceptSchemaID, dimensions);
		}
		
		dimensions.add( new Item (concept,codelist));
		
	}
	
	public void addNormalDimension (String conceptSchemaID, ConceptBean concept,CodelistBean codelist)
	{
		List<Item> dimensions = this.normalDimensions.get(conceptSchemaID);
		
		if (dimensions == null)
		{
			dimensions = new LinkedList<>();
			this.normalDimensions.put(conceptSchemaID, dimensions);
		}
		
		dimensions.add( new Item (concept,codelist));
		
	}
	
	public void addObservationAttributes (String conceptSchemaID, ConceptBean concept,CodelistBean codelist)
	{
		List<Item> attributes = this.observationAttributes.get(conceptSchemaID);
		
		if (attributes == null)
		{
			attributes = new LinkedList<>();
			this.observationAttributes.put(conceptSchemaID, attributes);
		}
		
		attributes.add( new Item (concept,codelist));
		
	}
	
	public void setTimeDimension (ConceptBean timeDimensionConcept)
	{
		this.timeDimension = timeDimensionConcept;
	}
	
	public List<CodelistBean> getAllCodelists ()
	{
		List<CodelistBean> response = new LinkedList<>();
		
		Collection<List<Item>> items = this.measureDimensions.values();
		items.addAll(this.normalDimensions.values());
		items.addAll(this.observationAttributes.values());
		
		Iterator<List<Item>> itemListIterator = items.iterator();
		
		while (itemListIterator.hasNext())
		{
			Iterator<Item> itemIterator = itemListIterator.next().iterator();
			
			while (itemIterator.hasNext())
			{
				CodelistBean codelist = itemIterator.next().codelist;
				
				if (codelist != null) response.add(codelist);
			}
			
			
		}
	
		return response;
	}
	

}
