package gr.cite.gos.resources;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import gr.cite.gaap.datatransferobjects.ShapeMessenger;
import gr.cite.geoanalytics.context.DataLayerConfig;
import gr.cite.geoanalytics.context.DataStoreConfig;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape.Attribute;
import gr.cite.geoanalytics.dataaccess.entities.shape.dao.ShapeDao;
import gr.cite.gos.helpers.Transforming;

@Service
@Path("/ShapeManagement")
public class ShapeManagementResource {

	private static Logger logger = LoggerFactory.getLogger(ShapeManagementResource.class);
	
	private ObjectMapper mapper;
	private ShapeDao shapeDao;
	
	private DataStoreConfig dataStoreConfig;
	private DataLayerConfig dataLayerConfig;
	
	@Inject
	public ShapeManagementResource(ShapeDao shapeDao, DataStoreConfig dataStoreConfig, DataLayerConfig dataLayerConfig) {
		this.shapeDao = shapeDao;
		this.dataStoreConfig = dataStoreConfig;
		this.dataLayerConfig = dataLayerConfig;
		mapper = new ObjectMapper();
	}
	
	@GET
	@Path("getShapeByID/{shapeID}")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Transactional(readOnly = true)
	public Response getShapeById(@PathParam("shapeID") String shapeID) throws Exception {
		Shape shape = shapeDao.read(UUID.fromString(shapeID));
		ShapeMessenger sm = (shape!=null) ? new ShapeMessenger(shape) : null;
		String json = (sm !=null) ? mapper.writeValueAsString(sm) : "";
		return Response.ok(json).build();
	}
	
	@GET
	@Path("countShapesOfLayer/{layerID}")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Transactional(readOnly = true)
	public Response countShapesOfLayer(@PathParam("layerID") String layerID) throws Exception {
		Long count = shapeDao.countShapesOfLayer(UUID.fromString(layerID));
		return Response.ok(count).build();
	}
	
	
	
	@DELETE
	@Path("deleteShape/{shapeID}")
	@Transactional(rollbackFor={Exception.class})
	public Response deleteShape(@PathParam("shapeID") String shapeID) throws Exception{
//		shapeManager.deleteShape(shapeID);
		UUID shapeUUID =null;
		try{
			shapeUUID = UUID.fromString(shapeID);
		}
		catch(IllegalArgumentException ex){
			return Response.status(400).entity("That's not a valid 'shapeID'. Used: "+shapeID).build();
		}
		shapeDao.deleteByShapeID(shapeUUID);
		return Response.status(200).entity("Deleted shapeID: "+shapeID).build();
	}


	
	
	@POST
	@Path("deleteShapes")
	@Transactional(rollbackFor={Exception.class})
	public Response deleteShapes(@FormParam("shapeIDs") String shapeIDsJSON) throws Exception{
		TypeReference<List<String>> mappingType = new TypeReference<List<String>>() {};
		List<String> shapeIDs = mapper.readValue(shapeIDsJSON, mappingType);
		for(String shapeID : shapeIDs)
			shapeDao.deleteByShapeID(UUID.fromString(shapeID));
		return Response.status(200).entity("{\"msg\":\"Deleted shapeIDs: "+shapeIDs+"\"}").build();
	}
	
	
	
	@DELETE
	@Path("deleteShapesOfLayer/{layerID}")
	@Transactional(rollbackFor={Exception.class})
	public Response deleteShapesOfLayer(@PathParam("layerID") String layerID) throws Exception{
		shapeDao.deleteShapesOfLayer(UUID.fromString(layerID));
		return Response.status(200)
				.entity("Deleted layerID: "+layerID)
				.build();
	}
	
	
	
	@POST
	@Path("insertShapes")
	@Transactional(rollbackFor={Exception.class})
	public Response insertShapes(@FormParam("shapeMessengerListJSON") String shapeMessengerListJSON) throws Exception{
		try{
	   		TypeReference<List<ShapeMessenger>> mappingType = new TypeReference<List<ShapeMessenger>>() {};
	   		List<ShapeMessenger> shapeMessengers = mapper.readValue(shapeMessengerListJSON, mappingType);
	   		if(shapeMessengers!=null && !shapeMessengers.isEmpty())
	   			logger.info("Inserting "+shapeMessengers.size()+" shapes of layer "+shapeMessengers.get(0).getLayerId());
	   		List<Shape> shapes = shapeMessengers.stream().map(shapeMessenger -> {
				try {
					return shapeMessenger.toShape();
				} catch (Exception e) {
					return new Shape(); //we expect this to trigger a rollback on the db side...
				}
			}).collect(Collectors.toList());
	   		for(Shape shape : shapes)
	   			shapeDao.create(shape);
			return Response.status(201)
					.entity("Inserted the "+shapes.size()+" following shapes (IDs): "+ shapes.parallelStream().map(shape -> shape.getId().toString()).collect(Collectors.toList()))
					.build();
		}
		catch(Exception ex){
			ex.printStackTrace();
			return Response.status(400).entity("Could not insert the shape").build();
		}
	}
	
