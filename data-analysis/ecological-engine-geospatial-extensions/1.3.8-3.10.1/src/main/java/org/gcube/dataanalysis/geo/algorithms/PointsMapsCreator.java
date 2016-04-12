package org.gcube.dataanalysis.geo.algorithms;

import java.util.ArrayList;
import java.util.List;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.ColumnType;
import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.InputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;

public class PointsMapsCreator extends MapsCreator {

	@Override
	public String getDescription() {
		return "A transducer algorithm to produce a GIS map of points from a set of points with x,y coordinates indications. A maximum of " + maxNPoints + " is allowed";
	}

	@Override
	public void init() throws Exception {
		log("MAPS_CREATOR");
		datastore = "timeseriesws";
		defaultStyle = "point";
		workspace = "aquamaps";
		username = "statistical.manager";
		purpose = "To Publish Geometric Layers for Points Maps";
		credits = "Generated via the Statistical Manager Service";
		keyword = "Points Map";
	}

	@Override
	protected void setInputParameters() {
		try {
			List<TableTemplates> templates = new ArrayList<TableTemplates>();
			addRemoteDatabaseInput(databaseParameterName, dburlParameterName, dbuserParameterName, dbpasswordParameterName, "driver", "dialect");
			templates.add(TableTemplates.GENERIC);
			addStringInput(layerNameParameter, "The name of the layer to produce", "Points Map");
			InputTable tinput = new InputTable(templates, inputTableParameter, "The table information to geo-spatialize");
			ColumnType xColumn = new ColumnType(inputTableParameter, xParameter, "The column containing longitude information", "", false);
			ColumnType yColumn = new ColumnType(inputTableParameter, yParameter, "The column containing latitude information", "", false);
			ColumnType customDimension = new ColumnType(inputTableParameter, infoParameter, "The column containing information you want to attach to each point", "", false);

			inputs.add(tinput);
			inputs.add(xColumn);
			inputs.add(yColumn);
			inputs.add(customDimension);
			DatabaseType.addDefaultDBPars(inputs);

		} catch (Throwable e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) throws Exception {
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./cfg/");
		config.setGcubeScope("/gcube/devsec");
		config.setParam("DatabaseUserName", "utente");
		config.setParam("DatabasePassword", "d4science");
		config.setParam("DatabaseURL", "jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");

		config.setParam(dburlParameterName, "jdbc:postgresql://geoserver-test.d4science-ii.research-infrastructures.eu/timeseriesgisdb");
		config.setParam(dbuserParameterName, "postgres");
		config.setParam(dbpasswordParameterName, "d4science2");

		config.setParam(inputTableParameter, "occurrence_species_id_fb60ce42_1704_43f9_91a3_2df7d82b1b96");
		config.setParam(xParameter, "decimallongitude");
		config.setParam(yParameter, "decimallatitude");
		config.setParam(infoParameter, "scientificname");
		config.setParam("ServiceUserName", "gianpaolo.coro");
		config.setParam(layerNameParameter, "Generic Species");

		MapsCreator maps = new PointsMapsCreator();
		maps.setConfiguration(config);
		maps.init();
		maps.compute();

	}
}
