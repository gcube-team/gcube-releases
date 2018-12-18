package org.gcube.portal.custom.communitymanager.types;
/**
 * 
 * @author massi
 */
public enum GCUBELayoutType {
	/**
	 *  gives you a single column layout
	 */
	ONE_COL, 
	/**
	 * gives you two columns split 50/50
	 */
	TWO_COL_5050, 
	/**
	 *  gives you two columns split 30/70
	 */
	TWO_COL_3070, 
	/**
	 *  gives you two columns split 70/30
	 */
	TWO_COL_7030,
	/**
	 *  gives you three columns
	 */
	THREE_COL, 
	/**
	 *  gives you one top initial row, followed by 2 columns split 30/70
	 */
	TWO_ROWS_1_2_3070,
	/**
	 *  gives you one top initial row, followed by 2 columns split 70/30
	 */
	TWO_ROWS_1_2_7030,
	/**
	 *  gives you one top initial row, followed by a row with 2 columns 50/50 split, followed by another row
	 */
	THREE_ROWS_1_2_5050_1, 
	/**
	 *   gives you one row with 2 columns 70/30 split followed by another row with 2 columns 30/70 split.
	 */
	TWO_ROWS_2_7030_2_3070;	
}
