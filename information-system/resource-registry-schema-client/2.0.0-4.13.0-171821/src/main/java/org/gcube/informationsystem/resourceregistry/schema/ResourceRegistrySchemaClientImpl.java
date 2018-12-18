package org.gcube.informationsystem.resourceregistry.schema;

import java.io.StringWriter;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.informationsystem.model.reference.AccessType;
import org.gcube.informationsystem.model.reference.ISManageable;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextAlreadyPresentException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.schema.SchemaException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.schema.SchemaNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.rest.TypePath;
import org.gcube.informationsystem.resourceregistry.api.rest.httputils.HTTPCall;
import org.gcube.informationsystem.resourceregistry.api.rest.httputils.HTTPCall.HTTPMETHOD;
import org.gcube.informationsystem.resourceregistry.api.utils.Utility;
import org.gcube.informationsystem.types.TypeBinder;
import org.gcube.informationsystem.types.TypeBinder.TypeDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class ResourceRegistrySchemaClientImpl implements ResourceRegistrySchemaClient {
	
	private static final Logger logger = LoggerFactory.getLogger(ResourceRegistrySchemaClientImpl.class);
	
	public static final String PATH_SEPARATOR = "/";
	
	protected final String address;
	protected HTTPCall httpCall;
	
	public ResourceRegistrySchemaClientImpl(String address) {
		this.address = address;
		
	}
	
	private HTTPCall getHTTPCall() throws MalformedURLException {
		if(httpCall == null) {
			httpCall = new HTTPCall(address, ResourceRegistrySchemaClient.class.getSimpleName());
		}
		return httpCall;
	}
	
	@Override
	public <ISM extends ISManageable> TypeDefinition create(Class<ISM> clz)
			throws SchemaException, ResourceRegistryException {
		try {
			String typeDefinition = TypeBinder.serializeType(clz);
			String type = AccessType.getAccessType(clz).getName();
			String res = create(type, typeDefinition);
			return TypeBinder.deserializeTypeDefinition(res);
		} catch(ResourceRegistryException e) {
			throw e;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String create(String baseType, String typeDefinitition) throws ContextAlreadyPresentException, ResourceRegistryException {
		try {
			logger.trace("Going to create: {}", typeDefinitition);
			TypeDefinition typeDefinitionObj = TypeBinder.deserializeTypeDefinition(typeDefinitition);
			
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(TypePath.TYPES_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(typeDefinitionObj.getName());
			
			HTTPCall httpCall = getHTTPCall();
			String c = httpCall.call(String.class, stringWriter.toString(), HTTPMETHOD.PUT, typeDefinitition);
			
			logger.trace("{} successfully created", c);
			return c;
			
		} catch(ResourceRegistryException e) {
			throw e;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	
	@Override
	public <ISM extends ISManageable> List<TypeDefinition> read(Class<ISM> clz, Boolean polymorphic)
			throws SchemaNotFoundException, ResourceRegistryException {
		try {
			String type = Utility.getType(clz);
			String res = read(type, polymorphic);
			return TypeBinder.deserializeTypeDefinitions(res);
		} catch(ResourceRegistryException e) {
			throw e;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String read(String type, Boolean polymorphic) throws ContextNotFoundException, ResourceRegistryException {
		try {
			logger.info("Going to get {} schema", type);
			StringWriter stringWriter = new StringWriter();
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(TypePath.TYPES_PATH_PART);
			stringWriter.append(PATH_SEPARATOR);
			stringWriter.append(type);
			
			Map<String,String> parameters = new HashMap<>();
			if(polymorphic != null) {
				parameters.put(TypePath.POLYMORPHIC_PARAM, polymorphic.toString());
			}
			
			HTTPCall httpCall = getHTTPCall();
			String json = httpCall.call(String.class, stringWriter.toString(), HTTPMETHOD.GET, parameters);
			
			logger.debug("Got schema for {} is {}", type, json);
			return json;
		} catch(ResourceRegistryException e) {
			throw e;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}
