/**
 * 
 */
package org.gcube.informationsystem.types;

import org.gcube.informationsystem.model.embedded.Header;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
public class EntitySchemaDefinition {

	private static Logger logger = LoggerFactory.getLogger(EntitySchemaDefinition.class);

	@Test
	public void test() throws Exception {
		Class<?> clz = Header.class;
		String json = TypeBinder.serializeType(clz);
		logger.trace(json);
	}
	
}
