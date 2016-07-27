package org.gcube.indexmanagement.common;

import java.util.ArrayList;
import java.util.List;

import org.gcube.rest.commons.helpers.XMLConverter;
import org.gcube.rest.commons.helpers.XPathEvaluator;

public class IndexTypeParser {

	
	static List<IndexField> readAllFields(String indexTypeStr){
		List<IndexField> fields = new ArrayList<IndexField>();
		
		XPathEvaluator eval = new XPathEvaluator(XMLConverter.stringToNode(indexTypeStr));
		
		List<String> fieldsXML = eval.evaluate("//field");
		
		for (String fieldXML : fieldsXML){
			
			
			IndexField field = readField(fieldXML);
			
			fields.add(field);
		}
		
		return fields;
	}
	
	static IndexField readField(String fieldXML){
		XPathEvaluator eval = new XPathEvaluator(XMLConverter.stringToNode(fieldXML));
		
		IndexField field = new IndexField();
		field.name = evalString(eval, "/field/@name", "");
		
		field.boost = evalFloat(eval, "//boost/text()", 1.0f);
		
		field.index = evalBool(eval, "//index/text()", true);
		field.tokenize = evalBool(eval, "//tokenize/text()", true);
		field.store = evalBool(eval, "//store/text()", true);
		
		field.returned = evalBool(eval, "//return/text()", true);
		
		field.highlightable = evalBool(eval, "//highlightable/text()", true);
		
		field.sort = evalBool(eval, "//sort/text()", true);
		
		field.type = evalString(eval, "//type/text()", "");
		
		return field;
	}
	
	static String evalString(XPathEvaluator eval, String xpath, String defaultValue){
		try {
			String val = eval.evaluate(xpath).get(0);
			return val;
		} catch (Exception e) {
			return defaultValue;
		}
		 
	}
	
	static float evalFloat(XPathEvaluator eval, String xpath, float defaultValue){
		try {
			String val = eval.evaluate(xpath).get(0);
			return new Float(val).floatValue();
		} catch (Exception e) {
			return defaultValue;
		}
		 
	}
	
	static boolean evalBool(XPathEvaluator eval, String xpath, boolean defaultValue){
		try {
			String val = eval.evaluate(xpath).get(0);
			if (val.equalsIgnoreCase("yes") || val.equalsIgnoreCase("true"))
				return true;
			else if (val.equalsIgnoreCase("no") || val.equalsIgnoreCase("false"))
				return false;
			else
				return defaultValue;
		} catch (Exception e) {
			return defaultValue;
		}
		 
	}
	
	
	
	public static void main(String[] args) {
		
		System.out.println(IndexTypeParser.readAllFields(indexTypeXML));
	}
	
