package org.gcube.contentmanagement.timeseries.geotools.gisconnectors;

import java.awt.Color;
import java.util.List;
import java.util.UUID;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.contentmanagement.timeseries.geotools.databases.ConnectionsManager;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISOperations.featuresTypes;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISStyleInformation.Scales;
import org.gcube.contentmanagement.timeseries.geotools.representations.GISLayer;

public class GISLayerSaver {

	ConnectionsManager connManager;

	public GISLayerSaver(ConnectionsManager connManager) {
		this.connManager = connManager;
	}

	public void createLayerOnDB(GISLayer layer, featuresTypes type) throws Exception {

		// connManager.getGeoserverConnection().getCurrentSession();

		String creationStatement = GISOperations.createLayerTableStatement(layer.getLayerName(), layer.getValuesColumnName(), type);
		StringBuffer insertStatement = null;

		connManager.GeoserverUpdate(creationStatement);
		int size = layer.getCsquareCodes().size();
		AnalysisLogger.getLogger().warn("CREATING LAYER: " + creationStatement + " with " + size + " squares");

		if (size > 0) {
			String insertStatementStart = "insert into " + layer.getLayerName() + " values ";
			insertStatement = new StringBuffer(insertStatementStart);
			for (int i = 0; i < size; i++) {
				insertStatement.append("(DEFAULT,'" + layer.getCsquareCodes().get(i) + "',NULL,'" + layer.getValues().get(i) + "','" + layer.getInfos().get(i).replace("\'", "") + "')");
				/*
				 * if (i > 20000) { AnalysisLogger.getLogger().warn("Partially Inserting ... "); connManager.GeoserverUpdate(insertStatement.toString()); insertStatement = new StringBuffer(insertStatementStart); } else
				 */
				if (i < size - 1)
					insertStatement.append(",");
			}

//			AnalysisLogger.getLogger().warn("Inserting ... " + insertStatement.toString());
			AnalysisLogger.getLogger().warn("Inserting ... ");
			connManager.GeoserverUpdate(insertStatement.toString());
			String fulfilQuery = GISOperations.createFulfilGeometriesStatement(layer.getLayerName());
			AnalysisLogger.getLogger().warn("UPDATING GEOMETRIES: " + fulfilQuery);
			connManager.GeoserverUpdate(fulfilQuery);
		}

	}

	public String createGISgroup(List<GISLayer> layers, GISInformation gisInfo, String externalGroupName) {
		return createGISLayers(layers, gisInfo, externalGroupName,true,false);
	}
	
	private GISOperations gisOperations;
	
	public GISOperations getGisOperation(){
		return gisOperations;
	}
	
	//generates a GIS group merging some layers to the current overall GIS Information
	public String createGISLayers(List<GISLayer> layers, GISInformation gisInfo, String externalGroupName, boolean generateMap, boolean ignoreStyleCreationFailure) {
		String groupName = null;
		if (layers.size() > 0) {
			if (externalGroupName == null)
				groupName = "group4" + UUID.randomUUID();
			else
				groupName = externalGroupName;
			
			// set group
			GISGroupInformation gisGroup = new GISGroupInformation();
			gisGroup.setGroupName(groupName);
			gisGroup.setTemplateGroupName(GISOperations.TEMPLATEGROUP);
			gisGroup.setTemplateGroup(true);

			int numOfClasses = 5;

			// for custom layers let's link the layers together assigning a common legend
			double absmaximum = -Double.MAX_VALUE;
			double absminimum = Double.MAX_VALUE;

			for (GISLayer layer : layers) {
				double min = layer.getMin();
				double max = layer.getMax();
				if (min < absminimum)
					absminimum = min;
				if (max > absmaximum)
					absmaximum = max;
			}

			for (GISLayer layer : layers) {
				if (!layer.isEmpty()) {
					// setup the main layer to visualize
					GISLayerInformation gisLayer1 = new GISLayerInformation();
					gisLayer1 = new GISLayerInformation();

					gisLayer1.setLayerName(layer.getLayerName());
					gisLayer1.setLayerTitle(layer.getLayerTitle());
					
					// set style
					GISStyleInformation newstyle = new GISStyleInformation();
					String styleName =  (layer.getPreferredStyleName()!=null)?layer.getPreferredStyleName():layer.getLayerName();
					
//					newstyle.setStyleName(styleName+"__"+((int)(10*Math.random())));
					newstyle.setStyleName(styleName);
					Color c1 = Color.yellow;
					Color c2 = Color.red;
					newstyle.setGradientBase(c1);
					newstyle.setGradientMax(c2);
					AnalysisLogger.getLogger().trace("STYLE: MAX:" + layer.getMax() + " MIN:" + layer.getMin());
					// old code: use layer's local max and min
					/*
					 * newstyle.setMax(layer.getMax()); newstyle.setMin(layer.getMin());
					 */
					// new code: use global max and min
					newstyle.setMax(absmaximum);
					newstyle.setMin(absminimum);

					newstyle.setNumberOfClasses(numOfClasses);
					newstyle.setScaleType(Scales.linear);
					newstyle.setStyleAttribute(layer.getValuesColumnName());
					newstyle.setValuesType(Double.class);

					gisLayer1.setDefaultStyle(newstyle.getStyleName());

					// add the layer to the visualizing ones
					gisInfo.addLayer(gisLayer1);
					// associate the style to the layer
					gisInfo.addStyle(gisLayer1.getLayerName(), newstyle);
				}
			}

			// add the group to the generating ones
			gisInfo.setGroup(gisGroup);
			if (generateMap) {
				if (gisInfo.getLayers().size() > 0) {
					gisOperations = new GISOperations();
					boolean generated = gisOperations.generateGisMap(gisInfo,ignoreStyleCreationFailure);
					if (!generated)
						groupName = null;
				} else
					groupName = null;
			}
			/*
			 * Alternative code: reduce the number of classes boolean generationSuccess = false; while (!generationSuccess && (numOfClasses>0)){ generationSuccess = GISOperations.generateGisMap(gisInfo); numOfClasses --; if (!generationSuccess){ AnalysisLogger.getLogger().trace("createGISgroup->REDUCING NUMBER OF CLASSES"); newstyle.setNumberOfClasses(numOfClasses); } }
			 */
		}

		return groupName;
	}

}
