package org.gcube.data.analysis.tabulardata.templates;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.data.analysis.tabulardata.commons.templates.model.Template;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.utils.Factories;

@Singleton
public class TemplateEngineFactory {

	@Inject
	Factories factories;
	
	@Inject
	TemplateFinalActionFactory tfaf;
	
	
	
	public TemplateEngine getEngine(Template template, Table table){
		return new TemplateEngine(template, table, factories, tfaf);
	}
	
}
