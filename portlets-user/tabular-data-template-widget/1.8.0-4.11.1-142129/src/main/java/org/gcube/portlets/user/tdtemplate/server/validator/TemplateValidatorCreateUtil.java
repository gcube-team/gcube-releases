/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.server.validator;

import java.util.HashMap;
import java.util.List;

import org.gcube.portlets.user.tdtemplate.shared.TdTColumnCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Apr 1, 2014
 *
 */
public class TemplateValidatorCreateUtil {
	
	public static Logger logger = LoggerFactory.getLogger(TemplateValidatorCreateUtil.class);
	
	public static HashMap<String, ColumnCategoryConstraint> createValidator(List<TdTColumnCategory> categories, TemplateCategoryValidator validator){
		
		if(categories==null)
			return null;
		
		HashMap<String, ColumnCategoryConstraint> hashTemplateValidator = new HashMap<String, ColumnCategoryConstraint>();
		
		HashMap<String, ColumnCategoryConstraint> validatorConstraints = validator.getTemplateColumnValidator();
		
		for (TdTColumnCategory tdTColumnCategory : categories) {
			
			String categoryKey = tdTColumnCategory.getId();
			logger.info("Getting constraint for category: "+categoryKey);
			
			ColumnCategoryConstraint existsConstraint = validatorConstraints.get(tdTColumnCategory.getId());
			
			if(existsConstraint!=null){
				logger.info("Category "+categoryKey+" is constrained");
				try{
				
					ColumnCategoryConstraint categoryConstraint = hashTemplateValidator.get(categoryKey);

					if(categoryConstraint==null){
						logger.info("Constraint is null, Creating constraint for: "+tdTColumnCategory);
						ColumnOccurrenceComparator cs = new ColumnOccurrenceComparator(new Integer(1),new Integer(1));
	//					hashTemplateValidator.put(categoryKey, new ColumnCategoryConstraint(tdTColumnCategory, cs, null));
						logger.info("New constraint is: "+cs);
						hashTemplateValidator.put(categoryKey, new ColumnCategoryConstraint(tdTColumnCategory, cs, null));
					}else{
						logger.info("Constraint already exists, Updating constraint for: "+tdTColumnCategory);
						ColumnOccurrenceComparator cs = categoryConstraint.getComparator();
						cs.setMaxOccurrence(cs.getMaxOccurrence()+1);
						logger.info("Updated constraint is: "+cs);
						categoryConstraint.setComparator(cs);
						hashTemplateValidator.put(categoryKey, categoryConstraint);
					}
				}catch (Exception e) {
					logger.info("Error on creating TemplateValidator for: "+tdTColumnCategory);
				}
			}else{
				logger.info("Category "+categoryKey+" is not constrained, skypping");
			}
		}
		logger.info("Created TemplateValidator: "+hashTemplateValidator);
		
		return hashTemplateValidator;
	}
}