	static String indexTypeXML = 
			"<Resource version=\"0.4.x\">\n" + 
			"    \n" + 
			"   <ID>5af7eb01-6351-4b72-b5d1-c055cb7d2a3f</ID>\n" + 
			"    \n" + 
			"   <Type>GenericResource</Type>\n" + 
			"    \n" + 
			"   <Scopes>\n" + 
			"        \n" + 
			"      <Scope>/d4science.research-infrastructures.eu</Scope>\n" + 
			"        \n" + 
			"      <Scope>/d4science.research-infrastructures.eu/FARM</Scope>\n" + 
			"    \n" + 
			"   </Scopes>\n" + 
			"    \n" + 
			"   <Profile>\n" + 
			"        \n" + 
			"      <SecondaryType>FullTextIndexType</SecondaryType>\n" + 
			"        \n" + 
			"      <Name>IndexType_SmartfishFT</Name>\n" + 
			"        \n" + 
			"      <Description>Definition of the fulltext index type for the Smartfish</Description>\n" + 
			"        \n" + 
			"      <Body>\n" + 
			"            \n" + 
			"         <index-type name=\"default\">\n" + 
			"                \n" + 
			"            <field-list sort-xnear-stop-word-threshold=\"2E8\">\n" + 
			"                    \n" + 
//			"               <field name=\"title\">\n" + 
//			"                        \n" + 
//			"                  <index>yes</index>\n" + 
//			"                        \n" + 
//			"                  <store>yes</store>\n" + 
//			"                        \n" + 
//			"                  <return>yes</return>\n" + 
//			"                        \n" + 
//			"                  <tokenize>yes</tokenize>\n" + 
//			"                        \n" + 
//			"                  <sort>no</sort>\n" + 
//			"                        \n" + 
//			"                  <boost>1.0</boost>\n" + 
//			"                    \n" + 
//			"               </field>\n" + 
//			"                    \n" + 
//			"               <field name=\"text\">\n" + 
//			"                        \n" + 
//			"                  <index>yes</index>\n" + 
//			"                        \n" + 
//			"                  <store>yes</store>\n" + 
//			"                        \n" + 
//			"                  <return>yes</return>\n" + 
//			"                        \n" + 
//			"                  <tokenize>yes</tokenize>\n" + 
//			"                        \n" + 
//			"                  <sort>no</sort>\n" + 
//			"                        \n" + 
//			"                  <boost>1.0</boost>\n" + 
//			"                    \n" + 
//			"               </field>\n" + 
//			"                    \n" + 
//			"               <field name=\"ObjectID\">\n" + 
//			"                        \n" + 
//			"                  <index>yes</index>\n" + 
//			"                        \n" + 
//			"                  <store>yes</store>\n" + 
//			"                        \n" + 
//			"                  <return>yes</return>\n" + 
//			"                        \n" + 
//			"                  <tokenize>yes</tokenize>\n" + 
//			"                        \n" + 
//			"                  <sort>no</sort>\n" + 
//			"                        \n" + 
//			"                  <boost>1.0</boost>\n" + 
//			"                    \n" + 
//			"               </field>\n" + 
//			"                    \n" + 
//			"               <field name=\"gDocCollectionID\">\n" + 
//			"                        \n" + 
//			"                  <index>yes</index>\n" + 
//			"                        \n" + 
//			"                  <store>yes</store>\n" + 
//			"                        \n" + 
//			"                  <return>yes</return>\n" + 
//			"                        \n" + 
//			"                  <tokenize>yes</tokenize>\n" + 
//			"                        \n" + 
//			"                  <sort>no</sort>\n" + 
//			"                        \n" + 
//			"                  <boost>1.0</boost>\n" + 
//			"                    \n" + 
//			"               </field>\n" + 
//			"                    \n" + 
//			"               <field name=\"gDocCollectionLang\">\n" + 
//			"                        \n" + 
//			"                  <index>yes</index>\n" + 
//			"                        \n" + 
//			"                  <store>yes</store>\n" + 
//			"                        \n" + 
//			"                  <return>yes</return>\n" + 
//			"                        \n" + 
//			"                  <tokenize>yes</tokenize>\n" + 
//			"                        \n" + 
//			"                  <sort>no</sort>\n" + 
//			"                        \n" + 
//			"                  <boost>1.0</boost>\n" + 
//			"                    \n" + 
//			"               </field>\n" + 
//			"                    \n" + 
//			"               <field name=\"fishery_local_name\">\n" + 
//			"                        \n" + 
//			"                  <index>yes</index>\n" + 
//			"                        \n" + 
//			"                  <store>yes</store>\n" + 
//			"                        \n" + 
//			"                  <return>yes</return>\n" + 
//			"                        \n" + 
//			"                  <tokenize>yes</tokenize>\n" + 
//			"                        \n" + 
//			"                  <sort>no</sort>\n" + 
//			"                        \n" + 
//			"                  <boost>1.0</boost>\n" + 
//			"                    \n" + 
//			"               </field>\n" + 
//			"                    \n" + 
//			"               <field name=\"type_of_vessel\">\n" + 
//			"                        \n" + 
//			"                  <index>yes</index>\n" + 
//			"                        \n" + 
//			"                  <store>yes</store>\n" + 
//			"                        \n" + 
//			"                  <return>yes</return>\n" + 
//			"                        \n" + 
//			"                  <tokenize>yes</tokenize>\n" + 
//			"                        \n" + 
//			"                  <sort>no</sort>\n" + 
//			"                        \n" + 
//			"                  <boost>1.0</boost>\n" + 
//			"                    \n" + 
//			"               </field>\n" + 
//			"                    \n" + 
//			"               <field name=\"gear_used\">\n" + 
//			"                        \n" + 
//			"                  <index>yes</index>\n" + 
//			"                        \n" + 
//			"                  <store>yes</store>\n" + 
//			"                        \n" + 
//			"                  <return>yes</return>\n" + 
//			"                        \n" + 
//			"                  <tokenize>yes</tokenize>\n" + 
//			"                        \n" + 
//			"                  <sort>no</sort>\n" + 
//			"                        \n" + 
//			"                  <boost>1.0</boost>\n" + 
//			"                    \n" + 
//			"               </field>\n" + 
//			"                    \n" + 
//			"               <field name=\"technology_used\">\n" + 
//			"                        \n" + 
//			"                  <index>yes</index>\n" + 
//			"                        \n" + 
//			"                  <store>yes</store>\n" + 
//			"                        \n" + 
//			"                  <return>yes</return>\n" + 
//			"                        \n" + 
//			"                  <tokenize>yes</tokenize>\n" + 
//			"                        \n" + 
//			"                  <sort>no</sort>\n" + 
//			"                        \n" + 
//			"                  <boost>1.0</boost>\n" + 
//			"                    \n" + 
//			"               </field>\n" + 
//			"                    \n" + 
//			"               <field name=\"species_scientific_name\">\n" + 
//			"                        \n" + 
//			"                  <index>yes</index>\n" + 
//			"                        \n" + 
//			"                  <store>yes</store>\n" + 
//			"                        \n" + 
//			"                  <return>yes</return>\n" + 
//			"                        \n" + 
//			"                  <tokenize>yes</tokenize>\n" + 
//			"                        \n" + 
//			"                  <sort>no</sort>\n" + 
//			"                        \n" + 
//			"                  <boost>1.0</boost>\n" + 
//			"                    \n" + 
//			"               </field>\n" + 
//			"                    \n" + 
//			"               <field name=\"species_english_name\">\n" + 
//			"                        \n" + 
//			"                  <index>yes</index>\n" + 
//			"                        \n" + 
//			"                  <store>yes</store>\n" + 
//			"                        \n" + 
//			"                  <return>yes</return>\n" + 
//			"                        \n" + 
//			"                  <tokenize>yes</tokenize>\n" + 
//			"                        \n" + 
//			"                  <sort>no</sort>\n" + 
//			"                        \n" + 
//			"                  <boost>1.0</boost>\n" + 
//			"                    \n" + 
//			"               </field>\n" + 
//			"                    \n" + 
//			"               <field name=\"country\">\n" + 
//			"                        \n" + 
//			"                  <index>yes</index>\n" + 
//			"                        \n" + 
//			"                  <store>yes</store>\n" + 
//			"                        \n" + 
//			"                  <return>yes</return>\n" + 
//			"                        \n" + 
//			"                  <tokenize>yes</tokenize>\n" + 
//			"                        \n" + 
//			"                  <sort>no</sort>\n" + 
//			"                        \n" + 
//			"                  <boost>1.0</boost>\n" + 
//			"                    \n" + 
//			"               </field>\n" + 
//			"                    \n" + 
//			"               <field name=\"provenance\">\n" + 
//			"                        \n" + 
//			"                  <index>no</index>\n" + 
//			"                        \n" + 
//			"                  <store>yes</store>\n" + 
//			"                        \n" + 
//			"                  <return>yes</return>\n" + 
//			"                        \n" + 
//			"                  <tokenize>yes</tokenize>\n" + 
//			"                        \n" + 
//			"                  <sort>no</sort>\n" + 
//			"                        \n" + 
//			"                  <boost>1.0</boost>\n" + 
//			"                    \n" + 
//			"               </field>\n" + 
//			"                    \n" + 
//			"               <field name=\"management\">\n" + 
//			"                        \n" + 
//			"                  <index>yes</index>\n" + 
//			"                        \n" + 
//			"                  <store>yes</store>\n" + 
//			"                        \n" + 
//			"                  <return>yes</return>\n" + 
//			"                        \n" + 
//			"                  <tokenize>yes</tokenize>\n" + 
//			"                        \n" + 
//			"                  <sort>no</sort>\n" + 
//			"                        \n" + 
//			"                  <boost>1.0</boost>\n" + 
//			"                    \n" + 
//			"               </field>\n" + 
//			"                    \n" + 
//			"               <field name=\"access_control\">\n" + 
//			"                        \n" + 
//			"                  <index>yes</index>\n" + 
//			"                        \n" + 
//			"                  <store>yes</store>\n" + 
//			"                        \n" + 
//			"                  <return>yes</return>\n" + 
//			"                        \n" + 
//			"                  <tokenize>yes</tokenize>\n" + 
//			"                        \n" + 
//			"                  <sort>no</sort>\n" + 
//			"                        \n" + 
//			"                  <boost>1.0</boost>\n" + 
//			"                    \n" + 
//			"               </field>\n" + 
//			"                    \n" + 
//			"               <field name=\"fishing_control\">\n" + 
//			"                        \n" + 
//			"                  <index>yes</index>\n" + 
//			"                        \n" + 
//			"                  <store>yes</store>\n" + 
//			"                        \n" + 
//			"                  <return>yes</return>\n" + 
//			"                        \n" + 
//			"                  <tokenize>yes</tokenize>\n" + 
//			"                        \n" + 
//			"                  <sort>no</sort>\n" + 
//			"                        \n" + 
//			"                  <boost>1.0</boost>\n" + 
//			"                    \n" + 
//			"               </field>\n" + 
//			"                    \n" + 
//			"               <field name=\"exploitation_status\">\n" + 
//			"                        \n" + 
//			"                  <index>yes</index>\n" + 
//			"                        \n" + 
//			"                  <store>yes</store>\n" + 
//			"                        \n" + 
//			"                  <return>yes</return>\n" + 
//			"                        \n" + 
//			"                  <tokenize>yes</tokenize>\n" + 
//			"                        \n" + 
//			"                  <sort>no</sort>\n" + 
//			"                        \n" + 
//			"                  <boost>1.0</boost>\n" + 
//			"                    \n" + 
//			"               </field>\n" + 
//			"                    \n" + 
//			"               <field name=\"enforcement_method\">\n" + 
//			"                        \n" + 
//			"                  <index>yes</index>\n" + 
//			"                        \n" + 
//			"                  <store>yes</store>\n" + 
//			"                        \n" + 
//			"                  <return>yes</return>\n" + 
//			"                        \n" + 
//			"                  <tokenize>yes</tokenize>\n" + 
//			"                        \n" + 
//			"                  <sort>no</sort>\n" + 
//			"                        \n" + 
//			"                  <boost>1.0</boost>\n" + 
//			"                    \n" + 
//			"               </field>\n" + 
//			"                    \n" + 
//			"               <field name=\"sector\">\n" + 
//			"                        \n" + 
//			"                  <index>yes</index>\n" + 
//			"                        \n" + 
//			"                  <store>yes</store>\n" + 
//			"                        \n" + 
//			"                  <return>yes</return>\n" + 
//			"                        \n" + 
//			"                  <tokenize>yes</tokenize>\n" + 
//			"                        \n" + 
//			"                  <sort>no</sort>\n" + 
//			"                        \n" + 
//			"                  <boost>1.0</boost>\n" + 
//			"                    \n" + 
//			"               </field>\n" + 
//			"                    \n" + 
//			"               <field name=\"seasonality\">\n" + 
//			"                        \n" + 
//			"                  <index>yes</index>\n" + 
//			"                        \n" + 
//			"                  <store>yes</store>\n" + 
//			"                        \n" + 
//			"                  <return>yes</return>\n" + 
//			"                        \n" + 
//			"                  <tokenize>yes</tokenize>\n" + 
//			"                        \n" + 
//			"                  <sort>no</sort>\n" + 
//			"                        \n" + 
//			"                  <boost>1.0</boost>\n" + 
//			"                    \n" + 
//			"               </field>\n" + 
//			"                    \n" + 
//			"               <field name=\"authority\">\n" + 
//			"                        \n" + 
//			"                  <index>yes</index>\n" + 
//			"                        \n" + 
//			"                  <store>yes</store>\n" + 
//			"                        \n" + 
//			"                  <return>yes</return>\n" + 
//			"                        \n" + 
//			"                  <tokenize>yes</tokenize>\n" + 
//			"                        \n" + 
//			"                  <sort>no</sort>\n" + 
//			"                        \n" + 
//			"                  <boost>1.0</boost>\n" + 
//			"                    \n" + 
//			"               </field>\n" + 
//			"                    \n" + 
//			"               <field name=\"vessel_uri\">\n" + 
//			"                        \n" + 
//			"                  <index>no</index>\n" + 
//			"                        \n" + 
//			"                  <store>yes</store>\n" + 
//			"                        \n" + 
//			"                  <return>yes</return>\n" + 
//			"                        \n" + 
//			"                  <tokenize>yes</tokenize>\n" + 
//			"                        \n" + 
//			"                  <sort>no</sort>\n" + 
//			"                        \n" + 
//			"                  <boost>1.0</boost>\n" + 
//			"                    \n" + 
//			"               </field>\n" + 
//			"                    \n" + 
//			"               <field name=\"seasonality_uri\">\n" + 
//			"                        \n" + 
//			"                  <index>no</index>\n" + 
//			"                        \n" + 
//			"                  <store>yes</store>\n" + 
//			"                        \n" + 
//			"                  <return>yes</return>\n" + 
//			"                        \n" + 
//			"                  <tokenize>yes</tokenize>\n" + 
//			"                        \n" + 
//			"                  <sort>no</sort>\n" + 
//			"                        \n" + 
//			"                  <boost>1.0</boost>\n" + 
//			"                    \n" + 
//			"               </field>\n" + 
//			"                    \n" + 
//			"               <field name=\"authority_uri\">\n" + 
//			"                        \n" + 
//			"                  <index>no</index>\n" + 
//			"                        \n" + 
//			"                  <store>yes</store>\n" + 
//			"                        \n" + 
//			"                  <return>yes</return>\n" + 
//			"                        \n" + 
//			"                  <tokenize>yes</tokenize>\n" + 
//			"                        \n" + 
//			"                  <sort>no</sort>\n" + 
//			"                        \n" + 
//			"                  <boost>1.0</boost>\n" + 
//			"                    \n" + 
//			"               </field>\n" + 
//			"                    \n" + 
//			"               <field name=\"management_uri\">\n" + 
//			"                        \n" + 
//			"                  <index>no</index>\n" + 
//			"                        \n" + 
//			"                  <store>yes</store>\n" + 
//			"                        \n" + 
//			"                  <return>yes</return>\n" + 
//			"                        \n" + 
//			"                  <tokenize>yes</tokenize>\n" + 
//			"                        \n" + 
//			"                  <sort>no</sort>\n" + 
//			"                        \n" + 
//			"                  <boost>1.0</boost>\n" + 
//			"                    \n" + 
//			"               </field>\n" + 
//			"                    \n" + 
//			"               <field name=\"exploitation_status_uri\">\n" + 
//			"                        \n" + 
//			"                  <index>no</index>\n" + 
//			"                        \n" + 
//			"                  <store>yes</store>\n" + 
//			"                        \n" + 
//			"                  <return>yes</return>\n" + 
//			"                        \n" + 
//			"                  <tokenize>yes</tokenize>\n" + 
//			"                        \n" + 
//			"                  <sort>no</sort>\n" + 
//			"                        \n" + 
//			"                  <boost>1.0</boost>\n" + 
//			"                    \n" + 
//			"               </field>\n" + 
//			"                    \n" + 
//			"               <field name=\"access_control_uri\">\n" + 
//			"                        \n" + 
//			"                  <index>no</index>\n" + 
//			"                        \n" + 
//			"                  <store>yes</store>\n" + 
//			"                        \n" + 
//			"                  <return>yes</return>\n" + 
//			"                        \n" + 
//			"                  <tokenize>yes</tokenize>\n" + 
//			"                        \n" + 
//			"                  <sort>no</sort>\n" + 
//			"                        \n" + 
//			"                  <boost>1.0</boost>\n" + 
//			"                    \n" + 
//			"               </field>\n" + 
//			"                    \n" + 
//			"               <field name=\"fishing_control_uri\">\n" + 
//			"                        \n" + 
//			"                  <index>no</index>\n" + 
//			"                        \n" + 
//			"                  <store>yes</store>\n" + 
//			"                        \n" + 
//			"                  <return>yes</return>\n" + 
//			"                        \n" + 
//			"                  <tokenize>yes</tokenize>\n" + 
//			"                        \n" + 
//			"                  <sort>no</sort>\n" + 
//			"                        \n" + 
//			"                  <boost>1.0</boost>\n" + 
//			"                    \n" + 
//			"               </field>\n" + 
//			"                    \n" + 
//			"               <field name=\"enforcement_method_uri\">\n" + 
//			"                        \n" + 
//			"                  <index>no</index>\n" + 
//			"                        \n" + 
//			"                  <store>yes</store>\n" + 
//			"                        \n" + 
//			"                  <return>yes</return>\n" + 
//			"                        \n" + 
//			"                  <tokenize>yes</tokenize>\n" + 
//			"                        \n" + 
//			"                  <sort>no</sort>\n" + 
//			"                        \n" + 
//			"                  <boost>1.0</boost>\n" + 
//			"                    \n" + 
//			"               </field>\n" + 
//			"                    \n" + 
//			"               <field name=\"sector_uri\">\n" + 
//			"                        \n" + 
//			"                  <index>no</index>\n" + 
//			"                        \n" + 
//			"                  <store>yes</store>\n" + 
//			"                        \n" + 
//			"                  <return>yes</return>\n" + 
//			"                        \n" + 
//			"                  <tokenize>yes</tokenize>\n" + 
//			"                        \n" + 
//			"                  <sort>no</sort>\n" + 
//			"                        \n" + 
//			"                  <boost>1.0</boost>\n" + 
//			"                    \n" + 
//			"               </field>\n" + 
//			"                    \n" + 
//			"               <field name=\"species_uri\">\n" + 
//			"                        \n" + 
//			"                  <index>no</index>\n" + 
//			"                        \n" + 
//			"                  <store>yes</store>\n" + 
//			"                        \n" + 
//			"                  <return>yes</return>\n" + 
//			"                        \n" + 
//			"                  <tokenize>yes</tokenize>\n" + 
//			"                        \n" + 
//			"                  <sort>no</sort>\n" + 
//			"                        \n" + 
//			"                  <boost>1.0</boost>\n" + 
//			"                    \n" + 
//			"               </field>\n" + 
//			"                    \n" + 
//			"               <field name=\"country_uri\">\n" + 
//			"                        \n" + 
//			"                  <index>no</index>\n" + 
//			"                        \n" + 
//			"                  <store>yes</store>\n" + 
//			"                        \n" + 
//			"                  <return>yes</return>\n" + 
//			"                        \n" + 
//			"                  <tokenize>yes</tokenize>\n" + 
//			"                        \n" + 
//			"                  <sort>no</sort>\n" + 
//			"                        \n" + 
//			"                  <boost>1.0</boost>\n" + 
//			"                    \n" + 
//			"               </field>\n" + 
//			"                    \n" + 
//			"               <field name=\"gear_uri\">\n" + 
//			"                        \n" + 
//			"                  <index>no</index>\n" + 
//			"                        \n" + 
//			"                  <store>yes</store>\n" + 
//			"                        \n" + 
//			"                  <return>yes</return>\n" + 
//			"                        \n" + 
//			"                  <tokenize>yes</tokenize>\n" + 
//			"                        \n" + 
//			"                  <sort>no</sort>\n" + 
//			"                        \n" + 
//			"                  <boost>1.0</boost>\n" + 
//			"                    \n" + 
//			"               </field>\n" + 
//			"                    \n" + 
//			"               <field name=\"doc_uri\">\n" + 
//			"                        \n" + 
//			"                  <index>no</index>\n" + 
//			"                        \n" + 
//			"                  <store>yes</store>\n" + 
//			"                        \n" + 
//			"                  <return>yes</return>\n" + 
//			"                        \n" + 
//			"                  <tokenize>yes</tokenize>\n" + 
//			"                        \n" + 
//			"                  <sort>no</sort>\n" + 
//			"                        \n" + 
//			"                  <boost>1.0</boost>\n" + 
//			"                    \n" + 
//			"               </field>\n" + 
//			"                    \n" + 
			"               <field name=\"S\">\n" + 
			"                        \n" + 
			"                  <index>no</index>\n" + 
			"                        \n" + 
			"                  <store>yes</store>\n" + 
			"                        \n" + 
			"                  <return>yes</return>\n" + 
			"                        \n" + 
			"                  <tokenize>no</tokenize>\n" + 
			"                        \n" + 
			"                  <sort>no</sort>\n" + 
			"                        \n" + 
			"                  <boost>1.0</boost>\n" + 
			"                    \n" + 
			"               </field>\n" + 
			"                \n" + 
			"            </field-list>\n" + 
			"            \n" + 
			"         </index-type>\n" + 
			"        \n" + 
			"      </Body>\n" + 
			"    \n" + 
			"   </Profile>\n" + 
			"\n" + 
			"</Resource>";
}
