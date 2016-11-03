package org.gcube.informationsystem.impl.facet;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.gcube.informationsystem.impl.entity.facet.CPUFacetImpl;
import org.gcube.informationsystem.impl.entity.facet.ContactFacetImpl;
import org.gcube.informationsystem.impl.entity.resource.HostingNodeImpl;
import org.gcube.informationsystem.impl.relation.ConsistsOfImpl;
import org.gcube.informationsystem.impl.utils.Entities;
import org.gcube.informationsystem.model.entity.Context;
import org.gcube.informationsystem.model.entity.Facet;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.entity.facet.AccessPointInterfaceFacet;
import org.gcube.informationsystem.model.entity.facet.CPUFacet;
import org.gcube.informationsystem.model.entity.facet.ContactFacet;
import org.gcube.informationsystem.model.entity.resource.HostingNode;
import org.gcube.informationsystem.model.relation.ConsistsOf;
import org.gcube.informationsystem.types.TypeBinder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class Serializer {
	
	private static Logger logger = LoggerFactory.getLogger(Serializer.class);
	
	@Test
	public void serializeAccessPoint() throws Exception{
		logger.trace(TypeBinder.serializeType(AccessPointInterfaceFacet.class));
	}
	
	@Test
	public void serializeFacet() throws JsonGenerationException, JsonMappingException, IOException{
		CPUFacetImpl cpuFacetImpl = new CPUFacetImpl();
		cpuFacetImpl.setClockSpeed("1 GHz");
		cpuFacetImpl.setModel("Opteron");
		cpuFacetImpl.setVendor("AMD");
		
		cpuFacetImpl.setAdditionalProperty("Test", "MyTest");
		cpuFacetImpl.setAdditionalProperty("Other", 1);
		cpuFacetImpl.setAdditionalProperty("MYLong", 3.56);
		
		StringWriter stringWriter = new StringWriter();
		Entities.marshal(cpuFacetImpl, stringWriter);
		logger.trace(stringWriter.toString());
		
		StringReader stringReader = new StringReader(stringWriter.toString());
		CPUFacet cpuFacet = Entities.unmarshal(CPUFacet.class, stringReader);
		logger.trace("Deserialized : {} ", cpuFacet);
	}
	
	
	@Test
	public void serializeDeserializeResource() throws JsonGenerationException, JsonMappingException, IOException{
		HostingNode hostingNode = new HostingNodeImpl();
		
		CPUFacet cpuFacet = new CPUFacetImpl();
		cpuFacet.setClockSpeed("1 GHz");
		cpuFacet.setModel("Opteron");
		cpuFacet.setVendor("AMD");
		
		ContactFacet contactFacet = new ContactFacetImpl();
		contactFacet.setName("Luca");
		contactFacet.setSurname("Frosini");
		contactFacet.setEMail("luca.frosini@isti.cnr.it");
		
		hostingNode.addFacet(cpuFacet);
		hostingNode.addFacet(contactFacet);
		
		StringWriter stringWriter = new StringWriter();
		Entities.marshal(hostingNode, stringWriter);
		logger.trace(stringWriter.toString());
		
		StringReader stringReader = new StringReader(stringWriter.toString());
		
		Entities.registerSubtypes(ContactFacet.class, CPUFacet.class);
		HostingNode hn = Entities.unmarshal(HostingNode.class, stringReader);
		
		logger.trace("Deserialized : {} ", hn);
		
	}
	
	@Test
	public void serializeRelation() throws JsonGenerationException, JsonMappingException, IOException{
		HostingNode hostingNode = new HostingNodeImpl();
		
		CPUFacet cpuFacet = new CPUFacetImpl();
		cpuFacet.setClockSpeed("1 GHz");
		cpuFacet.setModel("Opteron");
		cpuFacet.setVendor("AMD");
		
		ConsistsOf<Resource, Facet> consistsOf = new ConsistsOfImpl<Resource, Facet>(hostingNode, cpuFacet, null);
		StringWriter stringWriter = new StringWriter();
		Entities.marshal(consistsOf, stringWriter);
		logger.trace(stringWriter.toString());
	}
	
	@Test
	public void deserializeResource() throws JsonGenerationException, JsonMappingException, IOException{

		StringReader stringReader = new StringReader(
			"{"
				+ "\"@class\":\"HostingNode\","
				+ "\"header\":null,"
				+ "\"consistsOf\":[{"
						+ "\"@class\":\"ConsistsOf\","
						+ "\"header\":null,"
						+ "\"target\":{"
							+ "\"@class\":\"CPUFacet\","
							+ "\"header\":null,"
							+ "\"model\":\"Opteron\","
							+ "\"vendor\":\"AMD\","
							+ "\"clockSpeed\":\"1 GHz\""
						+ "},"
						+ "\"relationProperty\":null"
					+ "},"
					+ "{"
						+ "\"@class\":\"ConsistsOf\","
						+ "\"header\":null,"
						+ "\"target\":{"
							+ "\"@class\":\"ContactFacet\","
							+ "\"header\":null,"
							+ "\"title\":null,"
							+ "\"name\":\"Luca\","
							+ "\"middleName\":null,"
							+ "\"surname\":\"Frosini\","
							+ "\"eMail\":\"luca.frosini@isti.cnr.it\""
						+ "},"
						+ "\"relationProperty\":null"
						+ "}"
					+ "],"
				+ "\"isRelatedTo\":[]"
			+ "}");
		
		Entities.registerSubtypes(ContactFacet.class, CPUFacet.class);
		HostingNode hn = Entities.unmarshal(HostingNode.class, stringReader);
		
		logger.trace("Deserialized : {} ", hn);
		
	}
	
	@Test
	public void deserializeContext() throws JsonParseException, JsonMappingException, IOException {
		String contextString = "{" +
            "\"@type\": \"d\"," +
            "\"@rid\": \"#33:0\"," +
            "\"@version\": 3," +
            "\"@class\": \"Context\"," +
            "\"name\": \"gcube\"," +
            "\"header\": {" +
                "\"@type\": \"d\"," +
                "\"@version\": 0," +
                "\"@class\": \"Header\"," +
                "\"uuid\": \"50d9e8ab-71f5-43cc-afa0-61cdef161d04\"," +
                "\"creator\": \"UNKNOWN_USER\"," +
                "\"creationTime\": \"2016-09-07 11:45:17\"," +
                "\"lastUpdateTime\": \"2016-09-07 11:45:18\"," +
                "\"@fieldTypes\": \"creationTime=t,lastUpdateTime=t\"" +
            "}," +
            "\"_allow\": [" +
                "\"#5:0\"," +
                "\"#4:5\"" +
            "]," +
            "\"_allowRead\": [" +
                "\"#4:6\"" +
            "]," +
            "\"out_IsParentOf\": [" +
                "\"#260:0\"," +
                "\"#262:0\"" +
            "]," +
            "\"@fieldTypes\": \"_allow=n,_allowRead=n,out_IsParentOf=g\"" +
        "}";
		StringReader stringReader = new StringReader(contextString);
		Entities.registerSubtypes(Context.class);
		
		Context c = Entities.unmarshal(Context.class, stringReader);
		
		logger.trace("Deserialized Context : {} ", c);
	}
	
}
