/**
 *
 */


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.gcube.data.analysis.dminvocation.DataMinerInvocationManager;
import org.gcube.data.analysis.dminvocation.model.DataMinerInvocation;
import org.gcube.data.analysis.dminvocation.model.DataMinerParam;
import org.gcube.data.analysis.dminvocation.model.DataMinerParamList;
import org.gcube.data.analysis.dminvocation.model.DataMinerParameters;
import org.xml.sax.SAXException;


/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Dec 4, 2018
 */
public class DataMinerInvocationTest {

	static String operatorID = "[THE_OPERATOR_ID]";

	static Map<String,String> parameters = new HashMap<String,String>();

	static DataMinerInvocationManager dmMng;


	//@Before
	public void init() throws JAXBException, IOException, SAXException{
		parameters.put("fileId", "http://publicLinkToFile");
		parameters.put("[key2]", "[value2]");
		dmMng = DataMinerInvocationManager.getInstance();
	}


	//@Test
	public void marshallingTest() throws JAXBException, IOException, SAXException {
		System.out.println("marshallingTest called");
		//LOADING PARAMETERS
		List<DataMinerParam> inParams = new ArrayList<DataMinerParam>();
		for (String pm : parameters.keySet()) {
			inParams.add(new DataMinerParam(pm, parameters.get(pm)));
		}
//		DataMinerInputParams inputParams = new DataMinerInputParams(inParams);
//		DataMinerOutputParams outputParams = new DataMinerOutputParams(null);

//		Map<String, String>  inputList = new HashMap<String, String>();
//		inputList.putAll(parameters);
		//new DataMinerParamList(parameters)
		DataMinerParameters parameters = new DataMinerParameters(new DataMinerParamList(inParams), new DataMinerParamList(inParams));

		DataMinerInvocation dmInvocation = new DataMinerInvocation();
		dmInvocation.setOperatorId(operatorID);
		dmInvocation.setParameters(parameters);
		System.out.println(dmInvocation);

//		ByteArrayOutputStream outStreamJSON = DataMinerInvocationManager.marshaling(dmInvocation, MediaType.ApplicationJSON);
//		System.out.println(new String(outStreamJSON.toByteArray()));

		String marshXML = dmMng.marshalingXML(dmInvocation, true, true);
		System.out.println(marshXML);


		String marshJSON = dmMng.marshalingJSON(dmInvocation, true, true);
		System.out.println(marshJSON);
	}

	//@Test
	public void marshallingTest2() throws JAXBException, IOException, SAXException {
		System.out.println("marshallingTest called");
		//LOADING PARAMETERS
		List<DataMinerParam> inParams = new ArrayList<DataMinerParam>();
		for (String pm : parameters.keySet()) {
			inParams.add(new DataMinerParam(pm, parameters.get(pm)));
		}
//		DataMinerInputParams inputParams = new DataMinerInputParams(inParams);
//		DataMinerOutputParams outputParams = new DataMinerOutputParams(null);

//		Map<String, String>  inputList = new HashMap<String, String>();
//		inputList.putAll(parameters);
		//new DataMinerParamList(parameters)
		DataMinerParameters parameters = new DataMinerParameters(new DataMinerParamList(inParams), null);

		DataMinerInvocation dmInvocation = new DataMinerInvocation();
//		dmInvocation.setOperatorId(operatorID);
//		dmInvocation.setParameters(parameters);
//		System.out.println(dmInvocation);

//		ByteArrayOutputStream outStreamJSON = DataMinerInvocationManager.marshaling(dmInvocation, MediaType.ApplicationJSON);
//		System.out.println(new String(outStreamJSON.toByteArray()));

//		String marshXML = dmMng.marshalingXML(dmInvocation, true);
//		System.out.println(marshXML);


		String marshJSON = dmMng.marshalingJSON(dmInvocation, true, true);
		System.out.println(marshJSON);
	}

	//@Test
	public void unmarshallingXMLTest() throws JAXBException, IOException, SAXException{
		System.out.println("unmarshallingXMLTest called");
		FileInputStream dmInvocationXMLFile = new FileInputStream(new File("./src/test/resources/DataMinerInvocation.xml"));

		DataMinerInvocation dmInvocation = dmMng.unmarshalingXML(dmInvocationXMLFile, true);
		System.out.println(dmInvocation);

		String marshXML = dmMng.marshalingXML(dmInvocation, true, true);
		System.out.println("TO XML: \n"+marshXML);
//
		String marshJSON = dmMng.marshalingJSON(dmInvocation, true, true);
		System.out.println("TO JSON: \n"+marshJSON);

	}


	//@Test
	public void unmarshallingJSONTest() throws JAXBException, IOException, SAXException{
		System.out.println("unmarshallingJSONTest called");
		FileInputStream dmInvocationJSONFile = new FileInputStream(new File("./src/test/resources/DataMinerInvocation.json"));


		DataMinerInvocation dmInvocation = dmMng.unmarshalingJSON(dmInvocationJSONFile, true);
		System.out.println(dmInvocation);

		String marshXML = dmMng.marshalingXML(dmInvocation, true, true);
		System.out.println("TO XML: \n"+marshXML);
//
		String marshJSON = dmMng.marshalingJSON(dmInvocation, true, true);
		System.out.println("TO JSON: \n"+marshJSON);

	}
}
