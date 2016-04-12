/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.server.validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.portlets.user.tdtemplate.shared.TdTColumnCategory;
import org.gcube.portlets.user.tdtemplate.shared.validator.ViolationDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Mar 31, 2014
 *
 */
public class TemplateValidator {

	public static Logger logger = LoggerFactory.getLogger(TemplateValidator.class);
	private Map<String, ColumnCategoryConstraint> hashColumnTemplateValidator;
	private TemplateCategoryValidator validator;
	
	private Map<String, Boolean> results = new HashMap<String, Boolean>();
	
	private List<ViolationDescription> violations  = new ArrayList<ViolationDescription>();

	/**
	 * 
	 */
	public TemplateValidator(List<TdTColumnCategory> categories, TemplateCategoryValidator validator) {
		this.validator = validator;
		this.hashColumnTemplateValidator = TemplateValidatorCreateUtil.createValidator(categories,validator);
	}
	
	
	public void validate(){
		logger.info("Validating Template started..");
		
		HashMap<String, ColumnCategoryConstraint> columns = validator.getTemplateColumnValidator();
		
		for (String category : columns.keySet()) {
			logger.info("Fetched constraint "+category +" from Validator (ColumnCategoryTemplateValidator)");
			results.put(category, false);
			
			ColumnCategoryConstraint constraint = hashColumnTemplateValidator.get(category);
			
			if(constraint==null){
				logger.info("Constraint doesn't exists into template: "+category);
				
				logger.info("Checking if "+category+" is optional");
				ColumnCategoryConstraint valConstraint = columns.get(category);
				Integer minimun = valConstraint.getComparator().getMinOccurrence();
				logger.info("minimun is: "+minimun);
				boolean condition = valConstraint.getComparator().getMinOccurrence()==0;
				logger.info("valConstraint.getComparator().getMinOccurrence()==0 is: "+condition);
				logger.info("constraint is optional: "+condition);
				results.put(category, condition);
				logger.info("is violation: "+!condition);
				
			}else{
				ColumnCategoryConstraint validatorConstraint = columns.get(category);
				boolean isValid = compareConstraints(validatorConstraint, constraint);
				logger.info("Comparing is: "+isValid);
				results.put(category, isValid);
			}
		}
		
		createViolationReportByCategory();
	}
	
	
	private void createViolationReportByCategory() {
		
		HashMap<String, ColumnCategoryConstraint> columns = validator.getTemplateColumnValidator();
		for (String category : results.keySet()) {
			boolean isValid = results.get(category);
			
			if(!isValid){
				ColumnCategoryConstraint constraint = columns.get(category);
				violations.add(new ViolationDescription(category, constraint.getConstraintDescription()));
			}
		}
	}


	private boolean compareConstraints(ColumnCategoryConstraint validatorConstraint, ColumnCategoryConstraint constraint){
	
		ColumnOccurrenceComparator validatorComparator = validatorConstraint.getComparator();
		ColumnOccurrenceComparator constraintComparator = constraint.getComparator();
		
		logger.info("Comparing ValidatorComparator: "+validatorComparator + " and ConstraintComparator: "+constraintComparator);
		
		Integer valMin = validatorComparator.getMinOccurrence();
		Integer colMin = constraintComparator.getMinOccurrence();
		
		if(valMin!=null && colMin!=null){
			
			boolean minimunCondition = colMin.intValue()>=valMin.intValue();
			logger.info("Minimun condition colMin.intValue()>=valMin.intValue() is "+minimunCondition);
			if(minimunCondition){
				
				Integer valMax= validatorComparator.getMaxOccurrence();
				
				//MAX IS UNDEFINED INTO VALIDATOR
				if(valMax==null || valMax.intValue() == Integer.MAX_VALUE){
					logger.info("ValidatorComparator has max occurrence setted as null or Integer.MAX_VALUE, returning true");
					return true;
				}
				
				Integer colMax = constraintComparator.getMaxOccurrence();
				
				if(colMax==null || colMax.intValue() == Integer.MAX_VALUE){
					logger.info("ConstraintComparator has max occurrence setted as null or Integer.MAX_VALUE, returning true");
					return true;
				}
				
				boolean maximunCondition = colMax.intValue()<=valMax.intValue();
				
				logger.info("Maximun condition colMax.intValue()<=valMax.intValue() is "+maximunCondition);
				if(maximunCondition){
					logger.info("Maximun condition is true, returning true");
					return true;
				}

			}

			/*
			boolean condition1 = (valMin.intValue()==0) && (colMin.intValue()==1);
			boolean condition2 = valMin.intValue()==colMin.intValue();
			logger.info("(valMin.intValue()==0) && (colMin.intValue()==1) is "+condition1);
			logger.info("valMin.intValue()==colMin.intValue() is "+condition2);
			if(condition1 || condition2){
				logger.info("One minimun condition is true");
				
				Integer valMax= validatorComparator.getMaxOccurrence();
				
				//MAX IS UNDEFINED INTO VALIDATOR
				if(valMax==null){
					logger.info("ValidatorComparator has max occurrence setted as null, returning true");
					return true;
				}
				
				Integer colMax = constraintComparator.getMaxOccurrence();
				
				if(colMax==null){
					logger.info("ConstraintComparator has max occurrence setted as null, returning false");
					return false;
				}
					
				//COMPARE MAX VALUE
				if(valMax.intValue()==colMax.intValue()){
					logger.info("Max values are equal, returing true");
					return true;
				}else
					logger.info("Max values are not equal, returing false");
			}else{
				logger.info("Minimun conditions are false, returing false");
			}
			*/
		}else
			logger.info("Minimun values are null, returing false");

		logger.info("CompareConstraints returing false");
		return false;
	}


	public List<ViolationDescription> getViolations() {
		return violations;
	}
	

}
