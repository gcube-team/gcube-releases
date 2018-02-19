package org.gcube.data.analysis.tabulardata.operation.sdmx.excel.impl.beans;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.sdmxsource.sdmx.api.model.beans.codelist.CodelistBean;
import org.sdmxsource.sdmx.api.model.mutable.base.TextTypeWrapperMutableBean;
import org.sdmxsource.sdmx.api.model.mutable.conceptscheme.ConceptMutableBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ColumnBean 
{
	private Column column;
	private ConceptMutableBean associatedConcept;
	private CodelistBean codelist;
	private boolean isPrimary;
	private HashMap<String, String> columnNames;
	private Logger logger;
	
	public ColumnBean (Column column,ConceptMutableBean associatedConcept)
	{
		this.logger = LoggerFactory.getLogger(this.getClass());
		this.column = column;
		this.isPrimary = false;
		this.associatedConcept = associatedConcept;
		this.columnNames = new HashMap<>();
	}
	

	
	
	public boolean isPrimary() {
		return isPrimary;
	}





	public void setPrimary(boolean isPrimary) {
		this.isPrimary = isPrimary;
	}





	public String getName(String locale) {
		this.logger.debug("Looking name for locale "+locale);
		String name = this.columnNames.get(locale);
		
		if (name == null)
		{
			this.logger.debug("Column name not found, looking among the concepts");
			
			List<TextTypeWrapperMutableBean> names = this.associatedConcept.getNames();
			
			if (names != null && !names.isEmpty())
			{
				Iterator<TextTypeWrapperMutableBean> namesIterator = names.iterator();
				String defaultName = null;
				
				while (namesIterator.hasNext() && name == null)
				{
					TextTypeWrapperMutableBean textTypeWrapperName = namesIterator.next();
					
					if (defaultName == null)
					{
						defaultName = textTypeWrapperName.getValue();
						this.logger.debug("Default name = "+defaultName);
					}
					
					this.logger.debug("Found name for locale "+textTypeWrapperName.getLocale());
					
					if (textTypeWrapperName.getLocale().equalsIgnoreCase(locale)) name = textTypeWrapperName.getValue();
				}
				
				if (name == null) name = defaultName;
				
				
				this.columnNames.put(locale, name);
			}
			else
			{
				this.logger.warn("No names found among concepts: using column name");
				name = this.column.getName();
			}
		}

		this.logger.debug("Column name "+name);
		return name;
	}


	public void setCodelist (CodelistBean codelistBean)
	{
		this.codelist = codelistBean;
	}

	public Column getColumn() {
		return column;
	}

	public CodelistBean getAssociatedCodelist() {
		return codelist;
	}

	

	
	
}
