/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.server.validator.service;

import java.util.HashMap;
import java.util.List;

import org.gcube.data.analysis.tabulardata.commons.templates.model.TemplateCategory;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.ColumnDescription;
import org.gcube.portlets.user.tdtemplate.server.validator.ColumnCategoryConstraint;
import org.gcube.portlets.user.tdtemplate.server.validator.TemplateCategoryValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Apr 1, 2014
 *
 */
public class ColumnCategoryTemplateValidator extends ColumnCategoryConstraintCreator implements TemplateCategoryValidator{
	
	public static Logger logger = LoggerFactory.getLogger(ColumnCategoryTemplateValidator.class);
	/**
	 * 
	 */
	public ColumnCategoryTemplateValidator(TemplateCategory category) {
		
		if(category!=null){
			
			List<ColumnDescription> allowedColumn = category.getAllowedColumn();
			logger.info("Creating Hash Column Validator for "+category);
			
			for (ColumnDescription columnDescription : allowedColumn){
				super.addConstraint(columnDescription);
			}
			logger.info("Hash Column Validator for "+category+" created");
		}else
			logger.warn("Category is null, skypping");
		
	
	}
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplate.server.validator.TemplateCategoryValidator#getTemplateColumnValidator()
	 */
	@Override
	public HashMap<String, ColumnCategoryConstraint> getTemplateColumnValidator() {
		return hashColumnValidator;
	}
}
