package org.gcube.informationsystem.resourceregistry.api.exceptions;

import java.lang.reflect.Constructor;
import java.util.List;

import org.gcube.informationsystem.impl.utils.discovery.ReflectionUtility;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionSerialization {

	private static Logger logger = LoggerFactory.getLogger(ExceptionSerialization.class);
	
	public static final String MESSAGE = "Error Message to test ";
	
	@Test
	public void testAll() throws Exception{
		Package p =  ResourceRegistryException.class.getPackage();
		try {
			List<Class<?>> classes = ReflectionUtility.getClassesForPackage(p);
			for (Class<?> clz : classes) {
				
				if (ResourceRegistryException.class.isAssignableFrom(clz)) {
					logger.debug("Testing {}", clz);
					
					Constructor<?> constructor = clz.getConstructor(String.class);
					ResourceRegistryException rre = (ResourceRegistryException) constructor.newInstance(MESSAGE + clz.getSimpleName());
					
					String jsonString = ExceptionMapper.marshal(rre);
					
					ResourceRegistryException rreUnmashalled = ExceptionMapper.unmarshal(ResourceRegistryException.class, jsonString);
					
					Assert.assertTrue(rre.getClass() == rreUnmashalled.getClass());
					Assert.assertTrue(rre.getMessage().compareTo(rreUnmashalled.getMessage())==0);
					
					logger.debug("{} successfully tested", clz);
				}

			}
		} catch (ClassNotFoundException e) {
			logger.error("Error discovering classes inside package {}",
					p.getName(), e);
			throw new RuntimeException(e);
		}
	}
	
}
