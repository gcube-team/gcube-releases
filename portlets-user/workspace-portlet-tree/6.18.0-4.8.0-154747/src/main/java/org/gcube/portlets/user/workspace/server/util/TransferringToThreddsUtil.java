/**
 *
 */
package org.gcube.portlets.user.workspace.server.util;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.log4j.Logger;
import org.gcube.portlets.user.workspace.shared.TransferToThreddsProperty;


/**
 * The Class TransferringToThreddsUtil.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Sep 29, 2017
 */
public class TransferringToThreddsUtil {

	public static Logger logger = Logger.getLogger(TransferringToThreddsUtil.class);


	/**
	 * To xml.
	 *
	 * @param property the property
	 * @return the string
	 */
	public static String toXML(TransferToThreddsProperty property) {
		logger.debug("Marshalling: "+property);
		System.out.println("Marshalling: "+property);
		try{
			// Create a JaxBContext
			JAXBContext jc = JAXBContext.newInstance(TransferToThreddsProperty.class);
			// Create the Marshaller Object using the JaxB Context
			Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			marshaller.marshal(property, baos);
			String marshallingJsonString = baos.toString("UTF-8");
			//System.out.println("XML: "+marshallingJsonString);
			return marshallingJsonString;
		}catch(JAXBException | UnsupportedEncodingException e){
			logger.warn("Error during marshalling: "+property, e);
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * Unmarshalling To OBJECT.
	 *
	 * @param xml the xml
	 * @return the transfering to thredds property
	 */
	public static TransferToThreddsProperty toObject(String xml){
		// Create a JaxBContext
		JAXBContext jc;
		try {

			jc = JAXBContext.newInstance(TransferToThreddsProperty.class);

			// Create the Unmarshaller Object using the JaxB Context
			Unmarshaller unmarshaller = jc.createUnmarshaller();

			// Create the StreamSource by creating StringReader using the JSON input
			StreamSource stream = new StreamSource(new StringReader(xml));

			// Getting the TransferingToThreddsProperty pojo again from the json
			return unmarshaller.unmarshal(stream, TransferToThreddsProperty.class).getValue();

		}
		catch (JAXBException e) {
			logger.warn("Error during unmarshalling: "+xml, e);
			return null;
		}

	}
}
