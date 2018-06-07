package org.gcube.data.analysis.tabulardata.operation.sdmx.datastructuredefinition.beans;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.sdmx.excel.impl.beans.DataColumnBean;
import org.gcube.data.analysis.tabulardata.operation.sdmx.excel.impl.beans.TableBean;
import org.sdmxsource.sdmx.api.model.beans.codelist.CodelistBean;
import org.sdmxsource.sdmx.api.model.mutable.conceptscheme.ConceptMutableBean;
import org.sdmxsource.sdmx.api.model.mutable.conceptscheme.ConceptSchemeMutableBean;
import org.sdmxsource.sdmx.api.model.mutable.datastructure.DataStructureMutableBean;
import org.sdmxsource.sdmx.api.model.mutable.datastructure.DataflowMutableBean;

public class SDMXDataBean {

	private TableBean tableBean;
	private String id;
	private DataStructureMutableBean dsd;
	private ConceptSchemeMutableBean concepts;
	private DataflowMutableBean dataFlow;
	private Map<String,CodelistBean> associatedCodelists;
	
	public  SDMXDataBean(Table table) {

		this.tableBean = new TableBean(table);
		this.associatedCodelists = new HashMap<>();
	}
	

	public String getID() {
		return this.id;
	}


	public void setID(String id) {
		this.id = id;
		
	}

	public DataStructureMutableBean getDsd() {
		return dsd;
	}

	public void setDsd(DataStructureMutableBean dsd) {
		this.dsd = dsd;
	}

	public ConceptSchemeMutableBean getConcepts() {
		return concepts;
	}

	
	
	public void setConcepts(ConceptSchemeMutableBean concepts) {
		this.concepts = concepts;
	}

	
	public DataflowMutableBean getDataFlow() {
		return dataFlow;
	}

	
	public void setDataFlow(DataflowMutableBean dataFlow) {
		this.dataFlow = dataFlow;
	}

	
	
	public Map<String, CodelistBean> getAssociatedCodelists() {
		return associatedCodelists;
	}

	
	public void addAssociatedCodelist(String columnId, CodelistBean codelist) {
		this.associatedCodelists.put(columnId, codelist);
	}

	
	public Set<CodelistBean> getAllCodelists ()
	{
		return new HashSet<>(this.associatedCodelists.values());
	}


	
	public void setPrimaryMeasure (Column primaryMeasureColumn, ConceptMutableBean associatedConcept)
	{
		
		DataColumnBean primaryMeasure = new DataColumnBean(primaryMeasureColumn,associatedConcept);
		primaryMeasure.setPrimary(true);
		this.tableBean.setPrimaryMeasure(primaryMeasure);
	}
	

	
	public void setTimeDimension (Column timeDimensionColumn, ConceptMutableBean associatedConcept)
	{
		this.tableBean.setTimeDimension(new DataColumnBean(timeDimensionColumn,associatedConcept));

	}
	

	
	public void addMeasureColumn (Column measureColumn, ConceptMutableBean associatedConcept)
	{
		this.tableBean.addMeasureColumn(new DataColumnBean(measureColumn, associatedConcept));
	}
	

	
	
	public void addDimensionColumn (Column dimensionColumn, ConceptMutableBean associatedConcept, CodelistBean associatedCodelist)
	{
		DataColumnBean dimensionBean = new DataColumnBean(dimensionColumn,associatedConcept);
		this.tableBean.addDimensionColumn(dimensionBean);
		
		if (associatedCodelist != null)
		{
			dimensionBean.setCodelist(associatedCodelist);
			addAssociatedCodelist(dimensionColumn.getLocalId().getValue(), associatedCodelist);
		}
	}
	

	
	
	public void addAttributeColumn (Column attributeColumn, ConceptMutableBean associatedConcept, CodelistBean associatedCodelist)
	{
		DataColumnBean attributeBean = new DataColumnBean(attributeColumn,associatedConcept);
		this.tableBean.addAttributeColumn(attributeBean);
		
		if (associatedCodelist != null)
		{
			attributeBean.setCodelist(associatedCodelist);
			addAssociatedCodelist(attributeColumn.getLocalId().getValue(), associatedCodelist);
		}
	}


	
	public TableBean getTableBean() {
		return tableBean;
	}


	
	
	public void addData (String columnName, List<String> data)
	{
		DataColumnBean column = this.tableBean.getColumnByName(columnName);
		
		if (column!= null) column.setData(data);
	}
	
	




	
}
