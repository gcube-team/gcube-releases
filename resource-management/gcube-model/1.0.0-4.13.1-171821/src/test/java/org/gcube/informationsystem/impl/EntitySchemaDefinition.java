/**
 * 
 */
package org.gcube.informationsystem.impl;

import java.util.List;

import org.gcube.informationsystem.model.reference.annotations.ISProperty;
import org.gcube.informationsystem.model.reference.embedded.ValueSchema;
import org.gcube.informationsystem.model.reference.entity.Facet;
import org.gcube.informationsystem.types.TypeBinder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class EntitySchemaDefinition {

	private static Logger logger = LoggerFactory.getLogger(EntitySchemaDefinition.class);

	public interface AuxFacet extends Facet {

		public static final String NAME = AuxFacet.class.getSimpleName();

		@ISProperty
		public List<String> getProperties();

		public void setProperties(List<String> properties);

		@ISProperty(name = "vsProperties")
		public List<ValueSchema> getVSProperties();

		public void setVSProperties(List<ValueSchema> vsProperties);

	}

	@Test
	public void test() throws Exception {
		Class<? extends Facet> clz = AuxFacet.class;
		String json = TypeBinder.serializeType(clz);
		logger.trace(json);
	}

}
