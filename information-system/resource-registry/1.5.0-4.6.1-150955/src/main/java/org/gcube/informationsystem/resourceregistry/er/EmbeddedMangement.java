/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.er;

import java.util.HashSet;
import java.util.Set;

import org.gcube.informationsystem.model.AccessType;
import org.gcube.informationsystem.model.ISManageable;
import org.gcube.informationsystem.model.embedded.Header;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.schema.SchemaNotFoundException;
import org.gcube.informationsystem.resourceregistry.schema.SchemaManagementImpl;
import org.gcube.informationsystem.resourceregistry.utils.HeaderUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class EmbeddedMangement {

	private static Logger logger = LoggerFactory
			.getLogger(EmbeddedMangement.class);
	
	public static final Set<String> EMBEDDED_IGNORE_KEYS;
	public static final Set<String> EMBEDDED_IGNORE_START_WITH_KEYS;
	
	public static final String AT = "@";
	public static final String UNDERSCORE = "_";
	
	static {
		EMBEDDED_IGNORE_KEYS = new HashSet<String>();

		EMBEDDED_IGNORE_START_WITH_KEYS = new HashSet<String>();
		EMBEDDED_IGNORE_START_WITH_KEYS.add(AT);
		EMBEDDED_IGNORE_START_WITH_KEYS.add(UNDERSCORE);

	}
	
	
	public static ODocument getEmbeddedType(JsonNode jsonNode)
			throws ResourceRegistryException {
		if (jsonNode.has(ISManageable.CLASS_PROPERTY)) {
			// Complex type
			String type = ERManagement.getClassProperty(jsonNode);

			try {
				SchemaManagementImpl.getTypeSchema(type, AccessType.EMBEDDED);
			} catch (SchemaNotFoundException e) {
				throw e;
			}

			Header header = null;
			try {
				header = HeaderUtility.getHeader(jsonNode, false);
				
			} catch (Exception e) {
				logger.warn("An invalid Header has been provided. Anyway embedded object cannot have an Header.");
				throw new ResourceRegistryException("An embedded object cannot have an Header");
			}

			if (header != null) {
				throw new ResourceRegistryException("An embedded object cannot have an Header");
			}

			ODocument oDocument = new ODocument(type);
			return oDocument.fromJSON(jsonNode.toString());

		}
		
		ODocument oDocument = new ODocument();
		return oDocument.fromJSON(jsonNode.toString());
	}
}
