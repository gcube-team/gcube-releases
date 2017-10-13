/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.server.validator.service;

import java.util.HashMap;

import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.ColumnCategory;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.ColumnDescription;
import org.gcube.portlets.user.tdtemplate.server.converter.ConverterToTdTemplateModel;
import org.gcube.portlets.user.tdtemplate.server.validator.ColumnCategoryConstraint;
import org.gcube.portlets.user.tdtemplate.server.validator.ColumnOccurrenceComparator;
import org.gcube.portlets.user.tdtemplate.shared.TdTColumnCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Apr 2, 2014
 *
 */
public abstract class ColumnCategoryConstraintCreator {
	
	public static Logger logger = LoggerFactory.getLogger(ColumnCategoryConstraintCreator.class);
	
	protected HashMap<String, ColumnCategoryConstraint> hashColumnValidator = new HashMap<String, ColumnCategoryConstraint>();
	
	/**
	 * 
	 * @param columnDescription
	 * @return constraints for column type
	 */
	public void addConstraint(ColumnDescription columnDescription){
		
		if(columnDescription==null){
			logger.warn("ColumnDescription is null, returning");
			return;
		}
		
		logger.info("Creating Hash Column Validator entry...");
		
		try{
			
			ColumnCategory category = columnDescription.getColumnCategory();
			
			logger.info("Creating Constraint for category: "+category);
			Integer minimun = columnDescription.getCardinality().getMin();
			Integer maximun = columnDescription.getCardinality().getMax();
			logger.info("Cardinality is: ["+minimun+","+maximun+"]");
			
			TdTColumnCategory tdtColumn = ConverterToTdTemplateModel.getTdTColumnCategoryFromColumnCategory(category);
			
			ColumnOccurrenceComparator cm = new ColumnOccurrenceComparator(minimun, maximun);
			String constrDescr = getConstraintDescriptionByColumnOccurrenceComparator(tdtColumn.getName(), cm);
			
			ColumnCategoryConstraint cs = new ColumnCategoryConstraint(tdtColumn, cm, constrDescr);
			
			logger.info("Adding ColumnCategoryConstraint "+cs+ " to Hash Column Validator");
			
			hashColumnValidator.put(tdtColumn.getId(), cs);
			
		}catch (Exception e) {
			logger.error("Error on creating ColumnCategoryConstraint ", e);
		}
	}
	
	/**
	 * 
	 * @param columnName
	 * @param minimun
	 * @param maximun
	 * @return
	 */
	private static String getConstraintDescription(String columnName, Integer minimun, Integer maximun) {

		if(minimun==null && maximun==null)
			return "No constraint for "+columnName+" column";
		
		if (minimun != null && maximun == null){
			
			if(minimun.intValue()>0)
				return "Must have at least " + NumberToWordsConverter.convert(minimun) + " " + columnName + " column";
			else
				return "No constraint for "+columnName+" column";
		}
		
		if (minimun != null && maximun != null) {

			int min = minimun.intValue();
			int max = maximun.intValue();

			if (min == 0 && max >= 1) {
				return "Must have at most " + NumberToWordsConverter.convert(max) + " " + columnName + " column";
			}
			
			if (min == 1 && max == 1) {
				return "Must have one (and only one) " + columnName + " column";
			}
			
			if (min == 1 && max > 1) {
				return "Must have at least one and at most " + NumberToWordsConverter.convert(max) + " " + columnName + " column";
			}

			if (min > 1 && max > 1) {
				return "Must have at least "+NumberToWordsConverter.convert(min)+" and at most " + NumberToWordsConverter.convert(max) + " " + columnName + " column";
			}
		}

		return "";

	}
	
	/**
	 * 
	 * @param columnName
	 * @param compartor
	 * @return
	 */
	public static String getConstraintDescriptionByColumnOccurrenceComparator(String columnName, ColumnOccurrenceComparator compartor){
		
		//CHANGE INT MAX_VALUE AS NULL
		Integer maxValue = compartor.getMaxOccurrence();
		if(maxValue!=null && compartor.getMaxOccurrence() == Integer.MAX_VALUE)
			maxValue = null;
		
		return getConstraintDescription(columnName, compartor.getMinOccurrence(), maxValue);
		
	}
	
	public static class NumberToWordsConverter {

		final private  static String[] units = {"zero","one","two","three","four",
			"five","six","seven","eight","nine","ten",
			"eleven","twelve","thirteen","fourteen","fifteen",
			"sixteen","seventeen","eighteen","nineteen"};
		final private static String[] tens = {"","","Twenty","Thirty","Forty","Fifty",
			"Sixty","Seventy","Eighty","Ninety"};


		public static String convert(Integer i) {
			//
			if( i < 20)  return units[i];
			if( i < 100) return tens[i/10] + ((i % 10 > 0)? " " + convert(i % 10):"");
			if( i < 1000) return units[i/100] + " Hundred" + ((i % 100 > 0)?" and " + convert(i % 100):"");
			if( i < 1000000) return convert(i / 1000) + " Thousand " + ((i % 1000 > 0)? " " + convert(i % 1000):"") ;
			return convert(i / 1000000) + " Million " + ((i % 1000000 > 0)? " " + convert(i % 1000000):"") ;
		}
	}
	
	/*public static void main(String[] args) {
		System.out.println(ConstraintDescriptionUtil.getConstraintDescription("name", 3, 5));
		System.out.println(ConstraintDescriptionUtil.getConstraintDescription("name", 1, 1));
		
		System.out.println(ConstraintDescriptionUtil.getConstraintDescription("name", 3, null));
		System.out.println(ConstraintDescriptionUtil.getConstraintDescription("name", 0, 1));
		System.out.println(ConstraintDescriptionUtil.getConstraintDescription("name", 0, null));
		
		
		try {
			System.out.println(ConstraintDescriptionUtil.getConstraintDescriptionByColumnOccurrenceComparator("name", new ColumnOccurrenceComparator(1, Integer.MAX_VALUE)));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/

}
