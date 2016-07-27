package org.gcube.datatransformation.datatransformationlibrary.imanagers;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.axis.Message;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.message.SOAPEnvelope;
import org.gcube.datatransformation.datatransformationlibrary.imanagers.IManager;
import org.gcube.datatransformation.datatransformationlibrary.model.TransformationUnit;
import org.gcube.datatransformation.datatransformationlibrary.model.TransformationProgram;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * @author Dimitris Katris, NKUA
 * 
 * Implementation of <tt>IManager</tt> which uses as registry a remote DTS running instance.
 */
public class RemoteIManager implements IManager{

	private String remoteIManagerEndpoint;
	private Call call;
	
	/**
	 * Initializes the <tt>RemoteIManager</tt>.
	 * 
	 * @param remoteIManagerEndpoint The endpoint reference of the remote DTS RI.
	 * @throws Exception If the <tt>RemoteIManager</tt> could not be initialized.
	 */
	public RemoteIManager(String remoteIManagerEndpoint) throws Exception {
		this.remoteIManagerEndpoint=remoteIManagerEndpoint;
		Service service = new Service();
		call = (Call) service.createCall();
		call.setTargetEndpointAddress( remoteIManagerEndpoint );
	}
	
	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.imanagers.IManager#getAvailableTransformationProgramIDs()
	 * @return The available <tt>Transformation Program IDs</tt>.
	 * @throws Exception If the available <tt>Transformation Program IDs</tt> could not be fetched from the registry.
	 */
	public String[] getAvailableTransformationProgramIDs() throws Exception {
		//Will not be used currently...
		return null;
	}

	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.imanagers.IManager#getTransformationProgram(java.lang.String)
	 * @param transformationProgramID The id of the <tt>Transformation Program</tt>.
	 * @return The instance of the <tt>Transformation Program</tt>.
	 * @throws Exception If the IManager did not manage to fetch the <tt>Transformation Program</tt> from the registry.
	 */
	public TransformationProgram getTransformationProgram(String transformationProgramID) throws Exception {
		System.out.println("Getting transformationUnit program "+transformationProgramID+" from "+remoteIManagerEndpoint);
		try {
			Message trsmsg = new Message("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
					"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
					"<soapenv:Body>" +
					"<queryTransformationPrograms xmlns=\"http://gcube-system.org/namespaces/datatransformation/DataTransformationService\">GET DESCRIPTION WHERE TRANSFORMATIONPROGRAMID="+transformationProgramID+"</queryTransformationPrograms>" +
					"</soapenv:Body>" +
					"</soapenv:Envelope>");
			
			SOAPEnvelope response = call.invoke(trsmsg);
			String transformationXML = response.getAsDOM().getElementsByTagName("queryTransformationProgramsResponse").item(0).getTextContent();
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			builder = factory.newDocumentBuilder();
			StringReader reader = new StringReader( transformationXML );
			InputSource inputSource = new InputSource( reader );
			Document transformationDoc = builder.parse( inputSource );
			TransformationProgram tp = new TransformationProgram();
			tp.fromDOM(transformationDoc.getDocumentElement());
			
			return tp;
		} catch (Exception e) {
			System.err.println(e.toString());
			throw new Exception("Did not manage to find any transformationUnit", e);
		}
	}

	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.imanagers.IManager#getTransformationUnit(java.lang.String, java.lang.String)
	 * @param transformationProgramID The id of the <tt>Transformation Program</tt> in which the <tt>Transformation Unit</tt> belongs to.
	 * @param transformationUnitID The id of the <tt>Transformation Unit</tt>.
	 * @return The instance of the <tt>Transformation Unit</tt>.
	 * @throws Exception If the IManager did not manage to fetch the <tt>Transformation Unit</tt> from the registry.
	 */
	public TransformationUnit getTransformationUnit(String transformationProgramID, String transformationUnitID) throws Exception {
		TransformationProgram transformationProgram = getTransformationProgram(transformationProgramID);
		if(transformationProgram.getTransformationUnits()==null || transformationProgram.getTransformationUnits().size()==0){
			System.err.println("Transformation program with id "+transformationProgramID+" does not contain any transformations");
			throw new Exception("Transformation program with id "+transformationProgramID+" does not contain any transformations");
		}
		for(TransformationUnit transformationUnit: transformationProgram.getTransformationUnits()){
			if(transformationUnit.getId().equals(transformationUnitID)){
				return transformationUnit;
			}
		}
		System.err.println("Did not manage to find transformationUnit with id "+transformationUnitID+" in transformationUnit program with id "+transformationProgramID);
		throw new Exception("Did not manage to find transformationUnit with id "+transformationUnitID+" in transformationUnit program with id "+transformationProgramID);
	}
	
	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.imanagers.IManager#publishTransformationProgram(org.gcube.datatransformation.datatransformationlibrary.model.TransformationProgram)
	 * @param transformationProgram The <tt>Transformation Program</tt> instance which will be published.
	 * @throws Exception If the <tt>Transformation Program</tt> could not be published.
	 */
	public void publishTransformationProgram(TransformationProgram transformationProgram) throws Exception {
		//Will not be used currently...
	}

	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.imanagers.IManager#queryTransformationPrograms(java.lang.String)
	 * @param query The query.
	 * @return The result of the query in <tt>xml</tt> format.
	 * @throws Exception If the query could not be performed.
	 */
	public String queryTransformationPrograms(String query) throws Exception {
		//Will not be used currently...
		return null;
	}

}
