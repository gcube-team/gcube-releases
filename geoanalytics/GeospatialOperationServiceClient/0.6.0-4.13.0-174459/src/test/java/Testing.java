import static org.gcube.common.scope.impl.ScopeBean.Type.VRE;

import java.io.IOException;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.management.AttributeList;

//import org.apache.commons.collections.keyvalue.AbstractMapEntry;
//import org.apache.spark.broadcast.Broadcast;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.resources.discovery.icclient.stubs.MalformedQueryException;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import gr.cite.clustermanager.actuators.functions.ExecutionMonitor;
import gr.cite.clustermanager.actuators.functions.ExecutionNotifier;
import gr.cite.clustermanager.actuators.layers.DataCreatorGeoanalytics;
import gr.cite.clustermanager.model.layers.GosDefinition;
import gr.cite.clustermanager.model.layers.ZNodeData.ZNodeStatus;
import gr.cite.geoanalytics.client.GeoanalyticsImportManagement;
import gr.cite.geoanalytics.client.GeoanalyticsManagement;
import gr.cite.geoanalytics.dataaccess.entities.layer.DataSource;
import gr.cite.geoanalytics.dataaccess.entities.layer.Layer;
import gr.cite.geoanalytics.dataaccess.entities.layer.LayerTenant;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.project.Project;
import gr.cite.geoanalytics.dataaccess.entities.project.ProjectLayer;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
import gr.cite.geoanalytics.dataaccess.entities.tenant.Tenant;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.Bounds;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.FeatureType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.GeoserverLayer;
import gr.cite.gos.client.GeoserverManagement;
import gr.cite.gos.client.ShapeManagement;

public class Testing extends GeoanalyticsManagement implements Serializable{

	private static String geoanalyticsEP = "http://dl012.madgik.di.uoa.gr:8080/geoanalytics-2.3.2-SNAPSHOT";
	private static String gosEP = "http://dl008.madgik.di.uoa.gr:8080/GeospatialOperationService";
	private static String geoserverEP = "http://dl008.madgik.di.uoa.gr:8080/geoserver";
	private static String token = "d259036a-5558-4554-93d4-a979b1146ec0-98187548"; //this is app token for devvre
	private static String zkConnStr = "dl012.madgik.di.uoa.gr:2181";
	
	private static String principalID = "00000000-0000-0000-0000-000000000001";
	
	
	
	private static final long serialVersionUID = -204832173686069616L;
	
	private static final Logger logger = LoggerFactory.getLogger(GeoanalyticsImportManagement.class);

	@Autowired private DataCreatorGeoanalytics dataCreatorGeoanalytics;
	@Autowired private ExecutionNotifier executionNotifier;
	@Autowired private ExecutionMonitor executionMonitor;
	
	private Testing(){}
	
