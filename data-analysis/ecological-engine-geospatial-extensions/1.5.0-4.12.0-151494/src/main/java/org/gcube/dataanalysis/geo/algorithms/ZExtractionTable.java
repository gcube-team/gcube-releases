package org.gcube.dataanalysis.geo.algorithms;

import java.util.ArrayList;
import java.util.List;

import org.gcube.dataanalysis.ecoengine.datatypes.ColumnType;
import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.InputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube.dataanalysis.ecoengine.utils.IOHelper;
import org.gcube.dataanalysis.geo.connectors.table.TableMatrixRepresentation;


public class ZExtractionTable extends ZExtraction{

	@Override
	public String getDescription() {
		return "An algorithm to extract a time series of values associated to a table containing geospatial information. " +
				"The algorithm analyses the time series and automatically searches for hidden periodicities. " +
				"It produces one chart of the time series, one table containing the time series values and possibly the spectrogram.";
	}
	
	@Override
	public List<StatisticalType> getInputParameters() {
		
		List<StatisticalType> inputs = new ArrayList<StatisticalType>();
		
		List<TableTemplates> template= new ArrayList<TableTemplates>();
		template.add(TableTemplates.GENERIC);
		InputTable table = new InputTable(template,TableMatrixRepresentation.tableNameParameter,"A geospatial table containing at least x,y information","");
		inputs.add(table);
		ColumnType columnx = new ColumnType(TableMatrixRepresentation.tableNameParameter, TableMatrixRepresentation.xDimensionColumnParameter, "The column containing x (longitude) information", "x", false);
		inputs.add(columnx);
		ColumnType columny = new ColumnType(TableMatrixRepresentation.tableNameParameter, TableMatrixRepresentation.yDimensionColumnParameter, "The column containing y (latitude) information", "y", false);
		inputs.add(columny);
		ColumnType columnt = new ColumnType(TableMatrixRepresentation.tableNameParameter, TableMatrixRepresentation.zDimensionColumnParameter, "The column containing z information", "z", false);
		inputs.add(columnt);
		ColumnType columnvalue = new ColumnType(TableMatrixRepresentation.tableNameParameter, TableMatrixRepresentation.valueDimensionColumnParameter, "A column containing real valued features", "value", false);
		inputs.add(columnvalue);
		inputs.add(new PrimitiveType(String.class.getName(), null, PrimitiveTypes.STRING, TableMatrixRepresentation.filterParameter, "A filter on one of the columns (e.g. speed=2)", " "));
		
		IOHelper.addStringInput(inputs, TableMatrixRepresentation.timeDimensionColumnParameter, "The column containing time information (optional).", "time");
				
		List<StatisticalType> previnputs = super.getInputParameters();
		previnputs.remove(0);
		inputs.addAll(previnputs);
		
		DatabaseType.addDefaultDBPars(inputs);
		
		return inputs;
	}
	
}
