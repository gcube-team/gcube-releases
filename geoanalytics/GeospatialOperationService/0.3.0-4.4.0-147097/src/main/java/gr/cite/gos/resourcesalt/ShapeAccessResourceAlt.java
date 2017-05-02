//package gr.cite.gos.resourcesalt;
//
//import java.util.UUID;
//
//import javax.annotation.PostConstruct;
//import javax.inject.Inject;
//import javax.ws.rs.GET;
//import javax.ws.rs.Path;
//import javax.ws.rs.PathParam;
//import javax.ws.rs.Produces;
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.Response;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.sun.jersey.api.spring.Autowire;
//
//import gr.cite.gaap.datatransferobjects.ShapeMessenger;
//import gr.cite.geoanalytics.dataaccess.entities.layer.Layer;
//import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
//import gr.cite.geoanalytics.dataaccess.entities.shape.dao.ShapeDao;
//
//@Controller
//public class ShapeAccessResourceAlt {
//
//	
//	private ObjectMapper mapper;
//	
//	private ShapeDao shapeDao;
//
//	@Autowired
//	public void setShapeDao (ShapeDao shapeDao){
//		this.shapeDao = shapeDao;
//	}
//	
//	
//	@PostConstruct
//	private void preSetup() throws Exception{
//		System.out.println("Post-constructing ShapeAccessResource.class");
//		mapper = new ObjectMapper();
//	}
//	
//	
//	@RequestMapping(method = RequestMethod.GET, value = {"getShapeByID/{shapeID}"} )
//	public @ResponseBody Shape getShapeById(@PathVariable String shapeID) throws Exception {
//		System.out.println("Fetching shape with id: "+shapeID);
//		Shape shape = shapeDao.read(UUID.fromString(shapeID));
//		System.out.println("Got shape with id: "+shape.getId());
//		return shape;
//	}
//	
//	
//	
//	
//	
//}
