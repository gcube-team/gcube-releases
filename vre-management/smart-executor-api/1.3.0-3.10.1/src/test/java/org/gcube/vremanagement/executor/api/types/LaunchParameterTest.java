/**
 * 
 */
package org.gcube.vremanagement.executor.api.types;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.JAXBSource;
import javax.xml.namespace.QName;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public class LaunchParameterTest {

	private static Logger logger = LoggerFactory.getLogger(LaunchParameterTest.class);
	
	protected Marshaller createMarshaller(JAXBContext context) throws JAXBException{
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.FALSE);
		return marshaller;
	}
	
	@Test
	public void testSerialization() throws JAXBException {
		Map<String, Object> inputs = new HashMap<String, Object>();
		inputs.put("Hello", "World");
		long sleepTime = 10000;
		inputs.put("sleepTime", sleepTime);
		Scheduling scheduling = new Scheduling(20);
		
		LaunchParameter launchParameter = new LaunchParameter("HelloWorld", inputs, scheduling, true);
		logger.debug("{} to be serialized : {}", launchParameter.getClass().getSimpleName(), launchParameter);
		
		
		JAXBContext context = JAXBContext.newInstance(LaunchParameter.class);
        JAXBElement<LaunchParameter> jaxbElement = new JAXBElement<LaunchParameter>(new QName("org.gcube"), LaunchParameter.class, launchParameter);
        JAXBSource source = new JAXBSource(context, jaxbElement);
        
        Marshaller marshaller = createMarshaller(context);
		StringWriter stringWriter = new StringWriter();
		marshaller.marshal(jaxbElement, stringWriter);
		logger.debug("Marshalled {} : {}", launchParameter.getClass().getSimpleName(), stringWriter);
		
		
        
        JAXBContext contextOut = JAXBContext.newInstance(LaunchParameter.class);
        Unmarshaller unmarshaller = contextOut.createUnmarshaller();
        JAXBElement<LaunchParameter> jaxbElementOut = unmarshaller.unmarshal(source, LaunchParameter.class);

        
        LaunchParameter launchParameterUnmarshalled = jaxbElementOut.getValue();
        logger.debug("Deserialized {} : {}", launchParameter.getClass().getSimpleName(), launchParameterUnmarshalled);
        
	}
	
	/*
	private int check(Boolean a, Boolean b){
		if(a==null){
			if(b==null){
				return 0;
			}
			return -1;
		}
		
		if(b==null){
			return 1;
		}
		
		return a.compareTo(b);
	}
	
	@Test
	public void test(){
		List<Boolean> values = new ArrayList<Boolean>();
		values.add(null);
		values.add(true);
		values.add(false);
		
		for(Boolean valueA : values){
			Boolean a = valueA;
			for(Boolean valueB : values){
				Boolean b = valueB;
				logger.debug("a : {}, b : {}, a.compareTo(b) : {}", a, b, check(a, b));
				logger.debug("a : {}, b : {}, b.compareTo(a) : {}", b, a, check(b, a));
				
				
				logger.debug("---");
				
			}
		}
		
		
	}
	*/
}
