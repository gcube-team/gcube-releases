package org.gcube.spatial.data.sdi;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import javax.xml.bind.JAXBException;

import org.apache.commons.collections.CollectionUtils;
import org.gcube.spatial.data.sdi.model.faults.ErrorMessage;
import org.gcube.spatial.data.sdi.model.metadata.MetadataReport;
import org.gcube.spatial.data.sdi.model.metadata.TemplateApplicationRequest;
import org.gcube.spatial.data.sdi.model.metadata.TemplateCollection;
import org.gcube.spatial.data.sdi.model.metadata.TemplateDescriptor;
import org.gcube.spatial.data.sdi.model.metadata.TemplateInvocationBuilder;
import org.gcube.spatial.data.sdi.model.services.ACCESS_TYPE;
import org.gcube.spatial.data.sdi.model.services.GeoServerDefinition;
import org.gcube.spatial.data.sdi.model.services.WorkspaceDefinition;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

public class MarshallUnmarshallTest {

	
	
	static ObjectMapper mapper=null;
	
	static ArrayList<Object> modelInstance=new ArrayList<>();


	@BeforeClass
	public static void init() throws JAXBException{
		
		mapper=new ObjectMapper();
		AnnotationIntrospector introspector=new JaxbAnnotationIntrospector(mapper.getTypeFactory());
		mapper.setAnnotationIntrospector(introspector);
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		
		modelInstance.add(getTemplateInvocations());
		modelInstance.add(getDescriptors());
		modelInstance.add(getError());
		modelInstance.add(getReport());
		modelInstance.add(getGSDefinition());
		
		
	}
	
	public static boolean roundTrip(Object obj) throws JsonParseException, JsonMappingException, JsonProcessingException, IOException{
		Object roundTripResult=mapper.readValue(marshal(obj), obj.getClass());
		
		if(obj instanceof Collection) {
			return CollectionUtils.isEqualCollection((Collection)obj, (Collection)roundTripResult);
		}
		return obj.equals(roundTripResult);
		
	}
	
	@Test
	public void testHashAndEquals() throws IOException {
		for(Object obj:modelInstance) {
			assertTrue(obj.equals(copy(obj)));
			assertTrue(obj.hashCode()==copy(obj).hashCode());
		}		
	}
	
	
	@Test
	public void Marshall() throws IOException{
		for(Object obj:modelInstance)
			print(obj);
	}
	@Test
	public void unMarshall() throws JsonParseException, JsonMappingException, JsonProcessingException, IOException{
		for(Object obj:modelInstance)
			assertTrue(roundTrip(obj));		
	}
	
	@Test
	public void toStringTest() throws IOException{
		for(Object obj:modelInstance)
			System.out.println(obj);		
	}
	
	
	public static String marshal(Object toSerialize) throws JsonProcessingException {
		return mapper.writeValueAsString(toSerialize);
	}
	
	public static void print(Object obj) throws JsonProcessingException {
		System.out.println(marshal(obj));
	}
	
//	public static <T> T unmarshal(Class<T> resourceClass, String toRead) throws JsonParseException, JsonMappingException, IOException {
//		return mapper.readValue(toRead, resourceClass);
//	}
	
	
	
	private static TemplateApplicationRequest getTemplateInvocations(){
		return new TemplateApplicationRequest(new TemplateInvocationBuilder().threddsOnlineResources("localhost", "myDataset.nc", "my Catalog").get());
	}
	
	private static TemplateCollection getDescriptors(){
		HashSet<TemplateDescriptor> descriptors=new HashSet<>();
		descriptors.add(new TemplateDescriptor(TemplateInvocationBuilder.THREDDS_ONLINE.ID,"Thredds Online Resources","Online reousrce template for thredds resources","http://some.place.org/theTemplate", new ArrayList<>()));
		descriptors.add(new TemplateDescriptor(TemplateInvocationBuilder.THREDDS_ONLINE.ID,"Thredds Online Resources","Online reousrce template for thredds resources","http://some.place.org/theTemplate", new ArrayList<>()));
		return new TemplateCollection(descriptors);
	}
	
	private static MetadataReport getReport(){
		return new MetadataReport("theUUID", 12335l, Collections.singleton(TemplateInvocationBuilder.THREDDS_ONLINE.ID));
	}
	
	private static GeoServerDefinition getGSDefinition() {
		GeoServerDefinition toReturn=new GeoServerDefinition();
		toReturn.setAdminPassword("*****");
		toReturn.setDescription("Dummy geoserver");
		toReturn.setHostname("some.place.org");
		toReturn.setMajorVersion((short)2);
		toReturn.setMinorVersion((short)10);
		toReturn.setReleaseVersion((short)3);
		toReturn.setName("My GeoServer");
		toReturn.addWorkspace(new WorkspaceDefinition("myWS", ACCESS_TYPE.PUBLIC));
		toReturn.addProperty("my own application property", "some value");
		return toReturn;
		
	}
	private static ErrorMessage getError(){
		ErrorMessage error=new ErrorMessage();
		error.setCode(500);
		error.setDeveloperMessage("Develop it better!");
		error.setMessage("You didn't see anything");
		error.setLink("www.sto.ca.z.z.o.org");		
		return error;
	}
	
	
	private <T> T copy(T obj) throws IOException {		
		  return (T) mapper.readValue(marshal(obj), obj.getClass());		
	}
	
}
