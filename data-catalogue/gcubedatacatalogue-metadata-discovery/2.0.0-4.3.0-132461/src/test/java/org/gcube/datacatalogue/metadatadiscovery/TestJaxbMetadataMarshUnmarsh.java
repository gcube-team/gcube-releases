/**
 *
 */
package org.gcube.datacatalogue.metadatadiscovery;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.DataType;
import org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.MetadataField;
import org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.MetadataFormat;
import org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.MetadataValidator;
import org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.MetadataVocabulary;
import org.junit.Test;


/**
 * The Class TestJaxbMetadataMarshUnmarsh.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 7, 2016
 */
public class TestJaxbMetadataMarshUnmarsh {

	static MetadataField ckanMetadata;
	static MetadataFormat metadatas;
	static String tmpFileXML = "." + File.separator + "CkanMetadatas.xml";

	/**
	 * Test.
	 */
	@Test
	public void test() {

		metadatas = new MetadataFormat();

		ckanMetadata = new MetadataField();
		ckanMetadata.setFieldName("Name");
		ckanMetadata.setMandatory(true);
		ckanMetadata.setDefaultValue("default value");
		ckanMetadata.setDataType(DataType.Time);
		List<String> vocabulary = new ArrayList<String>();
		vocabulary.add("field1");
		vocabulary.add("field2");
		vocabulary.add("field3");
		MetadataVocabulary cvc = new MetadataVocabulary(vocabulary);
		ckanMetadata.setVocabulary(cvc);
		cvc.setIsMultiSelection(true);
		ckanMetadata.setNote("shown as suggestions in the insert/update metadata form of CKAN");
		MetadataValidator validator = new MetadataValidator("a regular expression for validating values");
		ckanMetadata.setValidator(validator);
		metadatas.addMetadata(ckanMetadata);

		ckanMetadata = new MetadataField();
		ckanMetadata.setFieldName("Accessibility");
		ckanMetadata.setMandatory(true);
		ckanMetadata.setDefaultValue("virtual/public");
		List<String> vocabulary2 = new ArrayList<String>();
		vocabulary2.add("virtual/public");
		vocabulary2.add("virtual/private");
		vocabulary2.add("transactional");
		MetadataVocabulary cvc2 = new MetadataVocabulary(vocabulary2);
		ckanMetadata.setVocabulary(cvc2);
		ckanMetadata.setNote("shown as suggestions in the insert metadata form of CKAN");
		MetadataValidator validator2 = new MetadataValidator("a regular expression for validating values");
		ckanMetadata.setValidator(validator2);

		metadatas.addMetadata(ckanMetadata);

		try {

//			marshalingExample();
			unMarshalingExample();
		}
		catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Marshaling example.
	 *
	 * @throws JAXBException the JAXB exception
	 */
	private static void marshalingExample() throws JAXBException
	{
	    JAXBContext jaxbContext = JAXBContext.newInstance(MetadataFormat.class);
	    Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

	    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

	    //Marshal the employees list in console
//	    jaxbMarshaller.marshal(employees, System.out);

	    //Marshal the employees list in file
	    jaxbMarshaller.marshal(metadatas, new File(tmpFileXML));
	}


	/**
	 * Un marshaling example.
	 *
	 * @throws JAXBException the JAXB exception
	 */
	private static void unMarshalingExample() throws JAXBException
	{
	    JAXBContext jaxbContext = JAXBContext.newInstance(MetadataFormat.class);
	    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

	    //We had written this file in marshalling example
	    MetadataFormat mtds = (MetadataFormat) jaxbUnmarshaller.unmarshal(new File(tmpFileXML));

	    for(MetadataField mtd : mtds.getMetadataFields())
	    {
	    	System.out.println("Unmarshall: "+mtd);
	    }
	}


	public static void main(String[] args) {

		System.out.println(DataType.valueOf("aa"));
	}
}
