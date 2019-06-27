/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import org.gcube.data.analysis.tabulardata.commons.templates.model.TemplateCategory;
import org.gcube.portlets.user.tdtemplate.server.validator.ColumnCategoryConstraint;
import org.gcube.portlets.user.tdtemplate.server.validator.service.ColumnCategoryTemplateValidator;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Apr 2, 2014
 *
 */
public class TemplateHelper {
	
	protected String html = "<html>";
	protected String closeHtml = "</html>";
	protected String closeBody= "</body>";
	
	private StringBuilder builderHTML = new StringBuilder();
	
	protected String head = "<head>"
			+ "<title>Template Helper</title>" + "<style type=\"text/css\">"
			+ "#templateHelper {" + "width: 390px;"
			+ "font-family: Arial, Verdana, sans-serif;"
			+ "/* 	color: #665544; */" + "font-size: 12px;"
			+ "margin: 5px !important;" + "padding: 5px !important;" + "}"
			+ ".templateHelperClass {" + "background-color: #F5F5F5;"
//			+ ".templateHelperClass {" + "background-color: #FFFACD;"
			+ "padding: 10px;" + "}" + ".paddingLeft {" + "padding-left: 15px;"
			+ "}" + ".paddingLeft10 {" + "padding-left: 10px;" + "}"
			+ "#templateHelper p {" + "width: 370px;" + "}"
			+ "#templateHelper li {" + "width: 350px;" + "padding-left: 20px;"
			+ "}" + ".templatediv {" + "margin-top: 5px !important;" + "}"
			+ "</style>" + "</head>";
	
	
	

	protected String presentation = "<body id=\"templateHelper\">" +
			"<h3 class=\"templateHelperClass\">Templates</h3>" +
			"<div class=\"templatediv\">" +
			"<p>A table template can be defined in two steps:</p>"+
			"<li> *Definition and Validation Template;</li>"+
			"<li> *Definition and Validation Post Operations.</li>"+
					"<ol class=\"paddingLeft10\" type=\"i\">" +
					"<li>A table template defines:</li>" +
					"<ul class=\"paddingLeft\" type=\"disc\">" +
					"<li>Table definition</li>" +
					"<li>Columns definition</li>" +
					"<li>A set of validation rules</li>" +
					"<li>A set of column post-operations</li>" +
					"</ul>" +
					"<li>It can be applied to any dataset</li>" +
					"<li>It can be modified and shared among people</li>" +
					"</ol>" +
					"</div>" +
					"<div class=\"templatediv\">" +
					"<p>A table template can be applied to:</p>" +
					"<ol class=\"paddingLeft10\" type=\"i\">";
	
	

	
	protected String onError = "<div class=\"templatediv\">" +
			"<p>In case of error one policy of the following policies has to be " +
			"selected:</p>" +
			"<ol class=\"paddingLeft10\" type=\"i\">" +
			"<li style=\"font-weight: bold;\">DISCARD:</li>" +
			"<ul class=\"paddingLeft\" type=\"disc\">" +
			"<li>The records that don’t pass the Validation Rules are " +
			"deleted</li>" +
			"</ul>" +
			"<li style=\"font-weight: bold;\">SAVE:</li>" +
			"<ul class=\"paddingLeft\" type=\"disc\">" +
			"<li>The records that don’t pass the Validation Rules are saved " +
			"in a separate table and removed from the resource</li>" +
			"</ul><li style=\"font-weight: bold;\">ASK:</li>" +
			"<ul class=\"paddingLeft\" type=\"disc\">" +
			"<li>The records that don’t pass the Validation Rules are " +
			"identified and the user has to take corrective actions and then " +
			"repeat the validation phase</li></ul></ol></div>";
	/**
	 * 
	 */
	public TemplateHelper() {
		
		builderHTML.append(html);
		builderHTML.append(head);
		builderHTML.append(presentation);
		
		ColumnCategoryTemplateValidator val = new ColumnCategoryTemplateValidator(TemplateCategory.GENERIC);
		HashMap<String, ColumnCategoryConstraint> hash = val.getTemplateColumnValidator();
		
		builderHTML.append("<li style=\"font-weight: bold;\">Generic table:</li>");
		builderHTML.append("<ul class=\"paddingLeft\" type=\"disc\">");
		for (String key : hash.keySet()) {
			ColumnCategoryConstraint constraint = hash.get(key);
			builderHTML.append("<li>"+constraint.getConstraintDescription()+"</li>");
		}
		
		builderHTML.append("</ul>");
		
		
		val = new ColumnCategoryTemplateValidator(TemplateCategory.CODELIST);
		hash = val.getTemplateColumnValidator();
		
		builderHTML.append("<li style=\"font-weight: bold;\">Codelist:</li>");
		builderHTML.append("<ul class=\"paddingLeft\" type=\"disc\">");
		for (String key : hash.keySet()) {
			ColumnCategoryConstraint constraint = hash.get(key);
			builderHTML.append("<li>"+constraint.getConstraintDescription()+"</li>");
		}
		
		builderHTML.append("</ul>");
		

		val = new ColumnCategoryTemplateValidator(TemplateCategory.DATASET);
		hash = val.getTemplateColumnValidator();
		
		builderHTML.append("<li style=\"font-weight: bold;\">Dataset:</li>");
		builderHTML.append("<ul class=\"paddingLeft\" type=\"disc\">");
		for (String key : hash.keySet()) {
			ColumnCategoryConstraint constraint = hash.get(key);
			builderHTML.append("<li>"+constraint.getConstraintDescription()+"</li>");
		}
		
		builderHTML.append("</ul>");
		
		builderHTML.append("</ol></div>");
		
		builderHTML.append(onError);
		builderHTML.append(closeBody);
		builderHTML.append(closeHtml);	
		
	}
	
	public StringBuilder getBuilderHTML() {
		return builderHTML;
	}
	
	public static void main(String[] args) {
		
		try {
			FileWriter writer = new FileWriter(new File("testHelper.html"));
			TemplateHelper helper = new TemplateHelper();
			
			writer.append(helper.getBuilderHTML());
			writer.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