	@POST
	@Path("insertShape")
	@Transactional(rollbackFor={Exception.class})
	public Response insertShape(@FormParam("shapeMessengerJSON") String shapeMessengerJSON) throws Exception{
//		System.out.println(shapeMessengerJSON);
		try{
			ShapeMessenger sm = mapper.readValue(shapeMessengerJSON, ShapeMessenger.class);
			Shape shape = sm.toShape();
//			shapeManager.insertShape(shape);
			shapeDao.create(shape);
			return Response.status(201).entity("Created shape with ID: "+shape.getId()).build();
		}
		catch(Exception ex){
			return Response.status(400).entity("Could not insert the shape").build();
		}
	}
	
	@POST
	@Path("updateShape")
	@Transactional(rollbackFor={Exception.class})
	public Response updateShape(@FormParam("shapeMessengerJSON") String shapeMessengerJSON){
		try{
			ShapeMessenger sm = mapper.readValue(shapeMessengerJSON, ShapeMessenger.class);
			Shape shape = sm.toShape();
//			shapeManager.updateShape(shape);
			shapeDao.update(shape);
			return Response.status(201).entity("Updated shape with ID: "+shape.getId()).build();
		}
		catch(Exception ex){
			return Response.status(400).entity("Could not update the shape").build();
		}
	}
	
	
	@POST
	@Path("searchShapes")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Transactional(readOnly = true)
	public Response searchShapes(@FormParam("termsListJSON") String termsListJSON) {
		
		TypeReference<List<String>> mappingType = new TypeReference<List<String>>() {};
		try{
			List<String> terms = mapper.readValue(termsListJSON, mappingType);
			List<ShapeMessenger> smList = Transforming.fromShape(shapeDao.searchShapes(terms));
			return Response.ok().entity(mapper.writeValueAsString(smList)).build();
		}
		catch(IOException ex){
			return Response.status(Status.BAD_REQUEST).entity("Not a valid json. Could not parse it").build();
		}
	}
	
	
	@POST
	@Path("searchShapesWithinByAttributes")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Transactional(readOnly = true)
	public Response searchShapesWithinByAttributes(@FormParam("attrsJSON") String attrsJSON, @FormParam("shapeMessengerJSON") String shapeMessengerJSON) {
		TypeReference<Map<String, Attribute>> attrsType = new TypeReference<Map<String, Attribute>>() {};
		try{
			Map<String, Attribute> attrs = mapper.readValue(attrsJSON, attrsType);
			Shape shapeInput = mapper.readValue(shapeMessengerJSON, ShapeMessenger.class).toShape();
			List<ShapeMessenger> smList = Transforming.fromShape(shapeDao.searchShapesWithinByAttributes(attrs, shapeInput));
			return Response.ok().entity(mapper.writeValueAsString(smList)).build();
		}
		catch(Exception ex){
			return Response.status(Status.BAD_REQUEST).entity("Not valid json parameters. Could not parse them").build();
		}
	}
	
	
	@POST
	@Path("findContains")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Transactional(readOnly = true)
	public Response findContains(@FormParam("shapeMessengerJSON") String shapeMessengerJSON) {
		try{
			Shape shape = mapper.readValue(shapeMessengerJSON, ShapeMessenger.class).toShape();
			List<ShapeMessenger> smList = Transforming.fromShape(shapeDao.findContains(shape));
			return Response.ok().entity(mapper.writeValueAsString(smList)).build();
		}
		catch(Exception ex){
			return Response.status(Status.BAD_REQUEST).entity("Not valid json parameters. Could not parse them").build();
		}
	}
	
	
	@POST
	@Path("findWithin")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Transactional(readOnly = true)
	public Response findWithin(@FormParam("shapeMessengerJSON") String shapeMessengerJSON) {
		try{
			Shape shape = mapper.readValue(shapeMessengerJSON, ShapeMessenger.class).toShape();
			List<ShapeMessenger> smList = Transforming.fromShape(shapeDao.findWithin(shape));
			return Response.ok().entity(mapper.writeValueAsString(smList)).build();
		}
		catch(Exception ex){
			return Response.status(Status.BAD_REQUEST).entity("Not valid json parameters. Could not parse them").build();
		}
	}
	
	
	
