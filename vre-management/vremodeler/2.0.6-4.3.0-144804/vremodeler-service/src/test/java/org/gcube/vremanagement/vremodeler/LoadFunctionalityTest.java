package org.gcube.vremanagement.vremodeler;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.resources.GCUBEGenericResource;
import org.gcube.vremanagement.vremodeler.resources.Functionality;
import org.gcube.vremanagement.vremodeler.resources.MainFunctionality;
import org.gcube.vremanagement.vremodeler.resources.ResourceDefinition;
import org.gcube.vremanagement.vremodeler.resources.kxml.KGCUBEGenericFunctionalityResource;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class LoadFunctionalityTest {

	@Test
	public void loadFunctionality() throws Exception{
		try{
			KGCUBEGenericFunctionalityResource resource= new KGCUBEGenericFunctionalityResource();
			InputStream is = KGCUBEGenericFunctionalityResource.class.getResourceAsStream("/functionality.xml");
			System.out.println("inputStream is null ?"+(is==null));
			if (is!=null) resource.load(new InputStreamReader(is));
			for (MainFunctionality mainFunct : resource.getBody().getMainFunctionalities()){
				System.out.println("MAIN FUNCTIONALITY ---- "+mainFunct.getName()+"  is mandatory "+mainFunct.isMandatory());
				for (Functionality funct: mainFunct.getFunctionalities()){
					System.out.println("FUNCTIONALITY ---- "+funct.getName()+" mandatory :"+funct.isMandatory());
					for(ResourceDefinition<?> selectable :funct.getSelectableResources())
						System.out.println("RESOURCE ---- "+selectable.getDescription()+" min:"+selectable.getMinSelectable()+" max:"+selectable.getMaxSelectable());
				}
					
			}
			System.out.println(resource.getBody().getMainFunctionalities().get(0).getFunctionalities().get(0).getMandatoryResources().size());
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	
	public void storeFunctionality() throws Exception{
		try{
			KGCUBEGenericFunctionalityResource resource= new KGCUBEGenericFunctionalityResource();
			InputStream is = KGCUBEGenericFunctionalityResource.class.getResourceAsStream("/functionality.xml");
			System.out.println("inputStream is null ?"+(is==null));
			if (is!=null) resource.load(new InputStreamReader(is));
			resource.store(new FileWriter(File.createTempFile("functionality", "xml")));
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	
	public void XPathtest() throws Exception{
		DocumentBuilderFactory domFactory = 
				DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true); 
		DocumentBuilder builder = domFactory.newDocumentBuilder();

		XPath xpath = XPathFactory.newInstance().newXPath();
		XPathExpression expr = xpath.compile("//CollectionInfo/user[text()='true']");
			
		GCUBEGenericResource genericResource = GHNContext.getImplementation(GCUBEGenericResource.class);
		InputStream is = KGCUBEGenericFunctionalityResource.class.getResourceAsStream("/collection.xml");
		System.out.println("inputStream is null ?"+(is==null));
		
		if (is!=null) genericResource.load(new InputStreamReader(is));
		
		Document doc = builder.parse(new InputSource(new StringReader(genericResource.getBody())));
		// XPath Query 
		System.out.println((Boolean)expr.evaluate(new InputSource(new StringReader(genericResource.getBody())),  XPathConstants.BOOLEAN));
			
		
	}
}
