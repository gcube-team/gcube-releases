package org.gcube.data.analysis.tabulardata;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.tabulardata.commons.templates.model.Template;
import org.gcube.data.analysis.tabulardata.commons.templates.model.TemplateCategory;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.ColumnCategory;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.TemplateColumn;
import org.gcube.data.analysis.tabulardata.commons.utils.AuthorizationProvider;
import org.gcube.data.analysis.tabulardata.commons.utils.AuthorizationToken;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.InternalSecurityException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTemplateException;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TemplateDescription;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;
import static org.gcube.data.analysis.tabulardata.clientlibrary.plugin.AbstractPlugin.template;
import org.junit.Before;
import org.junit.Test;

public class TemplateTST {

		
	@Before
	public void init(){
		ScopeProvider.instance.set("/gcube/devsec");
		AuthorizationProvider.instance.set(new AuthorizationToken("lucio.lelii"));
	}
	
	@Test
	public void getAll() throws InternalSecurityException{
		for (TemplateDescription desc: template().build().getTemplates())
			System.out.println(desc.getId()+" "+desc.getName());
	}
	
	@Test
	public void deteTemplate() throws NoSuchTemplateException, InternalSecurityException{
		template().build().removeTemplate(3l);
	}
	
	@Test
	public void saveTemplate() throws Exception{
		TemplateColumn<TextType> firstColumn= new TemplateColumn<TextType>(ColumnCategory.CODE, TextType.class);
		TemplateColumn<TextType> secondColumn= new TemplateColumn<TextType>(ColumnCategory.CODENAME, TextType.class);
		TemplateColumn<TextType> thirdColumn= new TemplateColumn<TextType>(ColumnCategory.CODENAME, TextType.class);
		TemplateColumn<TextType> forthColumn= new TemplateColumn<TextType>(ColumnCategory.CODENAME, TextType.class);
		TemplateColumn<TextType> fifthColumn= new TemplateColumn<TextType>(ColumnCategory.CODENAME, TextType.class);
		
		TemplateColumn<TextType> sixthColumn= new TemplateColumn<TextType>(ColumnCategory.ANNOTATION, TextType.class);
		
		Template template = Template.create(TemplateCategory.CODELIST, firstColumn, secondColumn, thirdColumn, forthColumn, fifthColumn, sixthColumn );
				
		long id = template().build().saveTemplate("test", "desc", "CN", template);
		
		template().build().removeTemplate(id);
		System.out.println(id);
	}
	
	
		
}
