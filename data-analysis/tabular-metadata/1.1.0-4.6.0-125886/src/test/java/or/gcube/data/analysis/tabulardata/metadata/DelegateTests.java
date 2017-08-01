package or.gcube.data.analysis.tabulardata.metadata;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import or.gcube.data.analysis.tabulardata.metadata.table.Container;
import or.gcube.data.analysis.tabulardata.metadata.table.ContainerMetadata;
import or.gcube.data.analysis.tabulardata.metadata.table.MetaInt;
import or.gcube.data.analysis.tabulardata.metadata.table.MetaString;

import org.gcube.data.analysis.tabulardata.metadata.MetadataHolder;
import org.junit.Assert;
import org.junit.Test;

public class DelegateTests {
	
	MetadataHolder<ContainerMetadata> metaHolder = new Container();
	
	@Test
	public void testSerialization() throws JAXBException{
		metaHolder.setMetadata(new MetaInt(5));
		metaHolder.setMetadata(new MetaString("ciao"));
		roundTripTest(metaHolder);
	}
	
	private void roundTripTest(Object object) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(object.getClass());
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		StringWriter stringWriter = new StringWriter();
		marshaller.marshal(object, stringWriter);
		String result = stringWriter.toString();
		System.err.println(result);

		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		Object unmarshalledObj = (Object) unmarshaller.unmarshal(new StringReader(result));
		Assert.assertEquals(object, unmarshalledObj);
	}


}
