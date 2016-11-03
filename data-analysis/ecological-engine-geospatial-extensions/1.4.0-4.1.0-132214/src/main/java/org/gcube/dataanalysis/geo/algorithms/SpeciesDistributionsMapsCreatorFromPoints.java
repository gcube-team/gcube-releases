package org.gcube.dataanalysis.geo.algorithms;

import java.util.ArrayList;
import java.util.List;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.ColumnType;
import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.InputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube.dataanalysis.geo.utils.GeospatialDataPublicationLevel;

public class SpeciesDistributionsMapsCreatorFromPoints extends MapsCreator {

	@Override
	public String getDescription() {
		return "A transducer algorithm to produce a GIS map from a probability distribution made upf of x,y coordinates and a certain resolution. A maximum of " + maxNPoints + " is allowed";
	}

	@Override
	public void init() throws Exception {
		log("MAPS_CREATOR");
		datastore = "timeseriesws";
		defaultStyle = "Species_prob";
		workspace = "aquamaps";
		username = "statistical.manager";
		purpose = "To Publish Geometric Layers for Species Distribution Maps";
		credits = "Generated via the Statistical Manager Service";
		keyword = "Species Probability Distribution";
	}

	@Override
	protected void setInputParameters() {
		try {
			PrimitiveType e = new PrimitiveType(Enum.class.getName(), GeospatialDataPublicationLevel.values(), PrimitiveTypes.ENUMERATED, publicationLevel, "The visibility level of the produced map",""+GeospatialDataPublicationLevel.PRIVATE);
			inputs.add(e);
		
			List<TableTemplates> templates = new ArrayList<TableTemplates>();
			addRemoteDatabaseInput(databaseParameterName, dburlParameterName, dbuserParameterName, dbpasswordParameterName, "driver", "dialect");
			templates.add(TableTemplates.GENERIC);
			addStringInput(layerNameParameter, "The name of the layer to produce", "Species Prob Distribution Points");
			InputTable tinput = new InputTable(templates, inputTableParameter, "The table information to geo-spatialize");
			ColumnType xColumn = new ColumnType(inputTableParameter, xParameter, "The column containing longitude information", "", false);
			ColumnType yColumn = new ColumnType(inputTableParameter, yParameter, "The column containing latitude information", "", false);
			ColumnType probabilityDimension = new ColumnType(inputTableParameter, probabilityParameter, "The column containing probability information", "", false);

			inputs.add(tinput);
			inputs.add(xColumn);
			inputs.add(yColumn);
			inputs.add(probabilityDimension);
			addDoubleInput(resolutionParameter, "The map resolution in degrees", "0.5");
			DatabaseType.addDefaultDBPars(inputs);

		} catch (Throwable e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) throws Exception {
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./cfg/");
		config.setGcubeScope("/gcube");
		config.setParam("DatabaseUserName", "utente");
		config.setParam("DatabasePassword", "d4science");
		config.setParam("DatabaseURL", "jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");

		config.setParam(dburlParameterName, "jdbc:postgresql://geoserver-test.d4science-ii.research-infrastructures.eu/timeseriesgisdb");
		config.setParam(dbuserParameterName, "postgres");
		config.setParam(dbpasswordParameterName, "d4science2");

		config.setParam(inputTableParameter, "occcluster_id_59005678_4863_49ba_9c66_ebac80829da3");
		config.setParam(xParameter, "centerlong");
		config.setParam(yParameter, "centerlat");
		config.setParam(probabilityParameter, "faoaream");
		config.setParam(resolutionParameter, "0.5");
		config.setParam("ServiceUserName", "gianpaolo.coro");
		config.setParam(layerNameParameter, "Generic Species");

		MapsCreator maps = new SpeciesDistributionsMapsCreatorFromPoints();
		maps.setConfiguration(config);
		maps.init();
		maps.compute();

	}
}