	@GET
	@Path("shapesOfLayerID/{layerID}")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Transactional(readOnly = true)
	public Response shapesOfLayerID(@PathParam("layerID") String layerID) throws JsonProcessingException {
		UUID layerUUID =null;
		try{
			layerUUID = UUID.fromString(layerID);
		}
		catch(IllegalArgumentException ex){
			return Response.status(400).entity("That's not a valid 'layerID'. Used: "+layerID).build();
		}
		List<ShapeMessenger> smList = Transforming.fromShape(shapeDao.findShapesOfLayer(layerUUID));
		return Response.ok().entity(mapper.writeValueAsString(smList)).build();
		
		
		//ALTERNATIVE
		/*
		//THIS WAY (BAD WAY -- memory issues in case of huge layers)
//		List<Shape> shapes = shapeManager.getAllShapesForLayer(layerUUID);
		List<Shape> shapes = shapeDao.findShapesOfLayer(layerUUID);
		StreamingOutput stream = new StreamingOutput() {
			@Override
			public void write(OutputStream os) throws IOException, WebApplicationException {
				JsonFactory jfactory = new JsonFactory();
				mapper.enable(SerializationFeature.INDENT_OUTPUT);
				JsonGenerator jsonWriter = jfactory.createGenerator(os, JsonEncoding.UTF8);
				jsonWriter.writeStartObject();
				jsonWriter.writeFieldName("shapes");
				jsonWriter.writeStartArray();
				for(Shape shape : shapes)
					jsonWriter.writeRawValue(mapper.writeValueAsString(new ShapeMessenger(shape)));
				jsonWriter.writeEndArray();
				jsonWriter.writeEndObject();
				jsonWriter.flush();
			}
		};
		return Response.ok(stream).build();
		*/
////		//OR BETTER THIS (does not work yet, ResultSet is closed...)
////		ScrollableResults results = this.shapeDao.findShapesOfLayerScrollable(layerUUID);
//		ScrollableResults results = shapeManager.getAllShapesForLayerScrollable(layerUUID);
//		StreamingOutput stream = new StreamingOutput() {
//            @Override
//            public void write(OutputStream os) throws IOException, WebApplicationException {
//                Writer writer = new BufferedWriter(new OutputStreamWriter(os));
//                writer.write("{[");
//                if(results.next()){
//                	writer.write(mapper.writeValueAsString(new ShapeMessenger((Shape) results.get()[0])));
//                	while(results.next())
//                		writer.write("," + mapper.writeValueAsString(new ShapeMessenger((Shape) results.get()[0])));
//                }
//                writer.write("]}");
//                writer.flush();
//            }
//        };
//		return Response.ok(stream).build();
	}
	
	
	
	@POST
	@Path("getAttributeValuesOfShapesByLayer")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Transactional(readOnly = true)
	public Response getAttributeValuesOfShapesByLayer(@PathParam("layerID") String layerID, @FormParam("attributeJSON") String attributeJSON) throws IOException {
		Attribute attribute = mapper.readValue(attributeJSON, Attribute.class);
		return Response.status(200).entity(mapper.writeValueAsString(shapeDao.getAttributeValuesOfShapesByLayer(UUID.fromString(layerID), attribute))).build();
	}
	
	
	
	
	@GET
	@Path("existingLayerIDs")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Transactional(readOnly = true)
	public Response existingLayerIDs() throws JsonProcessingException {
		return Response.status(201).entity(mapper.writeValueAsString(shapeDao.getAllLayerIDs())).build();
	}
	
	
	
	@POST
	@Path("applyOnView")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Transactional(rollbackFor={Exception.class})
    public Response applyOnView(@FormParam("statement") String statement ) {
		Connection con = null;
        Statement st = null;
        try{
            con = DriverManager.getConnection(dataLayerConfig.getDbUrl(), dataLayerConfig.getDbUser(), dataLayerConfig.getDbPass());
            st = con.createStatement();
            st.executeUpdate(statement);
        }
        catch (Exception ex){
            logger.error(ex.getMessage(), ex);
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Could NOT create view on gos node "+dataStoreConfig.getHost()+":"+dataStoreConfig.getPort()).build();
        }
        finally{
        	try{
                if (st != null)  st.close();
                if (con != null) con.close();
            } 
        	catch (SQLException ex){
                logger.warn(ex.getMessage(), ex);
            }
        }
        return Response.status(Status.CREATED).entity("Created successfully view on gos node "+dataStoreConfig.getHost()+":"+dataStoreConfig.getPort()).build(); 
    }
	
	
	
	
	@GET
	@Path("ping")
	public Response ping() {
		return Response.status(200).entity("GOS SERIVICE IS UP").build();
	}
	
}
