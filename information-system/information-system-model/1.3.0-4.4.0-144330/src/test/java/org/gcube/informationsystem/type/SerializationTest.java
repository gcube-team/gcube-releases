package org.gcube.informationsystem.type;

import org.gcube.informationsystem.impl.embedded.PropagationConstraintImpl;
import org.gcube.informationsystem.model.embedded.PropagationConstraint;
import org.gcube.informationsystem.model.embedded.PropagationConstraint.AddConstraint;
import org.gcube.informationsystem.model.embedded.PropagationConstraint.RemoveConstraint;
import org.gcube.informationsystem.types.TypeBinder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SerializationTest {

	private static Logger logger = LoggerFactory.getLogger(SerializationTest.class);
	
	@Test
	public void serialize() throws Exception{
		TypeBinder.serializeType(EntityTest.class);
	}
	
	@Test
	public void testGetEnumcostants(){
		Class<?> clz = PropagationConstraint.RemoveConstraint.class;
		
		Object[] constants = clz.getEnumConstants();
		for(Object constant : constants){
			logger.trace("{}", constant.toString());
		}
		
	}
	
	
	@Test
	public void testPropagationConstraint() throws Exception {
		PropagationConstraint propagationConstraint = new PropagationConstraintImpl();
		propagationConstraint.setAddConstraint(AddConstraint.propagate);
		propagationConstraint.setRemoveConstraint(RemoveConstraint.cascadeWhenOrphan);
		
		
		ObjectMapper mapper = new ObjectMapper();
		String pg = mapper.writeValueAsString(propagationConstraint);
		
		
		PropagationConstraint pgUnm  =mapper.readValue(pg, PropagationConstraint.class);
		
		logger.debug("{}", pgUnm);
	}
	
	
}
