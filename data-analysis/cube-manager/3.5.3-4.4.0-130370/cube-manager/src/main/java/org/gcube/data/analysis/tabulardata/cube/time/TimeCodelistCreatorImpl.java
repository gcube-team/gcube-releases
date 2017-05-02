package org.gcube.data.analysis.tabulardata.cube.time;

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.data.analysis.tabulardata.cube.data.SQLDatabaseWrangler;
import org.gcube.data.analysis.tabulardata.cube.metadata.CubeMetadataWrangler;
import org.gcube.data.analysis.tabulardata.cube.metadata.exceptions.NoSuchTableException;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.factories.AnnotationColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.factories.CodeColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.factories.DimensionColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.factories.IdColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.NamesMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.table.TimePeriodTypeMetadata;
import org.gcube.data.analysis.tabulardata.model.relationship.ImmutableColumnRelationship;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.type.HierarchicalCodelistTableType;
import org.gcube.data.analysis.tabulardata.model.table.type.TimeCodelistTableType;
import org.gcube.data.analysis.tabulardata.model.time.PeriodType;

import com.google.common.collect.Lists;

@Singleton
public class TimeCodelistCreatorImpl implements TimeCodelistCreator {

	@Inject
	SQLDatabaseWrangler dbWrangler;

	@Inject
	CubeMetadataWrangler cmWrangler;

	@Override
	public Table getTable(PeriodType periodType) {
		try {
			return cmWrangler.getTableByName(periodType.getName());
		} catch (NoSuchTableException e) {
			throw new RuntimeException("time codelists not initialized");
		}
	}

	@PostConstruct
	private void initilizeTimeCodelistTables() {
		for (PeriodType period :PeriodType.values()){
			try {
				if (cmWrangler.getTableByName(period.getName())!= null)
					continue;				
			} catch (NoSuchTableException e) {}
			Table codelist = createTableMeta(period);
			String tableName = createTableOnDb(codelist.getColumns(), period.getName());
			codelist.setName(tableName);
			cmWrangler.save(codelist, false);

			@SuppressWarnings("unchecked")
			List<Column> columns =codelist.getColumnsExceptTypes(IdColumnType.class);
			StringBuilder colSnippet = new StringBuilder();
			for (Column col: columns )
				colSnippet.append(col.getName()).append(",");
			colSnippet.deleteCharAt(colSnippet.lastIndexOf(","));

			dbWrangler.executeQuery(String.format("insert into %s %s", period.getName(), period.getSeriesSelectQuery() ));		    

		}

		for (Entry<PeriodType, List<PeriodType>> hPer: PeriodType.getHierarchicalRelation().entrySet()){
			for (PeriodType targetPeriod : hPer.getValue()){
				String hclName = hPer.getKey().getName()+"_"+targetPeriod.getName();
				try {
					if (cmWrangler.getTableByName(hclName)!= null)
						continue;
				} catch (NoSuchTableException e) {}

				Table hTable = createHierarchical(hPer.getKey(), targetPeriod);

				hTable.setName(createTableOnDb(hTable.getColumns(), hclName));

				cmWrangler.save(hTable, false);

				dbWrangler.executeQuery(String.format("insert into %1$s (%2$s_id, %3$s_id) SELECT k.id, v.id from  %2$s as k " +
						", %3$s v where normalize_%3$s(to_iso_%2$s(k.%2$s_code)) = v.%3$s_code;", 
						hTable.getName(), hPer.getKey().getName().toLowerCase(), targetPeriod.getName().toLowerCase() ));
			}
		}

	}

	private Table createHierarchical(PeriodType key, PeriodType value){
		List<Column> columns = Lists.newArrayList(IdColumnFactory.create(), createDimensionColumn(key), createDimensionColumn(value));
		Table hCodelist = new Table(new HierarchicalCodelistTableType());
		hCodelist.setColumns(columns);
		return hCodelist;
	}

	private Column createDimensionColumn(PeriodType value) {
		Column dimColumn = new DimensionColumnFactory().createDefault();
		dimColumn.setName(value.getName()+"_id");
		dimColumn.setDataType(new IntegerType());
		dimColumn.setMetadata(new NamesMetadata(Arrays.asList((LocalizedText)new ImmutableLocalizedText(value.getName()+"_id"))));
		dimColumn.setRelationship(new ImmutableColumnRelationship(getTable(value)));
		return dimColumn;
	}

	private Table createTableMeta(PeriodType periodType) {
		List<Column> columns = Lists.newArrayList(IdColumnFactory.create(), createCodeColumn(periodType), createHumanReadableValueColumn(periodType));
		Table dayCodelist = new Table(new TimeCodelistTableType());
		dayCodelist.setMetadata(new TimePeriodTypeMetadata(periodType));
		dayCodelist.setColumns(columns);
		return dayCodelist;
	}

	private String createTableOnDb(List<Column> columns, String tableName){
		if (dbWrangler.exists(tableName))
			dbWrangler.removeTable(tableName);
		dbWrangler.createTable(tableName);
		for (Column column : columns) {
			if (column.getColumnType() instanceof IdColumnType)
				continue;
			dbWrangler.addColumn(tableName, column.getName(), column.getDataType());
			dbWrangler.setNullable(tableName, column.getName(), false);
			dbWrangler.createIndex(tableName, column.getName());
		}
		return tableName;
	}



	private Column createCodeColumn(PeriodType periodType) {
		Column codeColumn = new CodeColumnFactory().createDefault();
		codeColumn.setName(periodType.getName()+"_code");
		codeColumn.setDataType(new TextType(10));
		codeColumn.setMetadata(new NamesMetadata(Arrays.asList((LocalizedText)new ImmutableLocalizedText(periodType.getName()+" code"))));
		return codeColumn;
	}

	private Column createHumanReadableValueColumn(PeriodType periodType) {
		Column dayColumn = new AnnotationColumnFactory().create(new TextType());
		dayColumn.setName(periodType.getName());
		dayColumn.setMetadata(new NamesMetadata(Arrays.asList((LocalizedText)new ImmutableLocalizedText(periodType.getName()))));
		return dayColumn;
	}


}
