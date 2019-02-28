/**
 *
 */

package org.gcube.datatransfer.resolver.catalogue.resource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.common.resources.gcore.Resource;
import org.gcube.common.resources.gcore.Resources;
import org.gcube.common.resources.gcore.utils.XPathHelper;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datatransfer.resolver.applicationprofile.ApplicationProfileNotFoundException;
import org.gcube.datatransfer.resolver.util.ScopeUtil;
import org.gcube.informationsystem.publisher.RegistryPublisherFactory;
import org.gcube.informationsystem.publisher.ScopedPublisher;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * The Class UpdateApplicationProfileCatalogueResolver.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it Feb 28, 2017
 */
public class UpdateApplicationProfileCatalogueResolver {


	private static Logger logger = Logger.getLogger(UpdateApplicationProfileCatalogueResolver.class);
	private static boolean useRootScope = false;

	/**
	 * Validate end point. If the EndPoint VRE-FULLNAME does not exist it will be added to Application Profile: {@link ApplicationProfileReaderForCatalogueResolver#RESOURCE_NAME}
	 *
	 * @param scopeToInstanceResolver the scope to instance resolver
	 * @param VRE the vre
	 * @param fullScope the full scope
	 * @return true, if Application Profile has been updated. No otherwise
	 * @throws ApplicationProfileNotFoundException the application profile not found exception
	 */
	public static boolean validateEndPoint(String scopeToInstanceResolver, String VRE, String fullScope) throws ApplicationProfileNotFoundException {

		String originalScope = ScopeProvider.instance.get();
		logger.info("Checking if the VRE_NAME: "+VRE+" exists into Application Profile: "+ApplicationProfileReaderForCatalogueResolver.RESOURCE_NAME+" using scope: "+scopeToInstanceResolver);
		ApplicationProfileReaderForCatalogueResolver appPrCatResolver = new ApplicationProfileReaderForCatalogueResolver(scopeToInstanceResolver, true);
		Element root = appPrCatResolver.getRootDocument();

		try {
			XPathHelper helper = new XPathHelper(root);
			List<String> scopes = helper.evaluate(ApplicationProfileReaderForCatalogueResolver.RESOURCE_PROFILE_BODY_END_POINT_SCOPE_TEXT);
			for (String scopeFound : scopes) {
				//List<String> vreName = helper.evaluate("/Resource/Profile/Body/EndPoint[SCOPE='" +scopeFound.toString() + "']/VRE_NAME/text()");
				if(fullScope.compareTo(scopeFound)==0){
					logger.info("The full scope: " + fullScope + ", exists into "+ApplicationProfileReaderForCatalogueResolver.RESOURCE_NAME+", skipping update VRE_NAME: "+VRE);
					return false;
				}
			}

			logger.info("The full scope: " + fullScope + " does not exist into "+ApplicationProfileReaderForCatalogueResolver.RESOURCE_NAME+", creating the new end point VRE_NAME: "+VRE+", fullScope: "+fullScope);
			NodeList body = root.getElementsByTagName(ApplicationProfileReaderForCatalogueResolver.BODY);

			if(body==null || body.getLength()==0)
				throw new Exception("Body not found");

			Document document = addNewEndPoint(appPrCatResolver.getDocument(), VRE, fullScope);

			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");
			//initialize StreamResult with File object to save to file
			StreamResult result = new StreamResult(new StringWriter());
			DOMSource source2 = new DOMSource(document);
			transformer.transform(source2, result);

			logger.debug("Updated resource: \n"+result.getWriter().toString());

			String discoveryScope = useRootScope?ScopeUtil.getInfrastructureNameFromScope(scopeToInstanceResolver):scopeToInstanceResolver;
			ScopeProvider.instance.set(discoveryScope);

	        ScopedPublisher rp=RegistryPublisherFactory.scopedPublisher();
	        Resource resource = toResource(result);
			rp.update(resource);
			logger.info("Application Profile: "+ApplicationProfileReaderForCatalogueResolver.RESOURCE_NAME+" updated on IS successfully using scope: "+discoveryScope);
			return true;
		}
		catch (Exception e) {
			logger.error("Error ", e);
			throw new ApplicationProfileNotFoundException("Error during parsing application profile with resource name: " +ApplicationProfileReaderForCatalogueResolver.RESOURCE_NAME + " in the scope: " + scopeToInstanceResolver);
		}finally{
			if(originalScope!=null){
				ScopeProvider.instance.set(originalScope);
				logger.info("scope provider set to orginal scope: "+originalScope);
			}else{
				ScopeProvider.instance.reset();
				logger.info("scope provider reset");
			}
		}
	}


	/**
	 * To resource.
	 *
	 * @param result the result
	 * @return the generic resource
	 */
	private static GenericResource toResource(StreamResult result){
		InputStream is = new ByteArrayInputStream(result.getWriter().toString().getBytes());
		return Resources.unmarshal(GenericResource.class, is);
	}



	/**
	 * Adds the new end point.
	 *
	 * @param document the document
	 * @param VRE the vre
	 * @param fullScope the full scope
	 * @return the document
	 */
	private static Document addNewEndPoint(Document document, String VRE, String fullScope){
        Element newEndPoint = document.createElement(ApplicationProfileReaderForCatalogueResolver.END_POINT);
        Element newScope = document.createElement(ApplicationProfileReaderForCatalogueResolver.SCOPE);
        newScope.setTextContent(fullScope);
        Element newVREName = document.createElement(ApplicationProfileReaderForCatalogueResolver.VRE_NAME);
        newVREName.setTextContent(VRE);

        newEndPoint.appendChild(newScope);
        newEndPoint.appendChild(newVREName);

        logger.info("Adding the "+ApplicationProfileReaderForCatalogueResolver.END_POINT+":");
        logger.info(ApplicationProfileReaderForCatalogueResolver.VRE_NAME +": "+VRE +" - "+ApplicationProfileReaderForCatalogueResolver.SCOPE +" "+fullScope);

        document.getElementsByTagName(ApplicationProfileReaderForCatalogueResolver.BODY).item(0).appendChild(newEndPoint);

        return document;
	}

//	public static void main(String[] args) {
//
//		String scope = "/gcube";
//		try {
//			UpdateApplicationProfileCatalogueResolver.validateEndPoint(
//				scope, "gcube", "/gcube/devsec");
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
}
