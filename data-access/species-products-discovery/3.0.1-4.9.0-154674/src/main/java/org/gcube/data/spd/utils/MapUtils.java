package org.gcube.data.spd.utils;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.Property;
import org.gcube.data.spd.model.PointInfo;
import org.gcube.data.spd.model.service.types.MetadataDetails;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.spatial.data.geonetwork.LoginLevel;
import org.gcube.spatial.data.geonetwork.iso.GcubeISOMetadata;
import org.gcube.spatial.data.geonetwork.iso.MissingInformationException;
import org.gcube.spatial.data.geonetwork.iso.Thesaurus;
import org.gcube.spatial.data.geonetwork.model.faults.EncryptionException;
import org.gcube.spatial.data.gis.GISInterface;
import org.gcube.spatial.data.gis.model.report.PublishResponse;
import org.gcube.spatial.data.gis.model.report.Report.OperationState;
import org.geotoolkit.metadata.iso.extent.DefaultExtent;
import org.opengis.metadata.Metadata;
import org.opengis.metadata.citation.PresentationForm;
import org.opengis.metadata.identification.TopicCategory;
import org.opengis.metadata.spatial.GeometricObjectType;
import org.opengis.metadata.spatial.TopologyLevel;

import it.geosolutions.geoserver.rest.encoder.GSLayerEncoder;
import it.geosolutions.geoserver.rest.encoder.feature.GSFeatureTypeEncoder;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class MapUtils {

	private static final String CRS = "GEOGCS[\"WGS 84\", DATUM[\"World Geodetic System 1984\", SPHEROID[\"WGS 84\", 6378137.0, 298.257223563, AUTHORITY[\"EPSG\",\"7030\"]],"+ 
			"AUTHORITY[\"EPSG\",\"6326\"]], PRIMEM[\"Greenwich\", 0.0, AUTHORITY[\"EPSG\",\"8901\"]],  UNIT[\"degree\", 0.017453292519943295],"+ 
			"AXIS[\"Geodetic longitude\", EAST],  AXIS[\"Geodetic latitude\", NORTH],  AUTHORITY[\"EPSG\",\"4326\"]]";



	@Data
	public static class LayerCreationOptions{
		@NonNull
		private String workspace;
		@NonNull
		private String defaultStyle;
		@NonNull
		private String store;
		//* GN
		@NonNull
		private String layerCategory;
		@NonNull
		private Boolean publishAsParentContext;
		@NonNull
		private Boolean accessibleParentContexts;

	}



	@Data	
	public static class DataBaseDescription{
		@NonNull
		private String databaseEndpoint;
		@NonNull
		private String user;
		@NonNull
		private String password;
	}


	@Data
	public static class Map{
		@NonNull
		private String layerUUID;
		@NonNull
		private String featureType;
		@NonNull
		private String databaseTable;		
	}


	
	public static final Map publishLayerByCoords(MetadataDetails metadata,Collection<PointInfo> points, Boolean publishAsParentContext, Boolean accessibleParentContexts) throws Exception{
		DataBaseDescription db=loadDB();
		LayerCreationOptions layerOpts=loadOptions(publishAsParentContext, accessibleParentContexts);
		return publishLayerByCoords(db, layerOpts, metadata, points);
	}
	
	

	public static final Map publishLayerByCoords(DataBaseDescription db,LayerCreationOptions layerOptions, MetadataDetails metadata,Collection<PointInfo> points) throws Exception{
		if(points==null||points.isEmpty()) throw new Exception("Empty or null collection cannot be a layer");
		String tableName=null;
		try{
			log.trace("Generating layer by points");
			tableName=createPointTable(db, points);
			log.debug("Created table {} in {} ",tableName,db);
			PublishResponse resp=createLayer(layerOptions, metadata, tableName);
			log.debug("Publish response output {} ",resp);

			if(!resp.getDataOperationResult().equals(OperationState.COMPLETE)){
				throw new Exception("Erors while publishing layer. Messages are : "+resp.getDataOperationMessages());
			}else if(!resp.getMetaOperationResult().equals(OperationState.COMPLETE)){
				throw new Exception("Erors while publishing layer metadata. Messages are : "+resp.getMetaOperationMessages());
			}else {
				String uuid=resp.getPublishedMetadata().getFileIdentifier();
				log.trace("Genrated layer {} ",uuid);
				return new Map(uuid, tableName, tableName);				
			}

		}catch(Exception e){
			log.trace("Unexpected errors while publishing layer. Throwing exception {} ",e.getMessage());
			if(tableName!=null){
				log.debug("Dropping created postgis table {} ",tableName);
				dropTable(tableName, db);
			}
			throw e;
		}
	}




	private static final boolean dropTable(String tableName,DataBaseDescription db){
		Connection conn=null;
		try{
			conn=connect(db);
			conn.createStatement().execute("DROP TABLE "+tableName);
			return true;
		}catch(Exception e){
			log.warn("Unable to drop table {}.",tableName,e);
			return false;
		}finally{
			closeQuietly(conn);
		}
	}


	private static final Connection connect(DataBaseDescription db) throws SQLException{
//		String dbUrl="jdbc:postgresql://"+db.getHost()+":"+db.getPort()+"/"+db.getDatabaseName();
		log.debug("Connecting to {}, user : {} ",db.getDatabaseEndpoint(),db.user);
		try{
			Class.forName("org.postgresql.Driver");
		}catch(Exception e){
			throw new RuntimeException(e);
		}
		return DriverManager.getConnection(db.getDatabaseEndpoint(),db.getUser(),db.getPassword());
	}

	private static final String createPointTable(DataBaseDescription db,Collection<PointInfo> points) throws SQLException{
		Connection conn=null;
		PreparedStatement psInsert=null;		
		try{
			conn=connect(db); 
			conn.setAutoCommit(false);
			String tableName="spd"+UUID.randomUUID().toString().replace("-", "");
			String createStatement="CREATE TABLE "+tableName+" (the_geom geometry)";
			log.debug("Executing {} ",createStatement);
			conn.createStatement().execute(createStatement);
			psInsert=conn.prepareStatement("INSERT INTO "+tableName+" (the_geom) VALUES( ST_GeomFromText(?, 4326))");
			log.debug("Gonna execute insert..");
			long count=0l;
			for(PointInfo point :points){
				psInsert.setString(1, "POINT("+point.getX()+" "+point.getY()+")"); // POINT(-71.060316 48.432044)
				count+=psInsert.executeUpdate();
			}
			conn.commit();
			log.debug("inserted {} / {} entries in table {}. Closing connection to db..", count,points.size(),tableName);

			return tableName;
		}catch(Throwable t){
			log.error("Unable to create table.",t);
			throw new SQLException("Rethrown exception, unable to create table.",t);
		}finally{			
			closeQuietly(psInsert);
			closeQuietly(conn);
		}
	}




	private static void closeQuietly(AutoCloseable toClose){
		if(toClose!=null){
			try {
				toClose.close();
			} catch (Exception e) {
				log.debug("Exception while closing... ",e);
			}
		}
	}


	private static final PublishResponse createLayer(LayerCreationOptions layerOpt,MetadataDetails details,String tableName) throws URISyntaxException, MissingInformationException, Exception{

		GSFeatureTypeEncoder fte=new GSFeatureTypeEncoder();
		fte.setEnabled(true);
		fte.setLatLonBoundingBox(-180.0, -90.0, 180.0, 90.0, CRS);
		fte.setName(tableName);
		fte.setNativeCRS(CRS);


		// GSLayerEncoder layerEncoder

		GSLayerEncoder le=new GSLayerEncoder();
		le.setDefaultStyle(layerOpt.getDefaultStyle());
		le.setEnabled(true);

		log.debug("Generating meta for layer table {}. Meta parameters are {}",tableName,details);
		Metadata meta=fillMeta(details).getMetadata();


		GISInterface gis=GISInterface.get();

		log.trace("Publishing layer from table {} with options {} in store {} ",tableName,layerOpt);

		LoginLevel login= layerOpt.getAccessibleParentContexts()?LoginLevel.SCOPE:LoginLevel.PRIVATE;


		return gis.publishDBTable(layerOpt.getWorkspace(),layerOpt.getStore(), fte, le, 
				meta, layerOpt.getLayerCategory(), "_none_", login,layerOpt.getPublishAsParentContext());
	}



	private static GcubeISOMetadata fillMeta(MetadataDetails metaDetails) throws Exception{
		GcubeISOMetadata meta=new GcubeISOMetadata();
		meta.setAbstractField(metaDetails.getAbstractField());
		meta.setCreationDate(new Date(System.currentTimeMillis()));
		meta.setExtent((DefaultExtent) DefaultExtent.WORLD);
		meta.setGeometricObjectType(GeometricObjectType.SURFACE);
		meta.setPresentationForm(PresentationForm.MAP_DIGITAL);
		meta.setPurpose(metaDetails.getPurpose());
		meta.setResolution(0.5d);
		meta.setTitle(metaDetails.getTitle());
		meta.setTopologyLevel(TopologyLevel.GEOMETRY_ONLY);
		meta.setUser(metaDetails.getAuthor());		


		meta.addCredits(metaDetails.getCredits());
		List<String> keywords=metaDetails.getKeywords();
		if(keywords!=null&&!keywords.isEmpty()){
			Thesaurus generalThesaurus=meta.getConfig().getThesauri().get("General");
			for(String key:keywords)
				meta.addKeyword(key, generalThesaurus);			
		}
		meta.addTopicCategory(TopicCategory.BIOTA);
		return meta;
	}
	
	
	
	//******************* IS QUERIES
	
	public static final DataBaseDescription loadDB() throws Exception{
		SimpleQuery query = queryFor(ServiceEndpoint.class);
		 
		query.addCondition("$resource/Profile/Category/text() eq 'Gis'")
		.addCondition("$resource/Profile/Name/text() eq 'TimeSeriesDataStore'")
		         .setResult("$resource/Profile/AccessPoint");
			
		DiscoveryClient<AccessPoint> client = clientFor(AccessPoint.class);
		 
		List<AccessPoint> accesspoints = client.submit(query);
		DataBaseDescription toReturn=null;
		for (AccessPoint point : accesspoints) {
			if (point.name().equals("jdbc")){
				toReturn=new DataBaseDescription(point.address(), point.username(), decrypt(point.password()));
				break;
			}
		}
		
		if(toReturn==null) throw new Exception("Database info not found in current scope");
		return toReturn;
	}
	
	
	
	public static final LayerCreationOptions loadOptions(Boolean publishAsParentContext, Boolean accessibleParentContexts) throws Exception{
		SimpleQuery query = queryFor(ServiceEndpoint.class);
		 
		query.addCondition("$resource/Profile/Category/text() eq 'Gis'")
		.addCondition("$resource/Profile/Name/text() eq 'GeoServer'")
		         .setResult("$resource/Profile/AccessPoint");
		
		
		DiscoveryClient<AccessPoint> client = clientFor(AccessPoint.class);
		
		List<AccessPoint> accesspoints = client.submit(query);
		LayerCreationOptions toReturn=null;
		
		for (AccessPoint point : accesspoints) {
			if (point.name().equals("geoserver")){		
				java.util.Map<String, Property> properties=point.propertyMap();
				toReturn=new LayerCreationOptions(properties.get("timeseriesWorkspace").value(), "point", properties.get("timeseriesDataStore").value(), "datasets",
						publishAsParentContext, accessibleParentContexts);
				break;
			}
		}
		
		if(toReturn==null) throw new Exception("Layer Creation Options not found in current scope");
		return toReturn;
		
	}
	
	
	public static final String decrypt(String toDecrypt) throws EncryptionException{
		try{
			return StringEncrypter.getEncrypter().decrypt(toDecrypt);
		}catch(Exception e){
			throw new EncryptionException(e);
		}
		
	}
	
}
