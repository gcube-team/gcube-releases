package it.eng.rdlab.soa3.um.rest.jaxrs;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;



 

public class jerseyClient {

	public static void main(String[] args) {
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		WebResource service = client.resource(getBaseURI());
		ObjectMapper mapper = new ObjectMapper();
		AnnotationIntrospector introspector = new JaxbAnnotationIntrospector();
		mapper.getDeserializationConfig().setAnnotationIntrospector(
				introspector);
		mapper.getSerializationConfig().setAnnotationIntrospector(introspector);

		//String value = "";


				/**
				 * Create tenant engineering
				 */
				service.path("userService/organizationmanager/test").put();
				
//				/**
//				 * Create user travaglino under tenant "engineering"
//				 */
//				UserBean user = new UserBean("travaglino", "Ermanno", "Travaglino", "engineering", "secret");
//				value = "";
//				try {
//					value = mapper.writeValueAsString(user);
//				} catch (JsonGenerationException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (JsonMappingException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				service.path("userService/usermanager").type(MediaType.APPLICATION_JSON).put(ClientResponse.class, value);
//
//				
//				
//				/**
//				 * Create role developers under tenant "engineering"
//				 */
//				
//			RoleJaxbBean roleDevelopers = new RoleJaxbBean("developers");
//				value = "";
//				try {
//					value = mapper.writeValueAsString(roleDevelopers);
//				} catch (JsonGenerationException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (JsonMappingException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				service.path("userService/rolemanager/engineering").type(MediaType.APPLICATION_JSON).put(ClientResponse.class, value);
//				
//				/**
//				 * Add role developers to user "travaglino" under tenant "engineering"
//				 */
//				
////				RoleJaxbBean roleDevelopers = new RoleJaxbBean("developers");
////				String value = "";
//				try {
//					value = mapper.writeValueAsString(roleDevelopers);
//				} catch (JsonGenerationException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (JsonMappingException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			service.path("userService/usermanager/roles/travaglino").type(MediaType.APPLICATION_JSON).put(ClientResponse.class, value);
//			service.path("userService/usermanager/roles/travaglino/engineering/").type(MediaType.APPLICATION_JSON).put(ClientResponse.class, value);
//
//
//
//
//		/**
//		 * Create role admins under tenant "engineering"
//		 */
//
//				RoleJaxbBean roleAdmins = new RoleJaxbBean("admins");
//				value = "";
//				try {
//					value = mapper.writeValueAsString(roleAdmins);
//				} catch (JsonGenerationException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (JsonMappingException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				service.path("userService/rolemanager/engineering").type(MediaType.APPLICATION_JSON).put(ClientResponse.class, value);
//
//		/**
//		 * Add role developers to user "travaglino" under tenant "engineering"
//		 */
//
//		//RoleJaxbBean roleDevelopers = new RoleJaxbBean("developers");
//		//String value = "";
//				try {
//					value = mapper.writeValueAsString(roleDevelopers);
//				} catch (JsonGenerationException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (JsonMappingException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				service.path("userService/rolemanager/travaglino/engineering").type(MediaType.APPLICATION_JSON).put(ClientResponse.class, value);
//				
//
//		/**
//		 * Create group itpeople under tenant "engineering"
//		 */
//		GroupJaxbBean itPeopleGroup = new GroupJaxbBean("itpeople","engineering");
//		try {
//			value = mapper.writeValueAsString(itPeopleGroup);
//		} catch (JsonGenerationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (JsonMappingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		service.path("userService/groupmanager").type(MediaType.APPLICATION_JSON).put(ClientResponse.class, value);
//		
//		/**
//		 * Add user travaglino to group "itpeople"
//		 */
//	//	GroupJaxbBean itPeopleGroup = new GroupJaxbBean("itpeople","engineering");
//		try {
//			value = mapper.writeValueAsString(itPeopleGroup);
//		} catch (JsonGenerationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (JsonMappingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		service.path("userService/usermanager/groups/travaglino").type(MediaType.APPLICATION_JSON).put(ClientResponse.class, value);
//		
//		/**
//		 * Add role "TENANT_ADMIN" to user travaglino
//		 */
//		RoleJaxbBean tenantAdminRole = new RoleJaxbBean("TENANT_ADMIN");
//		try {
//			value = mapper.writeValueAsString(tenantAdminRole);
//		} catch (JsonGenerationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (JsonMappingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		service.path("userService/rolemanager/travaglino/engineering").type(MediaType.APPLICATION_JSON).put(ClientResponse.class, value);
//		
//		/**
//		 * Get all users in group "itpeople" from tenant "engineering"
//		 */
//		System.out.println(service.path("userService/usermanager/groups/itpeople/engineering").type(MediaType.APPLICATION_JSON).get(String.class));
		
		
	}

	public static URI getBaseURI() {
		
		return UriBuilder.fromUri("http://localhost:8080/")
				.build();

	}

}
