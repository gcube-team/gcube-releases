/**
 * 
 */
package org.gcube.informationsystem.types;

import org.gcube.informationsystem.model.reference.ISManageable;
import org.gcube.informationsystem.model.reference.embedded.Embedded;
import org.gcube.informationsystem.model.reference.embedded.Header;
import org.gcube.informationsystem.model.reference.entity.Resource;
import org.gcube.informationsystem.model.reference.relation.IsRelatedTo;
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
