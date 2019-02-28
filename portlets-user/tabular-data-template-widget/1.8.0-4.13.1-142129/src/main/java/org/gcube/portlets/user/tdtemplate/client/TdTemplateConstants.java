/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client;

import org.gcube.portlets.user.tdtemplate.client.templateactions.AddColumnActionDialog;
import org.gcube.portlets.user.tdtemplate.client.templateactions.TemplatePanelActionEdit;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.TemplatePanel;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jan 8, 2014
 * 
 */
public interface TdTemplateConstants {

	public static int WINDOW_WIDTH = 800;
	public static int WINDOW_HEIGHT = 390;
	public static int  HEIGHT_PRINCIPAL = 358;

	public static String TEMPLATECREATOR = "Template Creator";
	public static String TEMPLATEUPDATER = "Template Updater";

	public static int MAXCOLUMNS = 50;
	

	public static final String LOADINGSTYLE = "x-mask-loading";

	public static final String PLEASE_SET_TYPE_TO_COLUMN_NUMBER = "Please, set type to column number ";
	public static final String SUGGESTION = "Suggestion:";
	public static final String DO_YOU_WANT_SAVE_THE_TEMPLATE = "Do you want save the template?";
	public static final String NOW_IS_POSSIBLE_TO_GENERATE_THE_TEMPLATE_CREATED = "Now is possible to generate the template created! Press 'Next' button";
	public static final String NOW_IS_POSSIBLE_TO_UPDATE_THE_TEMPLATE_CREATED = "Now is possible to update the template created! Press 'Next' button";
	
	public static final String TEMPLATE_COMPLETED = "Template completed!";


	public static final String TEMPLATE_CREATION_ERROR = "Sorry, an error occurred on server when creating the template, try again";
	public static final String TEMPLATE_UPDATE_ERROR = "Sorry, an error occurred on server when updating the template, try again";
	public static final String TEMPLATE_ERROR = "Template Error";
	public static final String TEMPLATE_CREATED_SUCCESSFULLY = "Template created successfully";
	public static final String TEMPLATE_CREATED = "Template Created";
	
//	public static final String TEMPLATE_UPDATED_SUCCESSFULLY = "Template updated successfully";
	public static final String TEMPLATE_UPDATED = "Template Updated";
	
	public static final int WIDTHWIDGETCOLUMN = 110;
	
	/**
	 * Used by {@link TdTemplateControllerActions}
	 */
	public static final String DO_YOU_WANT_ADD_ANOTHER_ACTION = "Do you want add another action?";
	public static final String ACTION_COMPLETED = "Action completed!";
	
	/**
	 * Used by {@link TemplatePanelActionEdit}
	 */
	public static final String REMOVE_LATEST_POST_OPERATION = "Remove the latest post-operation?";
	public static final String AGGREGATE_BY_TIME = "Aggregate By Time";
	public static final String UNDO_LATEST_OPERATION = "Undo Latest";
	public static final String HISTORY_OPERATION = "History Operations";
	public static final String DO_YOU_WANT_ADD_POST_ACTIONS = "Do you want add post Actions?";
	public static final String NONE = "None";
	public static final String LATEST_OPERATION = "#Latest Operation";
	public static final String HISTORY_OF_THE_ACTIONS_APPLIED_TO_TEMPLATE = "History of the actions applied to template!";
	public static final String HISTORY_OF_THE_POST_OPERATION_APPLIED = "History of the post-operation applied";
	public static String ADD_COLUMN = "Add Column";
	public static String REMOVE_COLUMN = "Remove Column";
	public static String CREATE_TIME_DIMENSION = "Create Time Dimension";
	public static String NORMALIZE = "Normalize";
	public static String NORMALIZE_COLUMNS_ACTION = "Normalize Columns Action";
	public static final String ADD_A_MULTI_COLUMN_RULE = "Add a multi-column rule";
	public static final String TABLE_RULE = "Table Rule";

	/**
	 * Used by {@link TemplatePanel}
	 */
	public static final String PLEASE_SET_TYPE_TO_ALL_COLUMNS = "Please, set type to all columns";
	public static final String EDIT_FLOW = "Edit Flow";
	public static final String CREATE_FLOW = "Create Flow";
	public static final String DEFINITION_AND_VALIDATION_TEMPLATE = "> Definition and Validation Template";
	
	/**
	 * Used by {@link AddColumnActionDialog}
	 */
	public static final String PLEASE_SET_THE_COLUMN_TYPE = "Please, set the column type";
	public static String ADD_COLUMN_CONFIRM = "Adding column, confirm?";
	public static String ACTION_ADD_COLUMN = "Action Add Column";
	
	public static String ACTION_REMOVE_COLUMN = "Action Remove Column";
}
