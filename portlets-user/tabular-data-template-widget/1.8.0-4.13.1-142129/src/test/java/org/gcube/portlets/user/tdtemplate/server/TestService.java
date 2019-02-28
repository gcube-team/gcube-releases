/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.server;

import java.util.List;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.TemplateDescription;
import org.gcube.data.analysis.tabulardata.service.tabular.TabularResource;
import org.gcube.data.analysis.tabulardata.service.tabular.metadata.NameMetadata;
import org.gcube.portlets.user.tdtemplate.server.service.TemplateService;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Apr 9, 2014
 *
 */
public class TestService {
	
	
	//USED FOR DEBUG
	public static void main(String[] args) {
		
		
//		double radice = Math.sqrt(125); 
//		System.out.println(radice);
		/*try {
			PeriodType period = ConverterToTemplateServiceModel.periodNameToPeriodType("DAY");
			System.out.println("period is "+period);
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		
		getTemplates("/gcube/devsec/devVRE", "test.user");
		
//		getTabularResoruces("/gcube/devsec/devVRE", "francesco.mangiacrapa");
	}
	
	//USED FOR DEBUG
	public static void getTemplates(String scope, String username) {
//		ASLSession session = getASLSession();
//		TemplateService service = new TemplateService(session.getScope());
		try{
			System.out.println("Start");
			TemplateService service = new TemplateService(scope,username);
			
			List<TemplateDescription> tsDescr = service.getTemplates();
			
			if(tsDescr!=null && tsDescr.size()>0){
	//			columnDef =  new ArrayList<TdColumnDefinition>(tsDescr.size());
				for (TemplateDescription template : tsDescr) {
					System.out.println("ID: "+template.getId() + ", name: "+template.getName() +", actions? "+ template.getTemplate().getActions().size() +" flow? "+ template.getTemplate().getAddToFlow());
					
				}
			}
			
			System.out.println("End");
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	//USED FOR DEBUG
	public static void getTabularResoruces(String scope, String username) {
//		ASLSession session = getASLSession();
//		TemplateService service = new TemplateService(session.getScope());
		try{
			System.out.println("Start");
			TemplateService service = new TemplateService(scope,username);
			
//			TabularResource tr1 = service.getTabularResourceById(244);
			System.out.println("table id "+service.getTable(244).getId());
			List<TabularResource> trs = service.getTabularResources();
			
			if(trs!=null && trs.size()>0){
	//			columnDef =  new ArrayList<TdColumnDefinition>(tsDescr.size());
				for (TabularResource  tr : trs) {
//					TableId tableId = service.getTable(tr.getId().getValue()).getId();
//					System.out.println(tr.getId() + ", type: "+tr.getTableType() +", name: "+tr.getMetadata(NameMetadata.class).getValue()+ ", tableId: "+tableId);
					System.out.println(tr.getId() + ", type: "+tr.getTableType() +", name: "+tr.getMetadata(NameMetadata.class).getValue());
				}
			}
			
			System.out.println("End");
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
