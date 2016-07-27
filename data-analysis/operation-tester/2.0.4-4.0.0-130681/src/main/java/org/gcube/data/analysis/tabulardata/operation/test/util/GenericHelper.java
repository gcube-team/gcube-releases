package org.gcube.data.analysis.tabulardata.operation.test.util;

import javax.inject.Inject;

import org.eclipse.persistence.jpa.jpql.Assert;
import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.cube.tablemanagers.TableCreator;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.factories.BaseColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.factories.ColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.factories.TimeDimensionColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.type.AttributeColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.DimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.TimeDimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.BooleanType;
import org.gcube.data.analysis.tabulardata.model.datatype.DateType;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;
import org.gcube.data.analysis.tabulardata.model.datatype.NumericType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.TableDescriptorMetadata;
import org.gcube.data.analysis.tabulardata.model.relationship.ImmutableColumnRelationship;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.type.GenericTableType;
import org.gcube.data.analysis.tabulardata.model.time.PeriodType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericHelper {
	
	private static final Logger log = LoggerFactory.getLogger(CodelistHelper.class);

	private ColumnFactory<?> factory=BaseColumnFactory.getFactory(new AttributeColumnType());
	
	private ColumnFactory<?> dimensionFactory=BaseColumnFactory.getFactory(new DimensionColumnType());
	
	private TimeDimensionColumnFactory timeDimensionFactory=(TimeDimensionColumnFactory)BaseColumnFactory.getFactory(new TimeDimensionColumnType());
	
	@Inject
	public CubeManager cm;

	@Inject
	private DatabaseConnectionProvider connectionProvider;

	@Inject
	private CopyHandler copyHandler;

	public Table createSpeciesGenericTable() {
		TableCreator tc = cm.createTable(new GenericTableType());
		Table table = null;

		// Create table structure
		try {
			
			tc.addColumn(factory.createDefault());
			tc.addColumn(factory.createDefault());
			tc.addColumn(factory.createDefault());
			tc.addColumn(factory.createDefault());
			tc.addColumn(factory.createDefault());
			tc.addColumn(factory.createDefault());
			table = tc.create();
			log.debug("Created species generic table:\n" + table);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

		table = cm.modifyTableMeta(table.getId()).setTableMetadata(new TableDescriptorMetadata("test species", "0.0.1", 12)).create();
		
		// Fill with data
		copyHandler.copy("cl_species.csv", table);
		return table;
	}
	
	public Table createNumbersGenericTable(){
		TableCreator tc = cm.createTable(new GenericTableType());
		Table table = null;

		// Create table structure
		try {
			tc.addColumn(factory.createDefault());
			tc.addColumn(factory.createDefault());
			tc.addColumn(factory.createDefault());
			table = tc.create();
			log.debug("Created generic table with numbers:\n" + table);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

		// Fill with data
		copyHandler.copy("numbers.csv", table);
		return table;
	}
	
	public Table createComplexTable(){
		TableCreator tc = cm.createTable(new GenericTableType());
		Table table = null;

		// Create table structure
		try {
			tc.addColumn(factory.createDefault());
			tc.addColumn(factory.createDefault());
			tc.addColumn(factory.createDefault());
			tc.addColumn(factory.create(new IntegerType()));
			tc.addColumn(factory.create(new NumericType()));
			tc.addColumn(factory.createDefault());
			tc.addColumn(factory.create(new BooleanType()));
			tc.addColumn(factory.createDefault());
			tc.addColumn(factory.create(new DateType()));
			table = tc.create();
			log.debug("Created table with complex types:\n" + table);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

		// Fill with data
		copyHandler.copy("complex.csv", table);
		return table;
	}
	
	public Table createDatasetWithSpecies(){
		TableCreator tc = cm.createTable(new GenericTableType());
		Table table = null;

		// Create table structure
		try {
			tc.addColumn(factory.create(new ImmutableLocalizedText("First attribute"),new TextType()));
			tc.addColumn(factory.create(new ImmutableLocalizedText("Second attribute"),new TextType()));
			tc.addColumn(factory.create(new ImmutableLocalizedText("English name"),new TextType()));
			tc.addColumn(factory.create(new ImmutableLocalizedText("Spanish name"),new TextType()));
			tc.addColumn(factory.create(new ImmutableLocalizedText("Species code"),new TextType()));
			tc.addColumn(factory.create(new ImmutableLocalizedText("A measure"),new TextType()));
			table = tc.create();
			log.debug("Created generic dataset with species names and codes:\n" + table);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

		// Fill with data
		copyHandler.copy("speciesdataset.csv", table);
		return table;
	}
	
	public Table createTimePeriodTable(){
		TableCreator tc = cm.createTable(new GenericTableType());
		Table table = null;
		// Create table structure
		try {
			tc.addColumn(factory.create(new ImmutableLocalizedText("Valid day"),new TextType()));
			tc.addColumn(factory.create(new ImmutableLocalizedText("Invalid day"),new TextType()));
			tc.addColumn(factory.create(new ImmutableLocalizedText("Valid week"),new TextType()));
			tc.addColumn(factory.create(new ImmutableLocalizedText("Invalid week"),new TextType()));
			tc.addColumn(factory.create(new ImmutableLocalizedText("Valid month"),new TextType()));
			tc.addColumn(factory.create(new ImmutableLocalizedText("Invalid month"),new TextType()));
			tc.addColumn(factory.create(new ImmutableLocalizedText("Valid quarter"),new TextType()));
			tc.addColumn(factory.create(new ImmutableLocalizedText("Invalid quarter"),new TextType()));
			tc.addColumn(factory.create(new ImmutableLocalizedText("Valid year"),new TextType()));
			tc.addColumn(factory.create(new ImmutableLocalizedText("Invalid year"),new TextType()));
			tc.addColumn(factory.create(new ImmutableLocalizedText("Dates with null"),new TextType()));
			table = tc.create();
			log.debug("Created generic table with several time periods:\n" + table);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		// Fill with data
		copyHandler.copy("timePeriod.csv", table);
		return table;
	}

	public Table createDatasetWithSpeciesAndRelationship(Table codelistTable) {
		TableCreator tc = cm.createTable(new GenericTableType());
		Table table = null;

		// Create table structure
		try {
			tc.addColumn(factory.create(new ImmutableLocalizedText("First attribute"),new TextType()));
			Column dimensionColumn = dimensionFactory.createDefault();
			dimensionColumn.setRelationship(new ImmutableColumnRelationship(codelistTable.getId(), codelistTable.getColumnsExceptTypes(IdColumnType.class).get(0).getLocalId() ));
			tc.addColumn(dimensionColumn);
			tc.addColumn(factory.create(new ImmutableLocalizedText("English name"),new TextType()));
			tc.addColumn(factory.create(new ImmutableLocalizedText("Spanish name"),new TextType()));
			tc.addColumn(factory.create(new ImmutableLocalizedText("Species code"),new TextType()));
			tc.addColumn(factory.create(new ImmutableLocalizedText("A measure"),new TextType()));
			table = tc.create();
			log.debug("Created generic dataset with species names and codes:\n" + table);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

		// Fill with data
		copyHandler.copy("speciesdatasetwithrel.csv", table);
		return table;
	}
	
	public Table createDatasetWithTimeDimension() {
		TableCreator tc = cm.createTable(new GenericTableType());
		Table table = null;
		
		// Create table structure
		try {
			Column dimensionColumn = timeDimensionFactory.create(PeriodType.DAY);
			//dimensionColumn.setRelationship(new ImmutableColumnRelationship(codelistTable.getId(), codelistTable.getColumnsExceptTypes(IdColumnType.class).get(0).getLocalId() ));
			tc.addColumn(dimensionColumn);
			tc.addColumn(factory.create(new ImmutableLocalizedText("First attribute"),new TextType()));
			tc.addColumn(factory.create(new ImmutableLocalizedText("mes"),new IntegerType()));
			table = tc.create();
			log.debug("Created time dataset :\n" + table);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

		// Fill with data
		copyHandler.copy("time.csv", table);
		return table;
	}

}
