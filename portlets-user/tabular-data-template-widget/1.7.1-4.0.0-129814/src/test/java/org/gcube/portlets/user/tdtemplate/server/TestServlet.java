/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.server;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.TemplateDescription;
import org.gcube.portlets.user.tdtemplate.server.converter.ConverterToTdTemplateModel;
import org.gcube.portlets.user.tdtemplate.server.service.TemplateService;
import org.gcube.portlets.user.tdtemplate.server.session.CacheServerExpressions;
import org.gcube.portlets.user.tdtemplate.shared.TdTemplateUpdater;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Apr 24, 2014
 *
 */
public class TestServlet {
	
	String scope = "/gcube/devsec";
	String username = "francesco.mangiacrapa";
	
	public static Logger logger = LoggerFactory.getLogger(TestServlet.class);
	/**
	 * 
	 */
	
	public TestServlet() {
		
		try {
			System.out.println("Start");
			TdTemplateUpdater tempUp = getTemplateUpdaterForTemplateId(1);
			
			System.out.println(tempUp);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void getTemplate(){
		
		try {
			System.out.println("Start");
			TdTemplateUpdater tempUp = getTemplateUpdaterForTemplateId(1);
			
			System.out.println(tempUp);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
	
	public TdTemplateUpdater getTemplateUpdaterForTemplateId(long templateId) throws Exception{
	
	
		TemplateService service = new TemplateService(scope, username);
	
		try{
			TemplateDescription templateDescr = service.getTemplate(templateId);
			if(templateDescr==null)
				throw new Exception("Sorry, an error occurred on recovering template with id "+templateId + " not exists");

			TemplateUpdaterForDescription updater = ConverterToTdTemplateModel.getTdTemplateUpdaterFromTemplateDescription(templateDescr,service,new CacheServerExpressions());
		
			return updater.getTdUpdater();
		}catch (Exception e) {
			logger.error("GetTemplateUpdaterForTemplateId error",e);
			throw new Exception("Sorry, an error occurred on recovering template with id "+templateId);
		}
	}
	
	public static void main(String[] args) {
		new TestServlet();
	}

}
