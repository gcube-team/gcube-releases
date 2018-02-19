/**
 * 
 */
package org.gcube.informationsystem.types;

import org.gcube.informationsystem.model.ISManageable;
import org.gcube.informationsystem.model.embedded.Embedded;
import org.gcube.informationsystem.model.embedded.Header;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.relation.IsRelatedTo;
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
		Class<? extends Embedded> clz = Header.class;
		String json = TypeBinder.serializeType(clz);
		logger.trace(json);
	}
	
	@Test
	public void testRelationSerialization() throws Exception {
		Class<? extends ISManageable> clz = IsRelatedTo.class;
		String json = TypeBinder.serializeType(clz);
		logger.trace(json);
	}
	
	@Test
	public void testResourceSerialization() throws Exception {
		Class<? extends Resource> clz = Resource.class;
		String json = TypeBinder.serializeType(clz);
		logger.trace(json);
	}
}
