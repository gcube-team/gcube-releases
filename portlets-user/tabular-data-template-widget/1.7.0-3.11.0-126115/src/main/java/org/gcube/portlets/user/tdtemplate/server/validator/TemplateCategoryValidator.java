/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.server.validator;

import java.util.HashMap;



/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Apr 1, 2014
 *
 */
public interface TemplateCategoryValidator {
	
	HashMap<String, ColumnCategoryConstraint> getTemplateColumnValidator();
}
