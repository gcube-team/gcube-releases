//package org.gcube.datatransformation.datatransformationservice.clients;
//
////import java.io.File;
////import java.io.FileInputStream;
////import java.io.FileNotFoundException;
////import java.util.ArrayList;
////
////import javax.xml.parsers.DocumentBuilder;
////import javax.xml.parsers.DocumentBuilderFactory;
////
////import org.apache.axis.message.addressing.AttributedURI;
////import org.apache.axis.message.addressing.EndpointReferenceType;
////import org.gcube.common.core.scope.GCUBEScope;
////import org.gcube.common.core.scope.GCUBEScopeManager;
////import org.gcube.common.core.scope.GCUBEScopeManagerImpl;
////import org.gcube.contentmanagement.dts.stubs.DataTransformationServicePortType;
////import org.gcube.contentmanagement.dts.stubs.Input;
////import org.gcube.contentmanagement.dts.stubs.IOType;
////import org.gcube.contentmanagement.dts.stubs.Output;
////import org.gcube.contentmanagement.dts.stubs.Parameter;
////import org.gcube.contentmanagement.dts.stubs.Transform;
////import org.gcube.contentmanagement.dts.stubs.service.DataTransformationServiceAddressingLocator;
////import org.gcube.contentmanagement.dtslib.model.ContentFormat;
////import org.gcube.contentmanagement.dtslib.model.XMLDefinitions;
////import org.gcube.contentmanagement.dtslib.rstypes.ResultElementObject;
////import org.gcube.contentmanagement.dtslib.rstypes.ResultElementTransformedObject;
////import org.gcube.searchservice.searchlibrary.rsclient.elements.RSLocator;
////import org.gcube.searchservice.searchlibrary.rsclient.elements.RSResourceWSRFType;
////import org.gcube.searchservice.searchlibrary.rsreader.RSBLOBIterator;
////import org.gcube.searchservice.searchlibrary.rsreader.RSBLOBReader;
////import org.gcube.searchservice.searchlibrary.rswriter.RSBLOBWriter;
////import org.w3c.dom.Document;
////import org.w3c.dom.Element;
//
//public class DTSClient {
//	public static void main(String[] args) throws Exception{
////		GCUBEScopeManager manager = new GCUBEScopeManagerImpl();
////		manager.setScope(GCUBEScope.getScope(args[1]));
////		
////		System.out.println("Stateless Client is running...");
////		EndpointReferenceType endpoint = new EndpointReferenceType();
////		endpoint.setAddress(new AttributedURI(args[0]));
////		
////		DataTransformationServicePortType dtsPT = new DataTransformationServiceAddressingLocator().getDataTransformationServicePortTypePort(endpoint);
////		manager.prepareCall(dtsPT, "ContentManagement", "DataTransformationService");
////		
////		/* Creating the RS */
////		RSBLOBWriter writer = RSBLOBWriter.getRSBLOBWriter();
////			
////		fillRS(writer, args[2]);
////		writer.close();
////		RSLocator rslocator = createLocator(writer);
////		
////		/* Program */
////		Transform params = new Transform();
////		params.setProgram("2222");
////		
////		Parameter[] progparams = new Parameter[1];
////		progparams[0] = new Parameter();
////		progparams[0].setName("codec");
////		progparams[0].setValue("flv3");
////		params.setProgramparameters(progparams);
////		
////		/* Input */
////		Input input = new Input();
////		input.setInputType(IOType.rsofblobs);
////		input.setInputValue(rslocator.getLocator());
////		Parameter[] inparams = new Parameter[1];
////		inparams[0] = new Parameter();
////		inparams[0].setName("codec");
////		inparams[0].setValue("mpeg2");
////		input.setInputparameters(inparams);
////		params.setInput(input);
////		
////		/* Target Mime Type */
////		params.setTargetMimeType("text/html");
////		
////		/* Output */
////		Output output = new Output();
////		output.setOutputType(IOType.rsofblobs);
////		output.setOutputValue("unused");
////		Parameter[] outparams = new Parameter[1];
////		inparams[0] = new Parameter();
////		inparams[0].setName("videotype");
////		inparams[0].setValue("sample");
////		output.setOutputparameters(outparams);
////		params.setOutput(output);
////		
////		String result = dtsPT.transform(params);
////		System.out.println(result);
////		
////		persistRS(result);
//	}
//	
////	public static void persistRS(String resultRS){
////		RSBLOBIterator iterator;
////		try{
////			iterator = RSBLOBReader.getRSBLOBReader(new RSLocator(resultRS)).getRSIterator();
////		
////			while(iterator.hasNext()){
////				ResultElementTransformedObject blob = (ResultElementTransformedObject)iterator.next(ResultElementTransformedObject.class);
////				
////				if(blob!=null){
////					System.out.println("ID: "+blob.getOID());
////					System.out.println("FromID: "+blob.getFromOID());
////					System.out.println("MimeType: "+blob.getMimeType());
////					org.gcube.contentmanagement.dtslib.utils.FilesUtils.streamToFile(blob.getContentOfBLOB(), ("f"+blob.getFromOID()+"o"+blob.getOID()+"mt"+blob.getMimeType()).replaceAll("/", "_"));
////				}
////			}
////		} catch (Exception e){
////			e.printStackTrace();
////		}
////	}
////	
////	public static RSLocator createLocator(RSBLOBWriter writer ) throws Exception {
////		
////		RSLocator locator=null; 
////	    locator=writer.getRSLocator(new RSResourceWSRFType("http://localhost:9876/wsrf/services/gcube/searchservice/ResultSetService"));
////	    return locator;
////	}
////	
////	public static void fillRS(RSBLOBWriter writer, String conffilename) throws FileNotFoundException, Exception{
////		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
////		DocumentBuilder builder = factory.newDocumentBuilder();
////		Document doc = builder.parse(new File(conffilename));
////		int i=0;
////		Element ObjectElement = null;
////		while((ObjectElement = (Element)doc.getElementsByTagName("object").item(i++))!=null){
////			String id = ((Element)ObjectElement.getElementsByTagName("id").item(0)).getTextContent();
////			ContentFormat contentFormat = new ContentFormat();
////			Element contentFormatElement = (Element)ObjectElement.getElementsByTagName("contentformat").item(0);
////			String mimetype = ((Element)contentFormatElement.getElementsByTagName("mimetype").item(0)).getTextContent();
////			contentFormat.setMimeType(mimetype);
////			Element formatparameters;
////			if((formatparameters=(Element)contentFormatElement.getElementsByTagName(XMLDefinitions.ELEMENT_formatparameters).item(0))!=null){
////				ArrayList <org.gcube.contentmanagement.dtslib.model.Parameter> formatParametersList = new ArrayList<org.gcube.contentmanagement.dtslib.model.Parameter>();
////				Element parameter;int cnt=0;
////				while((parameter=(Element)formatparameters.getElementsByTagName(XMLDefinitions.ELEMENT_parameter).item(cnt++))!=null){
////					org.gcube.contentmanagement.dtslib.model.Parameter param = new org.gcube.contentmanagement.dtslib.model.Parameter();
////					param.setName(parameter.getAttribute(XMLDefinitions.ATTRIBUTE_parameterName));
////					param.setValue(parameter.getAttribute(XMLDefinitions.ATTRIBUTE_parameterValue));
////					String isOptional = parameter.getAttribute(XMLDefinitions.ATTRIBUTE_parameterIsOptional);
////					if(isOptional==null || isOptional.equalsIgnoreCase("true"))
////						param.setOptional(true);
////					formatParametersList.add(param);
////				}
////				contentFormat.setFormatParameters(formatParametersList.toArray(new org.gcube.contentmanagement.dtslib.model.Parameter[formatParametersList.size()]));
////			}
////
////			String pathname = ((Element)ObjectElement.getElementsByTagName("file").item(0)).getTextContent();
////			
////			FileInputStream istream = new FileInputStream(pathname);
////			if(istream==null){
////				System.out.println("InputStream is null...");
////				continue;
////			}
////			ResultElementObject blob = new ResultElementObject(id,contentFormat,istream);
////			writer.addResults(blob);
////			
////		}
////	}
//}
