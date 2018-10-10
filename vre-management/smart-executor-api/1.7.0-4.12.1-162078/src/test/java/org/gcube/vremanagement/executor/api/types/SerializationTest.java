/**
 * 
 */
package org.gcube.vremanagement.executor.api.types;

import java.io.StringWriter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.JAXBSource;
import javax.xml.namespace.QName;

import org.gcube.vremanagement.executor.exception.InvalidPluginStateEvolutionException;
import org.gcube.vremanagement.executor.plugin.Plugin;
import org.gcube.vremanagement.executor.plugin.PluginDeclaration;
import org.gcube.vremanagement.executor.plugin.PluginState;
import org.gcube.vremanagement.executor.plugin.PluginStateEvolution;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
public class SerializationTest {

	private static Logger logger = LoggerFactory.getLogger(SerializationTest.class);
	
	protected Marshaller createMarshaller(JAXBContext context) throws JAXBException{
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.FALSE);
		return marshaller;
	}
	
	@Test
	public void testXMLSerializationDeserialization() throws JAXBException {
		Map<String, Object> inputs = new HashMap<String, Object>();
		inputs.put("Hello", "World");
		long sleepTime = 10000;
		inputs.put("sleepTime", sleepTime);
		
		Scheduling scheduling = new Scheduling(20);
		scheduling.setGlobal(true);
		
		LaunchParameter launchParameter = new LaunchParameter("HelloWorld", inputs, scheduling);
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
	
	@Test
	public void testPluginEvolutionState() throws InvalidPluginStateEvolutionException, JAXBException {
		PluginDeclaration pluginDeclaration = new PluginDeclaration(){

			@Override
			public void init() throws Exception {}

			@Override
			public String getName() {
				return PluginDeclaration.class.getSimpleName();
			}

			@Override
			public String getDescription() {
				return PluginDeclaration.class.getSimpleName() + " Description";
			}

			@Override
			public String getVersion() {
				return "1.0.0";
			}

			@Override
			public Map<String, String> getSupportedCapabilities() {
				return new HashMap<String, String>();
			}

			@Override
			public Class<? extends Plugin<? extends PluginDeclaration>> getPluginImplementation() {
				return null;
			}
			
			@Override
			public String toString(){
				return String.format("%s :{ %s - %s - %s }", 
						PluginDeclaration.class.getSimpleName(), 
						getName(), getVersion(), getDescription());
			}
			
		};
		
		
		PluginStateEvolution pes = new PluginStateEvolution(UUID.randomUUID(), 1, Calendar.getInstance().getTimeInMillis(), pluginDeclaration, PluginState.RUNNING, 10);
		logger.debug("{} to be Marshalled : {}", pes.getClass().getSimpleName(), pes);
		
		JAXBContext context = JAXBContext.newInstance(PluginStateEvolution.class);
        JAXBElement<PluginStateEvolution> jaxbElement = new JAXBElement<>(new QName("org.gcube"), PluginStateEvolution.class, pes);
        JAXBSource source = new JAXBSource(context, jaxbElement);
		
        Marshaller marshaller = createMarshaller(context);
		StringWriter stringWriter = new StringWriter();
		marshaller.marshal(jaxbElement, stringWriter);
		logger.debug("Marshalled {} : {}", pes.getClass().getSimpleName(), stringWriter);
		
		
		JAXBContext contextOut = JAXBContext.newInstance(PluginStateEvolution.class);
        Unmarshaller unmarshaller = contextOut.createUnmarshaller();
        JAXBElement<PluginStateEvolution> jaxbElementOut = unmarshaller.unmarshal(source, PluginStateEvolution.class);

		PluginStateEvolution pesUnmarshalled = jaxbElementOut.getValue();
        logger.debug("UnMarshalled : {}", pesUnmarshalled);
	}
	
}