	public Testing(String authenticationStr){
		super(authenticationStr);
	}
	
	
	
	
	public static void main (String [] args) throws Exception{
		
//		DataMonitor dm = DataMonitor.getInstance(zkConnStr);
//		while (dm.getAllGosEndpoints().size()==0){
//			System.out.println("Endpoints not yet discovered... will wait another 300ms... ");
//			Thread.sleep(300);
//		}
//		System.out.println(dm.getAllGosEndpoints());
		
		
//		ShapeManagement sm = new ShapeManagement(token);
//		List<String> layers = sm.getLayers(gosEP);
//		System.out.println(layers);
		
		
		
		Testing testing = new Testing(token);
		testing.createDummyLayer();
		
		
//		for(String layer: layers){
//			List<Shape> shapes = sm.getShapesOfLayerID(gosEP, layer);
//			System.out.println(shapes.size());
//		}
		
		
		//sm.insertShape(gosEndpoint, shape)
		
		
		
		
//		GeoserverManagement gm = new GeoserverManagement(token);
//		System.out.println(gm.listDataStores(gosEP));
		
		
		
		
		
		
		
	}
	
	
	public void createDummyLayer() throws Exception{
		
		
		GosDefinition gosDefinition = new GosDefinition("dl008.madgik.di.uoa.gr", gosEP, geoserverEP, "geoanalytics", "geoanalytics");
		
		
		//create Layer on geoanalytics
		Layer layer = new Layer();
		layer.setIsTemplate((short)0);
		layer.setName("DUMMY LAYER");
		layer.setDataSource(DataSource.PostGIS);
		layer.setDescription("This layer is generated by geoanalytics functions");
		layer.setReplicationFactor(1);
		layer.setStyle("dummystyle");
		
		Principal principal = new Principal();
		principal.setId(UUID.fromString(principalID));
		layer.setCreator(principal);
		
		Set<LayerTenant> layerTenants = new HashSet<LayerTenant>();
		LayerTenant lt = new LayerTenant();
		Tenant t = new Tenant();
		t.setId(UUID.fromString("f3f61dcd-9929-4d13-a18f-2a31009332c3"));
		lt.setTenant(t);
		layerTenants.add(lt);
		layer.setLayerTenants(layerTenants);
		
		Project project = new Project();
		project.setId(UUID.fromString("f47806b9-3c46-4488-ba42-475e2b7fa30f"));  //named "TestProject"
		project.setIsTemplate(false);
		Set<ProjectLayer> projectLayers = new HashSet<ProjectLayer>();
		ProjectLayer pl = new ProjectLayer();
		pl.setProject(project);
		projectLayers.add(pl);
		layer.setProjectLayers(projectLayers);
		
		
		String layerID = createLayer(geoanalyticsEP, layer);
		
		if(layerID==null || layerID.isEmpty()) //no layer was created, so it can't proceed.
			throw new Exception("Error while creating the layer on Geoanalytics to host the results of the analytics function... Will not proceed with function execution!");
		
		layer.setId(UUID.fromString(layerID));
		
		
//		Broadcast<String> sridBC = jsc.broadcast(srid);
//		Broadcast<String> layerIdBC = jsc.broadcast(layerID);
//		Broadcast<String> creatorIdBC = jsc.broadcast(creatorID);
//		Broadcast<String> authStrBC = jsc.broadcast(authenticationStr);
////				Broadcast<String> geoanalyticsEndpointBC = jsc.broadcast(geoanalyticsEndpoint);
//		Broadcast<GosDefinition> gosDefinitionBC = jsc.broadcast(gosDefinition);
		
		
//		Set<Boolean> results = featuresRDD
//				.map(new Mapper(layerIdBC.getValue(), creatorIdBC.getValue(), authStrBC.getValue(), gosDefinitionBC.getValue().getGosEndpoint()))
//				.reduce(new Reducer());
		
		ShapeManagement sm = new ShapeManagement(token);

		logger.debug("Function on all executors has completed successfully. Adding layer on geoanalytics, geoserver and notifying cluster");
		//means that all were successful, so go on with the remaining layer creation (create views, create geoserver entries, etc)
		
		
		//0. THIS STEP SHOULD BE DELETED -- create a dummy shape
		
		
		List<Attribute> result = new ArrayList<Attribute>();
		result.add(new Attribute("value1", new Integer(500)));
		result.add(new Attribute("value2", new Double(100.32)));
		
		
		List<ExtradataField> extraFields = new ArrayList<>();
		
		String valueSting = "";
		for(Attribute attribute : result){
			extraFields.add(new ExtradataField(attribute.getName(), attribute.getValue()));
			valueSting += attribute.getValue().toString();
		}
		
		String extraData = Helper.formExtradataField(extraFields.toArray(new ExtradataField[extraFields.size()]));
		Shape shape = new Shape();
		GeometryFactory gFactory = JTSFactoryFinder.getGeometryFactory();
		Point point = gFactory.createPoint(new Coordinate(10, 20));
		shape.setGeography(point);
		shape.setCode("EPSG:4326");
		shape.setExtraData(extraData);
		
		//additional
		shape.setLayerID(UUID.fromString(layerID)); //this is VERY important. DO NOT DELETE!
		shape.setCreatorID(UUID.fromString(principalID)); 
		
		
		List<Shape> shapes = new ArrayList<Shape>();
		shapes.add(shape);
		//insert into geoanalytics
		boolean status = sm.insertShapes(gosEP, shapes);
		
		System.out.println("STATUS OF SHAPE INSERT: " + status);
		
		
		List<Map.Entry<String, Class>> fieldName_Datatype_Pairs = new ArrayList<Map.Entry<String, Class>>();
		for(Attribute attrib : result)
			fieldName_Datatype_Pairs.add(new AbstractMap.SimpleEntry<String, Class>(attrib.getName(), attrib.getValue().getClass()));
		
		System.out.println("VIEW TO BE CREATED: "+ viewCreation(layerID, fieldName_Datatype_Pairs));
		
		//1. create layer view (optional)
//		ShapeManagement sm = new ShapeManagement(authenticationStr);
		boolean s1 = sm.applyOnView(gosDefinition.getGosEndpoint(), viewCreation(layerID, fieldName_Datatype_Pairs));
		//2. create geoserver layer mapping (optional)
		addGeoserverLayer(gosDefinition, layerID, "point");
		//3. Notify zookeeper about the new layer
//		dataCreatorGeoanalytics.addLayer(layerID, ZNodeStatus.ACTIVE, gosDefinition.getGosIdentifier());
		//4. notify the zookeeper about the status of the execution
//		currentExecution.setProgress(100);
//		currentExecution.setStopTimestamp(System.currentTimeMillis());
//		currentExecution.setStatus(ExecutionStatus.SUCCEEDED);
//		currentExecution.setLayerID(layerID);
//		executionNotifier.notifyAbout(currentExecution);
	
		
		
		//ROLLBACK PLZ
		
		//means that there was at least one failed on executors, so rollback
		//1. delete from geoserver layer mappings (optional)
		GeoserverManagement gm = new GeoserverManagement(authenticationStr);
		gm.deleteGeoserverLayer(gosDefinition.getGosEndpoint(), layerID, layer.getDataSource());
		//2. delete layer view (optional)
		sm.applyOnView(gosDefinition.getGosEndpoint(), viewDeletion(layerID));
		//3. delete shapes
		sm.deleteShapesOfLayer(gosDefinition.getGosEndpoint(), layerID);
		//4. delete layer
		deleteLayer(geoanalyticsEP, layer);
		
		
	}
	
	
	public String createLayer(String geoanalyticsEndpoint, Layer layer){
		
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
		headers.add(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr);
		headers.add("Content-Type", "application/json");

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		HttpEntity<Layer> request = new HttpEntity<Layer>( layer, headers);
		return restTemplate.postForObject(geoanalyticsEndpoint+"/createLayerSpark", request, String.class);
		
	}
	
