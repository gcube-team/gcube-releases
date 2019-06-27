/**
 *
 */
package org.gcube.datacatalogue.metadatadiscovery;

import java.io.File;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.MetadataField;
import org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.MetadataFormat;
import org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.NamespaceCategories;
import org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.NamespaceCategory;
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
	static String tmpCategoriesXML = "." + File.separator + "NamespacesCatalogueCategories.xml";
	private ArrayList<NamespaceCategory> ckanCategories;
	public static int MAX_CATEGORIES = 3;

	/**
	 * Test.
	 */
	@Test
	public void test() {

		/*metadatas = new MetadataFormat();

		ckanCategories = new ArrayList<MetadataCategory>();

		for (int i=0; i<MAX_CATEGORIES; i++) {
			ckanCategories.add(new MetadataCategory("cat "+i, "title cat "+i, "description "+i));
		}

		metadatas.setMetadataCategories(ckanCategories);


		ckanMetadata = new MetadataField();
		ckanMetadata.setFieldName("Name");
		ckanMetadata.setMandatory(true);
		ckanMetadata.setDefaultValue("default value");
		ckanMetadata.setDataType(DataType.Time);

		ckanMetadata.setCategoryRef(ckanCategories.get(new Random().nextInt(MAX_CATEGORIES)).getId());
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

		ckanMetadata.setTagging(new MetadataTagging(true, ":", TaggingGroupingValue.onValue_onFieldName));

		MetadataGrouping grouping = new MetadataGrouping();
		grouping.setCreate(true);
		grouping.setGroupingValue(TaggingGroupingValue.onValue);
		ckanMetadata.setGrouping(grouping);
		List<String> vocabulary2 = new ArrayList<String>();
		vocabulary2.add("virtual/public");
		vocabulary2.add("virtual/private");
		vocabulary2.add("transactional");
		MetadataVocabulary cvc2 = new MetadataVocabulary(vocabulary2);
		ckanMetadata.setVocabulary(cvc2);
		ckanMetadata.setNote("shown as suggestions in the insert metadata form of CKAN");
		MetadataValidator validator2 = new MetadataValidator("a regular expression for validating values");
		ckanMetadata.setValidator(validator2);

		metadatas.addMetadata(ckanMetadata);*/

		try {

			//marshalingExample();
			unMarshalingMetadataFields();
			//unMarshalingCategories();
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
	private static void unMarshalingMetadataFields() throws JAXBException
	{
		//unMarshalingCategories();
	    JAXBContext jaxbContext = JAXBContext.newInstance(MetadataFormat.class);
	    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

	    //We had written this file in marshalling example
	    MetadataFormat mtds = (MetadataFormat) jaxbUnmarshaller.unmarshal(new File(tmpFileXML));

	    System.out.println("Metadata Format");
	    System.out.println(mtds);

//	    System.out.println("Metadata Categories");
//	    if(mtds.getMetadataCategories()!=null){
//		    for (MetadataCategory cat : mtds.getMetadataCategories()) {
//		    	System.out.println(cat);
//		    	System.out.println(cat.getCategoryQName());
//		    }
//	    }

	    System.out.println("\n\nMetadata Fields");
	    for (MetadataField field : mtds.getMetadataFields()) {
	    	System.out.println(field);
	    	System.out.println("Category Ref: "+field.getCategoryRef());
	    	System.out.println("Category CategoryField Q Name: "+field.getCategoryFieldQName());
	    	System.out.println("QName: "+field.getCategoryFieldQName());
	    	System.out.println("MaxOccurs: "+field.getMaxOccurs());

//	    	System.out.println(field.getGrouping());
//	    	System.out.println(field.getTagging());
		}
	}


	/**
	 * Un marshaling example.
	 *
	 * @throws JAXBException the JAXB exception
	 */
	private static void unMarshalingCategories() throws JAXBException
	{
	    JAXBContext jaxbContext = JAXBContext.newInstance(NamespaceCategories.class);
	    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

	    //We had written this file in marshalling example
	    NamespaceCategories mtds = (NamespaceCategories) jaxbUnmarshaller.unmarshal(new File(tmpCategoriesXML));

	    System.out.println("NamespaceCategories: ");
	    System.out.println(mtds);

//	    System.out.println("Metadata Categories");
//	    if(mtds.getMetadataCategories()!=null){
//		    for (MetadataCategory cat : mtds.getMetadataCategories()) {
//		    	System.out.println(cat);
//		    	System.out.println(cat.getCategoryQName());
//		    }
//	    }

	    System.out.println("\nCategories: ");
	    for (NamespaceCategory category : mtds.getNamespaceCategories()) {
	    	System.out.println(category);

//	    	System.out.println(field.getGrouping());
//	    	System.out.println(field.getTagging());
		}
	}


	public static void main(String[] args) {

		//System.out.println(DataType.valueOf("aa"));
		try {
			unMarshalingMetadataFields();
		}
		catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
