package org.gcube.dataanalysis.geo.meta;

import it.geosolutions.geonetwork.util.GNPriv;
import it.geosolutions.geonetwork.util.GNPrivConfiguration;
import it.geosolutions.geoserver.rest.encoder.GSLayerEncoder;
import it.geosolutions.geoserver.rest.encoder.feature.GSFeatureTypeEncoder;

import java.util.Date;
import java.util.EnumSet;
import java.util.Set;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.spatial.data.geonetwork.GeoNetwork;
import org.gcube.spatial.data.geonetwork.GeoNetworkPublisher;
import org.gcube.spatial.data.geonetwork.LoginLevel;
import org.gcube.spatial.data.geonetwork.configuration.Configuration;
import org.gcube.spatial.data.geonetwork.iso.GcubeISOMetadata;
import org.gcube.spatial.data.geonetwork.iso.Thesaurus;
import org.gcube.spatial.data.geonetwork.model.ScopeConfiguration;
import org.gcube.spatial.data.gis.GISInterface;
import org.gcube.spatial.data.gis.model.report.PublishResponse;
import org.geotoolkit.metadata.iso.extent.DefaultExtent;
import org.opengis.metadata.citation.PresentationForm;
import org.opengis.metadata.identification.TopicCategory;
import org.opengis.metadata.spatial.GeometricObjectType;
import org.opengis.metadata.spatial.TopologyLevel;

public class PolyMapMetadata {

	private static final String crs = "GEOGCS[\"WGS 84\", DATUM[\"World Geodetic System 1984\", SPHEROID[\"WGS 84\", 6378137.0, 298.257223563, AUTHORITY[\"EPSG\",\"7030\"]]," + "AUTHORITY[\"EPSG\",\"6326\"]], PRIMEM[\"Greenwich\", 0.0, AUTHORITY[\"EPSG\",\"8901\"]],  UNIT[\"degree\", 0.017453292519943295]," + "AXIS[\"Geodetic longitude\", EAST],  AXIS[\"Geodetic latitude\", NORTH],  AUTHORITY[\"EPSG\",\"4326\"]]";

	public static PublishResponse publishTable(String scope, String tableName, double resolution, String username, String layerName, String defaultStyle, String workspace, String datastore, String purpose, String credits, String keyword, boolean isprivate) throws Exception {

		GSFeatureTypeEncoder fte = new GSFeatureTypeEncoder();
		fte.setEnabled(true);
		fte.setLatLonBoundingBox(-180.0, -90.0, 180.0, 90.0, crs);
		fte.setName(tableName);
		fte.setNativeCRS(crs);
		GSLayerEncoder le = new GSLayerEncoder();
		le.setDefaultStyle(defaultStyle);
		le.setEnabled(true);
		AnalysisLogger.getLogger().debug("Filling Metadata");
		GcubeISOMetadata meta = fillMeta(resolution, username, layerName, scope, tableName, purpose, credits, keyword);
		AnalysisLogger.getLogger().debug("Getting GIS from scope " + scope);
		GISInterface gis = GISInterface.get();
		Configuration gnConfig = gis.getGeoNetworkReader().getConfiguration();
		AnalysisLogger.getLogger().debug("Using the following GNetwork:" + gnConfig.getGeoNetworkEndpoint());
		LoginLevel level = LoginLevel.SCOPE;
		if (isprivate)
			level = LoginLevel.PRIVATE;

		// PublishResponse resp = gis.publishDBTable(workspace, datastore, fte, le, meta.getMetadata(), new GNInsertConfiguration(gnConfig.getScopeGroup() + "", "datasets", "_none_", true), LoginLevel.DEFAULT);
		
		PublishResponse resp = gis.publishDBTable(workspace, datastore, fte, le, meta.getMetadata(), "datasets", "_none_", level,!isprivate);
		
		AnalysisLogger.getLogger().debug(resp);
		AnalysisLogger.getLogger().debug("ID:" + resp.getReturnedMetaId());
		AnalysisLogger.getLogger().debug("Result:" + resp.getMetaOperationResult());
		if (resp.getReturnedMetaId() == 0)
			return null;
		else
			return resp;
	}

	private static GcubeISOMetadata fillMeta(double resolution, String username, String title, String scope, String tableName, String purpose, String credits, String keyword) throws Exception {

		/*
		if (scope == null)
			scope = ScopeProvider.instance.get();
		 */
		
		AnalysisLogger.getLogger().debug("Setting scope for filling Meta");
//		ScopeProvider.instance.set(scope);

		AnalysisLogger.getLogger().debug("Fulfilling metadata");
		GcubeISOMetadata meta = new GcubeISOMetadata();
		AnalysisLogger.getLogger().debug("Fulfilling metadata Begin");
		meta.setAbstractField("This metadata has been automatically generated from the Statistical Manager on the basis of a distribution of points and according the resolution of " + resolution + " degrees.");
		meta.setCreationDate(new Date(System.currentTimeMillis()));
		meta.setExtent((DefaultExtent) DefaultExtent.WORLD);
		meta.setGeometricObjectType(GeometricObjectType.SURFACE);
		meta.setPresentationForm(PresentationForm.MAP_DIGITAL);
		meta.setPurpose(purpose);
		meta.setResolution(resolution);
		if (title == null || title.length() == 0)
			meta.setTitle("Distribution");
		else
			meta.setTitle(title);

		meta.setTopologyLevel(TopologyLevel.GEOMETRY_ONLY);
		meta.setUser(username);

		meta.addGraphicOverview("http://www.d4science.org/D4ScienceOrg-Social-theme/images/custom/D4ScienceInfrastructure.png");
		meta.addCredits(credits);
		Thesaurus generalThesaurus = meta.getConfig().getThesauri().get("General");
		meta.addKeyword(title, generalThesaurus);
		meta.addKeyword(username, generalThesaurus);
		meta.addKeyword("DataMiner", generalThesaurus);
		meta.addKeyword(keyword, generalThesaurus);
		meta.addKeyword(tableName, generalThesaurus);
		meta.addTopicCategory(TopicCategory.BIOTA);
		AnalysisLogger.getLogger().debug("Fulfilling done");
		return meta;
	}

}
