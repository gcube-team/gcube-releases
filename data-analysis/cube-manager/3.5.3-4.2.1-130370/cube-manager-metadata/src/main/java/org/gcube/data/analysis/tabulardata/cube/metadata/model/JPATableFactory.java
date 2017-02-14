package org.gcube.data.analysis.tabulardata.cube.metadata.model;

import java.util.ArrayList;
import java.util.List;

import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.table.Table;

public class JPATableFactory {
	
	public static JPATable createJPATable(Table table){
		JPATable result = new JPATable();
		result.setName(table.getName());
		result.setTableType(table.getTableType());
		
		int pos=0;
		for (Column column : table.getColumns()) 
			result.getColumns().add(JPAColumnFactory.createJPAColumn(column, pos++));
		
		result.getMetadata().addAll(table.getAllMetadata());
		return result;
	}
	
	public static JPATable updateJPATable(JPATable jpaTable, Table table){
		jpaTable.setName(table.getName());
		jpaTable.setTableType(table.getTableType());
		List<JPAColumn> columns= new ArrayList<JPAColumn>();
		
		int pos=0;
		for (Column column : table.getColumns()) {
			JPAColumn jpaColumn = null;
			if((jpaColumn=jpaTable.getColumn(column.getLocalId()))!=null)
				columns.add(JPAColumnFactory.updateJPAColumn(jpaColumn, column, pos++));
			else
				columns.add(JPAColumnFactory.createJPAColumn(column, pos++));
			
		}
				
		jpaTable.setColumns(columns);
		jpaTable.getMetadata().addAll(table.getAllMetadata());
		return jpaTable;
	}
	
	private static class JPAColumnFactory {
		
		public static JPAColumn createJPAColumn(Column column, int position){
			JPAColumn result = new JPAColumn();
			result.setLocalId(column.getLocalId().getValue());
			result.setName(column.getName());
			result.setType(column.getColumnType());
			result.setDataType(column.getDataType());
			result.setRelationship(column.getRelationship());
			result.setMetadata(column.getAllMetadata());
			result.setPosition(position);
			return result;
		}
		
		public static JPAColumn updateJPAColumn(JPAColumn jpaColumn, Column column, int position){
			JPAColumn result = new JPAColumn();
			result.setColumnId(jpaColumn.getColumnId());
			result.setLocalId(column.getLocalId().getValue());
			result.setName(column.getName());
			result.setType(column.getColumnType());
			result.setDataType(column.getDataType());
			result.setRelationship(column.getRelationship());
			result.setMetadata(column.getAllMetadata());
			result.setPosition(position);
			return result;
		}
		
	}

}