	public String deleteLayer(String geoanalyticsEndpoint, Layer layer) {
		
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
		headers.add(super.HEADER_AUTHENTICATION_PARAM_NAME, authenticationStr);
		headers.add("Content-Type", "application/json");

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		HttpEntity<Layer> request = new HttpEntity<Layer>( layer, headers);
		return restTemplate.postForObject(geoanalyticsEndpoint+"/deleteLayerSpark", request, String.class);
	}
	
	public String viewCreation(String layerID, List<Map.Entry<String, Class>> fieldName_Datatype_Pairs){
		
		StringBuilder view = new StringBuilder();
		view.append("CREATE MATERIALIZED VIEW \""+layerID+"\" AS SELECT s.\"SHP_Geography\"::geometry AS \"SHP_Geography\", s.\"SHP_ID\"");
		for(Map.Entry<String, Class> name_DataType : fieldName_Datatype_Pairs)
			view.append(",(xpath('//extraData/"+name_DataType.getKey()+"/text()'::text, s.\"SHP_ExtraData\"))[1]::text::"+toPGSqlDataType(name_DataType.getValue())+" AS "+name_DataType.getKey());
		view.append(" FROM \"Shape\" s  WHERE s.\"SHP_LayerID\" = '"+layerID+"'::uuid WITH DATA");
		
		return view.toString();
		
		//return "CREATE MATERIALIZED VIEW \""+layerID+"\" AS SELECT s.\"SHP_Geography\"::geometry AS \"SHP_Geography\", s.\"SHP_ID\", (xpath('//extraData/function_result/text()'::text, s.\"SHP_ExtraData\"))[1]::text::"+valueDatatype+" AS value FROM \"Shape\" s  WHERE s.\"SHP_LayerID\" = '"+layerID+"'::uuid WITH DATA";
	}
	
	public String viewDeletion(String layerID){
		return "DROP MATERIALIZED VIEW IF EXISTS \""+layerID+"\"";
	}
	

