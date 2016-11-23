package org.gcube.datatransformation.datatransformationlibrary.model.graph;

import java.util.ArrayList;

import org.apache.axis.Message;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.message.SOAPEnvelope;
import org.gcube.datatransformation.datatransformationlibrary.imanagers.IManager;
import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;
import org.gcube.datatransformation.datatransformationlibrary.model.TransformationUnit;
import org.gcube.datatransformation.datatransformationlibrary.model.graph.TransformationsGraph;
import org.w3c.dom.Element;

/**
 * @author Dimitris Katris, NKUA
 * <p>
 * Implementation of <tt>TransformationsGraph</tt> which finds the available transformationUnit units by invoking a remote graph service. 
 * </p>
 */
public class RemoteTransformationsGraph implements TransformationsGraph {

	private IManager iManager;
	private Call call;
	private String remoteGraphEndpoint;
	
	/**
	 * Instantiates the <tt>RemoteTransformationsGraph</tt>.
	 * 
	 * @param iManager The IManager from which the graph will use to find information about transformationUnit programs.
	 * @param remoteGraphEndpoint The endpoind reference of the remote graph.
	 * @throws Exception If the service call could not be created.
	 */
	public RemoteTransformationsGraph(IManager iManager, String remoteGraphEndpoint) throws Exception{
		this.iManager=iManager;
		this.remoteGraphEndpoint=remoteGraphEndpoint;
		Service  service = new Service();
		call = (Call) service.createCall();
		call.setTargetEndpointAddress( remoteGraphEndpoint );
	}

	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.model.graph.TransformationsGraph#findApplicableTransformationUnits(org.gcube.datatransformation.datatransformationlibrary.model.ContentType, org.gcube.datatransformation.datatransformationlibrary.model.ContentType, boolean)
	 * @param sourceContentType The <tt>ContentType</tt> of the source <tt>DataElement</tt>.
	 * @param targetContentType The <tt>ContentType</tt> of the target <tt>DataElement</tt>.
	 * @param createAndPublishCompositeTP If true then a new composite <tt>TransformationProgram</tt> is created and published if no available <tt>TransformationProgram</tt> exists.
	 * @return The available <tt>TransformationUnits</tt>.
	 */
	public ArrayList<TransformationUnit> findApplicableTransformationUnits(ContentType sourceContentType, ContentType targetContentType, boolean createAndPublishCompositeTP){
		System.out.println("Going to get non generic transformationUnit Units from "+remoteGraphEndpoint);
		try {
			Message trsmsg = new Message("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
				"xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
				"<soapenv:Body>" +
				"<findApplicableTransformationUnits xmlns=\"http://gcube-system.org/namespaces/datatransformation/DataTransformationService\">" +
				"<sourceContentType xmlns=\"\"><mimeType>"+sourceContentType.getMimeType()+"</mimeType></sourceContentType>" +
				"<targetContentType xmlns=\"\"><mimeType>"+targetContentType.getMimeType()+"</mimeType></targetContentType>" +
				"<createAndPublishCompositeTP xmlns=\"\">"+createAndPublishCompositeTP+"</createAndPublishCompositeTP>" +
				"</findApplicableTransformationUnits>" +
				"</soapenv:Body></soapenv:Envelope>");
		
			SOAPEnvelope response = call.invoke(trsmsg);
//			System.out.println(response.toString());
			Element tpandtr = (Element)response.getAsDOM().getElementsByTagName("TPAndTransformationUnitIDs").item(0);
			String transformationProgramID = tpandtr.getElementsByTagName("transformationProgramID").item(0).getTextContent();
			String transformationUnitID = tpandtr.getElementsByTagName("transformationUnitID").item(0).getTextContent();
			System.out.println("Transformation Unit: "+transformationProgramID+"/"+transformationUnitID);
			if(transformationProgramID==null || transformationProgramID.trim().length()==0 ||
					transformationUnitID==null || transformationUnitID.trim().length()==0){
				throw new Exception("Could not find any transformationUnit");
			}
			ArrayList<TransformationUnit> transformationUnits = new ArrayList<TransformationUnit>();
			transformationUnits.add(iManager.getTransformationUnit(transformationProgramID, transformationUnitID));
			return transformationUnits;
		} catch (Exception e) {
			System.out.println("Did not manage to find applicable transformationUnit unit from "+sourceContentType+" to "+targetContentType);
			e.printStackTrace();
//			throw new Exception("Did not manage to find any transformationUnit", e);
		}
		return null;
	}

	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.model.graph.TransformationsGraph#findAnyTransformationUnits(org.gcube.datatransformation.datatransformationlibrary.model.ContentType, org.gcube.datatransformation.datatransformationlibrary.model.ContentType, boolean)
	 * @param sourceContentType The <tt>ContentType</tt> of the source <tt>DataElement</tt>.
	 * @param targetContentType The <tt>ContentType</tt> of the target <tt>DataElement</tt>.
	 * @param createAndPublishCompositeTP If true then a new composite <tt>TransformationProgram</tt> is created and published if no available <tt>TransformationProgram</tt> exists.
	 * @return The available <tt>TransformationUnits</tt>.
	 */
	public ArrayList<TransformationUnit> findAnyTransformationUnits(ContentType sourceContentType, ContentType targetContentType, boolean createAndPublishCompositeTP){
		//Will not be used currently...
		return null;
	}
	
	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.model.graph.TransformationsGraph#findAvailableTargetContentTypes(org.gcube.datatransformation.datatransformationlibrary.model.ContentType)
	 * @param sourceContentType The <tt>ContentType</tt> of the source <tt>DataElement</tt>.
	 * @return The available target <tt>ContentTypes</tt> from the <tt>sourceContentType</tt>.
	 */
	public ArrayList<ContentType> findAvailableTargetContentTypes(ContentType sourceContentType){
		//Will not be used currently...
		return null;
	}
	
	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.model.graph.TransformationsGraph#destroy()
	 */
	public void destroy() {}

}