	private String toPGSqlDataType(Class javaDataClass){
		if(javaDataClass == Float.class)
			return "decimal";
		if(javaDataClass == Double.class)
			return "numeric";
		if(javaDataClass == Integer.class)
			return "integer";
		if(javaDataClass == Long.class)
			return "bigint";
		if(javaDataClass == String.class)
			return "text";
		return "text";
	}
	
	
	
	private void addGeoserverLayer(GosDefinition gosDefinition, String layerID, String style) throws IOException{
		
		Bounds boundingBox = new Bounds();
		boundingBox.setMinx(0);
		boundingBox.setMiny(0);
		boundingBox.setMaxx(180);
		boundingBox.setMaxy(90);
		boundingBox.setCrs("EPSG:4326");

		FeatureType featureType = new FeatureType();
		featureType.setDatastore(gosDefinition.getDatastoreName());
		featureType.setWorkspace(gosDefinition.getGeoserverWorkspace());
		featureType.setEnabled(true);
		featureType.setName(layerID);
		featureType.setTitle(layerID);
		featureType.setSrs("EPSG:4326");
		featureType.setNativeCRS("EPSG:4326");
		featureType.setNativeBoundingBox(boundingBox);
		featureType.setLatLonBoundingBox(boundingBox);

		GeoserverLayer geoserverLayer = new GeoserverLayer();
		geoserverLayer.setDatastore(gosDefinition.getDatastoreName());
		geoserverLayer.setWorkspace(gosDefinition.getGeoserverWorkspace());
		geoserverLayer.setEnabled(true);
		geoserverLayer.setDefaultStyle(style);
		geoserverLayer.setId(layerID);
		geoserverLayer.setTitle(layerID);
		geoserverLayer.setType("VECTOR");
		
		Integer minScale = null;//layerConfig.getMinScale();
		Integer maxScale = null;//layerConfig.getMaxScale();
		
		Map<String, String> layerStyles = new HashMap<String, String>();//configurationManager.getLayerStyles();
		layerStyles.put("point", style);
		
		GeoserverManagement gm = new GeoserverManagement(authenticationStr);
		gm.addGeoserverLayer(gosDefinition.getGosEndpoint(), geoserverLayer, featureType, layerStyles, minScale, maxScale);
	}
	
	
	
}


class ExtradataField {
	
	private String fieldName;
	private Object value;
	
	public ExtradataField(String fieldName, Object value){
		this.fieldName = fieldName;
		this.value = value;
	}
	
	public String getFieldName(){
		return fieldName;
	}
	
	public Object getFieldValue(){
		return fieldName;
	}
	
	@Override
	public String toString(){
		if(value instanceof Double)
			return "<"+fieldName+" type="+"\"double\">"+value+"</"+fieldName+">";
		else if(value instanceof Long)
			return "<"+fieldName+" type="+"\"long\">"+value+"</"+fieldName+">";
		else if(value instanceof Integer)
			return "<"+fieldName+" type="+"\"int\">"+value+"</"+fieldName+">";
		else if(value instanceof Boolean)
			return "<"+fieldName+" type="+"\"boolean\">"+value+"</"+fieldName+">";
		else
			return "<"+fieldName+" type="+"\"string\">"+value+"</"+fieldName+">";
	}
	
	
}



class Attribute {
	private String name = null;
	private Object value = 0;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
	
	public Attribute() {
	
	}
	
	public Attribute(String name, Object value) {
		this.name = name;
		this.value = value;
	}
}

class Helper {

	public static void main(String[] args) {

		List<ExtradataField> l = new ArrayList<ExtradataField>();
		l.add(new ExtradataField("aaa", new Double(5.332)));
		l.add(new ExtradataField("bbb", new Integer(8)));
		l.add(new ExtradataField("ccc", new Boolean(true)));

		String xml = formExtradataField(l.toArray(new ExtradataField[l.size()]));
		System.out.println(xml);

	}

	public static String formExtradataField(ExtradataField... fields) {
		StringBuilder sb = new StringBuilder();
		sb.append("<extraData>");
		for (ExtradataField field : fields)
			sb.append(field);
		sb.append("</extraData>");
		return sb.toString();
	}

}

